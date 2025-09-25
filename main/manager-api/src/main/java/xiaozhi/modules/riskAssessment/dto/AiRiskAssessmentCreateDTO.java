package xiaozhi.modules.riskAssessment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

/**
 * 风险评估创建DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class AiRiskAssessmentCreateDTO {

    /**
     * 一级风险分类
     */
    @Schema(description = "一级风险分类")
    @NotBlank(message = "一级风险分类不能为空")
    @Size(max = 128, message = "一级风险分类长度不能超过128个字符")
    private String firstClassify;

    /**
     * 二级风险分类
     */
    @Schema(description = "二级风险分类")
    @Size(max = 128, message = "二级风险分类长度不能超过128个字符")
    private String secondClassify;

    /**
     * 风险原因描述
     */
    @Schema(description = "风险原因描述")
    private String riskReason;

    /**
     * 建议措施
     */
    @Schema(description = "建议措施")
    private String suggestion;

    /**
     * 分析开始时间
     */
    @Schema(description = "分析开始时间")
    private LocalDateTime startTime;

    /**
     * 分析结束时间
     */
    @Schema(description = "分析结束时间")
    private LocalDateTime endTime;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;
}