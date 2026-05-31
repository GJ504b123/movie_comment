package com.movie.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.comment.entity.Movie;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface MovieMapper extends BaseMapper<Movie> {
}
