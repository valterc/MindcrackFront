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
public class MindcrackFrontFragment extends Fragment implements GetVideosGDataAsyncTask.GetVideosGDataListener {

    private ListView listView;
    private MindcrackFrontListAdapter listAdapter;
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

        listAdapter = new MindcrackFrontListAdapter(getActivity());
        listView.setAdapter(listAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        usersToDownloadVideos = new ArrayList<>();
        for (Mindcracker m : MindcrackFrontApplication.getDataManager().getMindcrackers()){
            usersToDownloadVideos.add(m.getYoutubePlaylistId());
        }

        firstLoad = true;
        Toast.makeText(getActivity(), "DOWNLOAD START", Toast.LENGTH_SHORT).show();
        new GetRecentVideosAsyncTask().execute(new GetRecentVideosAsyncTask.GetRecentVideosInfo(new GetRecentVideosAsyncTask.GetRecentVideosListener() {
            @Override
            public void onComplete(ArrayList<MindcrackerVideo> videos) {
                Toast.makeText(getActivity(), "DOWNLOAD COMPLETE", Toast.LENGTH_LONG).show();
            }
        }));

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

        ArrayList<MindcrackFrontListItem> listItems = new ArrayList<>(3 + recentVideos.size() + recommendedVideos.size());

        listItems.add(new MindcrackFrontListItem("Recommended videos"));

        for (GDataYoutubeVideo recommendedVideo : recommendedVideos){
            listItems.add(new MindcrackFrontListItem(recommendedVideo));
        }

        listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
        listItems.add(new MindcrackFrontListItem("Recent videos"));

        for (GDataYoutubeVideo recentVideo : recentVideos){
            listItems.add(new MindcrackFrontListItem(recentVideo));
        }

        listAdapter.SetItems(listItems);

    }

    private void processRecentVideos(ArrayList<ArrayList<GDataYoutubeVideo>> videos) {
        if (recentVideos == null){
            recentVideos = new ArrayList<>(50);
        }

        for (ArrayList<GDataYoutubeVideo> userVideos : videos){
            if (userVideos != null && userVideos.size() > 0) {
                for (GDataYoutubeVideo video : userVideos) {
                    if (Calendar.getInstance().getTimeInMillis() - video.getPublishDate().getTime() < 3600 * 1000 * 48){
                        recentVideos.add(video);
                    }
                }
            }
        }

        Collections.sort(recentVideos, new Comparator<GDataYoutubeVideo>() {
            @Override
            public int compare(GDataYoutubeVideo lhs, GDataYoutubeVideo rhs) {
                return rhs.getPublishDate().compareTo(lhs.getPublishDate());
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

                Calendar lhsCal = Calendar.getInstance();
                lhsCal.setTime(lhs.getPublishDate());
                int lhsDay = lhsCal.get(Calendar.DAY_OF_YEAR);
                int lhsYear = lhsCal.get(Calendar.YEAR);

                Calendar rhsCal = Calendar.getInstance();
                rhsCal.setTime(rhs.getPublishDate());
                int rhsDay = rhsCal.get(Calendar.DAY_OF_YEAR);
                int rhsYear = rhsCal.get(Calendar.YEAR);

                if (lhsDay != rhsDay || lhsYear != rhsYear){
                    return rhs.getPublishDate().compareTo(lhs.getPublishDate());
                }

                if (lhs.getViewCount() > rhs.getViewCount()) {
                    return -1;
                } else if (lhs.getViewCount() < rhs.getViewCount()){
                    return 1;
                }

                return 0;
            }
        });

        recommendedVideos = new ArrayList<>(recommendedVideos.subList(0, 15 > recommendedVideos.size() ? recommendedVideos.size() : 15));
        Collections.shuffle(recommendedVideos);

    }

}
