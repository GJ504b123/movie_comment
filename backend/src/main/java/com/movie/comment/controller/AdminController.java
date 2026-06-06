package com.movie.comment.controller;

import com.movie.comment.aspect.LogAction;
import com.movie.comment.common.ActionType;
import com.movie.comment.common.BusinessException;
import com.movie.comment.common.PageResult;
import com.movie.comment.common.Result;
import com.movie.comment.dto.*;
import com.movie.comment.security.UserContext;
import com.movie.comment.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "管理员", description = "用户审批、影片管理、评论管理、日志查询（需 admin 权限）")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ========================================
    //  2.1 待审核用户列表
    // ========================================

    @Operation(summary = "2.1 获取待审核用户列表", description = "返回 status=pending 的用户分页列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "分页列表"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "非管理员")
    })
    @GetMapping("/users/pending")
    public Result<PageResult<UserVO>> getPendingUsers(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        requireAdmin();
        return Result.ok(adminService.getPendingUsers(page, size));
    }

    // ========================================
    //  2.2 审核用户
    // ========================================

    @Operation(summary = "2.2 审核用户", description = "通过或拒绝待审核用户")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "审核成功"),
            @ApiResponse(responseCode = "400", description = "用户非待审核状态或 action 非法"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @LogAction(value = ActionType.AUDIT_USER, targetParamName = "userId")
    @PutMapping("/users/{userId}/audit")
    public Result<Void> auditUser(
            @Parameter(description = "用户 ID") @PathVariable Long userId,
            @RequestBody AuditRequest req) {
        requireAdmin();
        adminService.auditUser(userId, req);

        String action = req.getAction();
        String message = "approve".equals(action) ? "审核通过" : "审核已拒绝";
        return Result.ok(message, null);
    }

    // ========================================
    //  2.3 添加影片
    // ========================================

    @Operation(summary = "2.3 添加影片", description = "管理员添加新影片")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "添加成功，返回 movieId"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "非管理员")
    })
    @LogAction(ActionType.ADD_MOVIE)
    @PostMapping("/movies")
    public Result<Map<String, Long>> addMovie(@RequestBody AddMovieRequest req) {
        requireAdmin();
        Long movieId = adminService.addMovie(req);
        return Result.created("影片添加成功", Map.of("movieId", movieId));
    }

    // ========================================
    //  2.4 修改影片
    // ========================================

    @Operation(summary = "2.4 修改影片信息", description = "管理员修改影片，所有字段可选")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "404", description = "影片不存在")
    })
    @LogAction(value = ActionType.EDIT_MOVIE, targetParamName = "movieId")
    @PutMapping("/movies/{movieId}")
    public Result<Void> updateMovie(
            @Parameter(description = "影片 ID") @PathVariable Long movieId,
            @RequestBody UpdateMovieRequest req) {
        requireAdmin();
        adminService.updateMovie(movieId, req);
        return Result.ok("更新成功", null);
    }

    // ========================================
    //  2.5 删除影片
    // ========================================

    @Operation(summary = "2.5 删除影片", description = "软删除影片（评论保留，前端不再展示）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "404", description = "影片不存在")
    })
    @LogAction(value = ActionType.DELETE_MOVIE, targetParamName = "movieId")
    @DeleteMapping("/movies/{movieId}")
    public Result<Void> deleteMovie(
            @Parameter(description = "影片 ID") @PathVariable Long movieId) {
        requireAdmin();
        adminService.deleteMovie(movieId);
        return Result.ok("删除成功", null);
    }

    // ========================================
    //  2.6 影评列表（管理视图）
    // ========================================

    @Operation(summary = "2.6 获取所有影评（管理视图）", description = "支持按影片、用户、隐藏状态过滤")
    @ApiResponse(responseCode = "200", description = "分页影评列表")
    @GetMapping("/reviews")
    public Result<PageResult<AdminReviewVO>> getReviews(
            @Parameter(description = "影片 ID 过滤（可选）") @RequestParam(required = false) Long movieId,
            @Parameter(description = "用户 ID 过滤（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "隐藏状态过滤（可选）") @RequestParam(required = false) Boolean hidden,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size) {
        requireAdmin();
        return Result.ok(adminService.getReviews(movieId, userId, hidden, page, size));
    }

    // ========================================
    //  2.7 隐藏/显示评论
    // ========================================

    @Operation(summary = "2.7 隐藏/显示评论", description = "管理员可隐藏不当评论或恢复显示")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @LogAction(value = ActionType.HIDE_REVIEW, targetParamName = "reviewId")
    @PutMapping("/reviews/{reviewId}/visibility")
    public Result<Void> setReviewVisibility(
            @Parameter(description = "评论 ID") @PathVariable Long reviewId,
            @RequestBody HideReviewRequest req) {
        requireAdmin();
        adminService.setReviewVisibility(reviewId, req.getHidden());
        return Result.ok("操作成功", null);
    }

    // ========================================
    //  2.8 查询访问日志
    // ========================================

    @Operation(summary = "2.8 查询访问日志", description = "支持按用户、动作类型、时间范围过滤")
    @ApiResponse(responseCode = "200", description = "分页日志列表")
    @GetMapping("/logs")
    public Result<PageResult<AccessLogVO>> getLogs(
            @Parameter(description = "用户 ID 过滤（可选）") @RequestParam(required = false) Long userId,
            @Parameter(description = "动作类型过滤（可选）") @RequestParam(required = false) String action,
            @Parameter(description = "开始时间 ISO 8601（可选）") @RequestParam(required = false) String startTime,
            @Parameter(description = "结束时间 ISO 8601（可选）") @RequestParam(required = false) String endTime,
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "20") int size) {
        requireAdmin();
        return Result.ok(adminService.getLogs(userId, action, startTime, endTime, page, size));
    }

    // ========================================
    //  权限校验
    // ========================================

    private Long requireAdmin() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未提供认证凭证");
        }
        if (!UserContext.isAdmin()) {
            throw new BusinessException(403, "需要管理员权限");
        }
        return userId;
    }
}
