package com.valterc.mindcrackfront.app.data;

import java.util.Date;

/**
 * Created by Valter on 29/05/2014.
 */
public class MindcrackerVideo {
//TODO: Parcelable
    private Mindcracker mindcracker;
    private String title;
    private String description;
    private String youtubeId;
    private String thumbnailMediumUrl;
    private Date publishDate;
    private Boolean liked;
    private Boolean watched;

    public MindcrackerVideo() {
    }

    public MindcrackerVideo(Mindcracker mindcracker, String title, String description, String youtubeId, String thumbnailMediumUrl, Date publishDate, Boolean liked, Boolean watched) {
        this.mindcracker = mindcracker;
        this.title = title;
        this.description = description;
        this.youtubeId = youtubeId;
        this.thumbnailMediumUrl = thumbnailMediumUrl;
        this.publishDate = publishDate;
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

    public String getThumbnailMediumUrl() {
        return thumbnailMediumUrl;
    }

    public void setThumbnailMediumUrl(String thumbnailMediumUrl) {
        this.thumbnailMediumUrl = thumbnailMediumUrl;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
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
