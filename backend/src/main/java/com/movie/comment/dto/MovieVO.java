package com.movie.comment.dto;

import com.movie.comment.entity.Movie;
import java.math.BigDecimal;

/**
 * 影片列表项——对齐 apiDesigner.md 3.1 的 list 元素
 */
public class MovieVO {
    private Long id;
    private String title;
    private String coverUrl;
    private BigDecimal averageScore;
    private Integer reviewCount;
    private String releaseDate;

    public static MovieVO from(Movie m) {
        MovieVO vo = new MovieVO();
        vo.id = m.getId();
        vo.title = m.getTitle();
        vo.coverUrl = m.getCoverUrl();
        vo.averageScore = m.getAverageScore();
        vo.reviewCount = m.getReviewCount();
        vo.releaseDate = m.getReleaseDate() != null ? m.getReleaseDate().toString() : null;
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
}
