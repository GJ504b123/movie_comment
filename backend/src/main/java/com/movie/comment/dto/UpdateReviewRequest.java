package com.movie.comment.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

/**
 * 修改评论——对齐 apiDesigner.md 3.4，所有字段可选
 */
public class UpdateReviewRequest {
    @Min(value = 1, message = "评分最小为1")
    @Max(value = 10, message = "评分最大为10")
    private Integer rating;

    private String comment;

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
