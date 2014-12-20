package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 19/12/2014.
 */
public class DislikeVideoAsyncTask extends AsyncTask<DislikeVideoAsyncTask.DislikeVideoInfo, Void, String> {

    private DislikeVideoListener listener;

    @Override
    protected String doInBackground(DislikeVideoInfo... params) {
        DislikeVideoInfo info = params[0];
        this.listener = info.listener;

        try {
            MindcrackFrontApplication.getYoutubeManager().dislikeVideo(info.videoId);
        } catch (IOException e) {
            MindcrackFrontApplication.handleException(e);
            return null;
        }

        return info.videoId;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null) {
            listener.onDislikeVideoComplete(s);
        }
    }

    public static class DislikeVideoInfo {
        private DislikeVideoListener listener;
        public String videoId;

        public DislikeVideoInfo(DislikeVideoListener listener, String videoId) {
            this.listener = listener;
            this.videoId = videoId;
        }
    }

    public interface DislikeVideoListener {
        public void onDislikeVideoComplete(String videoId);
    }

}
