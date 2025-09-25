package xiaozhi.common.ai.enums;

/**
 * AI应用类型
 */
public enum ModelAppType {
    /**
     * 文本生成
     */
    COMPLETION("completion"),
    /**
     * 工作流
     */
    WORKFLOW("workflow"),
    /**
     * 聊天助手
     */
    CHAT("chat"),
    /**
     * Agent
     */
    AGENT("agent"),
    ;
    private final String code;

    ModelAppType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public boolean match(String code) {
        return this.code.equalsIgnoreCase(code);
    }

    public static ModelAppType fromCode(String code) {
        for (ModelAppType type : ModelAppType.values()) {
            if (type.code.equalsIgnoreCase(code)) {
                return type;
            }
        }
        return null;
    }
}
