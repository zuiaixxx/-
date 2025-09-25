package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.CourseQueryDTO;
import com.platform.agriculture.domain.entity.Course;
import com.platform.agriculture.domain.entity.User;
import com.platform.agriculture.service.ICourseService;
import com.platform.agriculture.service.IUserService;
import com.platform.agriculture.utils.AjaxResult;
import com.platform.agriculture.utils.CommonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 课程排班表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-09-25
 */
@RestController
@RequestMapping("/course")
@RequiredArgsConstructor
public class CourseController {
    private final ICourseService courseService;
    private final IUserService userService;

    @GetMapping("/list")
    public AjaxResult<Page<Course>> list(CourseQueryDTO courseQueryDTO){
        Page<Course> page = new Page<>(courseQueryDTO.getPageNum(), courseQueryDTO.getPageSize());
        courseService.lambdaQuery()
                .like(StringUtils.isNotBlank(courseQueryDTO.getCoachUsername()), Course::getCoachUsername, courseQueryDTO.getCoachUsername())
                .eq(Objects.nonNull(courseQueryDTO.getCampusId()), Course::getCampusId, courseQueryDTO.getCampusId())
                .page( page);
        return AjaxResult.success(page);
    }
    @GetMapping("/getById/{id}")
    public AjaxResult<Course> getById(@PathVariable Long id){
        return AjaxResult.success(courseService.getById(id));
    }
    @DeleteMapping("/delete/{id}")
    public AjaxResult<Void> delete(@PathVariable Long id){
        courseService.removeById(id);
        return AjaxResult.success();
    }
    @PostMapping("/save")
    public AjaxResult<Course> save(@RequestBody Course course){
        courseService.saveOrUpdate(course);
        return AjaxResult.success(course);
    }
    @GetMapping("/getMyCourse")
    public AjaxResult<List<Course>> getMyCourse(HttpServletRequest request){
        Long coachId = CommonUtils.getUserId(request);
        User user = userService.getById(coachId);
        List<Course> courseList = courseService.lambdaQuery()
                .eq(Course::getCoachUsername, user.getUsername())
                .list();
        return AjaxResult.success(courseList);
    }

}
