package xiaozhi.common.ai.enums;

public enum DifyClientErrorEnum {
    NOT_FOUND(DifyClientStatusEnum.NOT_FOUND.status(), "not_found","对话不存在"),
    INVALID_PARAM(DifyClientStatusEnum.BUSYNESS_ERROR.status(), "invalid_param","传入参数异常"),
    APP_UNAVAILABLE(DifyClientStatusEnum.BUSYNESS_ERROR.status(),"app_unavailable","App 配置不可用"),
    PROVIDER_NOT_INITIALIZE(DifyClientStatusEnum.BUSYNESS_ERROR.status(),"provider_not_initialize","无可用模型凭据配置"),
    PROVIDER_QUOTA_EXCEEDED(DifyClientStatusEnum.BUSYNESS_ERROR.status(),"provider_quota_exceeded","模型调用额度不足"),
    MODEL_CURRENTLY_NOT_SUPPORT(DifyClientStatusEnum.BUSYNESS_ERROR.status(),"model_currently_not_support","当前模型不可用"),
    COMPLETION_REQUEST_ERROR(DifyClientStatusEnum.BUSYNESS_ERROR.status(),"completion_request_error","文本生成失败"),
    SERVER_ERROR(DifyClientStatusEnum.SERVER_ERROR.status(),"","服务器内部异常"),

    //
    HTTP_FAILED(DifyClientStatusEnum.INVOKE_ERROR.status(),"http_failed","http返回失败"),
    INVOKE_FAILED(DifyClientStatusEnum.INVOKE_ERROR.status(),"invoke_failed","调用失败" ),
    PARSE_FAILED(DifyClientStatusEnum.PARSE_ERROR.status(),"parse_failed","解析失败" ),
    FORKING_FAILED(DifyClientStatusEnum.INVOKE_ERROR.status(),"forking_failed","分叉调用后返回异常"),
    FORKING_PARAM_ERROR(DifyClientStatusEnum.INVOKE_ERROR.status(),"forking_param_error","分叉调用参数异常"),
    INTERRUPTED(DifyClientStatusEnum.INVOKE_ERROR.status(),"interrupted","interrupted"),
    POLL_TIMEOUT(DifyClientStatusEnum.INVOKE_ERROR.status(), "poll_timeout","拉取分叉调用后的结果超时"),
    WORKFLOW_STATUS_ERROR(DifyClientStatusEnum.INVOKE_ERROR.status(), "workflow_status_error","工作"),


    ;
    private final Integer status;
    private final String code;
    private final String description;

    DifyClientErrorEnum(Integer status, String code, String description) {
        this.status = status;
        this.code = code;
        this.description = description;
    }

    public Integer status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }
}
