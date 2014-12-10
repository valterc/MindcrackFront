package com.valterc.mindcrackfront.app.youtube;

import java.util.Date;

/**
 * Created by Valter on 10/12/2014.
 */
public class GDataYoutubeVideo {

    private String id;
    private String name;
    private String description;
    private String userId;
    private Date publishDate;
    private long viewCount;
    private long commentCount;

    public GDataYoutubeVideo(String id, String name, String description, String userId, Date publishDate, long viewCount, long commentCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.userId = userId;
        this.publishDate = publishDate;
        this.viewCount = viewCount;
        this.commentCount = commentCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(long commentCount) {
        this.commentCount = commentCount;
    }
}
