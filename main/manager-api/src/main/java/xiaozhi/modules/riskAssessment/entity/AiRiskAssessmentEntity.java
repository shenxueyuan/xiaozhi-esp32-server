package xiaozhi.modules.riskAssessment.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 智能体风险评估结果表
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("ai_risk_assessment")
public class AiRiskAssessmentEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
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
    
    // Manual getter and setter methods for VSCode compatibility
    public Long getId() {
        return this.id;
    }
    
    public String getFirstClassify() {
        return this.firstClassify;
    }
    
    public String getSecondClassify() {
        return this.secondClassify;
    }
    
    public String getRiskReason() {
        return this.riskReason;
    }
    
    public String getSuggestion() {
        return this.suggestion;
    }
    
    public LocalDateTime getStartTime() {
        return this.startTime;
    }
    
    public LocalDateTime getEndTime() {
        return this.endTime;
    }
    
    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
    
    public Long getUserId() {
        return this.userId;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}