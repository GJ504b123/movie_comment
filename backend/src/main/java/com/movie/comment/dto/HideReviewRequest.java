package com.movie.comment.dto;

/**
 * 管理员隐藏/显示评论请求——对齐 apiDesigner.md 2.7
 */
public class HideReviewRequest {
    private Boolean hidden;   // true=隐藏, false=恢复显示

    public Boolean getHidden() { return hidden; }
    public void setHidden(Boolean hidden) { this.hidden = hidden; }
}
