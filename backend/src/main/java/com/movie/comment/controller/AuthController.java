package com.movie.comment.controller;

import com.movie.comment.common.ActionType;
import com.movie.comment.common.Result;
import com.movie.comment.dto.LoginRequest;
import com.movie.comment.dto.LoginResponse;
import com.movie.comment.dto.RegisterRequest;
import com.movie.comment.dto.RegisterVO;
import com.movie.comment.entity.AccessLog;
import com.movie.comment.mapper.AccessLogMapper;
import com.movie.comment.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "认证", description = "用户注册与登录（公开接口）")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final AccessLogMapper accessLogMapper;

    public AuthController(UserService userService, AccessLogMapper accessLogMapper) {
        this.userService = userService;
        this.accessLogMapper = accessLogMapper;
    }

    @Operation(summary = "1.1 用户注册", description = "注册后状态为 pending，需管理员审核通过才能登录")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "注册成功"),
            @ApiResponse(responseCode = "409", description = "用户名已存在")
    })
    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterRequest req, HttpServletRequest request) {
        RegisterVO vo = userService.register(req);
        // 注册成功 → 记录日志
        AccessLog log = new AccessLog();
        log.setUserId(vo.getUserId());
        log.setUsername(req.getUsername());
        log.setAction(ActionType.REGISTER.getValue());
        log.setIp(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        accessLogMapper.insert(log);
        return Result.created("注册成功，等待管理员审核", vo);
    }

    @Operation(summary = "1.2 用户登录", description = "登录成功返回 JWT token；pending/rejected 用户返回 403")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功，返回 token + 用户信息"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @ApiResponse(responseCode = "403", description = "用户未通过审核")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req, HttpServletRequest request) {
        LoginResponse resp = userService.login(req);
        // 登录成功 → 记录日志
        AccessLog log = new AccessLog();
        log.setUserId(resp.getUser().getId());
        log.setUsername(resp.getUser().getUsername());
        log.setAction(ActionType.LOGIN.getValue());
        log.setIp(request.getRemoteAddr());
        log.setUserAgent(request.getHeader("User-Agent"));
        accessLogMapper.insert(log);
        return Result.ok("登录成功", resp);
    }
}
