package com.movie.comment.controller;

import com.movie.comment.aspect.LogAction;
import com.movie.comment.common.ActionType;
import com.movie.comment.common.PageResult;
import com.movie.comment.common.Result;
import com.movie.comment.dto.MovieVO;
import com.movie.comment.security.UserContext;
import com.movie.comment.service.MovieService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "影片", description = "影片列表搜索、详情浏览（游客可访问）")
@RestController
@RequestMapping("/api")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @Operation(summary = "3.1 查询影片列表", description = "支持关键词模糊搜索（标题/导演/演员）和分页排序")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "分页影片列表"))
    @LogAction(ActionType.SEARCH_MOVIE)
    @GetMapping("/movies")
    public Result<PageResult<MovieVO>> listMovies(
            @Parameter(description = "搜索关键词，模糊匹配标题/导演/演员") @RequestParam(required = false) String keyword,
            @Parameter(description = "页码，从 1 开始") @RequestParam(defaultValue = "1") int page,
            @Parameter(description = "每页条数") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "排序方式：rating（按评分）/ releaseDate（按上映时间）") @RequestParam(required = false) String sort) {
        PageResult<MovieVO> result = movieService.listMovies(keyword, page, size, sort);
        return Result.ok(result);
    }

    @Operation(summary = "3.2 获取影片详情", description = "返回影片完整信息 + 该影片的评论分页列表")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "影片详情 + 评论列表"),
            @ApiResponse(responseCode = "404", description = "影片不存在")
    })
    @LogAction(value = ActionType.VIEW_MOVIE_DETAIL, targetParamName = "movieId")
    @GetMapping("/movies/{movieId}")
    public Result<Map<String, Object>> getMovieDetail(
            @Parameter(description = "影片 ID") @PathVariable Long movieId,
            @Parameter(description = "评论分页页码") @RequestParam(defaultValue = "1") int reviewPage,
            @Parameter(description = "评论每页条数") @RequestParam(defaultValue = "5") int reviewSize) {
        Long currentUserId = UserContext.getUserId();
        Map<String, Object> data = movieService.getMovieDetail(movieId, reviewPage, reviewSize, currentUserId);
        return Result.ok(data);
    }
}
