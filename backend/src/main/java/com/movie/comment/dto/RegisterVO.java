package com.movie.comment.dto;

/**
 * 注册成功返回
 */
public class RegisterVO {
    private Long userId;
    private String status;

    public RegisterVO(Long userId, String status) {
        this.userId = userId;
        this.status = status;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
