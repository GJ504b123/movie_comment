package com.movie.comment.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

/**
 * JWT 认证过滤器——解析 token → 设置 UserContext。
 * 公开接口（登录/注册/Swagger）不强制要求 token。
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    /** 无需认证的路径前缀 */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/auth/",
            "/api-docs",
            "/swagger-ui",
            "/swagger-ui.html"
    );

    private final JwtUtils jwtUtils;

    public JwtAuthFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        String path = request.getRequestURI();

        // 公开路径放行
        if (isPublicPath(path)) {
            chain.doFilter(request, response);
            return;
        }

        // 提取 Authorization 头
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 部分接口允许游客（影片列表/详情/排行榜）——此时不设上下文即可
            chain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Claims claims = jwtUtils.parse(token);
        if (claims == null) {
            chain.doFilter(request, response);
            return;
        }

        // 设置用户上下文
        UserContext.set(
                jwtUtils.getUserId(claims),
                jwtUtils.getUsername(claims),
                jwtUtils.getRole(claims)
        );

        try {
            chain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private boolean isPublicPath(String path) {
        return PUBLIC_PATHS.stream().anyMatch(path::startsWith);
    }
}
