package com.platform.agriculture.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.platform.agriculture.domain.dto.UserQueryDTO;
import com.platform.agriculture.domain.entity.User;
import com.platform.agriculture.service.IUserService;
import com.platform.agriculture.utils.AjaxResult;
import com.platform.agriculture.utils.JwtUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author baomidou
 * @since 2025-04-19
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserService userService;
    private final JwtUtils jwtUtils;
    @PostMapping("/login")
    public AjaxResult<String> login(@RequestBody User user) {
        User one = this.userService.lambdaQuery().eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword())
                .eq(User::getRole, user.getRole())
                .one();
        if (one == null) {
            return AjaxResult.error("用户名或密码错误");
        }
        if (one.getStatus() == 1){
            return AjaxResult.error("账号已被禁用");
        }
        String token = jwtUtils.createToken(one.getId());
        return AjaxResult.success(token);
    }
    @PostMapping("/register")
    public AjaxResult<User> register(@RequestBody User user) {
        if (user.getId() == null){
            if (this.userService.lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .eq(User::getRole, user.getRole())
                .one() != null) {
            return AjaxResult.error("用户名已存在");
        }
        }
        return this.userService.saveOrUpdate(user) ? AjaxResult.success() : AjaxResult.error();
    }

    @DeleteMapping("/delete/{id}")
    public AjaxResult<User> delete(@PathVariable Long id) {
        return this.userService.removeById(id) ? AjaxResult.success() : AjaxResult.error();
    }
    @GetMapping("/list")
    public AjaxResult<Page<User>> get(UserQueryDTO userQueryDTO) {
        Page<User> page = new Page<>(userQueryDTO.getPageNum(), userQueryDTO.getPageSize());
        this.userService.lambdaQuery()
                .like(StringUtils.isNotBlank(userQueryDTO.getName()), User::getName, userQueryDTO.getName()).or()
                .eq(Objects.nonNull(userQueryDTO.getStatus()), User::getStatus, userQueryDTO.getStatus()).page(page);
        return AjaxResult.success(page);
    }

    @GetMapping("/getById/{id}")
    public AjaxResult<User> getById(@PathVariable Long id) {
        return AjaxResult.success(this.userService.getById(id));
    }


    @GetMapping("/getByToken")
    public AjaxResult<User> getByToken(HttpServletRequest request) {
        Long uid = (Long) request.getAttribute("uid");
        if (Objects.isNull(uid)) {
            return AjaxResult.error("token无效");
        }
        User user = this.userService.getById(uid);
        user.setPassword(null);
        return AjaxResult.success(user);
    }

    @PutMapping("/updatePassword")
    public AjaxResult<User> updatePassword(@RequestBody User user, HttpServletRequest request) {
        String oldPassword = user.getOldPassword();
        if (StringUtils.isBlank(oldPassword)) {
            return AjaxResult.error("旧密码不能为空");
        }
        User one = this.userService.lambdaQuery().eq(User::getId, user.getId()).eq(User::getPassword, oldPassword).one();
        if (Objects.isNull(one)) {
            return AjaxResult.error("旧密码错误");
        }
        userService.lambdaUpdate().eq(User::getId, user.getId()).set(User::getPassword, user.getPassword()).update();
        return AjaxResult.success();
    }

    @GetMapping("/updateStatus")
    public AjaxResult<Void> updateStatus(Integer id, Integer status){
        this.userService.lambdaUpdate().eq(User::getId, id).set(User::getStatus, status).update();
        return AjaxResult.success();
    }

    // 角色名称:superAdmin, campusAdmin, user, coach.
    @GetMapping("/getAllCampusAdmin")
    public AjaxResult<List<User>> getAllCampusAdmin() {
        List<User> campusAdmin = this.userService.lambdaQuery().eq(User::getRole, "campusAdmin").list();
        return AjaxResult.success(campusAdmin);
    }
    @GetMapping("/getAllCoach")
    public AjaxResult<List<User>> getAllCoach() {
        List<User> coach = this.userService.lambdaQuery().eq(User::getRole, "coach").list();
        return AjaxResult.success(coach);
    }
    @GetMapping("/getAllUser")
    public AjaxResult<List<User>> getAllUser() {
        List<User> user = this.userService.lambdaQuery().eq(User::getRole, "user").list();
        return AjaxResult.success(user);
    }
}
