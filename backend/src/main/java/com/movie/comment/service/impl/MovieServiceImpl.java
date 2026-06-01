package com.movie.comment.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.movie.comment.common.BusinessException;
import com.movie.comment.common.PageResult;
import com.movie.comment.dto.MovieDetailVO;
import com.movie.comment.dto.MovieVO;
import com.movie.comment.dto.ReviewVO;
import com.movie.comment.entity.Movie;
import com.movie.comment.entity.Review;
import com.movie.comment.entity.User;
import com.movie.comment.mapper.MovieMapper;
import com.movie.comment.mapper.ReviewMapper;
import com.movie.comment.mapper.UserMapper;
import com.movie.comment.service.MovieService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService {

    private final MovieMapper movieMapper;
    private final ReviewMapper reviewMapper;
    private final UserMapper userMapper;

    public MovieServiceImpl(MovieMapper movieMapper, ReviewMapper reviewMapper, UserMapper userMapper) {
        this.movieMapper = movieMapper;
        this.reviewMapper = reviewMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResult<MovieVO> listMovies(String keyword, int page, int size, String sort) {
        // count wrapper（不带排序，避免 H2 严格模式报错）
        LambdaQueryWrapper<Movie> countWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            countWrapper.and(w -> w.like(Movie::getTitle, keyword)
                    .or().like(Movie::getDirector, keyword)
                    .or().like(Movie::getCast, keyword));
        }
        long total = movieMapper.selectCount(countWrapper);

        // list wrapper（带排序 + 分页）
        LambdaQueryWrapper<Movie> listWrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isBlank()) {
            listWrapper.and(w -> w.like(Movie::getTitle, keyword)
                    .or().like(Movie::getDirector, keyword)
                    .or().like(Movie::getCast, keyword));
        }
        if ("rating".equals(sort)) {
            listWrapper.orderByDesc(Movie::getAverageScore);
        } else if ("releaseDate".equals(sort)) {
            listWrapper.orderByDesc(Movie::getReleaseDate);
        } else {
            listWrapper.orderByDesc(Movie::getId);
        }
        int offset = (page - 1) * size;
        listWrapper.last("LIMIT " + size + " OFFSET " + offset);

        List<MovieVO> list = movieMapper.selectList(listWrapper).stream()
                .map(MovieVO::from)
                .collect(Collectors.toList());

        return new PageResult<>(list, total, page, size);
    }

    @Override
    public Map<String, Object> getMovieDetail(Long movieId, int reviewPage, int reviewSize, Long currentUserId) {
        Movie movie = movieMapper.selectById(movieId);
        if (movie == null || movie.getDeleted() != null && movie.getDeleted() == 1) {
            throw new BusinessException(404, "影片不存在");
        }

        // 查询非隐藏评论（count 不带排序）
        LambdaQueryWrapper<Review> countWrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getMovieId, movieId)
                .eq(Review::getHidden, 0);
        long total = reviewMapper.selectCount(countWrapper);

        // list wrapper（带排序 + 分页）
        LambdaQueryWrapper<Review> listWrapper = new LambdaQueryWrapper<Review>()
                .eq(Review::getMovieId, movieId)
                .eq(Review::getHidden, 0)
                .orderByDesc(Review::getCreateTime);
        int offset = (reviewPage - 1) * reviewSize;
        listWrapper.last("LIMIT " + reviewSize + " OFFSET " + offset);
        List<Review> reviews = reviewMapper.selectList(listWrapper);

        // 批量查用户名
        Set<Long> userIds = reviews.stream()
                .map(Review::getUserId)
                .collect(Collectors.toSet());
        Map<Long, String> usernameMap = new HashMap<>();
        if (!userIds.isEmpty()) {
            List<User> users = userMapper.selectBatchIds(userIds);
            for (User u : users) {
                usernameMap.put(u.getId(), u.getUsername());
            }
        }

        // 组装 ReviewVO
        List<ReviewVO> rvos = reviews.stream()
                .map(r -> {
                    boolean canEdit = currentUserId != null && currentUserId.equals(r.getUserId());
                    return ReviewVO.from(r, usernameMap.getOrDefault(r.getUserId(), "未知用户"), canEdit);
                })
                .collect(Collectors.toList());

        PageResult<ReviewVO> reviewPageResult = new PageResult<>(rvos, total, reviewPage, reviewSize);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("movie", MovieDetailVO.from(movie));
        data.put("reviews", reviewPageResult);
        return data;
    }
}
