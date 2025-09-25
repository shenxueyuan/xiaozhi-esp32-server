package xiaozhi.common.ai.enums;

public enum DifyResponseModel {
    STREAMING("streaming","流式"),
    BLOCKING("blocking","阻塞")
    ;
    private final String code;
    private final String description;

    DifyResponseModel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() {
        return code;
    }

    public String description() {
        return description;
    }
}
