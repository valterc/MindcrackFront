package com.valterc.mindcrackfront.app.youtube;

import android.os.AsyncTask;

import com.google.api.services.youtube.model.SearchListResponse;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 30/05/2014.
 */
public class GetVideoSearchListAsyncTask extends AsyncTask<GetVideoSearchListAsyncTask.GetVideoSearchListInfo, Void, SearchListResponse> {

    private GetVideoListListener listener;

    @Override
    protected SearchListResponse doInBackground(GetVideoSearchListInfo... params) {
        GetVideoSearchListInfo info = params[0];
        this.listener = info.listener;

        try {
            SearchListResponse response = MindcrackFrontApplication.getYoutubeManager().getVideosFromUserSearch(info.userId, info.pageToken);
            return response;
        } catch (IOException e) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(SearchListResponse searchListResponse) {
        super.onPostExecute(searchListResponse);
        listener.onGetVideoListComplete(searchListResponse);
    }


    public static class GetVideoSearchListInfo {
        private GetVideoListListener listener;
        public String userId;
        public String pageToken;

        public GetVideoSearchListInfo(GetVideoListListener listener, String userId, String pageToken) {
            this.listener = listener;
            this.userId = userId;
            this.pageToken = pageToken;
        }
    }

    public interface GetVideoListListener {
        public void onGetVideoListComplete(SearchListResponse response);
    }

}
