package xiaozhi.modules.riskAssessment.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;
import xiaozhi.modules.riskAssessment.entity.AiRiskAssessmentEntity;

/**
 * 智能体风险评估结果表 Mapper 接口
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Mapper
public interface AiRiskAssessmentDao extends BaseMapper<AiRiskAssessmentEntity> {

    /**
     * 分页查询风险评估记录
     *
     * @param page 分页参数
     * @param firstClassify 一级风险分类
     * @param secondClassify 二级风险分类
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 分页结果
     */
    IPage<AiRiskAssessmentEntity> selectAssessmentPage(
            Page<AiRiskAssessmentEntity> page,
            @Param("firstClassify") String firstClassify,
            @Param("secondClassify") String secondClassify,
            @Param("userId") Long userId,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );

    /**
     * 根据用户ID查询风险评估记录
     *
     * @param userId 用户ID
     * @return 评估记录列表
     */
    List<AiRiskAssessmentEntity> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据风险分类查询
     *
     * @param firstClassify 一级风险分类
     * @param secondClassify 二级风险分类
     * @return 评估记录列表
     */
    List<AiRiskAssessmentEntity> selectByClassify(
            @Param("firstClassify") String firstClassify,
            @Param("secondClassify") String secondClassify
    );

    /**
     * 根据时间范围查询风险评估记录
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 评估记录列表
     */
    List<AiRiskAssessmentEntity> selectByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}