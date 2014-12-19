package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.api.services.youtube.model.VideoGetRatingResponse;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 19/12/2014.
 */
public class GetVideoRatingAsyncTask extends AsyncTask<GetVideoRatingAsyncTask.GetVideoRatingInfo, Void, VideoGetRatingResponse> {

    private GetVideoRatingListener listener;

    @Override
    protected VideoGetRatingResponse doInBackground(GetVideoRatingInfo... params) {
        GetVideoRatingInfo info = params[0];
        listener = info.listener;

        VideoGetRatingResponse response = null;

        try {
            response = MindcrackFrontApplication.getYoutubeManager().getVideoRating(info.videoId);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    @Override
    protected void onPostExecute(VideoGetRatingResponse response) {
        super.onPostExecute(response);
        if (listener != null){
            listener.onGetVideoRatingComplete(response);
        }
    }

    public static class GetVideoRatingInfo {
        private GetVideoRatingListener listener;
        public String videoId;

        public GetVideoRatingInfo(GetVideoRatingListener listener, String videoId) {
            this.listener = listener;
            this.videoId = videoId;
        }
    }

    public interface GetVideoRatingListener {
        public void onGetVideoRatingComplete(VideoGetRatingResponse response);
    }

}
