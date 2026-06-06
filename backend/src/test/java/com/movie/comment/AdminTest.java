package com.movie.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.comment.common.UserStatus;
import com.movie.comment.dto.AddMovieRequest;
import com.movie.comment.dto.AuditRequest;
import com.movie.comment.dto.HideReviewRequest;
import com.movie.comment.dto.UpdateMovieRequest;
import com.movie.comment.entity.Movie;
import com.movie.comment.entity.Review;
import com.movie.comment.entity.User;
import com.movie.comment.mapper.MovieMapper;
import com.movie.comment.mapper.ReviewMapper;
import com.movie.comment.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AdminTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private MovieMapper movieMapper;
    @Autowired private ReviewMapper reviewMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    private static String adminToken;
    private static Long adminUserId;
    private static String userToken;
    private static Long regularUserId;
    private static Long pendingUserId;
    private static Long movieId;
    private static Long reviewId;

    // ========================================
    //  Setup
    // ========================================

    @Test
    @Order(0)
    void setup() throws Exception {
        // --- 创建管理员 ---
        User admin = new User();
        admin.setUsername("admin");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setEmail("admin@test.com");
        admin.setRole("admin");
        admin.setStatus(UserStatus.APPROVED.getValue());
        userMapper.insert(admin);
        adminUserId = admin.getId();

        MvcResult loginAdmin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        adminToken = objectMapper.readTree(loginAdmin.getResponse().getContentAsString())
                .get("data").get("token").asText();
        assertThat(adminToken).isNotBlank();

        // --- 创建普通用户 ---
        User regular = new User();
        regular.setUsername("normaluser");
        regular.setPassword(passwordEncoder.encode("123456"));
        regular.setEmail("normal@test.com");
        regular.setRole("user");
        regular.setStatus(UserStatus.APPROVED.getValue());
        userMapper.insert(regular);
        regularUserId = regular.getId();

        MvcResult loginUser = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"normaluser\",\"password\":\"123456\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        userToken = objectMapper.readTree(loginUser.getResponse().getContentAsString())
                .get("data").get("token").asText();
        assertThat(userToken).isNotBlank();

        // --- 创建待审核用户 ---
        User pending = new User();
        pending.setUsername("pendinguser");
        pending.setPassword(passwordEncoder.encode("123456"));
        pending.setEmail("pending@test.com");
        pending.setRole("user");
        pending.setStatus(UserStatus.PENDING.getValue());
        userMapper.insert(pending);
        pendingUserId = pending.getId();

        // --- 创建影片 ---
        Movie movie = new Movie();
        movie.setTitle("测试影片");
        movie.setDescription("一部测试用影片");
        movie.setDirector("测试导演");
        movie.setCast("演员甲, 演员乙");
        movie.setAverageScore(BigDecimal.ZERO);
        movie.setReviewCount(0);
        movieMapper.insert(movie);
        movieId = movie.getId();

        // --- 创建一条评论（普通用户发表）---
        Review review = new Review();
        review.setMovieId(movieId);
        review.setUserId(regularUserId);
        review.setRating(8);
        review.setComment("不错");
        review.setLikeCount(0);
        review.setHidden(0);
        reviewMapper.insert(review);
        reviewId = review.getId();
    }

    // ========================================
    //  权限校验：非管理员访问 → 403
    // ========================================

    @Test
    @Order(1)
    void nonAdmin403_pendingUsers() throws Exception {
        mockMvc.perform(get("/api/admin/users/pending")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(2)
    void nonAdmin403_auditUser() throws Exception {
        AuditRequest req = new AuditRequest();
        req.setAction("approve");
        mockMvc.perform(put("/api/admin/users/" + pendingUserId + "/audit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(3)
    void nonAdmin403_addMovie() throws Exception {
        AddMovieRequest req = new AddMovieRequest();
        req.setTitle("x");
        req.setDescription("x");
        req.setDirector("x");
        mockMvc.perform(post("/api/admin/movies")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(4)
    void noToken401() throws Exception {
        mockMvc.perform(get("/api/admin/users/pending"))
                .andExpect(jsonPath("$.code").value(401));
    }

    // ========================================
    //  2.1 待审核用户列表
    // ========================================

    @Test
    @Order(5)
    void getPendingUsers_happyPath() throws Exception {
        mockMvc.perform(get("/api/admin/users/pending")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("pendinguser"))
                .andExpect(jsonPath("$.data.list[0].status").value("pending"))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @Order(6)
    void getPendingUsers_pagination() throws Exception {
        mockMvc.perform(get("/api/admin/users/pending")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("page", "1").param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.size").value(5));
    }

    // ========================================
    //  2.2 审核用户
    // ========================================

    @Test
    @Order(7)
    void auditUser_approve() throws Exception {
        AuditRequest req = new AuditRequest();
        req.setAction("approve");

        mockMvc.perform(put("/api/admin/users/" + pendingUserId + "/audit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("审核通过"));

        // 验证数据库状态已变
        User user = userMapper.selectById(pendingUserId);
        assertThat(user.getStatus()).isEqualTo(UserStatus.APPROVED.getValue());
    }

    @Test
    @Order(8)
    void auditUser_alreadyProcessed() throws Exception {
        // 上一个测试已经 approve 了，再审核应返回 400
        AuditRequest req = new AuditRequest();
        req.setAction("reject");

        mockMvc.perform(put("/api/admin/users/" + pendingUserId + "/audit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(9)
    void auditUser_notFound() throws Exception {
        AuditRequest req = new AuditRequest();
        req.setAction("approve");

        mockMvc.perform(put("/api/admin/users/99999/audit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(10)
    void auditUser_badAction() throws Exception {
        // Create another pending user for this test
        User p2 = new User();
        p2.setUsername("pending2");
        p2.setPassword(passwordEncoder.encode("123456"));
        p2.setEmail("p2@test.com");
        p2.setRole("user");
        p2.setStatus(UserStatus.PENDING.getValue());
        userMapper.insert(p2);

        AuditRequest req = new AuditRequest();
        req.setAction("badaction");

        mockMvc.perform(put("/api/admin/users/" + p2.getId() + "/audit")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(400));
    }

    // ========================================
    //  2.3 添加影片
    // ========================================

    @Test
    @Order(11)
    void addMovie_happyPath() throws Exception {
        AddMovieRequest req = new AddMovieRequest();
        req.setTitle("肖申克的救赎");
        req.setDescription("一部关于希望的电影");
        req.setDirector("弗兰克·德拉邦特");
        req.setCast("蒂姆·罗宾斯, 摩根·弗里曼");
        req.setReleaseDate("1994-09-23");
        req.setCoverUrl("https://example.com/shawshank.jpg");

        MvcResult result = mockMvc.perform(post("/api/admin/movies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("影片添加成功"))
                .andExpect(jsonPath("$.data.movieId").isNumber())
                .andReturn();

        Long newMovieId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("movieId").asLong();

        Movie movie = movieMapper.selectById(newMovieId);
        assertThat(movie).isNotNull();
        assertThat(movie.getTitle()).isEqualTo("肖申克的救赎");
        assertThat(movie.getAverageScore()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(movie.getReviewCount()).isEqualTo(0);
    }

    // ========================================
    //  2.4 修改影片
    // ========================================

    @Test
    @Order(12)
    void updateMovie_happyPath() throws Exception {
        UpdateMovieRequest req = new UpdateMovieRequest();
        req.setTitle("修改后的标题");
        req.setDescription("修改后的描述");

        mockMvc.perform(put("/api/admin/movies/" + movieId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Movie movie = movieMapper.selectById(movieId);
        assertThat(movie.getTitle()).isEqualTo("修改后的标题");
        assertThat(movie.getDescription()).isEqualTo("修改后的描述");
        // 未改的字段保持原样
        assertThat(movie.getDirector()).isEqualTo("测试导演");
    }

    @Test
    @Order(13)
    void updateMovie_notFound() throws Exception {
        UpdateMovieRequest req = new UpdateMovieRequest();
        req.setTitle("x");

        mockMvc.perform(put("/api/admin/movies/99999")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========================================
    //  2.5 删除影片（软删除）
    // ========================================

    @Test
    @Order(14)
    void deleteMovie_notFound() throws Exception {
        mockMvc.perform(delete("/api/admin/movies/99999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========================================
    //  2.6 影评列表（管理视图）
    // ========================================

    @Test
    @Order(15)
    void getReviews_all() throws Exception {
        mockMvc.perform(get("/api/admin/reviews")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].username").value("normaluser"))
                .andExpect(jsonPath("$.data.list[0].movieTitle").value("修改后的标题"))
                .andExpect(jsonPath("$.data.list[0].rating").value(8))
                .andExpect(jsonPath("$.data.list[0].hidden").value(false));
    }

    @Test
    @Order(16)
    void getReviews_byMovieId() throws Exception {
        mockMvc.perform(get("/api/admin/reviews")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("movieId", String.valueOf(movieId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1));
    }

    @Test
    @Order(17)
    void getReviews_byHidden() throws Exception {
        // 目前没有隐藏的评论
        mockMvc.perform(get("/api/admin/reviews")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("hidden", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(0));
    }

    // ========================================
    //  2.7 隐藏/显示评论 + 评分冗余联动
    // ========================================

    @Test
    @Order(18)
    void setReviewVisibility_hide() throws Exception {
        // 先刷新影片评分（评论 r=8, 只有1条 → avg=8.0）
        mockMvc.perform(put("/api/admin/reviews/" + reviewId + "/visibility")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hidden\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证评论已隐藏
        Review review = reviewMapper.selectById(reviewId);
        assertThat(review.getHidden()).isEqualTo(1);

        // 验证影片评分归零（隐藏的评论不计入）
        Movie movie = movieMapper.selectById(movieId);
        assertThat(movie.getAverageScore()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(movie.getReviewCount()).isEqualTo(0);
    }

    @Test
    @Order(19)
    void setReviewVisibility_show() throws Exception {
        // 恢复显示
        mockMvc.perform(put("/api/admin/reviews/" + reviewId + "/visibility")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"hidden\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证评分恢复
        Movie movie = movieMapper.selectById(movieId);
        assertThat(movie.getAverageScore()).isEqualByComparingTo(BigDecimal.valueOf(8.0));
        assertThat(movie.getReviewCount()).isEqualTo(1);
    }

    @Test
    @Order(20)
    void setReviewVisibility_notFound() throws Exception {
        HideReviewRequest req = new HideReviewRequest();
        req.setHidden(true);

        mockMvc.perform(put("/api/admin/reviews/99999/visibility")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========================================
    //  2.8 查询访问日志
    // ========================================

    @Test
    @Order(21)
    void getLogs_empty() throws Exception {
        // 尚无日志记录（AOP 未实现），但接口应正常返回空列表
        mockMvc.perform(get("/api/admin/logs")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(0))
                .andExpect(jsonPath("$.data.total").value(0));
    }

    @Test
    @Order(22)
    void getLogs_withFilters() throws Exception {
        mockMvc.perform(get("/api/admin/logs")
                        .header("Authorization", "Bearer " + adminToken)
                        .param("action", "login")
                        .param("page", "1").param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    // ========================================
    //  2.5 删除影片（放在后面，不影响其他测试）
    // ========================================

    @Test
    @Order(23)
    void deleteMovie_happyPath() throws Exception {
        // 创建一部临时影片用于删除测试
        Movie temp = new Movie();
        temp.setTitle("待删除影片");
        temp.setDescription("将被软删除");
        temp.setDirector("无名");
        temp.setCast("无");
        temp.setAverageScore(BigDecimal.ZERO);
        temp.setReviewCount(0);
        movieMapper.insert(temp);
        Long tempId = temp.getId();

        mockMvc.perform(delete("/api/admin/movies/" + tempId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证软删除：@TableLogic 自动过滤，selectById 返回 null
        Movie deleted = movieMapper.selectById(tempId);
        assertThat(deleted).isNull();
    }
}
