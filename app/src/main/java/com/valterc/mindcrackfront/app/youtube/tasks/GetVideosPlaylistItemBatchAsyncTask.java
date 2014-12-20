package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Valter on 13/12/2014.
 */
public class GetVideosPlaylistItemBatchAsyncTask extends AsyncTask<GetVideosPlaylistItemBatchAsyncTask.GetVideosPlaylistItemBatchInfo, Void, ArrayList<PlaylistItemListResponse>> {

    private GetVideosPlaylistItemBatchListener listener;

    @Override
    protected ArrayList<PlaylistItemListResponse> doInBackground(GetVideosPlaylistItemBatchInfo... params) {
        GetVideosPlaylistItemBatchInfo info = params[0];
        this.listener = info.listener;

        ArrayList<PlaylistItemListResponse> responseArrayList = null;

        try {
            responseArrayList = MindcrackFrontApplication.getYoutubeManager().batchGetVideosFromPlaylists(info.playlists);
        } catch (IOException e) {
            MindcrackFrontApplication.handleException(e);
            e.printStackTrace();
        }

        return responseArrayList;
    }

    @Override
    protected void onPostExecute(ArrayList<PlaylistItemListResponse> playlistItemListResponses) {
        super.onPostExecute(playlistItemListResponses);
        if (listener != null){
            listener.onGetVideoListComplete(playlistItemListResponses);
        }
    }

    public static class GetVideosPlaylistItemBatchInfo {
        private GetVideosPlaylistItemBatchListener listener;
        public ArrayList<String> playlists;

        public GetVideosPlaylistItemBatchInfo(GetVideosPlaylistItemBatchListener listener, ArrayList<String> playlists) {
            this.listener = listener;
            this.playlists = playlists;
        }
    }

    public interface GetVideosPlaylistItemBatchListener {
        public void onGetVideoListComplete(ArrayList<PlaylistItemListResponse> batchResponse);
    }

}
