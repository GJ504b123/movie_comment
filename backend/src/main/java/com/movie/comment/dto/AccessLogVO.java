package com.movie.comment.dto;

import com.movie.comment.entity.AccessLog;

/**
 * 访问日志列表项——对齐 apiDesigner.md 2.8
 */
public class AccessLogVO {
    private Long id;
    private Long userId;
    private String username;
    private String action;
    private Long targetId;
    private String ip;
    private String userAgent;
    private String createTime;

    public static AccessLogVO from(AccessLog log) {
        AccessLogVO vo = new AccessLogVO();
        vo.id = log.getId();
        vo.userId = log.getUserId();
        vo.username = log.getUsername();
        vo.action = log.getAction();
        vo.targetId = log.getTargetId();
        vo.ip = log.getIp();
        vo.userAgent = log.getUserAgent();
        vo.createTime = log.getCreateTime() != null ? log.getCreateTime().toString() : null;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public Long getTargetId() { return targetId; }
    public void setTargetId(Long targetId) { this.targetId = targetId; }
    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public String getCreateTime() { return createTime; }
    public void setCreateTime(String createTime) { this.createTime = createTime; }
}
