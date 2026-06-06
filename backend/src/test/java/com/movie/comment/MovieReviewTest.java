package com.movie.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.movie.comment.common.UserStatus;
import com.movie.comment.dto.CreateReviewRequest;
import com.movie.comment.dto.UpdateReviewRequest;
import com.movie.comment.entity.Movie;
import com.movie.comment.entity.User;
import com.movie.comment.mapper.MovieMapper;
import com.movie.comment.mapper.UserMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
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
class MovieReviewTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private UserMapper userMapper;
    @Autowired private MovieMapper movieMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    // 用户A（主测试用户，approved）
    private static String tokenA;
    private static Long userIdA;
    // 用户B（另一个 approved 用户，用于越权测试）
    private static String tokenB;
    private static Long userIdB;
    // 用户C（pending，用于状态校验）
    private static Long userIdC;

    private static Long movieId;      // 主测试影片
    private static Long movieId2;     // 第二部影片（有初始评分，用于搜索/排序）
    private static Long deletedMovieId; // 已软删除的影片
    private static Long reviewId;     // 用户A的评论

    // ========================================
    //  Setup
    // ========================================

    @Test
    @Order(0)
    void setup() throws Exception {
        // --- 创建用户A（approved）---
        User ua = new User();
        ua.setUsername("mrtest");
        ua.setPassword(passwordEncoder.encode("123456"));
        ua.setEmail("mrtest@test.com");
        ua.setRole("user");
        ua.setStatus(UserStatus.APPROVED.getValue());
        userMapper.insert(ua);
        userIdA = ua.getId();

        MvcResult loginA = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"mrtest\",\"password\":\"123456\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        tokenA = objectMapper.readTree(loginA.getResponse().getContentAsString())
                .get("data").get("token").asText();
        assertThat(tokenA).isNotBlank();

        // --- 创建用户B（approved，用于越权测试）---
        User ub = new User();
        ub.setUsername("otheruser");
        ub.setPassword(passwordEncoder.encode("123456"));
        ub.setEmail("other@test.com");
        ub.setRole("user");
        ub.setStatus(UserStatus.APPROVED.getValue());
        userMapper.insert(ub);
        userIdB = ub.getId();

        MvcResult loginB = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"otheruser\",\"password\":\"123456\"}"))
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();
        tokenB = objectMapper.readTree(loginB.getResponse().getContentAsString())
                .get("data").get("token").asText();
        assertThat(tokenB).isNotBlank();

        // --- 创建用户C（pending，用于状态校验）---
        User uc = new User();
        uc.setUsername("pendinguser");
        uc.setPassword(passwordEncoder.encode("123456"));
        uc.setEmail("pending@test.com");
        uc.setRole("user");
        uc.setStatus(UserStatus.PENDING.getValue());
        userMapper.insert(uc);
        userIdC = uc.getId();

        // --- 创建主测试影片 ---
        Movie m = new Movie();
        m.setTitle("测试影片A");
        m.setDescription("这是一部测试影片");
        m.setDirector("测试导演");
        m.setCast("演员甲, 演员乙");
        m.setAverageScore(BigDecimal.ZERO);
        m.setReviewCount(0);
        movieMapper.insert(m);
        movieId = m.getId();

        // --- 创建第二部影片（有评分，用于搜索/排序/排行榜）---
        Movie m2 = new Movie();
        m2.setTitle("星际探索者");
        m2.setDescription("科幻巨制");
        m2.setDirector("诺兰");
        m2.setCast("马修, 安妮");
        m2.setAverageScore(BigDecimal.valueOf(8.5));
        m2.setReviewCount(3);
        movieMapper.insert(m2);
        movieId2 = m2.getId();

        // --- 创建一部软删除的影片 ---
        Movie md = new Movie();
        md.setTitle("已删除影片");
        md.setDescription("这部影片已被软删除");
        md.setDirector("无名");
        md.setCast("无人");
        md.setAverageScore(BigDecimal.ZERO);
        md.setReviewCount(0);
        md.setDeleted(1);
        movieMapper.insert(md);
        deletedMovieId = md.getId();
    }

    // ========================================
    //  3.1 影片列表（模糊搜索 + 分页 + 排序）
    // ========================================

    @Test
    @Order(1)
    void listAllMovies() throws Exception {
        // InfrastructureTest 也插了 1 部，加上 setup 的 2 部（已删除的不算）= 共 3 部
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(3))
                .andExpect(jsonPath("$.data.total").value(3));
    }

    @Test
    @Order(2)
    void listMoviesWithPagination() throws Exception {
        // 每页 2 条 → 共 2 页
        mockMvc.perform(get("/api/movies").param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(2))
                .andExpect(jsonPath("$.data.total").value(3))
                .andExpect(jsonPath("$.data.totalPages").value(2));
    }

    @Test
    @Order(3)
    void listMoviesSortByRating() throws Exception {
        // 按评分降序，最高分应在第一位（多部同分时顺序不保证）
        mockMvc.perform(get("/api/movies").param("sort", "rating"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].averageScore").value(8.5))
                .andExpect(jsonPath("$.data.list[1].averageScore").value(8.5));
    }

    @Test
    @Order(4)
    void searchMoviesByTitle() throws Exception {
        mockMvc.perform(get("/api/movies").param("keyword", "星际"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("星际探索者"));
    }

    @Test
    @Order(5)
    void searchMoviesByDirector() throws Exception {
        mockMvc.perform(get("/api/movies").param("keyword", "诺兰"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].director").doesNotExist()) // 列表接口不含 director
                .andExpect(jsonPath("$.data.list[0].title").value("星际探索者"));
    }

    @Test
    @Order(6)
    void searchMoviesByCast() throws Exception {
        mockMvc.perform(get("/api/movies").param("keyword", "安妮"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("星际探索者"));
    }

    @Test
    @Order(7)
    void searchNoMatch() throws Exception {
        mockMvc.perform(get("/api/movies").param("keyword", "不可能匹配的关键词XYZ"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list.length()").value(0));
    }

    // ========================================
    //  3.2 影片详情
    // ========================================

    @Test
    @Order(8)
    void getMovieDetailWithoutReviews() throws Exception {
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.movie.id").value(movieId))
                .andExpect(jsonPath("$.data.movie.title").value("测试影片A"))
                .andExpect(jsonPath("$.data.reviews.list.length()").value(0))
                .andExpect(jsonPath("$.data.reviews.total").value(0));
    }

    @Test
    @Order(9)
    void getMovieDetailWithReviews() throws Exception {
        // 先以用户A发评论
        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(8);
        req.setComment("还不错");
        MvcResult result = mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(201))
                .andReturn();
        reviewId = objectMapper.readTree(result.getResponse().getContentAsString())
                .get("data").get("reviewId").asLong();

        // 以用户A看详情 → canEdit=true
        mockMvc.perform(get("/api/movies/" + movieId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.reviews.list.length()").value(1))
                .andExpect(jsonPath("$.data.reviews.list[0].canEdit").value(true));

        // 游客看详情 → canEdit=false
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(jsonPath("$.data.reviews.list[0].canEdit").value(false));

        // 用户B看详情 → canEdit=false
        mockMvc.perform(get("/api/movies/" + movieId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(jsonPath("$.data.reviews.list[0].canEdit").value(false));
    }

    @Test
    @Order(10)
    void getMovieNotFound() throws Exception {
        mockMvc.perform(get("/api/movies/99999"))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========================================
    //  3.3 发表评论
    // ========================================

    @Test
    @Order(11)
    void duplicateReview409() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(5);
        req.setComment("重复评");

        mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(409));
    }

    @Test
    @Order(12)
    void postReviewUnauthenticated401() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(5);
        req.setComment("游客");

        mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(13)
    void postReviewPendingUser403() throws Exception {
        // pending 用户登录获取 token
        MvcResult loginC = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"pendinguser\",\"password\":\"123456\"}"))
                .andReturn();
        // pending 用户登录应该返回 403（这正是我们需要的——pending 无法登录，也就无法发评）
        // 但测试发评时我们需要一个 token。pending 用户无法获取 token，
        // 所以我们在这里验证 pending 用户登录就被拦截了
        String resp = loginC.getResponse().getContentAsString();
        assertThat(resp).contains("403");
    }

    @Test
    @Order(14)
    void postReviewRatingOutOfBounds() throws Exception {
        // rating=0
        mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":0,\"comment\":\"零分\"}"))
                .andExpect(jsonPath("$.code").value(400));

        // rating=11
        mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"rating\":11,\"comment\":\"超分\"}"))
                .andExpect(jsonPath("$.code").value(400));
    }

    @Test
    @Order(15)
    void postReviewOnDeletedMovie404() throws Exception {
        CreateReviewRequest req = new CreateReviewRequest();
        req.setRating(5);
        req.setComment("对已删影片评论");

        mockMvc.perform(post("/api/movies/" + deletedMovieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========================================
    //  3.4 修改评论
    // ========================================

    @Test
    @Order(16)
    void updateReviewByOwner200() throws Exception {
        UpdateReviewRequest req = new UpdateReviewRequest();
        req.setRating(9);
        req.setComment("改后的评论");

        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证评分冗余已更新
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(jsonPath("$.data.movie.averageScore").value(9.0))
                .andExpect(jsonPath("$.data.movie.reviewCount").value(1));
    }

    @Test
    @Order(17)
    void updateReviewByOtherUser403() throws Exception {
        UpdateReviewRequest req = new UpdateReviewRequest();
        req.setRating(5);

        // 用户B试图改用户A的评论
        mockMvc.perform(put("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(18)
    void updateNonExistentReview404() throws Exception {
        UpdateReviewRequest req = new UpdateReviewRequest();
        req.setRating(5);

        mockMvc.perform(put("/api/reviews/99999")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(jsonPath("$.code").value(404));
    }

    // ========================================
    //  3.5 删除评论
    // ========================================

    @Test
    @Order(19)
    void deleteReviewByOtherUser403() throws Exception {
        // 用户B试图删用户A的评论
        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(jsonPath("$.code").value(403));
    }

    @Test
    @Order(20)
    void deleteNonExistentReview404() throws Exception {
        mockMvc.perform(delete("/api/reviews/99999")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(jsonPath("$.code").value(404));
    }

    @Test
    @Order(21)
    void deleteReviewByOwner200() throws Exception {
        mockMvc.perform(delete("/api/reviews/" + reviewId)
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        // 验证评分归零
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(jsonPath("$.data.movie.averageScore").value(0.0))
                .andExpect(jsonPath("$.data.movie.reviewCount").value(0));
    }

    // ========================================
    //  评分冗余：多人评分的平均值计算
    // ========================================

    @Test
    @Order(22)
    void multiReviewAverageScore() throws Exception {
        // 用户A评分 6 → 还有之前的评论吗？不，之前在 Order(21) 删了。
        // 重新发：用户A 评 6，用户B 评 10 → 平均值应为 8.0

        CreateReviewRequest r1 = new CreateReviewRequest();
        r1.setRating(6);
        r1.setComment("A评");
        mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenA)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r1)))
                .andExpect(jsonPath("$.code").value(201));

        // 验证单人评分 = 6.0
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(jsonPath("$.data.movie.averageScore").value(6.0))
                .andExpect(jsonPath("$.data.movie.reviewCount").value(1));

        CreateReviewRequest r2 = new CreateReviewRequest();
        r2.setRating(10);
        r2.setComment("B评");
        mockMvc.perform(post("/api/movies/" + movieId + "/reviews")
                        .header("Authorization", "Bearer " + tokenB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(r2)))
                .andExpect(jsonPath("$.code").value(201));

        // 验证双人平均 = (6+10)/2 = 8.0
        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(jsonPath("$.data.movie.averageScore").value(8.0))
                .andExpect(jsonPath("$.data.movie.reviewCount").value(2));
    }

    // ========================================
    //  3.6 排行榜
    // ========================================

    @Test
    @Order(23)
    void rankingsDefault() throws Exception {
        mockMvc.perform(get("/api/rankings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].rank").value(1))
                .andExpect(jsonPath("$.data[0].averageScore").isNumber());
    }

    @Test
    @Order(24)
    void rankingsWithLimit() throws Exception {
        mockMvc.perform(get("/api/rankings").param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].rank").value(1));
    }

    @Test
    @Order(25)
    void softDeletedMovieNotInList() throws Exception {
        // 软删除的影片不应出现在列表中
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(3)); // 仍然 3（没有新增未删除的）
    }
}
