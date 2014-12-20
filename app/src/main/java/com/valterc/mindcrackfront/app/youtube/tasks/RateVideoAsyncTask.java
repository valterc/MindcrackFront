package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 19/12/2014.
 */
public class RateVideoAsyncTask extends AsyncTask<RateVideoAsyncTask.RateVideoInfo, Void, String[]> {

    private RateVideoListener listener;

    @Override
    protected String[] doInBackground(RateVideoInfo... params) {
        RateVideoInfo info = params[0];
        this.listener = info.listener;

        try {
            MindcrackFrontApplication.getYoutubeManager().rateVideo(info.videoId, info.rate);
        } catch (IOException e) {
            MindcrackFrontApplication.handleException(e);
            e.printStackTrace();
            return new String[]{info.videoId, null};
        }

        return new String[]{info.videoId, info.rate};
    }

    @Override
    protected void onPostExecute(String[] s) {
        super.onPostExecute(s);
        if (listener != null) {
            listener.onRateVideoComplete(s[0], s[1]);
        }
    }

    public static class RateVideoInfo {
        private RateVideoListener listener;
        private String videoId;
        private String rate;

        public RateVideoInfo(RateVideoListener listener, String videoId, String rate) {
            this.listener = listener;
            this.videoId = videoId;
            this.rate = rate;
        }
    }

    public interface RateVideoListener {
        public void onRateVideoComplete(String videoId, String rate);
    }

}
