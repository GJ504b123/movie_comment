package com.movie.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.comment.entity.Review;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ReviewMapper extends BaseMapper<Review> {
}
