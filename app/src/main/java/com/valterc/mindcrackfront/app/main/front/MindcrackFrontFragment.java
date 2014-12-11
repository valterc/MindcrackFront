package com.valterc.mindcrackfront.app.main.front;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideosGDataAsyncTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontFragment extends Fragment implements GetVideosGDataAsyncTask.GetVideosGDataListener {

    private ListView listView;
    private View viewStreamingHeader;
    private ArrayList<String> usersToDownloadVideos;
    private ArrayList<GDataYoutubeVideo> recentVideos;
    private ArrayList<GDataYoutubeVideo> recommendedVideos;
    private boolean firstLoad;
    private int tryCount;


    public MindcrackFrontFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front, null);

        listView = (ListView) view.findViewById(R.id.listView);

        viewStreamingHeader = inflater.inflate(R.layout.list_front_header_streaming, null);
        listView.addHeaderView(viewStreamingHeader);
        listView.setAdapter(new MindcrackFrontListAdapter(getActivity()));

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersToDownloadVideos = MindcrackFrontApplication.getDataManager().getMindcrackersYoutubeId();
        firstLoad = true;
        new GetVideosGDataAsyncTask().execute(new GetVideosGDataAsyncTask.GetVideosGDataInfo(this, usersToDownloadVideos.toArray(new String[usersToDownloadVideos.size()])));
    }

    @Override
    public void onGetVideoListComplete(ArrayList<ArrayList<GDataYoutubeVideo>> videos) {

        boolean anyData = false;

        for (ArrayList<GDataYoutubeVideo> userVideos : videos){
            if (userVideos != null && userVideos.size() > 0) {
                usersToDownloadVideos.remove(userVideos.get(0).getUserId());
                anyData = true;
            }
        }

        if (!anyData && firstLoad){
            //TODO: Show error view
            return;
        }

        firstLoad = false;

        //TODO: Do this operation on a background thread?
        processVideos(videos);

        //TODO: Hide loading view

        if (tryCount < 5 && !usersToDownloadVideos.isEmpty()){
            tryCount++;
            new GetVideosGDataAsyncTask().execute(new GetVideosGDataAsyncTask.GetVideosGDataInfo(this, (String[]) usersToDownloadVideos.toArray()));
        }

    }

    private void processVideos(ArrayList<ArrayList<GDataYoutubeVideo>> videos){
        processRecentVideos(videos);
        processRecommendedVideos();
    }

    private void processRecentVideos(ArrayList<ArrayList<GDataYoutubeVideo>> videos) {
        if (recentVideos == null){
            recentVideos = new ArrayList<>(50);
        }

        for (ArrayList<GDataYoutubeVideo> userVideos : videos){
            if (userVideos != null && userVideos.size() > 0) {
                recentVideos.addAll(userVideos);
            }
        }

        Collections.sort(recentVideos, new Comparator<GDataYoutubeVideo>() {
            @Override
            public int compare(GDataYoutubeVideo lhs, GDataYoutubeVideo rhs) {
                return lhs.getPublishDate().compareTo(rhs.getPublishDate());
            }
        });
    }

    private void processRecommendedVideos(){

        ArrayList<String> recommendedMindcrackersYoutubeId = MindcrackFrontApplication.getDataManager().getRecommendedMindcrackersYoutubeId();

        recommendedVideos = new ArrayList<>();

        for (GDataYoutubeVideo video : recentVideos){
            if (recommendedMindcrackersYoutubeId.contains(video.getUserId())){
                recommendedVideos.add(video);
            }
        }

        Collections.sort(recommendedVideos, new Comparator<GDataYoutubeVideo>() {
            @Override
            public int compare(GDataYoutubeVideo lhs /*-1*/, GDataYoutubeVideo rhs/*-1*/) {

                //TODO: Order by date, view count, rating?

                return 0;
            }
        });

        recommendedVideos = new ArrayList<>(recommendedVideos.subList(0, 10 > recommendedVideos.size() ? recommendedVideos.size() : 15));
        Collections.shuffle(recommendedVideos);

    }

}
