package com.valterc.mindcrackfront.app.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by Valter on 29/05/2014.
 */
public class MindcrackerVideo implements Parcelable {

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

    protected MindcrackerVideo(Parcel in) {
        mindcracker = (Mindcracker) in.readValue(Mindcracker.class.getClassLoader());
        title = in.readString();
        description = in.readString();
        youtubeId = in.readString();
        thumbnailMediumUrl = in.readString();
        long tmpPublishDate = in.readLong();
        publishDate = tmpPublishDate != -1 ? new Date(tmpPublishDate) : null;
        byte likedVal = in.readByte();
        liked = likedVal == 0x02 ? null : likedVal != 0x00;
        byte watchedVal = in.readByte();
        watched = watchedVal == 0x02 ? null : watchedVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mindcracker);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(youtubeId);
        dest.writeString(thumbnailMediumUrl);
        dest.writeLong(publishDate != null ? publishDate.getTime() : -1L);
        if (liked == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (liked ? 0x01 : 0x00));
        }
        if (watched == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (watched ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MindcrackerVideo> CREATOR = new Parcelable.Creator<MindcrackerVideo>() {
        @Override
        public MindcrackerVideo createFromParcel(Parcel in) {
            return new MindcrackerVideo(in);
        }

        @Override
        public MindcrackerVideo[] newArray(int size) {
            return new MindcrackerVideo[size];
        }
    };

}
