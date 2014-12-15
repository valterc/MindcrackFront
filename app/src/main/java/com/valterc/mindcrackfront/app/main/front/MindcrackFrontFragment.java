package com.valterc.mindcrackfront.app.main.front;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.data.MindcrackerVideo;
import com.valterc.mindcrackfront.app.data.backend.GetRecentVideosAsyncTask;
import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideosGDataAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideosPlaylistItemBatchAsyncTask;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontFragment extends Fragment implements GetRecentVideosAsyncTask.GetRecentVideosListener {

    private ListView listView;
    private MindcrackFrontListAdapter listAdapter;
    private View viewStreamingHeader;
    private ArrayList<MindcrackerVideo> recentVideos;
    private ArrayList<MindcrackerVideo> recommendedVideos;


    public MindcrackFrontFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front, null);

        listView = (ListView) view.findViewById(R.id.listView);

        viewStreamingHeader = inflater.inflate(R.layout.list_front_header_streaming, null);
        listView.addHeaderView(viewStreamingHeader);

        listAdapter = new MindcrackFrontListAdapter(getActivity());
        listView.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GetRecentVideosAsyncTask().execute(new GetRecentVideosAsyncTask.GetRecentVideosInfo(this));
    }

    @Override
    public void onComplete(ArrayList<MindcrackerVideo> videos) {
        if (videos != null){
            //TODO: Hide loading view

            processVideos(videos);

        } else {
            //TODO: Show error video
        }
    }

    private void processVideos(ArrayList<MindcrackerVideo> videos){
        processRecentVideos(videos);
        processRecommendedVideos();

        ArrayList<MindcrackFrontListItem> listItems = new ArrayList<>(3 + recentVideos.size() + recommendedVideos.size());

        if (recommendedVideos.size() > 0) {

            listItems.add(new MindcrackFrontListItem("Recommended videos"));

            for (MindcrackerVideo recommendedVideo : recommendedVideos) {
                listItems.add(new MindcrackFrontListItem(recommendedVideo));
            }

            listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
            listItems.add(new MindcrackFrontListItem("Recent videos"));

            for (int i = 0; i < recentVideos.size(); i++) {
                MindcrackerVideo recentVideo = recentVideos.get(i);
                listItems.add(new MindcrackFrontListItem(recentVideo));

                if ((i + 1) % 10 == 0){
                    listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
                }

            }

            for (MindcrackerVideo recentVideo : recentVideos){
                listItems.add(new MindcrackFrontListItem(recentVideo));
            }

        } else {
            listItems.add(new MindcrackFrontListItem("Latest videos"));

            for (int i = 0; i < recentVideos.size(); i++) {
                MindcrackerVideo recentVideo = recentVideos.get(i);
                listItems.add(new MindcrackFrontListItem(recentVideo));

                if ((i + 1) % 10 == 0) {
                    listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
                }

            }
        }


        listAdapter.SetItems(listItems);

    }

    private void processRecentVideos(ArrayList<MindcrackerVideo> videos) {
            recentVideos = new ArrayList<>(videos);
    }

    private void processRecommendedVideos(){

        ArrayList<String> recommendedMindcrackersYoutubeId = MindcrackFrontApplication.getDataManager().getRecommendedMindcrackersYoutubeId();

        recommendedVideos = new ArrayList<>();

        for (MindcrackerVideo video : recentVideos){
            if (recommendedMindcrackersYoutubeId.contains(video.getMindcracker().getYoutubeId())){
                recommendedVideos.add(video);
            }
        }

    }

}
