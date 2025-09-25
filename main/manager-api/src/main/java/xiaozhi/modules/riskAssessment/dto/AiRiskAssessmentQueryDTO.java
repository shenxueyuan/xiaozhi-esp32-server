package xiaozhi.modules.riskAssessment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 风险评估查询DTO
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Data
@Schema(description = "风险评估查询DTO")
public class AiRiskAssessmentQueryDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "页码", example = "1")
    private Integer pageNum = 1;
    
    @Schema(description = "页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "一级风险分类")
    private String firstClassify;

    @Schema(description = "二级风险分类")
    private String secondClassify;
    
    @Schema(description = "用户ID")
    private Long userId;
    
    @Schema(description = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    
    @Schema(description = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Schema(description = "分析开始时间-起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analysisStartTimeBegin;

    @Schema(description = "分析开始时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analysisStartTimeEnd;

    @Schema(description = "分析结束时间-起始")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analysisEndTimeBegin;

    @Schema(description = "分析结束时间-结束")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime analysisEndTimeEnd;

    // Getter methods for controller access
    public Integer getPageNum() {
        return pageNum;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public String getFirstClassify() {
        return firstClassify;
    }

    public String getSecondClassify() {
        return secondClassify;
    }

    public Long getUserId() {
        return userId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}