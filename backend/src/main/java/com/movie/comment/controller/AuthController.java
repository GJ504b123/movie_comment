package com.movie.comment.controller;

import com.movie.comment.common.Result;
import com.movie.comment.dto.LoginRequest;
import com.movie.comment.dto.LoginResponse;
import com.movie.comment.dto.RegisterRequest;
import com.movie.comment.dto.RegisterVO;
import com.movie.comment.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /** 1.1 用户注册 */
    @PostMapping("/register")
    public Result<RegisterVO> register(@Valid @RequestBody RegisterRequest req) {
        RegisterVO vo = userService.register(req);
        return Result.created("注册成功，等待管理员审核", vo);
    }

    /** 1.2 用户登录 */
    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest req) {
        LoginResponse resp = userService.login(req);
        return Result.ok("登录成功", resp);
    }
}
