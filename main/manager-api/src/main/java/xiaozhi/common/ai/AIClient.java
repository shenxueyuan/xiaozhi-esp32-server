package xiaozhi.common.ai;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson2.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import xiaozhi.common.ai.request.*;
import xiaozhi.common.ai.response.*;

@Slf4j
@Component
@RefreshScope
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AIClient {

    private final OkHttpClient aiOkHttpClient;

    @Value("${ai.api.url:http://106.15.33.103:8080/v1}")
    private String apiUrl;


    public CompletionMessageResponse completionMessageBlocking(CompletionMessageRequest completionMessageRequest, String apiKey) {
        CompletableFuture<CompletionMessageResponse> future = new CompletableFuture<>();

        Request request = createRequest(apiUrl + "/completion-messages", apiKey, completionMessageRequest);

        aiOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                log.error("completionMessage onFailure error", e);
                throw  new RuntimeException(e);
            }

            @Override
            public void onResponse(Call call, Response response) {
                ResponseBody body = null;
                try (response) {
                    body = response.body();

                    if (!response.isSuccessful() || body == null) {
                        log.error("completionMessage onResponse error, responseCode: {}, responseMessage: {}", response.code(), response.message());
                        future.completeExceptionally(new RuntimeException("completionMessage onResponse error, responseCode: " + response.code() + ", responseMessage: " + response.message()));
                        return;
                    }
                    String bodyString = body.string();
                    log.info("completionMessage onResponse: {}", JSONObject.toJSONString(bodyString));
                    CompletionMessageResponse result = JSONObject.parseObject(bodyString, CompletionMessageResponse.class);
                    future.complete(result);

                } catch (IOException e) {
                    log.error("completionMessage onResponse error, responseBody: {}", body, e);
                    future.completeExceptionally(e);
                }
            }
        });

        return future.join();
    }


    private Request createRequest(String url, String apiKey, CompletionMessageRequest completionMessageRequest) {
        log.info("createRequest: {}", JSONObject.toJSONString(completionMessageRequest));
        return new Request.Builder()
                .url(url)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(JSONObject.toJSONString(completionMessageRequest), MediaType.parse("application/json")))
                .build();
    }

    public void completionMessageStreaming(CompletionMessageRequest completionMessageRequest,
                                           String apiKey,
                                           CompletionMessageResponse finalResponse,
                                           Consumer<CompletionMessageResponse> onNext,
                                           Consumer<Error> onError,
                                           Runnable onComplete) {
        Request request = createRequest(apiUrl + "/completion-messages", apiKey, completionMessageRequest);
        aiOkHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                log.error("completionMessageStreaming onFailure error", e);
                onError.accept(new Error(e));
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) {
                try (response) {
                    if (!response.isSuccessful() || response.body() == null) {
                        log.error("completionMessageStreaming onResponse error, responseCode: {}, responseMessage: {}", response.code(), response.message());
                        return;
                    }
                    try (InputStream inputStream = response.body().byteStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                        String line;
                        while ((line = reader.readLine()) != null && !line.equals("[DONE]")) {
                            if (StringUtils.isBlank(line)) {
                                continue;
                            }
                            String data = line.startsWith("data:") ? line.substring(5) : line;
                            CompletionMessageResponse chunk = JSONObject.parseObject(data, CompletionMessageResponse.class);
                            if ("message_end".equals(chunk.getEvent())) {
                                BeanUtil.copyProperties(chunk, finalResponse);
                            }
                            onNext.accept(chunk);
                        }
                        onComplete.run();
                    }
                } catch (IOException e) {
                    log.error("completionMessage onResponse error", e);
                    onError.accept(new Error(e));
                }
            }
        });
    }
}
