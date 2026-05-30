package com.movie.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.comment.entity.AccessLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccessLogMapper extends BaseMapper<AccessLog> {
}
