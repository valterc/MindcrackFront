package com.valterc.mindcrackfront.app.youtube;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.util.GenericData;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.Video;
import com.google.api.services.youtube.model.VideoListResponse;
import com.vcutils.DownloadResponse;
import com.vcutils.Downloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

        youtube = new YouTube.Builder(transport, jsonFactory, new HttpRequestInitializer() {
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


    public void likeVideo(String videoId) {

    }

    public void dislikeVideo(String videoId) {

    }

    public static class Gdata {

        private final static String GDATA_USER_UPLOADS = "https://gdata.youtube.com/feeds/api/users/%s/uploads?fields=entry(id,title,published,gd:comments,yt:statistics)&max-results=10&alt=json";

        public static ArrayList<GDataYoutubeVideo> GetVideosFromUserGdata(String userId) {

            String gdataUrl = String.format(GDATA_USER_UPLOADS, userId);

            DownloadResponse<String> gdataResponse = Downloader.downloadGet(gdataUrl);

            if (gdataResponse.getResult() != DownloadResponse.DownloadResult.Ok) {
                return null;
            }

            ArrayList<GDataYoutubeVideo> videos = new ArrayList<>();

            try {
                JSONObject jsonResponse = new JSONObject(gdataResponse.getResponse());
                JSONObject feed = jsonResponse.getJSONObject("feed");
                JSONArray entryArray = feed.getJSONArray("entry");

                for (int i = 0; i < entryArray.length(); i++) {
                    JSONObject entry = entryArray.getJSONObject(i);

                    String videoId = entry.getJSONObject("id").getString("$t");
                    videoId = videoId.substring(videoId.lastIndexOf('/') + 1);


                    String videoName = entry.getJSONObject("title").getString("$t");
                    long commentCount = entry.getJSONObject("gd$comments").getJSONObject("gd$feedLink").getLong("countHint");
                    long viewCount = Long.parseLong(entry.getJSONObject("yt$statistics").getString("viewCount"));
                    String publishDateString = entry.getJSONObject("published").getString("$t");

                    //2014-12-09T23:03:33.000Z
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    Date publishDate = null;

                    try {
                        publishDate = dateFormat.parse(publishDateString);
                    } catch (ParseException e) {
                        publishDate = Calendar.getInstance().getTime();
                    }

                    GDataYoutubeVideo youtubeVideo = new GDataYoutubeVideo(videoId, videoName, null, publishDate, viewCount, commentCount);
                    videos.add(youtubeVideo);
                }


            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }

            return videos;
        }

    }

}
