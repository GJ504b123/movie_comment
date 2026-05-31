package com.movie.comment;

import com.movie.comment.entity.Movie;
import com.movie.comment.entity.User;
import com.movie.comment.mapper.MovieMapper;
import com.movie.comment.mapper.UserMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InfrastructureTest {

    @Autowired private UserMapper userMapper;
    @Autowired private MovieMapper movieMapper;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void contextLoads() {
        // 验证 Spring 容器启动 + Flyway 迁移成功
    }

    @Test
    void userCrud() {
        User u = new User();
        u.setUsername("testuser");
        u.setPassword(passwordEncoder.encode("123456"));
        u.setEmail("test@example.com");
        u.setRole("user");
        u.setStatus("pending");
        userMapper.insert(u);
        assertThat(u.getId()).isNotNull();

        User found = userMapper.selectById(u.getId());
        assertThat(found.getUsername()).isEqualTo("testuser");
        assertThat(found.getStatus()).isEqualTo("pending");
        assertThat(passwordEncoder.matches("123456", found.getPassword())).isTrue();
    }

    @Test
    void movieCrud() {
        Movie m = new Movie();
        m.setTitle("测试影片");
        m.setDescription("测试简介");
        m.setDirector("测试导演");
        m.setReleaseDate(LocalDate.of(2025, 1, 1));
        m.setAverageScore(java.math.BigDecimal.valueOf(8.5));
        m.setReviewCount(10);
        movieMapper.insert(m);
        assertThat(m.getId()).isNotNull();

        List<Movie> list = movieMapper.selectList(null);
        assertThat(list).isNotEmpty();
    }
}
