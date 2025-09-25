package xiaozhi.common.config;

import jakarta.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class OkHttpClientConfig {

    @Resource
    private ExecutorService okhttpExecutor;

    @Bean(value = "aiOkHttpClient")
    public OkHttpClient aiOkHttpClient() {
        Dispatcher dispatcher = new Dispatcher(okhttpExecutor);
        dispatcher.setMaxRequests(100);       // 最大并发请求数
        dispatcher.setMaxRequestsPerHost(10); // 单个主机最大并发请求数
        return new OkHttpClient.Builder()
                .dispatcher(dispatcher)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
    }
}
