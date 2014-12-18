package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.api.services.youtube.model.Video;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 31/05/2014.
 */
public class GetVideoAsyncTask extends AsyncTask<GetVideoAsyncTask.GetVideoInfo, Void, Video> {

    private GetVideoListener listener;

    @Override
    protected Video doInBackground(GetVideoInfo... params) {
        GetVideoInfo info = params[0];
        this.listener = info.listener;

        try {
            Video response = MindcrackFrontApplication.getYoutubeManager().getVideo(info.videoId);
            return response;
        } catch (IOException e) {
        }

        return null;
    }

    @Override
    protected void onPostExecute(Video video) {
        super.onPostExecute(video);
        if (listener != null) {
            listener.onGetVideoComplete(video);
        }
    }


    public static class GetVideoInfo {
        private GetVideoListener listener;
        public String videoId;

        public GetVideoInfo(String videoId, GetVideoListener listener){
            this.videoId = videoId;
            this.listener = listener;
        }

    }

    public interface GetVideoListener {
        public void onGetVideoComplete(Video response);
    }

}
