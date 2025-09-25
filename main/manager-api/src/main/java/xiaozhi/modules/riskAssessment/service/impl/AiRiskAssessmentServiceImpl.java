package xiaozhi.modules.riskAssessment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson2.JSONObject;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;

import lombok.AllArgsConstructor;
import xiaozhi.common.ai.AIClient;
import xiaozhi.common.ai.enums.ApiKeyEnum;
import xiaozhi.common.ai.request.CompletionMessageRequest;
import xiaozhi.common.ai.response.AIResponse;
import xiaozhi.common.ai.response.CompletionMessageResponse;
import xiaozhi.common.constant.Constant;
import xiaozhi.common.page.PageData;
import xiaozhi.common.service.impl.BaseServiceImpl;
import xiaozhi.common.user.UserDetail;
import xiaozhi.modules.agent.dao.AgentDao;
import xiaozhi.modules.agent.dao.AiAgentChatHistoryDao;
import xiaozhi.modules.agent.entity.AgentChatHistoryEntity;
import xiaozhi.modules.agent.entity.AgentEntity;
import xiaozhi.modules.riskAssessment.RiskAssessmentVO;
import xiaozhi.modules.riskAssessment.dao.AiRiskAssessmentDao;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentCreateDTO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentDTO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentReportDTO;
import xiaozhi.modules.riskAssessment.dto.AiRiskAssessmentUpdateDTO;
import xiaozhi.modules.riskAssessment.entity.AiRiskAssessmentEntity;
import xiaozhi.modules.riskAssessment.service.AiRiskAssessmentService;
import xiaozhi.modules.security.user.SecurityUser;

