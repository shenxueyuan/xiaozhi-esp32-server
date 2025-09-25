package xiaozhi.common.ai.enums;

/**
 * dify status
 */
public enum DifyClientStatusEnum {
    /**
     * dify 调用成功时没有状态码, dify client封装响应为  java习惯的方式
     * 这里定义了一个成功的status 为200
     */
    SUCCESS(200),
    NOT_FOUND(404),
    BUSYNESS_ERROR(400),
    SERVER_ERROR(500),
    /**
     * 调用失败
     */
    INVOKE_ERROR(800),
    /**
     * 解析失败
     */
    PARSE_ERROR(900)
    ;
    private final Integer status;
    DifyClientStatusEnum(Integer status) {
        this.status = status;
    }

    public Integer status() {
        return status;
    }

    public boolean match(Integer status) {
        return this.status.equals(status);
    }
}
