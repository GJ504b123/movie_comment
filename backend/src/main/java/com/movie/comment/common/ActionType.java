package com.movie.comment.common;

/**
 * 访问日志动作枚举——对齐 apiDesigner.md 第4节
 */
public enum ActionType {
    LOGIN("login"),
    REGISTER("register"),
    SEARCH_MOVIE("search_movie"),
    VIEW_MOVIE_DETAIL("view_movie_detail"),
    POST_REVIEW("post_review"),
    UPDATE_REVIEW("update_review"),
    DELETE_REVIEW("delete_review"),
    VIEW_RANKING("view_ranking"),
    LIKE_REVIEW("like_review"),
    AUDIT_USER("audit_user"),
    ADD_MOVIE("add_movie"),
    EDIT_MOVIE("edit_movie"),
    DELETE_MOVIE("delete_movie"),
    HIDE_REVIEW("hide_review");

    private final String value;

    ActionType(String value) { this.value = value; }

    public String getValue() { return value; }
}
