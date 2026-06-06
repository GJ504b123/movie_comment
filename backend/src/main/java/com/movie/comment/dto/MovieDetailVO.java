package com.movie.comment.dto;

import com.movie.comment.entity.Movie;
import java.math.BigDecimal;

/**
 * 影片详情——对齐 apiDesigner.md 3.2 的 movie 节点
 */
public class MovieDetailVO {
    private Long id;
    private String title;
    private String description;
    private String releaseDate;
    private String coverUrl;
    private String director;
    private String cast;
    private BigDecimal averageScore;
    private Integer reviewCount;

    public static MovieDetailVO from(Movie m) {
        MovieDetailVO vo = new MovieDetailVO();
        vo.id = m.getId();
        vo.title = m.getTitle();
        vo.description = m.getDescription();
        vo.releaseDate = m.getReleaseDate() != null ? m.getReleaseDate().toString() : null;
        vo.coverUrl = m.getCoverUrl();
        vo.director = m.getDirector();
        vo.cast = m.getCast();
        vo.averageScore = m.getAverageScore();
        vo.reviewCount = m.getReviewCount();
        return vo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}
