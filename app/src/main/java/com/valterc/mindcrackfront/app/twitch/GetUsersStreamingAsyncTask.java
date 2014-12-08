package com.valterc.mindcrackfront.app.twitch;

import android.os.AsyncTask;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.data.Mindcracker;

import java.util.ArrayList;

/**
 * Created by Valter on 08/12/2014.
 */
public class GetUsersStreamingAsyncTask extends AsyncTask<GetUsersStreamingAsyncTask.GetUsersStreamingInfo, Void, String[]> {

    private GetUsersStreamingListener listener;

    @Override
    protected String[] doInBackground(GetUsersStreamingInfo... params) {
        this.listener = params[0].listener;

        ArrayList<Mindcracker> mindcrackers = MindcrackFrontApplication.getDataManager().getMindcrackers();
        String[] usersTwitchIds = new String[mindcrackers.size()];

        for (int i = 0; i < usersTwitchIds.length; i++) {
            usersTwitchIds[i] = mindcrackers.get(i).getTwitchId();
        }

        return TwitchAPI.GetUsersStreaming(usersTwitchIds);
    }

    @Override
    protected void onPostExecute(String[] users) {
        super.onPostExecute(users);
        if (listener != null){
            listener.onGetUsersStreamingComplete(users);
        }
    }

    public static class GetUsersStreamingInfo {

        private GetUsersStreamingListener listener;

        public GetUsersStreamingInfo(GetUsersStreamingListener listener) {
            this.listener = listener;
        }

    }

    public interface GetUsersStreamingListener {

        /**
         * Delivers the result of the AsyncTask
         * @param users NULL on error, Twitch id of the users currently streaming
         */
        public void onGetUsersStreamingComplete(String[] users);
    }

}
