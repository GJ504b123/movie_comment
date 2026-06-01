package com.movie.comment.controller;

import com.movie.comment.common.Result;
import com.movie.comment.dto.LoginRequest;
import com.movie.comment.dto.LoginResponse;
import com.movie.comment.dto.RegisterRequest;
import com.movie.comment.dto.RegisterVO;
import com.movie.comment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证", description = "用户注册与登录（公开接口）")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "1.1 用户注册", description = "注册后状态为 pending，需管理员审核通过才能登录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterRequest req) {
        RegisterVO vo = userService.register(req);
        return Result.created("注册成功，等待管理员审核", vo);
    }

    @Operation(summary = "1.2 用户登录", description = "登录成功返回 JWT token；pending/rejected 用户返回 403")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功，返回 token + 用户信息"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "403", description = "用户未通过审核")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = userService.login(req);
        return Result.ok("登录成功", resp);
    }
}
