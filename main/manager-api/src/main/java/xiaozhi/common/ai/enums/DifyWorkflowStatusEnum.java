package xiaozhi.common.ai.enums;

public enum DifyWorkflowStatusEnum {
    RUNNING("running"),
    SUCCEEDED("succeeded"),
    FAILED("failed"),
    STOPPED("stopped"),
    ;
    private final String code;
    DifyWorkflowStatusEnum(String code) {
        this.code = code;
    }
    public String code() {
        return code;
    }

    public boolean match(String code) {
        return this.code.equalsIgnoreCase(code);
    }
}
