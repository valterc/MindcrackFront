package com.valterc.mindcrackfront.app.main.front;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.MindcrackerVideo;
import com.valterc.mindcrackfront.app.data.backend.GetRecentVideosAsyncTask;
import com.valterc.mindcrackfront.app.main.video.MindcrackerVideoFragment;

import java.util.ArrayList;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontFragment extends Fragment implements GetRecentVideosAsyncTask.GetRecentVideosListener {

    private MindcrackFrontListAdapter listAdapter;
    private View viewLoading;
    private View viewErrorLoading;
    private ArrayList<MindcrackerVideo> recentVideos;
    private ArrayList<MindcrackerVideo> recommendedVideos;
    private Typeface typefaceLight;

    public MindcrackFrontFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front, null);

        ListView listView = (ListView) view.findViewById(R.id.listView);
        viewLoading = view.findViewById(R.id.relativeLayoutLoading);
        viewErrorLoading = view.findViewById(R.id.relativeLayoutErrorLoading);

        TextView loadingText = (TextView) view.findViewById(R.id.textViewLoading);
        TextView errorLoadingText = (TextView) view.findViewById(R.id.textViewErrorText);
        TextView errorLoadingTitle = (TextView) view.findViewById(R.id.textViewErrorTitle);
        Button tryAgainButton = (Button) view.findViewById(R.id.buttonTryAgain);

        typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        loadingText.setTypeface(typefaceLight);
        errorLoadingText.setTypeface(typefaceLight);
        errorLoadingTitle.setTypeface(typefaceLight);
        tryAgainButton.setTypeface(typefaceLight);

        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewErrorLoading.setVisibility(View.GONE);
                viewLoading.setVisibility(View.VISIBLE);
                new GetRecentVideosAsyncTask().execute(new GetRecentVideosAsyncTask.GetRecentVideosInfo(MindcrackFrontFragment.this));
            }
        });

        View viewStreamingHeader = inflater.inflate(R.layout.list_front_header_streaming, null);
        listView.addHeaderView(viewStreamingHeader);

        listAdapter = new MindcrackFrontListAdapter(getActivity());
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Position is offset by -1 because the header view counts as 1 item but it is not in this adapter
                MindcrackFrontListItem listItem = (MindcrackFrontListItem) listAdapter.getItem(position - 1);
                FragmentManager fragmentManager = getFragmentManager();

                //TODO: Use interface in MainActivity to open video
                fragmentManager.beginTransaction()
                        .add(R.id.container, MindcrackerVideoFragment.newInstance(listItem.video.getMindcracker().getId(), listItem.video.getYoutubeId(), true))
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                        .commit();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GetRecentVideosAsyncTask().execute(new GetRecentVideosAsyncTask.GetRecentVideosInfo(this));
    }

    @Override
    public void onComplete(ArrayList<MindcrackerVideo> videos) {
        if (videos != null) {
            viewLoading.setVisibility(View.GONE);
            processVideos(videos);
        } else {
            viewErrorLoading.setVisibility(View.VISIBLE);
        }
    }

    private void processVideos(ArrayList<MindcrackerVideo> videos) {
        processRecentVideos(videos);
        processRecommendedVideos();

        ArrayList<MindcrackFrontListItem> listItems = new ArrayList<>(8 + recentVideos.size() + recommendedVideos.size());

        if (recommendedVideos.size() > 0) {

            listItems.add(new MindcrackFrontListItem("Recommended videos"));

            for (MindcrackerVideo recommendedVideo : recommendedVideos) {
                listItems.add(new MindcrackFrontListItem(recommendedVideo));
            }

            if (MindcrackFrontApplication.getSettings().getShowAds()) {
                listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
            }

            listItems.add(new MindcrackFrontListItem("Recent videos"));

            for (int i = 0; i < recentVideos.size(); i++) {
                MindcrackerVideo recentVideo = recentVideos.get(i);
                listItems.add(new MindcrackFrontListItem(recentVideo));

                if (MindcrackFrontApplication.getSettings().getShowAds()) {
                    if ((i + 1) % 10 == 0) {
                        listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
                    }
                }
            }

            for (MindcrackerVideo recentVideo : recentVideos) {
                listItems.add(new MindcrackFrontListItem(recentVideo));
            }

        } else {
            listItems.add(new MindcrackFrontListItem("Latest videos"));

            for (int i = 0; i < recentVideos.size(); i++) {
                MindcrackerVideo recentVideo = recentVideos.get(i);
                listItems.add(new MindcrackFrontListItem(recentVideo));

                if (MindcrackFrontApplication.getSettings().getShowAds()) {
                    if ((i + 1) % 10 == 0) {
                        listItems.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
                    }
                }

            }
        }


        listAdapter.SetItems(listItems);

    }

    private void processRecentVideos(ArrayList<MindcrackerVideo> videos) {
        recentVideos = new ArrayList<>(videos);
    }

    private void processRecommendedVideos() {

        ArrayList<String> recommendedMindcrackersYoutubeId = MindcrackFrontApplication.getDataManager().getRecommendedMindcrackersYoutubeId();

        recommendedVideos = new ArrayList<>();

        for (MindcrackerVideo video : recentVideos) {
            if (recommendedMindcrackersYoutubeId.contains(video.getMindcracker().getYoutubeId())) {
                recommendedVideos.add(video);
            }
        }

    }

    public void setIsStreamingHeaderVisible(boolean headerVisible) {
        listAdapter.setUseMarginInFirstItem(!headerVisible);
        listAdapter.notifyDataSetChanged();
    }

}
