package com.movie.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.comment.common.BusinessException;
import com.movie.comment.common.UserStatus;
import com.movie.comment.dto.*;
import com.movie.comment.entity.User;
import com.movie.comment.mapper.UserMapper;
import com.movie.comment.security.JwtUtils;
import com.movie.comment.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    @Transactional
    public RegisterVO register(RegisterRequest req) {
        // 检查用户名唯一
        User exist = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (exist != null) {
            throw new BusinessException(409, "用户名已存在");
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPassword(passwordEncoder.encode(req.getPassword()));
        u.setEmail(req.getEmail());
        u.setRole("user");
        u.setStatus(UserStatus.PENDING.getValue());
        userMapper.insert(u);

        return new RegisterVO(u.getId(), u.getStatus());
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest req) {
        User u = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, req.getUsername()));
        if (u == null) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        if (!passwordEncoder.matches(req.getPassword(), u.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }

        // 检查审核状态
        if (UserStatus.PENDING.getValue().equals(u.getStatus())) {
            throw new BusinessException(403, "用户尚未通过审核，无法登录");
        }
        if (UserStatus.REJECTED.getValue().equals(u.getStatus())) {
            throw new BusinessException(403, "审核未通过，无法登录");
        }

        // 生成 JWT
        String token = jwtUtils.generate(u.getId(), u.getUsername(), u.getRole());

        // 更新最后登录时间
        u.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(u);

        return new LoginResponse(token, UserVO.from(u));
    }

    @Override
    public UserVO getProfile(Long userId) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return UserVO.from(u);
    }

    @Override
    @Transactional
    public void updateProfile(Long userId, UpdateProfileRequest req) {
        User u = userMapper.selectById(userId);
        if (u == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 更新邮箱
        if (req.getEmail() != null && !req.getEmail().isBlank()) {
            u.setEmail(req.getEmail());
        }

        // 更新密码（需要旧密码验证）
        if (req.getNewPassword() != null && !req.getNewPassword().isBlank()) {
            if (req.getOldPassword() == null || !passwordEncoder.matches(req.getOldPassword(), u.getPassword())) {
                throw new BusinessException(400, "旧密码错误");
            }
            u.setPassword(passwordEncoder.encode(req.getNewPassword()));
        }

        userMapper.updateById(u);
    }
}
