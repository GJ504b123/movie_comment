package com.movie.comment.service;

/**
 * 评论增/改/删服务
 */
public interface ReviewService {
    /** 发表评论，返回 reviewId */
    Long createReview(Long movieId, Long userId, Integer rating, String comment);

    /** 修改评论 */
    void updateReview(Long reviewId, Long userId, Integer rating, String comment);

    /** 删除评论 */
    void deleteReview(Long reviewId, Long userId);

    /** 点赞/取消点赞（toggle） */
    void likeReview(Long reviewId, boolean liked);
}
