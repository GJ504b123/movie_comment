package com.movie.comment.dto;

/**
 * 管理员审核用户请求——对齐 apiDesigner.md 2.2
 */
public class AuditRequest {
    private String action;   // "approve" / "reject"
    private String reason;   // 可选

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
