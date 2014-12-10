package com.valterc.mindcrackfront.app.data;

import java.util.Date;

/**
 * Created by Valter on 29/05/2014.
 */
public class MindcrackerVideo {

    private Mindcracker mindcracker;
    private String title;
    private String description;
    private String youtubeId;
    private String imageUrl;
    private Date publishDate;
    private long commentCount;
    private long viewCount;
    private double rating;
    private Boolean liked;
    private Boolean watched;

    public MindcrackerVideo() {
    }

    public MindcrackerVideo(Mindcracker mindcracker, String title, String description, String youtubeId, String imageUrl, Date publishDate, long commentCount, long viewCount, double rating, Boolean liked, Boolean watched) {
        this.mindcracker = mindcracker;
        this.title = title;
        this.description = description;
        this.youtubeId = youtubeId;
        this.imageUrl = imageUrl;
        this.publishDate = publishDate;
        this.commentCount = commentCount;
        this.viewCount = viewCount;
        this.rating = rating;
        this.liked = liked;
        this.watched = watched;
    }

    public Mindcracker getMindcracker() {
        return mindcracker;
    }

    public void setMindcracker(Mindcracker mindcracker) {
        this.mindcracker = mindcracker;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public Boolean getLiked() {
        return liked;
    }

    public void setLiked(Boolean liked) {
        this.liked = liked;
    }

    public Boolean getWatched() {
        return watched;
    }

    public void setWatched(Boolean watched) {
        this.watched = watched;
    }
}
