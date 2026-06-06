package com.movie.comment.controller;

import com.movie.comment.aspect.LogAction;
import com.movie.comment.common.ActionType;
import com.movie.comment.common.BusinessException;
import com.movie.comment.common.Result;
import com.movie.comment.dto.CreateReviewRequest;
import com.movie.comment.dto.LikeRequest;
import com.movie.comment.dto.UpdateReviewRequest;
import com.movie.comment.security.UserContext;
import com.movie.comment.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "评论", description = "发表、修改、删除影评（需登录 + 已审核）")
@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "3.3 发表评论/评分", description = "一个用户对一部影片只能评论一次，重复返回 409")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "评论成功，返回 reviewId"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "用户未通过审核"),
            @ApiResponse(responseCode = "404", description = "影片不存在"),
            @ApiResponse(responseCode = "409", description = "已评论过该影片")
    })
    @LogAction(value = ActionType.POST_REVIEW, targetParamName = "movieId")
    @PostMapping("/movies/{movieId}/reviews")
    public Result<Map<String, Long>> createReview(
            @Parameter(description = "影片 ID") @PathVariable Long movieId,
            @Valid @RequestBody CreateReviewRequest req) {
        Long userId = requireLogin();
        Long reviewId = reviewService.createReview(movieId, userId, req.getRating(), req.getComment());
        return Result.created("评论成功", Map.of("reviewId", reviewId));
    }

    @Operation(summary = "3.4 修改自己的评论", description = "只能修改自己发表的评论，rating 和 comment 均可选填")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "只能修改自己的评论"),
            @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @LogAction(value = ActionType.UPDATE_REVIEW, targetParamName = "reviewId")
    @PutMapping("/reviews/{reviewId}")
    public Result<Void> updateReview(
            @Parameter(description = "评论 ID") @PathVariable Long reviewId,
            @RequestBody UpdateReviewRequest req) {
        Long userId = requireLogin();
        reviewService.updateReview(reviewId, userId, req.getRating(), req.getComment());
        return Result.ok("修改成功", null);
    }

    @Operation(summary = "3.5 删除自己的评论", description = "物理删除评论，同时刷新影片评分冗余")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "403", description = "只能删除自己的评论"),
            @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @LogAction(value = ActionType.DELETE_REVIEW, targetParamName = "reviewId")
    @DeleteMapping("/reviews/{reviewId}")
    public Result<Void> deleteReview(
            @Parameter(description = "评论 ID") @PathVariable Long reviewId) {
        Long userId = requireLogin();
        reviewService.deleteReview(reviewId, userId);
        return Result.ok("删除成功", null);
    }

    @Operation(summary = "点赞/取消点赞评论", description = "toggle 模式：liked=true 点赞，liked=false 取消")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "操作成功"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "404", description = "评论不存在")
    })
    @LogAction(value = ActionType.LIKE_REVIEW, targetParamName = "reviewId")
    @PostMapping("/reviews/{reviewId}/like")
    public Result<Void> likeReview(
            @Parameter(description = "评论 ID") @PathVariable Long reviewId,
            @RequestBody LikeRequest req) {
        Long userId = requireLogin();
        // 点赞/取消点赞不校验本人——任何人都可以给别人点赞
        reviewService.likeReview(reviewId, req.isLiked());
        return Result.ok(req.isLiked() ? "点赞成功" : "取消点赞", null);
    }

    private Long requireLogin() {
        Long userId = UserContext.getUserId();
        if (userId == null) {
            throw new BusinessException(401, "未提供认证凭证");
        }
        return userId;
    }
}