/**
 * 智能体风险评估结果表 服务实现类
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Service
@AllArgsConstructor
public class AiRiskAssessmentServiceImpl extends
    BaseServiceImpl<AiRiskAssessmentDao, AiRiskAssessmentEntity> implements
    AiRiskAssessmentService {


    private final AIClient aiClient;

    private final AgentDao agentDao;
    private final AiRiskAssessmentDao riskAssessmentDao;
    private final AiAgentChatHistoryDao agentChatHistoryDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createAssessment(AiRiskAssessmentCreateDTO createDTO) {
        AiRiskAssessmentEntity entity = new AiRiskAssessmentEntity();
        BeanUtils.copyProperties(createDTO, entity);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        super.insert(entity);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateAssessment(AiRiskAssessmentUpdateDTO updateDTO) {
        AiRiskAssessmentEntity entity = new AiRiskAssessmentEntity();
        BeanUtils.copyProperties(updateDTO, entity);
        entity.setUpdatedAt(LocalDateTime.now());
        return super.updateById(entity);
    }

    @Override
    public PageData<AiRiskAssessmentReportDTO> getAssessmentPage(
            Integer pageNum, Integer pageSize,
            String firstClassify, String secondClassify,
            Long userId, LocalDateTime startTime, LocalDateTime endTime) {

        Map<String, Object> params = new HashMap<>();
        params.put(Constant.PAGE, String.valueOf(pageNum));
        params.put(Constant.LIMIT, String.valueOf(pageSize));

        QueryWrapper<AiRiskAssessmentEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(firstClassify)) {
            wrapper.eq("first_classify", firstClassify);
        }
        if (StringUtils.hasText(secondClassify)) {
            wrapper.eq("second_classify", secondClassify);
        }
        if (userId != null) {
            wrapper.eq("user_id", userId);
        }
        if (startTime != null) {
            wrapper.ge("created_at", startTime);
        }
        if (endTime != null) {
            wrapper.le("created_at", endTime);
        }

        IPage<AiRiskAssessmentEntity> page = riskAssessmentDao.selectPage(
            getPage(params, "created_at", false),
            wrapper);

        if (page == null || page.getRecords() == null) {
            return new PageData<>(new ArrayList<>(), 0);
        }

        return new PageData<>(page.getRecords().stream()
            .map(this::convertToReportDTO)
            .collect(Collectors.toList()), page.getTotal());
    }

    @Override
    public List<AiRiskAssessmentDTO> listByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }
        QueryWrapper<AiRiskAssessmentEntity> wrapper = new QueryWrapper<>();
        wrapper.in("id", ids);
        List<AiRiskAssessmentEntity> entities = riskAssessmentDao.selectList(wrapper);
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<AiRiskAssessmentDTO> listByUserId(Long userId) {
        QueryWrapper<AiRiskAssessmentEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<AiRiskAssessmentEntity> entities = riskAssessmentDao.selectList(wrapper);
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<AiRiskAssessmentDTO> listByClassify(String firstClassify, String secondClassify) {
        QueryWrapper<AiRiskAssessmentEntity> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(firstClassify)) {
            wrapper.eq("first_classify", firstClassify);
        }
        if (StringUtils.hasText(secondClassify)) {
            wrapper.eq("second_classify", secondClassify);
        }
        List<AiRiskAssessmentEntity> entities = riskAssessmentDao.selectList(wrapper);
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public List<AiRiskAssessmentDTO> listByTimeRange(LocalDateTime startTime, LocalDateTime endTime) {
        QueryWrapper<AiRiskAssessmentEntity> wrapper = new QueryWrapper<>();
        if (startTime != null) {
            wrapper.ge("start_time", startTime);
        }
        if (endTime != null) {
            wrapper.le("end_time", endTime);
        }
        List<AiRiskAssessmentEntity> entities = riskAssessmentDao.selectList(wrapper);
        if (entities == null) {
            return new ArrayList<>();
        }
        return entities.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }

    @Override
    public AiRiskAssessmentDTO getAssessmentById(Long id) {
        AiRiskAssessmentEntity entity = super.selectById(id);
        if (entity == null) {
            return null;
        }
        return convertToDTO(entity);
    }

    @Override
    public RiskAssessmentVO generateReport(Integer day, String userId) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(day);
        LocalDateTime endTime = LocalDateTime.now();
        List<AgentEntity> agentList = agentDao.selectList(new QueryWrapper<AgentEntity>().eq("user_id", userId));
        if (agentList == null || agentList.isEmpty()) {
            return null;
        }
        List<String> agentIds = agentList.stream().map(AgentEntity::getId).collect(Collectors.toList());
        List<AgentChatHistoryEntity> chatRecordList = agentChatHistoryDao.selectList(new QueryWrapper<AgentChatHistoryEntity>()
            .in("agent_id", agentIds)
            .ge("created_at", startTime)
            .le("created_at", endTime));
        CompletionMessageRequest request = CompletionMessageRequest.builder()
            .inputs(Collections.singletonMap("chatHistory", Optional.ofNullable(chatRecordList)
                    .filter(CollectionUtil::isNotEmpty)
                    .map(chatRecords -> chatRecords.stream()
                    .map(AgentChatHistoryEntity::getContent)
                    .collect(Collectors.joining("\n")))
                    .orElse("无")))
            .responseMode("blocking")
            .user(userId)
            .build();
        CompletionMessageResponse response = aiClient.completionMessageBlocking(request, ApiKeyEnum.RISK_ASSESMENT_REPORT.apiKey());
        AiRiskAssessmentDTO dto = JSONObject.parseObject(response.getAnswer(), AiRiskAssessmentDTO.class);
        dto.setUserId(Long.valueOf(userId));
        dto.setStartTime(startTime);
        dto.setEndTime(endTime);
        AiRiskAssessmentEntity entity = convertToEntity(dto);
        riskAssessmentDao.insert(entity);
        dto.setId(entity.getId());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        return convertToVO(dto);
    }

    private AiRiskAssessmentEntity convertToEntity(AiRiskAssessmentDTO dto) {
        return Optional.ofNullable(dto)
            .map(d -> {
                AiRiskAssessmentEntity entity = new AiRiskAssessmentEntity();
                BeanUtils.copyProperties(d, entity);
                return entity;
            })
            .orElse(null);
    }

    private RiskAssessmentVO convertToVO(AiRiskAssessmentDTO dto) {
        return Optional.ofNullable(dto)
            .map(d -> {
                RiskAssessmentVO vo = new RiskAssessmentVO();
                BeanUtils.copyProperties(d, vo);
                vo.setRiskReason(Optional.ofNullable(d.getRiskReason())
                    .map(r -> Arrays.asList(r.split("\\|\\|\\|")))
                    .orElse(null));
                vo.setSuggestion(Optional.ofNullable(d.getSuggestion())
                    .map(s -> Arrays.asList(s.split("\\|\\|\\|")))
                    .orElse(null));
                return vo;
            })
            .orElse(null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean deleteAssessment(Long id) {
        return super.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean batchDeleteAssessment(List<Long> ids) {
        if (ids != null && !ids.isEmpty()) {
            return super.deleteBatchIds(ids);
        }
        return false;
    }

    /**
     * 转换为DTO
     */
    private AiRiskAssessmentDTO convertToDTO(AiRiskAssessmentEntity entity) {
        AiRiskAssessmentDTO dto = new AiRiskAssessmentDTO();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }

    /**
     * 转换为报表DTO
     */
    private AiRiskAssessmentReportDTO convertToReportDTO(AiRiskAssessmentEntity entity) {
        AiRiskAssessmentReportDTO dto = new AiRiskAssessmentReportDTO();
        BeanUtils.copyProperties(entity, dto);
        
        // 计算分析持续时间
        if (entity.getStartTime() != null && entity.getEndTime() != null) {
            long duration = ChronoUnit.MINUTES.between(entity.getStartTime(), entity.getEndTime());
            dto.setAnalysisDuration(duration);
        }
        
        // 计算风险等级
        dto.setRiskLevel(calculateRiskLevel(entity));
        
        return dto;
    }

    /**
     * 计算风险等级
     */
    private String calculateRiskLevel(AiRiskAssessmentEntity entity) {
        // 简单的风险等级计算逻辑
        if (StringUtils.hasText(entity.getFirstClassify())) {
            if (entity.getFirstClassify().contains("高") || entity.getFirstClassify().contains("严重")) {
                return "高风险";
            } else if (entity.getFirstClassify().contains("中")) {
                return "中风险";
            }
        }
        return "低风险";
    }
}