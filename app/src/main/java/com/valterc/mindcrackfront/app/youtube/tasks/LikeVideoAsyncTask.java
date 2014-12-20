package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.android.gms.auth.GoogleAuthException;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 19/12/2014.
 */
public class LikeVideoAsyncTask extends AsyncTask<LikeVideoAsyncTask.LikeVideoInfo, Void, String> {


    private LikeVideoListener listener;

    @Override
    protected String doInBackground(LikeVideoInfo... params) {
        LikeVideoInfo info = params[0];
        this.listener = info.listener;

        try {
            MindcrackFrontApplication.getYoutubeManager().likeVideo(info.videoId);
        } catch (IOException e) {
            MindcrackFrontApplication.handleException(e);
            e.printStackTrace();
            return null;
        }

        return info.videoId;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (listener != null){
            listener.onLikeVideoComplete(s);
        }
    }

    public static class LikeVideoInfo {
        private LikeVideoListener listener;
        public String videoId;

        public LikeVideoInfo(LikeVideoListener listener, String videoId) {
            this.listener = listener;
            this.videoId = videoId;
        }
    }

    public interface LikeVideoListener {
        public void onLikeVideoComplete(String videoId);
    }

}
