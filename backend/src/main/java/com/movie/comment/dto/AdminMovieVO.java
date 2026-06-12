package com.movie.comment.dto;

import com.movie.comment.entity.Movie;
import java.math.BigDecimal;

/**
 * 管理端影片列表项——比 MovieVO 多出 director/cast/description（编辑回填用）
 * 和 deleted（已下架留痕标记）。对应 GET /api/admin/movies。
 */
public class AdminMovieVO {
    private Long id;
    private String title;
    private String coverUrl;
    private BigDecimal averageScore;
    private Integer reviewCount;
    private String releaseDate;
    private String director;
    private String cast;
    private String description;
    private Boolean deleted;

    public static AdminMovieVO from(Movie m) {
        AdminMovieVO vo = new AdminMovieVO();
        vo.id = m.getId();
        vo.title = m.getTitle();
        vo.coverUrl = m.getCoverUrl();
        vo.averageScore = m.getAverageScore();
        vo.reviewCount = m.getReviewCount();
        vo.releaseDate = m.getReleaseDate() != null ? m.getReleaseDate().toString() : null;
        vo.director = m.getDirector();
        vo.cast = m.getCast();
        vo.description = m.getDescription();
        vo.deleted = m.getDeleted() != null && m.getDeleted() == 1;
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Boolean getDeleted() { return deleted; }
    public void setDeleted(Boolean deleted) { this.deleted = deleted; }
}
