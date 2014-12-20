package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.api.services.youtube.model.Channel;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 31/05/2014.
 */
public class GetMyChannelAsyncTask extends AsyncTask<GetMyChannelAsyncTask.GetMyChannelInfo, Void, Channel> {

    private GetMyChannelListener listener;

    @Override
    protected Channel doInBackground(GetMyChannelInfo... params) {
        if (params != null && params.length > 0) {
            GetMyChannelInfo info = params[0];
            if (info != null) {
                this.listener = info.listener;
            }
        }

        try {
            Channel response = MindcrackFrontApplication.getYoutubeManager().getMyChannel();
            return response;
        } catch (IOException e) {
            MindcrackFrontApplication.handleException(e);
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Channel channel) {
        super.onPostExecute(channel);
        if (listener != null) {
            listener.onGetMyChannelComplete(channel);
        }
    }

    public static class GetMyChannelInfo {
        private GetMyChannelListener listener;

        public GetMyChannelInfo(GetMyChannelListener listener) {
            this.listener = listener;
        }
    }

    public interface GetMyChannelListener {
        public void onGetMyChannelComplete(Channel response);
    }

}
