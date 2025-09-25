package xiaozhi.modules.riskAssessment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 风险评估报告DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
public class AiRiskAssessmentReportDTO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 一级风险分类
     */
    @Schema(description = "一级风险分类")
    private String firstClassify;

    /**
     * 二级风险分类
     */
    @Schema(description = "二级风险分类")
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
     * 创建时间
     */
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private Long userId;

    /**
     * 分析耗时（毫秒）
     */
    @Schema(description = "分析耗时（毫秒）")
    private Long analysisDuration;

    /**
     * 风险等级（可根据分类计算得出）
     */
    @Schema(description = "风险等级")
    private String riskLevel;
    
    // Manual setter methods for VSCode compatibility
    public void setAnalysisDuration(Long analysisDuration) {
        this.analysisDuration = analysisDuration;
    }
    
    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }
}