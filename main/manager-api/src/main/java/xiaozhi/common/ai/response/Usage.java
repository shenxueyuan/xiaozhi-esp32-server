package xiaozhi.common.ai.response;

import com.alibaba.fastjson2.annotation.JSONField;
import java.io.Serializable;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Usage implements Serializable {
    @JSONField(name = "prompt_tokens")
    private Integer promptTokens;
    @JSONField(name = "prompt_unit_price")
    private String promptUnitPrice;
    @JSONField(name = "prompt_price_unit")
    private String promptPriceUnit;
    @JSONField(name = "prompt_price")
    private String promptPrice;
    @JSONField(name = "completion_tokens")
    private Integer completionTokens;
    @JSONField(name = "completion_unit_price")
    private String completionUnitPrice;
    @JSONField(name = "completion_price_unit")
    private String completionPriceUnit;
    @JSONField(name = "completion_price")
    private String completionPrice;
    @JSONField(name = "total_tokens")
    private Integer totalTokens;
    @JSONField(name = "total_price")
    private String totalPrice;
    private String currency;
    private BigDecimal latency;
}
