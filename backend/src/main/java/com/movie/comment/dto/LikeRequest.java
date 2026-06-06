package com.movie.comment.dto;

/**
 * 点赞/取消点赞请求
 */
public class LikeRequest {
    private boolean liked;   // true=点赞, false=取消点赞

    public boolean isLiked() { return liked; }
    public void setLiked(boolean liked) { this.liked = liked; }
}
