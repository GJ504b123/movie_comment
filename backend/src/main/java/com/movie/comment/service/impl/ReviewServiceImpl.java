package com.movie.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.comment.common.BusinessException;
import com.movie.comment.common.UserStatus;
import com.movie.comment.entity.Movie;
import com.movie.comment.entity.Review;
import com.movie.comment.entity.User;
import com.movie.comment.mapper.MovieMapper;
import com.movie.comment.mapper.ReviewMapper;
import com.movie.comment.mapper.UserMapper;
import com.movie.comment.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewMapper reviewMapper;
    private final MovieMapper movieMapper;
    private final UserMapper userMapper;

    public ReviewServiceImpl(ReviewMapper reviewMapper, MovieMapper movieMapper, UserMapper userMapper) {
        this.reviewMapper = reviewMapper;
        this.movieMapper = movieMapper;
        this.userMapper = userMapper;
    }

    @Override
    @Transactional
    public Long createReview(Long movieId, Long userId, Integer rating, String comment) {
        // 校验用户状态
        User user = userMapper.selectById(userId);
        if (user == null || !UserStatus.APPROVED.getValue().equals(user.getStatus())) {
            throw new BusinessException(403, "用户尚未通过审核，无法评论");
        }

        // 校验影片存在且未删除
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null || (movie.getDeleted() != null && movie.getDeleted() == 1)) {
            throw new BusinessException(404, "影片不存在");
        }

        // 一人一片一评
        Long count = reviewMapper.selectCount(
                new LambdaQueryWrapper<Review>()
                        .eq(Review::getUserId, userId)
                        .eq(Review::getMovieId, movieId));
        if (count > 0) {
            throw new BusinessException(409, "您已经评论过这部电影，不能重复评论");
        }

        Review review = new Review();
        review.setMovieId(movieId);
        review.setUserId(userId);
        review.setRating(rating);
        review.setComment(comment);
        review.setLikeCount(0);
        review.setHidden(0);
        reviewMapper.insert(review);

        // 刷新影片评分冗余
        refreshMovieScore(movieId);

        return review.getId();
    }

    @Override
    @Transactional
    public void updateReview(Long reviewId, Long userId, Integer rating, String comment) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能修改自己的评论");
        }

        if (rating != null) {
            review.setRating(rating);
        }
        if (comment != null) {
            review.setComment(comment);
        }
        reviewMapper.updateById(review);

        // 评分变了就刷新冗余
        if (rating != null) {
            refreshMovieScore(review.getMovieId());
        }
    }

    @Override
    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewMapper.selectById(reviewId);
        if (review == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (!review.getUserId().equals(userId)) {
            throw new BusinessException(403, "只能删除自己的评论");
        }

        Long movieId = review.getMovieId();
        reviewMapper.deleteById(reviewId);

        // 刷新影片评分冗余
        refreshMovieScore(movieId);
    }

    /**
     * 同事务内重算并更新影片的 average_score 与 review_count（仅统计非隐藏评论）
     */
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
