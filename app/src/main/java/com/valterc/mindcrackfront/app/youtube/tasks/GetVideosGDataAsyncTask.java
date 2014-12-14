package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.twitch.GetUsersStreamingAsyncTask;
import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;
import com.valterc.mindcrackfront.app.youtube.YoutubeManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by Valter on 10/12/2014.
 */
public class GetVideosGDataAsyncTask extends AsyncTask<GetVideosGDataAsyncTask.GetVideosGDataInfo, Void, ArrayList<ArrayList<GDataYoutubeVideo>>> {

    private GetVideosGDataListener listener;

    @Override
    protected ArrayList<ArrayList<GDataYoutubeVideo>> doInBackground(GetVideosGDataInfo... params) {
        GetVideosGDataInfo info = params[0];
        this.listener = info.listener;

        ArrayList<ArrayList<GDataYoutubeVideo>> usersVideos = new ArrayList<>(info.users.length);

        long time = Calendar.getInstance().getTimeInMillis();

        for (int i = 0; i < info.users.length; i++) {
            ArrayList<GDataYoutubeVideo> userVideos = YoutubeManager.Gdata.GetVideosFromUserGdata(info.users[i]);
            usersVideos.add(userVideos);
        }

        Log.d(getClass().getSimpleName(), "Elapsed total time: " + (Calendar.getInstance().getTimeInMillis() - time));

        return usersVideos;
    }

    @Override
    protected void onPostExecute(ArrayList<ArrayList<GDataYoutubeVideo>> arrayLists) {
        super.onPostExecute(arrayLists);
        listener.onGetVideoListComplete(arrayLists);
    }

    public static class GetVideosGDataInfo {
        private GetVideosGDataListener listener;
        public String[] users;

        public GetVideosGDataInfo(GetVideosGDataListener listener, String... users) {
            this.listener = listener;
            this.users = users;
        }
    }

    public interface GetVideosGDataListener {
        public void onGetVideoListComplete(ArrayList<ArrayList<GDataYoutubeVideo>> videos);
    }

}
