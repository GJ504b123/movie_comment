package com.movie.comment.controller;

import com.movie.comment.common.BusinessException;
import com.movie.comment.common.Result;
import com.movie.comment.dto.UpdateProfileRequest;
import com.movie.comment.dto.UserVO;
import com.movie.comment.security.UserContext;
import com.movie.comment.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /** 1.3 获取当前用户信息 */
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Long userId = requireLogin();
        return Result.ok(userService.getProfile(userId));
    }

    /** 1.4 更新用户信息（自己） */
    @PutMapping("/profile")
    public Result<Void> updateProfile(@RequestBody UpdateProfileRequest req) {
        Long userId = requireLogin();
        userService.updateProfile(userId, req);
        return Result.ok("更新成功", null);
    }

    /** 要求登录，未登录抛 401 */
    private Long requireLogin() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未提供认证凭证");
        }
        return userId;
    }
}
