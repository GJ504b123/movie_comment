package com.movie.comment.dto;

/**
 * 管理员添加影片请求——对齐 apiDesigner.md 2.3
 */
public class AddMovieRequest {
    private String title;
    private String description;
    private String releaseDate;   // "1994-09-23"
    private String coverUrl;
    private String director;
    private String cast;

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
}
