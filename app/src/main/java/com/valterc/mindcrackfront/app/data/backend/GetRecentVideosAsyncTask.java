package com.valterc.mindcrackfront.app.data.backend;

import android.os.AsyncTask;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.data.MindcrackerVideo;
import com.valterc.mindcrackfront.app.twitch.GetUsersStreamingAsyncTask;
import com.vcutils.DownloadResponse;
import com.vcutils.Downloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Valter on 14/12/2014.
 */
public class GetRecentVideosAsyncTask extends AsyncTask<GetRecentVideosAsyncTask.GetRecentVideosInfo, Void, ArrayList<MindcrackerVideo>> {
    private GetRecentVideosListener listener;

    @Override
    protected ArrayList<MindcrackerVideo> doInBackground(GetRecentVideosInfo... params) {
        GetRecentVideosInfo info = params[0];
        this.listener = info.listener;

        DownloadResponse<String> response = Downloader.downloadGet("http://valterc.com/mcf/recentVideos.php?key=0");

        if (response.getResponse() != null){

            ArrayList<MindcrackerVideo> videos = new ArrayList<>();

            try {
                JSONArray videosJson = new JSONArray(response.getResponse());
                for (int i = 0; i < videosJson.length(); i++) {
                    JSONObject videoJson = videosJson.getJSONObject(i);

                    MindcrackerVideo video = new MindcrackerVideo();
                    video.setYoutubeId(videoJson.getString("id"));
                    video.setTitle(videoJson.getString("title"));
                    video.setThumbnailMediumUrl(videoJson.getString("thumbnail_medium_url"));
                    video.setTitle(videoJson.getString("title"));
                    video.setMindcracker(MindcrackFrontApplication.getDataManager().getMindcrackerYoutubeId(videoJson.getString("user_id")));

                    Date date = null;

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));

                    try {
                        date = dateFormat.parse(videoJson.getString("publish_date"));
                    } catch (ParseException e) {
                        date = Calendar.getInstance().getTime();
                    }

                    video.setPublishDate(date);

                    videos.add(video);
                }
            } catch (JSONException e) {
                return null;
            }

            return videos;
        }

        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<MindcrackerVideo> mindcrackerVideos) {
        super.onPostExecute(mindcrackerVideos);
        if (listener != null){
            listener.onComplete(mindcrackerVideos);
        }
    }

    public static class GetRecentVideosInfo {
        private GetRecentVideosListener listener;
        public GetRecentVideosInfo(GetRecentVideosListener listener){
            this.listener = listener;
        }
    }

    public interface GetRecentVideosListener{
        void onComplete(ArrayList<MindcrackerVideo> videos);
    }
}
