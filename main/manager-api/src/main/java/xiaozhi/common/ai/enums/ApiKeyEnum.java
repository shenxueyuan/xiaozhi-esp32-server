package xiaozhi.common.ai.enums;

import lombok.Getter;

@Getter
public enum ApiKeyEnum {
    RISK_ASSESMENT_REPORT("app-BeDRNfMMTSUqgoKeTHZk4Wiy","心理健康分析"),
    ;
    private final String apiKey;
    private final String description;

    ApiKeyEnum(String apiKey, String description) {
        this.apiKey = apiKey;
        this.description = description;
    }

    public String apiKey() {
        return apiKey;
    }

    public String description() {
        return description;
    }
}
