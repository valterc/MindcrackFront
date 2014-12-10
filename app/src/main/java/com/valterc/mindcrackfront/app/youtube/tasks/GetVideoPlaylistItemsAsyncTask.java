package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 30/05/2014.
 */
public class GetVideoPlaylistItemsAsyncTask extends AsyncTask<GetVideoPlaylistItemsAsyncTask.GetVideoPlaylistItemsInfo, Void, PlaylistItemListResponse> {

    private GetVideoPlaylistItemsListener listener;

    @Override
    protected PlaylistItemListResponse doInBackground(GetVideoPlaylistItemsInfo... params) {
        GetVideoPlaylistItemsInfo info = params[0];
        this.listener = info.listener;

        try {
            PlaylistItemListResponse response = MindcrackFrontApplication.getYoutubeManager().getVideosFromUserPlaylist(info.playlistId, info.pageToken);
            return response;
        } catch (IOException e) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(PlaylistItemListResponse playlistItemListResponse) {
        super.onPostExecute(playlistItemListResponse);
        listener.onGetVideoListComplete(playlistItemListResponse);
    }


    public static class GetVideoPlaylistItemsInfo {
        private GetVideoPlaylistItemsListener listener;
        public String playlistId;
        public String pageToken;

        public GetVideoPlaylistItemsInfo(GetVideoPlaylistItemsListener listener, String playlistId, String pageToken) {
            this.listener = listener;
            this.playlistId = playlistId;
            this.pageToken = pageToken;
        }
    }

    public interface GetVideoPlaylistItemsListener {
        public void onGetVideoListComplete(PlaylistItemListResponse response);
    }

}
