package com.movie.comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.comment.aspect.LogAction;
import com.movie.comment.common.ActionType;
import com.movie.comment.common.Result;
import com.movie.comment.dto.RankingItemVO;
import com.movie.comment.entity.Movie;
import com.movie.comment.mapper.MovieMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.IntStream;

@Tag(name = "排行榜", description = "影片评分排行榜（游客可访问）")
@RestController
@RequestMapping("/api")
public class RankingController {

    private final MovieMapper movieMapper;

    public RankingController(MovieMapper movieMapper) {
        this.movieMapper = movieMapper;
    }

    @Operation(summary = "3.6 影片排行榜", description = "按评分或评论数降序排列，返回前 N 名")
    @ApiResponse(responseCode = "200", description = "排行榜列表")
    @LogAction(ActionType.VIEW_RANKING)
    @GetMapping("/rankings")
    public Result<List<RankingItemVO>> rankings(
            @Parameter(description = "排序维度：rating（均分）/ reviewCount（热度）") @RequestParam(defaultValue = "rating") String sortBy,
            @Parameter(description = "返回前几名，默认 100，最大 100") @RequestParam(defaultValue = "100") int limit) {
        LambdaQueryWrapper<Movie> wrapper = new LambdaQueryWrapper<>();
        if ("reviewCount".equals(sortBy)) {
            wrapper.orderByDesc(Movie::getReviewCount);
        } else {
            wrapper.orderByDesc(Movie::getAverageScore);
        }
        wrapper.last("LIMIT " + Math.min(limit, 100) + " OFFSET 0");

        List<Movie> movies = movieMapper.selectList(wrapper);
        List<RankingItemVO> list = IntStream.range(0, movies.size())
                .mapToObj(i -> RankingItemVO.from(movies.get(i), i + 1))
                .toList();

        return Result.ok(list);
    }
}
