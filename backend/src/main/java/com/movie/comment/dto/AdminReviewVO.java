package com.movie.comment.dto;

import com.movie.comment.entity.Review;

/**
 * 管理端评论列表项——对齐 apiDesigner.md 2.6
 */
public class AdminReviewVO {
    private Long id;
    private String movieTitle;
    private String username;
    private Integer rating;
    private String comment;
    private Integer likeCount;
    private Boolean hidden;
    private String createTime;

    public static AdminReviewVO from(Review r, String movieTitle, String username) {
        AdminReviewVO vo = new AdminReviewVO();
        vo.id = r.getId();
        vo.movieTitle = movieTitle;
        vo.username = username;
        vo.rating = r.getRating();
        vo.comment = r.getComment();
        vo.likeCount = r.getLikeCount();
        vo.hidden = r.getHidden() != null && r.getHidden() == 1;
        vo.createTime = r.getCreateTime() != null ? r.getCreateTime().toString() : null;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
