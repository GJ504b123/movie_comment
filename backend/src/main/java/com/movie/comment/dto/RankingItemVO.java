package com.movie.comment.dto;

import com.movie.comment.entity.Movie;
import java.math.BigDecimal;

/**
 * 排行榜项——对齐 apiDesigner.md 3.6
 */
public class RankingItemVO {
    private int rank;
    private Long movieId;
    private String title;
    private String coverUrl;
    private BigDecimal averageScore;
    private Integer reviewCount;

    public static RankingItemVO from(Movie m, int rank) {
        RankingItemVO vo = new RankingItemVO();
        vo.rank = rank;
        vo.movieId = m.getId();
        vo.title = m.getTitle();
        vo.coverUrl = m.getCoverUrl();
        vo.averageScore = m.getAverageScore();
        vo.reviewCount = m.getReviewCount();
        return vo;
    }

    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
    public Long getMovieId() { return movieId; }
    public void setMovieId(Long movieId) { this.movieId = movieId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getCoverUrl() { return coverUrl; }
    public void setCoverUrl(String coverUrl) { this.coverUrl = coverUrl; }
    public BigDecimal getAverageScore() { return averageScore; }
    public void setAverageScore(BigDecimal averageScore) { this.averageScore = averageScore; }
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
}
