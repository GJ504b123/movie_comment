package com.movie.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.comment.common.BusinessException;
import com.movie.comment.common.PageResult;
import com.movie.comment.common.UserStatus;
import com.movie.comment.dto.*;
import com.movie.comment.entity.*;
import com.movie.comment.mapper.*;
import com.movie.comment.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserMapper userMapper;
    private final MovieMapper movieMapper;
    private final ReviewMapper reviewMapper;
    private final AccessLogMapper accessLogMapper;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public AdminServiceImpl(UserMapper userMapper, MovieMapper movieMapper,
                            ReviewMapper reviewMapper, AccessLogMapper accessLogMapper) {
        this.userMapper = userMapper;
        this.movieMapper = movieMapper;
        this.reviewMapper = reviewMapper;
        this.accessLogMapper = accessLogMapper;
    }

    // ========================================
    //  2.1 待审核用户列表
    // ========================================

    @Override
    public PageResult<UserVO> getPendingUsers(int page, int size) {
        LambdaQueryWrapper<User> countWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getStatus, UserStatus.PENDING.getValue());
        long total = userMapper.selectCount(countWrapper);

        LambdaQueryWrapper<User> listWrapper = new LambdaQueryWrapper<User>()
                .eq(User::getStatus, UserStatus.PENDING.getValue())
                .orderByDesc(User::getCreateTime);
        int offset = (page - 1) * size;
        listWrapper.last("LIMIT " + size + " OFFSET " + offset);

        List<UserVO> list = userMapper.selectList(listWrapper).stream()
                .map(UserVO::from)
                .collect(Collectors.toList());

        return new PageResult<>(list, total, page, size);
    }

    // ========================================
    //  2.2 审核用户
    // ========================================

    @Override
    @Transactional
    public void auditUser(Long userId, AuditRequest req) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        if (!UserStatus.PENDING.getValue().equals(user.getStatus())) {
            throw new BusinessException(400, "该用户不是待审核状态");
        }

        String action = req.getAction();
        if ("approve".equals(action)) {
            user.setStatus(UserStatus.APPROVED.getValue());
        } else if ("reject".equals(action)) {
            user.setStatus(UserStatus.REJECTED.getValue());
        } else {
            throw new BusinessException(400, "action 必须为 approve 或 reject");
        }

        userMapper.updateById(user);
    }

    // ========================================
    //  2.3 添加影片
    // ========================================

    @Override
    @Transactional
    public Long addMovie(AddMovieRequest req) {
        Movie movie = new Movie();
        movie.setTitle(req.getTitle());
        movie.setDescription(req.getDescription());
        movie.setDirector(req.getDirector());
        movie.setCast(req.getCast());
        movie.setCoverUrl(req.getCoverUrl());
        if (req.getReleaseDate() != null && !req.getReleaseDate().isBlank()) {
            movie.setReleaseDate(LocalDate.parse(req.getReleaseDate(), DATE_FMT));
        }
        movie.setAverageScore(BigDecimal.ZERO);
        movie.setReviewCount(0);
        movieMapper.insert(movie);
        return movie.getId();
    }

    // ========================================
    //  2.4 修改影片
    // ========================================

    @Override
    @Transactional
    public void updateMovie(Long movieId, UpdateMovieRequest req) {
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null || (movie.getDeleted() != null && movie.getDeleted() == 1)) {
            throw new BusinessException(404, "影片不存在");
        }

        if (req.getTitle() != null && !req.getTitle().isBlank()) {
            movie.setTitle(req.getTitle());
        }
        if (req.getDescription() != null) {
            movie.setDescription(req.getDescription());
        }
        if (req.getDirector() != null) {
            movie.setDirector(req.getDirector());
        }
        if (req.getCast() != null) {
            movie.setCast(req.getCast());
        }
        if (req.getCoverUrl() != null) {
            movie.setCoverUrl(req.getCoverUrl());
        }
        if (req.getReleaseDate() != null && !req.getReleaseDate().isBlank()) {
            movie.setReleaseDate(LocalDate.parse(req.getReleaseDate(), DATE_FMT));
        }

        movieMapper.updateById(movie);
    }

    // ========================================
    //  2.5 删除影片（软删除）
    // ========================================

    @Override
    @Transactional
    public void deleteMovie(Long movieId) {
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null) {
            throw new BusinessException(404, "影片不存在");
        }
        // MyBatis-Plus @TableLogic：deleteById 生成 UPDATE ... SET deleted=1
        movieMapper.deleteById(movieId);
    }

    // ========================================
    //  2.6 影评列表（管理视图）
    // ========================================

    @Override
    public PageResult<AdminReviewVO> getReviews(Long movieId, Long userId, Boolean hidden,
                                                 int page, int size) {
        LambdaQueryWrapper<Review> countWrapper = new LambdaQueryWrapper<>();
        if (movieId != null) {
            countWrapper.eq(Review::getMovieId, movieId);
        }
        if (userId != null) {
            countWrapper.eq(Review::getUserId, userId);
        }
        if (hidden != null) {
            countWrapper.eq(Review::getHidden, hidden ? 1 : 0);
        }
        long total = reviewMapper.selectCount(countWrapper);

        LambdaQueryWrapper<Review> listWrapper = new LambdaQueryWrapper<>();
        if (movieId != null) {
            listWrapper.eq(Review::getMovieId, movieId);
        }
        if (userId != null) {
            listWrapper.eq(Review::getUserId, userId);
        }
        if (hidden != null) {
            listWrapper.eq(Review::getHidden, hidden ? 1 : 0);
        }
        listWrapper.orderByDesc(Review::getCreateTime);
        int offset = (page - 1) * size;
        listWrapper.last("LIMIT " + size + " OFFSET " + offset);

        List<Review> reviews = reviewMapper.selectList(listWrapper);

        // 批量查影片标题
        Set<Long> movieIds = reviews.stream()
                .map(Review::getMovieId)
                .collect(Collectors.toSet());
        Map<Long, String> movieTitleMap = new HashMap<>();
        if (!movieIds.isEmpty()) {
            List<Movie> movies = movieMapper.selectBatchIds(movieIds);
            for (Movie m : movies) {
                movieTitleMap.put(m.getId(), m.getTitle());
            }
        }

        // 批量查用户名
        Set<Long> userIds = reviews.stream()
                .map(Review::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> usernameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) {
                usernameMap.put(u.getId(), u.getUsername());
            }
        }

        List<AdminReviewVO> list = reviews.stream()
                .map(r -> AdminReviewVO.from(r,
                        movieTitleMap.getOrDefault(r.getMovieId(), "未知影片"),
                        usernameMap.getOrDefault(r.getUserId(), "未知用户")))
                .collect(Collectors.toList());

        return new PageResult<>(list, total, page, size);
    }

    // ========================================
    //  2.7 隐藏/显示评论
    // ========================================

    @Override
    @Transactional
    public void setReviewVisibility(Long reviewId, boolean hidden) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评论不存在");
        }

        review.setHidden(hidden ? 1 : 0);
        reviewMapper.updateById(review);

        // 隐藏/显示影响影片评分冗余
        refreshMovieScore(review.getMovieId());
    }

    // ========================================
    //  2.8 查询访问日志
    // ========================================

    @Override
    public PageResult<AccessLogVO> getLogs(Long userId, String action,
                                           String startTime, String endTime,
                                           int page, int size) {
        LambdaQueryWrapper<AccessLog> countWrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            countWrapper.eq(AccessLog::getUserId, userId);
        }
        if (action != null && !action.isBlank()) {
            countWrapper.eq(AccessLog::getAction, action);
        }
        if (startTime != null && !startTime.isBlank()) {
            countWrapper.ge(AccessLog::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (endTime != null && !endTime.isBlank()) {
            countWrapper.le(AccessLog::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        long total = accessLogMapper.selectCount(countWrapper);

        LambdaQueryWrapper<AccessLog> listWrapper = new LambdaQueryWrapper<>();
        if (userId != null) {
            listWrapper.eq(AccessLog::getUserId, userId);
        }
        if (action != null && !action.isBlank()) {
            listWrapper.eq(AccessLog::getAction, action);
        }
        if (startTime != null && !startTime.isBlank()) {
            listWrapper.ge(AccessLog::getCreateTime, LocalDateTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        if (endTime != null && !endTime.isBlank()) {
            listWrapper.le(AccessLog::getCreateTime, LocalDateTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        listWrapper.orderByDesc(AccessLog::getCreateTime);
        int offset = (page - 1) * size;
        listWrapper.last("LIMIT " + size + " OFFSET " + offset);

        List<AccessLogVO> list = accessLogMapper.selectList(listWrapper).stream()
                .map(AccessLogVO::from)
                .collect(Collectors.toList());

        return new PageResult<>(list, total, page, size);
    }

    // ========================================
    //  评分冗余刷新（与 ReviewServiceImpl 逻辑一致）
    // ========================================

    private void refreshMovieScore(Long movieId) {
        List<Review> reviews = reviewMapper.selectList(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getMovieId, movieId)
                        .eq(Review::getHidden, 0));

        Movie movie = movieMapper.selectById(movieId);
        if (movie == null) return;

        int count = reviews.size();
        if (count == 0) {
            movie.setAverageScore(BigDecimal.ZERO);
            movie.setReviewCount(0);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0);
            movie.setAverageScore(BigDecimal.valueOf(avg).setScale(1, RoundingMode.HALF_UP));
            movie.setReviewCount(count);
        }
        movieMapper.updateById(movie);
    }
}
