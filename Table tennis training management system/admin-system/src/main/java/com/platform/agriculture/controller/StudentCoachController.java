package com.platform.agriculture.controller;

import com.platform.agriculture.domain.entity.CoachDetail;
import com.platform.agriculture.domain.entity.StudentCoach;
import com.platform.agriculture.domain.entity.User;
import com.platform.agriculture.service.ICoachDetailService;
import com.platform.agriculture.service.IStudentCoachService;
import com.platform.agriculture.service.IUserService;
import com.platform.agriculture.utils.AjaxResult;
import com.platform.agriculture.utils.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 学员选择教练关系表（使用 id 关联） 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/studentCoach")
@RequiredArgsConstructor
public class StudentCoachController {

    private final IStudentCoachService studentCoachService;
    private final ICoachDetailService coachDetailService;
    private final IUserService userService;
    @PostMapping("/save")
    public AjaxResult<Void> save(@RequestBody StudentCoach studentCoach) {

        if (studentCoach.getId() == null){
            StudentCoach one = studentCoachService.lambdaQuery()
                .eq(StudentCoach::getUserId, studentCoach.getUserId())
                .eq(StudentCoach::getCoachId, studentCoach.getCoachId())
                .one();
        if (one != null) {
            return AjaxResult.error("你已经选择过了");
        }
        }

        studentCoachService.saveOrUpdate(studentCoach);
        return AjaxResult.success();
    }
    @GetMapping("/getMyCoach") // 获取我的教练
    public AjaxResult<List<StudentCoach>> getMyCoach(HttpServletRequest  request) {
        Long userId = CommonUtils.getUserId(request);
        List<StudentCoach> studentCoachList = studentCoachService
                .lambdaQuery()
                .eq(StudentCoach::getUserId, userId)
                .list();
        return AjaxResult.success(studentCoachList);
    }
    @GetMapping("/getMyStudent") // 获取我的学员
    public AjaxResult<List<StudentCoach>> getMyStudent(HttpServletRequest  request) {
        Long coachId = CommonUtils.getUserId(request);
        User one = userService.lambdaQuery().eq(User::getId, coachId).one();
        // 根据教练username查询
        List<CoachDetail> list = coachDetailService.lambdaQuery().eq(CoachDetail::getUsername, one.getUsername()).list();
        Set<Long> collect = list.stream().map(CoachDetail::getId).collect(Collectors.toSet());
        List<StudentCoach> studentCoachList = studentCoachService
                .lambdaQuery()
                .in(StudentCoach::getCoachId, collect)
                .list();
        return AjaxResult.success(studentCoachList);
    }
}
