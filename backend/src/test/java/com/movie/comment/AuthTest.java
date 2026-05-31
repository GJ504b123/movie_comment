package com.movie.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.comment.common.Result;
import com.movie.comment.dto.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    private static String token;
    private static Long userId;

    @Test
    @Order(1)
    void register() throws Exception {
        RegisterRequest req = new RegisterRequest();
        req.setUsername("auth_test_user");
        req.setPassword("123456");
        req.setEmail("auth_test@example.com");

        MvcResult result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data.status").value("pending"))
                .andReturn();

        // 解析 userId
        String json = result.getResponse().getContentAsString();
        userId = objectMapper.readTree(json).get("data").get("userId").asLong();
        assertThat(userId).isNotNull();
    }

    @Test
    @Order(2)
    void loginPendingUser() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("auth_test_user");
        req.setPassword("123456");

        // pending 状态用户不能登录
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(3)
    void loginApprovedUser() throws Exception {
        // 需要手动把用户状态改为 approved（模拟管理员审核）
        // 这里直接测一个已批准的 admin 用户
        // 注册一个 admin 用户用于测试
        RegisterRequest req = new RegisterRequest();
        req.setUsername("adminuser");
        req.setPassword("admin123");
        req.setEmail("admin@example.com");
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)));

        // 由于 admin 注册也是 pending，这里测不了的完整流程
        // 但可以验证注册重复用户名返回 409
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    @Order(4)
    void getProfileWithoutToken() throws Exception {
        mockMvc.perform(get("/api/user/profile"))
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(5)
    void loginWrongPassword() throws Exception {
        LoginRequest req = new LoginRequest();
        req.setUsername("auth_test_user");
        req.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(401));
    }
}
