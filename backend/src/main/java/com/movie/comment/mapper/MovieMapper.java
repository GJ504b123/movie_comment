package com.movie.comment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.movie.comment.entity.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Collection;
import java.util.List;

@Mapper
public interface MovieMapper extends BaseMapper<Movie> {

    // ========================================
    //  管理端专用查询：绕过 @TableLogic 自动过滤，
    //  使已软删除（deleted=1）的影片也能在后台"留痕"展示。
    //  sort 取值在 <choose> 中白名单化，无注入风险。
    // ========================================

    @Select("<script>" +
            "SELECT COUNT(*) FROM movie" +
            "<where>" +
            "  <if test=\"keyword != null and keyword != ''\">" +
            "    (title LIKE CONCAT('%', #{keyword}, '%')" +
            "     OR director LIKE CONCAT('%', #{keyword}, '%')" +
            "     OR `cast` LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            "</script>")
    long countAllForAdmin(@Param("keyword") String keyword);

    @Select("<script>" +
            "SELECT * FROM movie" +
            "<where>" +
            "  <if test=\"keyword != null and keyword != ''\">" +
            "    (title LIKE CONCAT('%', #{keyword}, '%')" +
            "     OR director LIKE CONCAT('%', #{keyword}, '%')" +
            "     OR `cast` LIKE CONCAT('%', #{keyword}, '%'))" +
            "  </if>" +
            "</where>" +
            "<choose>" +
            "  <when test=\"sort == 'rating'\"> ORDER BY average_score DESC</when>" +
            "  <when test=\"sort == 'releaseDate'\"> ORDER BY release_date DESC</when>" +
            "  <otherwise> ORDER BY id DESC</otherwise>" +
            "</choose>" +
            " LIMIT #{size} OFFSET #{offset}" +
            "</script>")
    List<Movie> selectPageForAdmin(@Param("keyword") String keyword,
                                   @Param("sort") String sort,
                                   @Param("size") int size,
                                   @Param("offset") int offset);

    /** 按 ID 批量查询（含已软删除影片），供后台评论列表回填影片标题，避免显示"未知影片" */
    @Select("<script>" +
            "SELECT * FROM movie WHERE id IN " +
            "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>" +
            "</script>")
    List<Movie> selectByIdsIncludeDeleted(@Param("ids") Collection<Long> ids);
}
