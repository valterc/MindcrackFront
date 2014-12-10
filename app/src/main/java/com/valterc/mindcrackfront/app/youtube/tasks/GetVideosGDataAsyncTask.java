package com.valterc.mindcrackfront.app.youtube.tasks;

import android.os.AsyncTask;

import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;
import com.valterc.mindcrackfront.app.youtube.YoutubeManager;

import java.util.ArrayList;


/**
 * Created by Valter on 10/12/2014.
 */
public class GetVideosGDataAsyncTask extends AsyncTask<GetVideosGDataAsyncTask.GetVideosGDataInfo, Void, ArrayList<ArrayList<GDataYoutubeVideo>>> {

    @Override
    protected ArrayList<ArrayList<GDataYoutubeVideo>> doInBackground(GetVideosGDataInfo... params) {
        GetVideosGDataInfo info = params[0];
        ArrayList<ArrayList<GDataYoutubeVideo>> usersVideos = new ArrayList<>(info.users.length);

        for (int i = 0; i < info.users.length; i++) {
            ArrayList<GDataYoutubeVideo> userVideos = YoutubeManager.Gdata.GetVideosFromUserGdata(info.users[i]);
            usersVideos.add(userVideos);
        }

        return usersVideos;
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
