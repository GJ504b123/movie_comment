package com.movie.comment.controller;

import com.movie.comment.common.BusinessException;
import com.movie.comment.common.Result;
import com.movie.comment.dto.UpdateProfileRequest;
import com.movie.comment.dto.UserVO;
import com.movie.comment.security.UserContext;
import com.movie.comment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "用户", description = "当前用户个人信息管理（需登录）")
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "1.3 获取当前用户信息", description = "需携带有效 JWT token")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "返回用户信息"),
            @ApiResponse(responseCode = "401", description = "未提供认证凭证")
    })
    @GetMapping("/profile")
    public Result<UserVO> getProfile() {
        Long userId = requireLogin();
        return Result.ok(userService.getProfile(userId));
    }

    @Operation(summary = "1.4 更新个人信息", description = "可修改邮箱和密码；修改密码需提供旧密码验证")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "旧密码错误"),
            @ApiResponse(responseCode = "401", description = "未提供认证凭证")
    })
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
