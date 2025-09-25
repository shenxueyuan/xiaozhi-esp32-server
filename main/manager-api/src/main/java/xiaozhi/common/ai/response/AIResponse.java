package xiaozhi.common.ai.response;

import java.io.Serializable;
import lombok.Data;
import xiaozhi.common.ai.enums.DifyClientStatusEnum;

@Data
public class AIResponse<T> implements Serializable {
    private Integer status;
    private String code;
    private String message;
    private T data;
    private String rawResponse;
    private Long startTimestamp;
    private Long endTimestamp;

    /**
     * 判断响应是否成功
     * @return 是否成功
     */
    public boolean success(){
        return DifyClientStatusEnum.SUCCESS.match(status);
    }

    public static <T> AIResponse<T> success(T data, String rawResponse, Long startTimestamp, Long endTimestamp){
        AIResponse<T> response = new AIResponse<>();
        response.setStatus(DifyClientStatusEnum.SUCCESS.status());
        response.setCode("success");
        response.setMessage("调用成功");
        response.setData(data);
        response.setRawResponse(rawResponse);
        response.setStartTimestamp(startTimestamp);
        response.setEndTimestamp(endTimestamp);
        return response;
    }

    public static <T> AIResponse<T> fail(Integer status, String code, String message, String rawResponse, Long startTimestamp, Long endTimestamp){
        AIResponse<T> response = new AIResponse<>();
        response.setStatus(status);
        response.setCode(code);
        response.setMessage(message);
        response.setRawResponse(rawResponse);
        response.setStartTimestamp(startTimestamp);
        response.setEndTimestamp(endTimestamp);
        return response;
    }

    public static <T> AIResponse<T> fail(T data, Integer status, String code, String message, String rawResponse, Long startTimestamp, Long endTimestamp){
        AIResponse<T> response = new AIResponse<>();
        response.setData(data);
        response.setStatus(status);
        response.setCode(code);
        response.setMessage(message);
        response.setRawResponse(rawResponse);
        response.setStartTimestamp(startTimestamp);
        response.setEndTimestamp(endTimestamp);
        return response;
    }
}
