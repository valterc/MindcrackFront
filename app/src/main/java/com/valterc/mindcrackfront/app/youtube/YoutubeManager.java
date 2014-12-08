package com.valterc.mindcrackfront.app.youtube;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;

import java.io.IOException;

/**
 * Created by Valter on 30/05/2014.
 */
public class YoutubeManager {

    public final static String YOUTUBE_ANDROID_KEY = "AIzaSyDIFbSrCkqksheS7xS3lPYcF6vXM7X69JU";
    private final static String YOUTUBE_BROWSER_KEY = "AIzaSyAWyK1n0vTD45qSFTax7SDNpBV-FZFDJaQ";

    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = new AndroidJsonFactory();
    private YouTube youtube;

    public YoutubeManager() {

        youtube  = new YouTube.Builder(transport, jsonFactory, new HttpRequestInitializer() {
            public void initialize(HttpRequest request) throws IOException {
            }
        }).setApplicationName("Mindcrack Front").build();

    }

    public SearchListResponse getVideosFromUserSearch(String userId) throws IOException {
        return getVideosFromUserSearch(userId, null);
    }

    public SearchListResponse getVideosFromUserSearch(String userId, String pageToken) throws IOException {
        YouTube.Search.List search = youtube.search().list("id, snippet");
        search.setKey(YOUTUBE_BROWSER_KEY);
        search.setChannelId(userId);
        search.setOrder("date");
        search.setType("video");
        search.setMaxResults(20L);

        if (pageToken != null)
            search.setPageToken(pageToken);

        return search.execute();
    }

    public PlaylistItemListResponse getVideosFromUserPlaylist(String playlistId, String pageToken) throws IOException {
        YouTube.PlaylistItems.List list = youtube.playlistItems().list("id, snippet");
        list.setKey(YOUTUBE_BROWSER_KEY);
        list.setPlaylistId(playlistId);
        list.setMaxResults(20L);

        if (pageToken != null)
            list.setPageToken(pageToken);

        return list.execute();
    }

    public Video getVideo(String videoId) throws IOException {
        YouTube.Videos.List list = youtube.videos().list("id, snippet, statistics, status");
        list.setKey(YOUTUBE_BROWSER_KEY);
        list.setId(videoId);

        VideoListResponse response = list.execute();
        return response.getItems().size() == 0 ? null : response.getItems().get(0);
    }

    public Channel getChannel(String channelId) throws IOException {
        YouTube.Channels.List list = youtube.channels().list("id, snippet, statistics, brandingSettings");
        list.setKey(YOUTUBE_BROWSER_KEY);
        list.setId(channelId);

        ChannelListResponse response = list.execute();
        return response.getItems().size() == 0 ? null : response.getItems().get(0);
    }

    public void likeVideo(String videoId){

    }

    public void dislikeVideo(String videoId){

    }

}
