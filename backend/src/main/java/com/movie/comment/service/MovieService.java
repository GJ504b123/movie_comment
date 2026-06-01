package com.movie.comment.service;

import com.movie.comment.common.PageResult;
import com.movie.comment.dto.MovieDetailVO;
import com.movie.comment.dto.MovieVO;

import java.util.Map;

/**
 * 影片与评论查询服务
 */
public interface MovieService {
    /**
     * 影片列表（模糊搜索 + 分页 + 排序）
     * @return {list, total, page, size, totalPages} 包装在 PageResult 里，
     *         额外把 reviews 分页通过 result.data.reviews 返回
     */
    PageResult<MovieVO> listMovies(String keyword, int page, int size, String sort);

    /**
     * 影片详情（含评论分页）。
     * @param currentUserId 当前登录用户 id，游客为 null
     * @return Map 包含 "movie" (MovieDetailVO) 和 "reviews" (PageResult<ReviewVO>)
     */
    Map<String, Object> getMovieDetail(Long movieId, int reviewPage, int reviewSize, Long currentUserId);
}
