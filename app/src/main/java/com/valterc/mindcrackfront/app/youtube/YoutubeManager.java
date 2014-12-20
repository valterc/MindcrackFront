package com.valterc.mindcrackfront.app.youtube;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.batch.BatchCallback;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.http.HttpHeaders;
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
import com.google.api.services.youtube.model.VideoGetRatingResponse;
import com.google.api.services.youtube.model.VideoListResponse;
import com.valterc.mindcrackfront.app.youtube.tasks.GetMyChannelAsyncTask;
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
import java.util.Collections;
import java.util.Date;

/**
 * Created by Valter on 30/05/2014.
 */
public class YoutubeManager {

    public final static String YOUTUBE_ANDROID_KEY = "AIzaSyDIFbSrCkqksheS7xS3lPYcF6vXM7X69JU";
    private final static String YOUTUBE_BROWSER_KEY = "AIzaSyAWyK1n0vTD45qSFTax7SDNpBV-FZFDJaQ";
    private final static String PREF_ACCOUNT_NAME = "pref_youtube_auth_account_name";

    private Context context;
    private final HttpTransport transport = AndroidHttp.newCompatibleTransport();
    private final JsonFactory jsonFactory = new AndroidJsonFactory();
    private YouTube youtube;
    private GoogleAccountCredential credential;

    public YoutubeManager(Context context) {
        this.context = context;

        HttpRequestInitializer httpRequestInitializer = null;

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.contains(PREF_ACCOUNT_NAME)) {
            Log.d(getClass().getSimpleName(), "Using Account: " + preferences.getString(PREF_ACCOUNT_NAME, null));
            credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton("https://www.googleapis.com/auth/youtube"));
            credential.setSelectedAccountName(preferences.getString(PREF_ACCOUNT_NAME, null));
            httpRequestInitializer = credential;
        } else {
            httpRequestInitializer = new HttpRequestInitializer() {
                public void initialize(HttpRequest request) throws IOException {
                }
            };
        }

        youtube = new YouTube.Builder(transport, jsonFactory, httpRequestInitializer).setApplicationName("Mindcrack Front").build();
    }

    /**
     * Authenticates the user with the selected account name
     * Must run from the UI Thread.
     *
     * @return Authentication status or intent to request account.
     */
    public AuthenticationResult authenticate() {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences.contains(PREF_ACCOUNT_NAME) && preferences.getString(PREF_ACCOUNT_NAME, null) != null) {
            credential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton("https://www.googleapis.com/auth/youtube"));
            credential.setSelectedAccountName(preferences.getString(PREF_ACCOUNT_NAME, null));
            youtube = new YouTube.Builder(transport, jsonFactory, credential).setApplicationName("Mindcrack Front").build();
            return new AuthenticationResult();
        } else {
            this.credential = null;
            GoogleAccountCredential googleAccountCredential = GoogleAccountCredential.usingOAuth2(context, Collections.singleton("https://www.googleapis.com/auth/youtube"));
            return new AuthenticationResult(googleAccountCredential.newChooseAccountIntent());
        }
    }

    /**
     * Authenticates the user with the specified account name
     *
     * @param accountName User account
     */
    public void authenticate(String accountName) {
        if (accountName != null) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences.edit().putString(PREF_ACCOUNT_NAME, accountName).apply();
            authenticate();
            new GetMyChannelAsyncTask().execute((GetMyChannelAsyncTask.GetMyChannelInfo)null);
        }
    }

    public boolean isAuthenticated() {
        return credential != null && credential.getSelectedAccount() != null;
    }

    public void clearAuth() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        preferences.edit().remove(PREF_ACCOUNT_NAME).apply();
        credential = null;

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

    public Channel getMyChannel() throws IOException {
        YouTube.Channels.List list = youtube.channels().list("id");
        list.setKey(YOUTUBE_BROWSER_KEY);
        list.setMine(true);

        ChannelListResponse response = list.execute();
        return response.getItems().size() == 0 ? null : response.getItems().get(0);
    }


    public Channel getChannel(String channelId) throws IOException {
        YouTube.Channels.List list = youtube.channels().list("id, snippet, statistics, brandingSettings");
        list.setKey(YOUTUBE_BROWSER_KEY);
        list.setId(channelId);

        ChannelListResponse response = list.execute();
        return response.getItems().size() == 0 ? null : response.getItems().get(0);
    }


    public ArrayList<PlaylistItemListResponse> batchGetVideosFromPlaylists(ArrayList<String> playlists) throws IOException {

        final ArrayList<PlaylistItemListResponse> batchResponse = new ArrayList<>(playlists.size());
        BatchRequest batch = youtube.batch();

        for (int i = 0; i < playlists.size(); i++) {
            YouTube.PlaylistItems.List list = youtube.playlistItems().list("id, snippet");
            list.setKey(YOUTUBE_BROWSER_KEY);
            list.setPlaylistId(playlists.get(i));
            list.setMaxResults(3L);

            batch.queue(list.buildHttpRequest(), PlaylistItemListResponse.class, PlaylistItemListResponse.class, new BatchCallback<PlaylistItemListResponse, PlaylistItemListResponse>() {
                @Override
                public void onSuccess(PlaylistItemListResponse playlistItemListResponse, HttpHeaders httpHeaders) throws IOException {
                    batchResponse.add(playlistItemListResponse);
                }

                @Override
                public void onFailure(PlaylistItemListResponse playlistItemListResponse, HttpHeaders httpHeaders) throws IOException {

                }
            });
        }

        batch.execute();

        return batchResponse;
    }

    public void likeVideo(String videoId) throws IOException {
        YouTube.Videos.Rate rate = youtube.videos().rate(videoId, "like");
        rate.setKey(YOUTUBE_BROWSER_KEY);
        rate.execute();
    }

    public void dislikeVideo(String videoId) throws IOException {
        YouTube.Videos.Rate rate = youtube.videos().rate(videoId, "dislike");
        rate.setKey(YOUTUBE_BROWSER_KEY);
        rate.execute();
    }

    public void rateVideo(String videoId, String rateString) throws IOException {
        if (TextUtils.isEmpty(rateString))
            throw new IllegalArgumentException("rateString cannot be null");

        if (!rateString.equals("like") && !rateString.equals("dislike") && !rateString.equals("none"))
            throw new IllegalArgumentException("rateString must be either 'like', 'dislike' or 'none'");

        YouTube.Videos.Rate rate = youtube.videos().rate(videoId, rateString);
        rate.setKey(YOUTUBE_BROWSER_KEY);
        rate.execute();
    }

    public String getVideoRating(String videoId) throws IOException {
        YouTube.Videos.GetRating rating = youtube.videos().getRating(videoId);
        rating.setKey(YOUTUBE_BROWSER_KEY);
        VideoGetRatingResponse response = rating.execute();
        return response.getItems().size() > 0 ? response.getItems().get(0).getRating() : null;
    }

    public static class Gdata {

        private final static String GDATA_USER_UPLOADS = "https://gdata.youtube.com/feeds/api/users/%s/uploads?fields=entry(id,title,published,gd:comments,yt:statistics)&max-results=5&alt=json";

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

                    GDataYoutubeVideo youtubeVideo = new GDataYoutubeVideo(videoId, videoName, null, userId, publishDate, viewCount, commentCount);
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
