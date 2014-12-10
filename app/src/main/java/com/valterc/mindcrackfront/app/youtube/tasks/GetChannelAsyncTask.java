package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.google.api.services.youtube.model.Channel;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;

import java.io.IOException;

/**
 * Created by Valter on 31/05/2014.
 */
public class GetChannelAsyncTask extends AsyncTask<GetChannelAsyncTask.GetChannelInfo, Void, Channel> {

    private GetChannelListener listener;

    @Override
    protected Channel doInBackground(GetChannelInfo... params) {
        GetChannelInfo info = params[0];
        this.listener = info.listener;

        try {
            Channel response = MindcrackFrontApplication.getYoutubeManager().getChannel(info.channelId);
            return response;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Channel channel) {
        super.onPostExecute(channel);
        listener.onGetChannelComplete(channel);
    }

    public static class GetChannelInfo {
        private GetChannelListener listener;
        public String channelId;

        public GetChannelInfo(GetChannelListener listener, String channelId) {
            this.listener = listener;
            this.channelId = channelId;
        }
    }

    public interface GetChannelListener {
        public void onGetChannelComplete(Channel response);
    }

}
