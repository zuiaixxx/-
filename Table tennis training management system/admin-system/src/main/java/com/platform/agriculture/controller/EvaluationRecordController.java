package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.EvaluationRecordQueryDTO;
import com.platform.agriculture.domain.entity.EvaluationRecord;
import com.platform.agriculture.domain.entity.User;
import com.platform.agriculture.service.IEvaluationRecordService;
import com.platform.agriculture.service.IUserService;
import com.platform.agriculture.utils.AjaxResult;
import com.platform.agriculture.utils.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 训练课评价记录表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/evaluationRecord")
@RequiredArgsConstructor
public class EvaluationRecordController {
    private final IEvaluationRecordService evaluationRecordService;
    private final IUserService  userService;
    @GetMapping("/list")
    public AjaxResult<Page<EvaluationRecord>> list(EvaluationRecordQueryDTO evaluationRecordQueryDTO) {
        Page<EvaluationRecord> page = new Page<>(evaluationRecordQueryDTO.getPageNum(), evaluationRecordQueryDTO.getPageSize());
        this.evaluationRecordService.lambdaQuery()
                .eq(StringUtils.isNotBlank(evaluationRecordQueryDTO.getBookingNo()), EvaluationRecord::getBookingNo, evaluationRecordQueryDTO.getBookingNo())
                .page( page);
        return AjaxResult.success(page);
    }
    @GetMapping("/getById/{id}")
    public AjaxResult<EvaluationRecord> getById(@PathVariable Long id) {
        return AjaxResult.success(this.evaluationRecordService.getById(id));
    }
    @DeleteMapping("/delete/{id}")
    public AjaxResult<Void> delete(@PathVariable Long id) {
        this.evaluationRecordService.removeById(id);
        return AjaxResult.success();
    }
    @PostMapping("/save")
    public AjaxResult<Void> save(@RequestBody EvaluationRecord evaluationRecord) {
        if (evaluationRecord.getId() == null) {
            EvaluationRecord one = this.evaluationRecordService.lambdaQuery()
                    .eq(EvaluationRecord::getBookingNo, evaluationRecord.getBookingNo()).one();
            if (one != null) {
                return AjaxResult.error("已评价过！");
            }
        }
        this.evaluationRecordService.saveOrUpdate(evaluationRecord);
        return AjaxResult.success();
    }
    @GetMapping("/getCoachEvaluation")
    public AjaxResult<List<EvaluationRecord>> getCoachEvaluation(HttpServletRequest request) {
        Long coachId = CommonUtils.getUserId(request);
        User user = userService.getById(coachId);
        return AjaxResult.success(this.evaluationRecordService.lambdaQuery()
                .eq(EvaluationRecord::getCoachUsername, user.getUsername())
                .list());
    }
    @GetMapping("/getStudentEvaluation")
    public AjaxResult<List<EvaluationRecord>> getStudentEvaluation(HttpServletRequest request) {
        Long studentId = CommonUtils.getUserId(request);
        User user = userService.getById(studentId);
        return AjaxResult.success(this.evaluationRecordService.lambdaQuery()
                .eq(EvaluationRecord::getStudentUsername, user.getUsername())
                .list());
    }
}
