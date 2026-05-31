package com.movie.comment.common;

/**
 * 用户状态枚举
 */
public enum UserStatus {
    PENDING("pending"),
    APPROVED("approved"),
    REJECTED("rejected");

    private final String value;

    UserStatus(String value) { this.value = value; }

    public String getValue() { return value; }
}
