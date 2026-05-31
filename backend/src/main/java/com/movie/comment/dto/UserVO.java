package com.movie.comment.dto;

import com.movie.comment.entity.User;

/**
 * 返回给前端的用户信息（不含密码）
 */
public class UserVO {
    private Long id;
    private String username;
    private String email;
    private String role;
    private String status;
    private String createTime;
    private String lastLoginTime;

    public static UserVO from(User u) {
        UserVO vo = new UserVO();
        vo.id = u.getId();
        vo.username = u.getUsername();
        vo.email = u.getEmail();
        vo.role = u.getRole();
        vo.status = u.getStatus();
        vo.createTime = u.getCreateTime() != null ? u.getCreateTime().toString() : null;
        vo.lastLoginTime = u.getLastLoginTime() != null ? u.getLastLoginTime().toString() : null;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
    public String getLastLoginTime() { return lastLoginTime; }
    public void setLastLoginTime(String lastLoginTime) { this.lastLoginTime = lastLoginTime; }
}
