package com.movie.comment.service;

import com.movie.comment.common.PageResult;
import com.movie.comment.dto.*;

/**
 * 管理员服务——用户审批、影片管理、评论管理、日志查询
 */
public interface AdminService {

    /** 2.1 获取待审核用户列表 */
    PageResult<UserVO> getPendingUsers(int page, int size);

    /** 2.2 审核用户（通过/拒绝） */
    void auditUser(Long userId, AuditRequest req);

    /** 2.3 添加影片，返回 movieId */
    Long addMovie(AddMovieRequest req);

    /** 2.4 修改影片信息 */
    void updateMovie(Long movieId, UpdateMovieRequest req);

    /** 2.5 删除影片（软删除） */
    void deleteMovie(Long movieId);

    /** 2.6 获取所有影评（管理视图），支持过滤 */
    PageResult<AdminReviewVO> getReviews(Long movieId, Long userId, Boolean hidden, int page, int size);

    /** 2.7 隐藏/显示评论 */
    void setReviewVisibility(Long reviewId, boolean hidden);

    /** 2.8 查询访问日志 */
    PageResult<AccessLogVO> getLogs(Long userId, String action, String startTime, String endTime, int page, int size);
}
