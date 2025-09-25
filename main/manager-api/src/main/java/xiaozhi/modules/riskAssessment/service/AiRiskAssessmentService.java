package xiaozhi.modules.riskAssessment.service;

import java.time.LocalDateTime;
import java.util.List;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.BaseService;
import xiaozhi.modules.riskAssessment.RiskAssessmentVO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentCreateDTO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentDTO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentReportDTO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentUpdateDTO;
import xiaozhi.modules.riskAssessment.entity.AiRiskAssessmentEntity;

/**
 * 智能体风险评估结果表 服务类
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
public interface AiRiskAssessmentService extends BaseService<AiRiskAssessmentEntity> {

    /**
     * 创建风险评估记录
     *
     * @param createDTO 创建参数
     * @return 评估记录ID
     */
    Long createAssessment(AiRiskAssessmentCreateDTO createDTO);

    /**
     * 更新风险评估记录
     *
     * @param updateDTO 更新参数
     * @return 是否更新成功
     */
    Boolean updateAssessment(AiRiskAssessmentUpdateDTO updateDTO);

    /**
     * 分页查询风险评估记录
     *
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param firstClassify 一级风险分类
     * @param secondClassify 二级风险分类
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    PageData<AiRiskAssessmentReportDTO> getAssessmentPage(
            Integer pageNum, Integer pageSize,
            String firstClassify, String secondClassify,
            Long userId, LocalDateTime startTime, LocalDateTime endTime
    );

    /**
     * 根据ID列表查询风险评估记录列表
     *
     * @param ids ID列表
     * @return 评估记录列表
     */
    List<AiRiskAssessmentDTO> listByIds(List<Long> ids);

    /**
     * 根据用户ID查询风险评估记录列表
     *
     * @param userId 用户ID
     * @return 评估记录列表
     */
    List<AiRiskAssessmentDTO> listByUserId(Long userId);

    /**
     * 根据风险分类查询评估记录
     *
     * @param firstClassify 一级风险分类
     * @param secondClassify 二级风险分类
     * @return 评估记录列表
     */
    List<AiRiskAssessmentDTO> listByClassify(String firstClassify, String secondClassify);

    /**
     * 根据时间范围查询风险评估记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评估记录列表
     */
    List<AiRiskAssessmentDTO> listByTimeRange(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 删除风险评估记录
     *
     * @param id 记录ID
     * @return 是否删除成功
     */
    Boolean deleteAssessment(Long id);

    /**
     * 批量删除风险评估记录
     *
     * @param ids ID列表
     * @return 是否删除成功
     */
    Boolean batchDeleteAssessment(List<Long> ids);

    /**
     * 根据ID获取风险评估记录
     *
     * @param id 记录ID
     * @return 评估记录
     */
    AiRiskAssessmentDTO getAssessmentById(Long id);
    
    /**
     * 生成最近多少天内的分析报告
     * @param day 最近多少天
     * @return 分析报告
     */
    RiskAssessmentVO generateReport(Integer day);
}