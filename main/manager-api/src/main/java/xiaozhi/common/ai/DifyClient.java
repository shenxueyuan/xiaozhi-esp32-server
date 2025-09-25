package xiaozhi.common.ai;//package com.zstt.lecture.service.ai;
//
//import com.alibaba.fastjson2.JSON;
//import com.alibaba.fastjson2.JSONObject;
//import com.zstt.lecture.service.ai.enums.DifyClientErrorEnum;
//import com.zstt.lecture.service.ai.enums.DifyWorkflowStatusEnum;
//import com.zstt.lecture.service.ai.response.*;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpStatusCodeException;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.Objects;
//import java.util.UUID;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import cn.hutool.core.text.UnicodeUtil;
//
//
//@Slf4j
//@Component
//@RequiredArgsConstructor(onConstructor = @__(@Autowired))
//public class DifyClient {
//    private final RestTemplate restTemplate;
//
//    @Value("${model.invoke.url}")
//    private String modelInvokeUrl;
//
//    /**
//     * blocking模式下的文本生成
//     *
//     * @return 文本生成结果
//     */
//    public AIResponse<CompletionMessageResponse> completion(String apiKey, String requestBody) {
//        String responseBody;
//        Long startTimeStamp = System.currentTimeMillis();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + apiKey);
//            headers.set("Content-Type", "application/json");
//            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//            log.info("start invoke dify chat completion modelInvokeUrl:{}; apiKey:{}; requestBody:{};", modelInvokeUrl, apiKey, requestBody);
//            ResponseEntity<String> response = restTemplate.postForEntity(modelInvokeUrl, entity, String.class);
//            log.info("end invoke dify chat completion modelInvokeUrl:{}; apiKey:{}; requestBody:{}; responseBody:{}; cost:{}ms;",
//                    modelInvokeUrl, apiKey, requestBody, response.getBody(), System.currentTimeMillis() - startTimeStamp);
//
//            if (!response.getStatusCode().isError() && Objects.nonNull(response.getBody())) {
//                responseBody = response.getBody();
//                responseBody = UnicodeUtil.toString(responseBody);
//                // responseBody = decodeUnicode(responseBody);
//            } else {
//                return AIResponse.fail(DifyClientErrorEnum.HTTP_FAILED.status(), DifyClientErrorEnum.HTTP_FAILED.code(),
//                        DifyClientErrorEnum.HTTP_FAILED.description(), response.toString(), startTimeStamp, System.currentTimeMillis());
//            }
//        } catch (HttpStatusCodeException he) {
//            long endTime = System.currentTimeMillis();
//            responseBody = he.getResponseBodyAsString();
//            int responseCode = he.getStatusCode().value();
//            log.info("end invoke dify chat completion with http error modelInvokeUrl:{}; apiKey:{}; requestBody:{}; cost:{}ms; responseCode:{}; responseBody:{}",
//                    modelInvokeUrl, apiKey, requestBody, endTime - startTimeStamp, responseCode, responseBody);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(),
//                    DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        } catch (Exception e) {
//            log.info("end invoke dify chat completion with error modelInvokeUrl:{}; apiKey:{}; requestBody:{}; cost:{}ms; ",
//                    modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(),
//                    DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        }
//        try {
//            JSONObject json = JSONObject.parseObject(responseBody);
//            if (isError(json)) {
//                return AIResponse.fail(json.getInteger("status"), json.getString("code"), json.getString("message"), responseBody, startTimeStamp, System.currentTimeMillis());
//            }
//            return AIResponse.success(json.toJavaObject(CompletionMessageResponse.class), responseBody, startTimeStamp, System.currentTimeMillis());
//        } catch (Exception e) {
//            log.error("dify chat completion 响应解析失败 requestBody: {}; responseBody: {} ", requestBody, responseBody, e);
//            return AIResponse.fail(DifyClientErrorEnum.PARSE_FAILED.status(), DifyClientErrorEnum.PARSE_FAILED.code(), DifyClientErrorEnum.PARSE_FAILED.description(), responseBody, startTimeStamp, System.currentTimeMillis());
//        }
//    }
//
//    /**
//     * blocking模式下的文本生成
//     *
//     * @return 文本生成结果
//     */
//    public AIResponse<MessageResponse> chat(String apiKey, String requestBody) {
//        String responseBody;
//        Long startTimeStamp = System.currentTimeMillis();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + apiKey);
//            headers.set("Content-Type", "application/json");
//            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//            log.info("start invoke dify chat modelInvokeUrl:{}; apiKey:{}; requestBody:{};", modelInvokeUrl, apiKey, requestBody);
//            ResponseEntity<String> response = restTemplate.postForEntity(modelInvokeUrl, entity, String.class);
//            log.info("end invoke dify chat modelInvokeUrl:{}; apiKey:{}; requestBody:{}; cost:{}ms;",
//                    modelInvokeUrl, apiKey, entity.getBody(), System.currentTimeMillis() - startTimeStamp);
//            if (!response.getStatusCode().isError() && Objects.nonNull(response.getBody())) {
//                responseBody = response.getBody();
//                responseBody = decodeUnicode(responseBody);
//            } else {
//                return AIResponse.fail(DifyClientErrorEnum.HTTP_FAILED.status(), DifyClientErrorEnum.HTTP_FAILED.code(), DifyClientErrorEnum.HTTP_FAILED.description(), response.toString(), startTimeStamp, System.currentTimeMillis());
//            }
//        } catch (HttpStatusCodeException he) {
//            long endTime = System.currentTimeMillis();
//            int responseCode = he.getStatusCode().value();
//            responseBody = he.getResponseBodyAsString();
//            log.error("end invoke dify chat with http error modelInvokeUrl:{}; apiKey:{}; requestBody:{}; cost:{}ms; responseCode:{}; responseBody:{}",
//                    modelInvokeUrl, apiKey, requestBody, endTime - startTimeStamp, responseCode, responseBody, he);
//        } catch (Exception e) {
//            log.error("end invoke dify chat with error modelInvokeUrl:{}; apiKey:{}; requestBody:{}; cost:{}ms; ",
//                    modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp, e);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(), DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        }
//        try {
//            JSONObject json = JSONObject.parseObject(responseBody);
//            if (isError(json)) {
//                return AIResponse.fail(json.getInteger("status"), json.getString("code"), json.getString("message"), responseBody, startTimeStamp, System.currentTimeMillis());
//            }
//            return AIResponse.success(json.toJavaObject(MessageResponse.class), responseBody, startTimeStamp, System.currentTimeMillis());
//        } catch (Exception e) {
//            log.error("dify chat completion 响应解析失败 requestBody: {}; responseBody: {} ", requestBody, responseBody, e);
//            return AIResponse.fail(DifyClientErrorEnum.PARSE_FAILED.status(), DifyClientErrorEnum.PARSE_FAILED.code(), DifyClientErrorEnum.PARSE_FAILED.description(), responseBody, startTimeStamp, System.currentTimeMillis());
//        }
//    }
//
//
//    public AIResponse<MessageSuggestedResponse> messageSuggested(String apiKey, String msgId, String userId) {
//        log.info("invoke messages suggested msgId:{}; user:{}; apiKey:{}", msgId, userId, apiKey);
//        String responseBody;
//        Long startTimeStamp = System.currentTimeMillis();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + apiKey);
//            HttpEntity<String> entity = new HttpEntity<>(headers);
//            log.info("start invoke dify messages suggested modelInvokeUrl:{} apiKey:{}; msgId:{}; userId:{}", modelInvokeUrl, apiKey, msgId, userId);
//            modelInvokeUrl = StringUtils.replace(modelInvokeUrl, "{message_id}", msgId);
//            modelInvokeUrl = StringUtils.replace(modelInvokeUrl, "{dify_user}", userId);
//            ResponseEntity<String> response = restTemplate.exchange(modelInvokeUrl, HttpMethod.GET, entity, String.class);
//            log.info("end invoke dify messages suggested modelInvokeUrl:{} apiKey:{}; msgId:{}; userId:{}; cost:{};",
//                    modelInvokeUrl, apiKey, msgId, userId, System.currentTimeMillis() - startTimeStamp);
//            if (!response.getStatusCode().isError() && Objects.nonNull(response.getBody())) {
//                responseBody = response.getBody();
//            } else {
//                return AIResponse.fail(DifyClientErrorEnum.HTTP_FAILED.status(), DifyClientErrorEnum.HTTP_FAILED.code(),
//                        DifyClientErrorEnum.HTTP_FAILED.description(), response.toString(), startTimeStamp, System.currentTimeMillis());
//            }
//        } catch (HttpStatusCodeException he) {
//            responseBody = he.getResponseBodyAsString();
//            log.error("end invoke dify messages suggested with http error modelInvokeUrl:{} apiKey:{}; msgId:{}; userId:{}; cost:{};",
//                    modelInvokeUrl, apiKey, msgId, userId, System.currentTimeMillis() - startTimeStamp, he);
//        } catch (Exception e) {
//            log.error("end invoke dify messages suggested with error modelInvokeUrl:{} apiKey:{}; msgId:{}; userId:{}; cost:{};",
//                    modelInvokeUrl, apiKey, msgId, userId, System.currentTimeMillis() - startTimeStamp, e);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(),
//                    DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        }
//        try {
//            JSONObject json = JSONObject.parseObject(responseBody);
//            if (isError(json)) {
//                return AIResponse.fail(json.getInteger("status"), json.getString("code"), json.getString("message"),
//                        responseBody, startTimeStamp, System.currentTimeMillis());
//            }
//            return AIResponse.success(json.toJavaObject(MessageSuggestedResponse.class), responseBody, startTimeStamp, System.currentTimeMillis());
//        } catch (Exception e) {
//            log.error("dify messages suggested 响应解析失败 msgId:{}; user:{}; responseBody {} ", msgId, userId, responseBody, e);
//            return AIResponse.fail(DifyClientErrorEnum.PARSE_FAILED.status(), DifyClientErrorEnum.PARSE_FAILED.code(),
//                    DifyClientErrorEnum.PARSE_FAILED.description(), responseBody, startTimeStamp, System.currentTimeMillis());
//        }
//    }
//
//    /**
//     * blocking模式下的工作流
//     *
//     * @param requestBody 文本生成
//     * @return 文本生成结果
//     */
//    public AIResponse<WorkflowResponse> runWorkflow(String apiKey, String requestBody) {
//        String responseBody = null;
//        Long startTimeStamp = System.currentTimeMillis();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + apiKey);
//            headers.set("Content-Type", "application/json");
//            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//            log.info("start invoke dify workflow modelInvokeUrl:{}; apiKey:{}; requestBody{};", modelInvokeUrl, apiKey, requestBody);
//            ResponseEntity<String> response = restTemplate.postForEntity(modelInvokeUrl, entity, String.class);
//            log.info("end invoke dify workflow modelInvokeUrl:{}; apiKey:{}; requestBody{}; cost:{};", modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp);
//            if (!response.getStatusCode().isError() && Objects.nonNull(response.getBody())) {
//                responseBody = response.getBody();
//            } else {
//                return AIResponse.fail(DifyClientErrorEnum.HTTP_FAILED.status(), DifyClientErrorEnum.HTTP_FAILED.code(), DifyClientErrorEnum.HTTP_FAILED.description(), response.toString(), startTimeStamp, System.currentTimeMillis());
//            }
//        } catch (HttpStatusCodeException he) {
//            responseBody = he.getResponseBodyAsString();
//            log.error("end invoke dify workflow with http error modelInvokeUrl:{}; apiKey:{}; requestBody{}; cost:{};", modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp, he);
//        } catch (Exception e) {
//            log.error("end invoke dify workflow with error modelInvokeUrl:{}; apiKey:{}; requestBody{}; cost:{};", modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp, e);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(), DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        }
//        try {
//            JSONObject json = JSONObject.parseObject(responseBody);
//            if (isError(json)) {
//                return AIResponse.fail(json.getInteger("status"), json.getString("code"), json.getString("message"), responseBody, startTimeStamp, System.currentTimeMillis());
//            }
//            WorkflowResponse response = json.toJavaObject(WorkflowResponse.class);
//            if (!DifyWorkflowStatusEnum.SUCCEEDED.match(response.getData().getStatus())) {
//                log.error("dify workflow failed workflow not succeed request {} response responseBody {} ", requestBody, responseBody);
//                return AIResponse.fail(response, DifyClientErrorEnum.WORKFLOW_STATUS_ERROR.status(), DifyClientErrorEnum.WORKFLOW_STATUS_ERROR.code(), response.getData().getStatus(), responseBody, startTimeStamp, System.currentTimeMillis());
//            }
//            return AIResponse.success(json.toJavaObject(WorkflowResponse.class), responseBody, startTimeStamp, System.currentTimeMillis());
//        } catch (Exception e) {
//            log.error("dify workflow 响应解析失败 requestBody: {}; responseBody: {} ", requestBody, responseBody, e);
//            return AIResponse.fail(DifyClientErrorEnum.PARSE_FAILED.status(), DifyClientErrorEnum.PARSE_FAILED.code(), DifyClientErrorEnum.PARSE_FAILED.description(), responseBody, startTimeStamp, System.currentTimeMillis());
//        }
//    }
//
//    /**
//     * agent模式请求dify streaming
//     *
//     * @param requestBody
//     * @param apiKey
//     * @return
//     */
//    public AIResponse<AgentMessageResponse> requestAgent(String apiKey, String requestBody) {
//        String body;
//        Long startTimeStamp = System.currentTimeMillis();
//        try {
//            HttpHeaders headers = new HttpHeaders();
//            headers.set("Authorization", "Bearer " + apiKey);
//            headers.set("Content-Type", "application/json");
//            HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//            log.info("start invoke dify agent modelInvokeUrl:{} apiKey{}; requestBody:{}", modelInvokeUrl, apiKey, requestBody);
//            ResponseEntity<Resource> response = restTemplate.postForEntity(modelInvokeUrl, entity, Resource.class);
//            log.info("invoke dify agent response:{}", response.getBody());
//            log.info("end invoke dify agent modelInvokeUrl:{} apiKey{}; requestBody:{}; cost:{};",
//                    modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp);
//            if (!response.getStatusCode().isError() && Objects.nonNull(response.getBody())) {
//                // 处理流式数据
//                AgentMessageResponse agentResponse = handleAgentStream(response.getBody().getInputStream());
//                if (agentResponse == null) {
//                    return AIResponse.fail(DifyClientErrorEnum.PARSE_FAILED.status(), DifyClientErrorEnum.PARSE_FAILED.code(), DifyClientErrorEnum.PARSE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//                }
//                return AIResponse.success(agentResponse, JSON.toJSONString(agentResponse), startTimeStamp, System.currentTimeMillis());
//            } else {
//                return AIResponse.fail(DifyClientErrorEnum.HTTP_FAILED.status(), DifyClientErrorEnum.HTTP_FAILED.code(), DifyClientErrorEnum.HTTP_FAILED.description(), response.toString(), startTimeStamp, System.currentTimeMillis());
//            }
//        } catch (HttpStatusCodeException he) {
//            log.error("end invoke dify agent with http error modelInvokeUrl:{} apiKey{}; requestBody:{}; cost:{};",
//                    modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp, he);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(), DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        } catch (Exception e) {
//            log.error("end invoke dify agent with error modelInvokeUrl:{} apiKey{}; requestBody:{}; cost:{};",
//                    modelInvokeUrl, apiKey, requestBody, System.currentTimeMillis() - startTimeStamp, e);
//            return AIResponse.fail(DifyClientErrorEnum.INVOKE_FAILED.status(), DifyClientErrorEnum.INVOKE_FAILED.code(), DifyClientErrorEnum.INVOKE_FAILED.description(), "", startTimeStamp, System.currentTimeMillis());
//        }
//    }
//
//    /**
//     * 处理agent的streaming rest
//     *
//     * @param inputStream
//     * @return
//     */
//    private AgentMessageResponse handleAgentStream(InputStream inputStream) {
//        AgentMessageResponse agentResponse = null;
//        StringBuilder sb = new StringBuilder();
//        // 生成uuid
//        String uuid = UUID.randomUUID().toString();
//        try {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                log.info("dify agent request uuid: {}, stream==>{}", uuid, line);
//                if (line.startsWith("data: ")) {
//                    // 去除前缀 "data: "
//                    String dataStr = line.substring(6);
//                    JSONObject jsonObject = JSON.parseObject(dataStr);
//                    if (jsonObject.getString("event").equals("message_end")) {
//                        agentResponse = JSON.parseObject(dataStr, AgentMessageResponse.class);
//                    }
//                    if (jsonObject.getString("event").equals("agent_message")) {
//                        String answer = jsonObject.getString("answer");
//                        sb.append(answer);
//                    }
//                }
//            }
//            reader.close();
//            agentResponse.setAnswer(sb.toString());
//        } catch (IOException e) {
//            log.error("agent处理流式数据时异常！uuid：{}", uuid, e);
//        }
//        return agentResponse;
//    }
//
//
//    private boolean isError(JSONObject json) {
//        return json != null && json.containsKey("status");
//    }
//
//    public static String decodeUnicode(String input) {
//        if (StringUtils.isBlank(input)) {
//            return input;
//        }
//        StringBuilder output = new StringBuilder();
//        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
//        Matcher matcher = pattern.matcher(input);
//
//        while (matcher.find()) {
//            String unicode = matcher.group(1);
//            char unicodeChar = (char) Integer.parseInt(unicode, 16);
//            matcher.appendReplacement(output, String.valueOf(unicodeChar));
//        }
//        matcher.appendTail(output);
//        return output.toString();
//    }
//}