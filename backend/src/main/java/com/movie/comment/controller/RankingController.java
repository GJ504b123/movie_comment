package com.movie.comment.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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

    @Operation(summary = "3.6 影片排行榜", description = "按影片平均评分降序排列，返回前 N 名")
    @ApiResponse(responseCode = "200", description = "排行榜列表")
    @GetMapping("/rankings")
    public Result<List<RankingItemVO>> rankings(
            @Parameter(description = "返回前几名，默认 10，最大 50") @RequestParam(defaultValue = "10") int limit,
            @Parameter(description = "时间范围：week/month/all，当前仅支持 all") @RequestParam(defaultValue = "all") String timeRange) {
        List<Movie> movies = movieMapper.selectList(
                new LambdaQueryWrapper<Movie>()
                        .orderByDesc(Movie::getAverageScore)
                        .last("LIMIT " + Math.min(limit, 50) + " OFFSET 0"));

        List<RankingItemVO> list = IntStream.range(0, movies.size())
                .mapToObj(i -> RankingItemVO.from(movies.get(i), i + 1))
                .toList();

        return Result.ok(list);
    }
}
