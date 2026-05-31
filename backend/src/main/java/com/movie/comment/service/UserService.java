package com.movie.comment.service;

import com.movie.comment.dto.*;

public interface UserService {
    /** 注册，返回 userId + status */
    RegisterVO register(RegisterRequest req);

    /** 登录，返回 token + 用户信息 */
    LoginResponse login(LoginRequest req);

    /** 获取当前用户信息 */
    UserVO getProfile(Long userId);

    /** 更新当前用户信息（邮箱/密码） */
    void updateProfile(Long userId, UpdateProfileRequest req);
}
