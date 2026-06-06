package com.movie.comment.dto;

import com.movie.comment.entity.Review;

/**
 * 评论展示——对齐 apiDesigner.md 3.2 的 reviews.list 元素
 */
public class ReviewVO {
    private Long id;
    private Long userId;
    private String username;
    private Integer rating;
    private String comment;
    private Integer likeCount;
    private String createTime;
    private boolean canEdit;

    public static ReviewVO from(Review r, String username, boolean canEdit) {
        ReviewVO vo = new ReviewVO();
        vo.id = r.getId();
        vo.userId = r.getUserId();
        vo.username = username;
        vo.rating = r.getRating();
        vo.comment = r.getComment();
        vo.likeCount = r.getLikeCount();
        vo.createTime = r.getCreateTime() != null ? r.getCreateTime().toString() : null;
        vo.canEdit = canEdit;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Integer getLikeCount() { return likeCount; }
    public void setLikeCount(Integer likeCount) { this.likeCount = likeCount; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public boolean isCanEdit() { return canEdit; }
    public void setCanEdit(boolean canEdit) { this.canEdit = canEdit; }
}
