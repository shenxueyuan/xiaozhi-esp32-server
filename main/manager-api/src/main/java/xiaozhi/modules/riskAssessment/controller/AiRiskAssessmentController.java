package xiaozhi.modules.riskAssessment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import xiaozhi.common.page.PageData;
import xiaozhi.common.utils.Result;
import xiaozhi.modules.riskAssessment.RiskAssessmentVO;
import xiaozhi.modules.riskAssessment.dto.*;
import xiaozhi.modules.riskAssessment.service.AiRiskAssessmentService;

import java.util.List;

/**
 * 智能体风险评估结果表 控制器
 *
 * @author xiaozhi
 * @since 2024-01-01
 */
@Slf4j
@RestController
@AllArgsConstructor
@Tag(name = "风险评估管理")
@RequestMapping("/risk/assessment")
public class AiRiskAssessmentController {

    private final AiRiskAssessmentService aiRiskAssessmentService;

    @GetMapping("/report/generate")
    @Operation(summary = "生成风险评估报告")
    @RequiresPermissions("sys:role:normal")
    public Result<RiskAssessmentVO> generateReport(@RequestParam("day") Integer day, @RequestParam("userId") String userId) {
        RiskAssessmentVO result = aiRiskAssessmentService.generateReport(day, userId);
        return new Result<RiskAssessmentVO>().ok(result);
    }

    @PostMapping("/list")
    @Operation(summary = "分页查询风险评估列表")
    public Result<PageData<AiRiskAssessmentReportDTO>> list(@RequestBody AiRiskAssessmentQueryDTO queryDTO) {
        PageData<AiRiskAssessmentReportDTO> result = aiRiskAssessmentService.getAssessmentPage(
                queryDTO.getPageNum(), 
                queryDTO.getPageSize(),
                queryDTO.getFirstClassify(),
                queryDTO.getSecondClassify(),
                queryDTO.getUserId(),
                queryDTO.getStartTime(),
                queryDTO.getEndTime()
        );
        return new Result<PageData<AiRiskAssessmentReportDTO>>().ok(result);
    }

    @PostMapping("/create")
    @Operation(summary = "创建风险评估记录")
    public Result<Long> create(@RequestBody @Valid AiRiskAssessmentCreateDTO createDTO) {
        Long id = aiRiskAssessmentService.createAssessment(createDTO);
        return new Result<Long>().ok(id);
    }

    @PostMapping("/update")
    @Operation(summary = "更新风险评估记录")
    public Result<Boolean> update(@RequestBody @Valid AiRiskAssessmentUpdateDTO updateDTO) {
        Boolean result = aiRiskAssessmentService.updateAssessment(updateDTO);
        return new Result<Boolean>().ok(result);
    }

    @GetMapping("/get/{id}")
    @Operation(summary = "根据ID获取风险评估记录")
    public Result<AiRiskAssessmentDTO> getById(@PathVariable Long id) {
        AiRiskAssessmentDTO result = aiRiskAssessmentService.getAssessmentById(id);
        return new Result<AiRiskAssessmentDTO>().ok(result);
    }

    @PostMapping("/listByIds")
    @Operation(summary = "根据ID列表获取风险评估记录")
    public Result<List<AiRiskAssessmentDTO>> listByIds(@RequestBody List<Long> ids) {
        List<AiRiskAssessmentDTO> result = aiRiskAssessmentService.listByIds(ids);
        return new Result<List<AiRiskAssessmentDTO>>().ok(result);
    }

    @GetMapping("/listByUserId/{userId}")
    @Operation(summary = "根据用户ID获取风险评估记录")
    public Result<List<AiRiskAssessmentDTO>> listByUserId(@PathVariable Long userId) {
        List<AiRiskAssessmentDTO> result = aiRiskAssessmentService.listByUserId(userId);
        return new Result<List<AiRiskAssessmentDTO>>().ok(result);
    }

    @GetMapping("/listByClassify")
    @Operation(summary = "根据风险分类获取评估记录")
    public Result<List<AiRiskAssessmentDTO>> listByClassify(
            @RequestParam(required = false) String firstClassify,
            @RequestParam(required = false) String secondClassify) {
        List<AiRiskAssessmentDTO> result = aiRiskAssessmentService.listByClassify(firstClassify, secondClassify);
        return new Result<List<AiRiskAssessmentDTO>>().ok(result);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(summary = "删除风险评估记录")
    public Result<Boolean> delete(@PathVariable Long id) {
        Boolean result = aiRiskAssessmentService.deleteAssessment(id);
        return new Result<Boolean>().ok(result);
    }

    @PostMapping("/batchDelete")
    @Operation(summary = "批量删除风险评估记录")
    public Result<Boolean> batchDelete(@RequestBody List<Long> ids) {
        Boolean result = aiRiskAssessmentService.batchDeleteAssessment(ids);
        return new Result<Boolean>().ok(result);
    }
}