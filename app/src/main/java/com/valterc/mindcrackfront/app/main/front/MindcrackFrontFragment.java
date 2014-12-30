package com.valterc.mindcrackfront.app.main.front;

import android.content.res.Configuration;
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
import android.widget.HeaderViewListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.MindcrackerVideo;
import com.valterc.mindcrackfront.app.data.backend.GetRecentVideosAsyncTask;
import com.valterc.mindcrackfront.app.main.front.tablet.MindcrackFrontListTabletAdapter;
import com.valterc.mindcrackfront.app.main.front.tablet.MindcrackFrontSectionListItem;
import com.valterc.mindcrackfront.app.main.front.tablet.OnMindcrackItemClickListener;
import com.valterc.mindcrackfront.app.main.video.MindcrackerVideoFragment;

import java.util.ArrayList;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontFragment extends Fragment implements GetRecentVideosAsyncTask.GetRecentVideosListener {

    private ListView listView;
    private View viewLoading;
    private View viewErrorLoading;
    private ArrayList<MindcrackerVideo> recentVideos;
    private ArrayList<MindcrackerVideo> recommendedVideos;
    private Typeface typefaceLight;
    private boolean tabletMode;
    private Boolean anyUserStreaming;

    public MindcrackFrontFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front, null);

        tabletMode = getResources().getBoolean(R.bool.isTablet);

        listView = (ListView) view.findViewById(R.id.listView);
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

        if (!tabletMode) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    MindcrackFrontListItem listItem = (MindcrackFrontListItem) listView.getAdapter().getItem(position);
                    showVideoFragment(listItem.video.getMindcracker().getId(), listItem.video.getYoutubeId());
                }
            });
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GetRecentVideosAsyncTask().execute(new GetRecentVideosAsyncTask.GetRecentVideosInfo(this));
    }

    @Override
    public void onComplete(ArrayList<MindcrackerVideo> videos) {

        if (isDetached() || getActivity() == null){
            return;
        }

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

        if (tabletMode){
            processItemsForTabletLayout();
        } else {
            processItemsForPhoneLayout();
        }

    }

    private void processItemsForTabletLayout() {
        ArrayList<MindcrackFrontSectionListItem> sections = new ArrayList<>(2);

        if (recommendedVideos.size() > 0){

            MindcrackFrontSectionListItem section = new MindcrackFrontSectionListItem();
            section.title = "Recommended videos";
            section.items = new ArrayList<>();

            for (MindcrackerVideo recommendedVideo : recommendedVideos) {
                section.items.add(new MindcrackFrontListItem(recommendedVideo));
            }

            if (MindcrackFrontApplication.getSettings().getShowAds()) {
                section.items.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
            }

            sections.add(section);

            section = new MindcrackFrontSectionListItem();
            section.title = "Recent videos";
            section.items = new ArrayList<>();

            for (int i = 0; i < recentVideos.size(); i++) {
                MindcrackerVideo recentVideo = recentVideos.get(i);
                section.items.add(new MindcrackFrontListItem(recentVideo));

                if (MindcrackFrontApplication.getSettings().getShowAds()) {
                    if ((i + 1) % 10 == 0) {
                        section.items.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
                    }
                }
            }

            sections.add(section);

        } else {

            MindcrackFrontSectionListItem section = new MindcrackFrontSectionListItem();
            section.title = "Latest videos";
            section.items = new ArrayList<>();

            for (int i = 0; i < recentVideos.size(); i++) {
                MindcrackerVideo recentVideo = recentVideos.get(i);
                section.items.add(new MindcrackFrontListItem(recentVideo));

                if (MindcrackFrontApplication.getSettings().getShowAds()) {
                    if ((i + 1) % 10 == 0) {
                        section.items.add(new MindcrackFrontListItem(MindcrackFrontListItem.TYPE_AD));
                    }
                }
            }

            sections.add(section);
        }

        MindcrackFrontListTabletAdapter adapter = new MindcrackFrontListTabletAdapter(getActivity(), sections);
        listView.setAdapter(adapter);

        adapter.setOnMindcrackItemClickListener(new OnMindcrackItemClickListener() {
            @Override
            public void OnItemClick(MindcrackFrontListItem item) {
                showVideoFragment(item.video.getMindcracker().getId(), item.video.getYoutubeId());
            }
        });

        if (anyUserStreaming != null){
            adapter.setUseMarginInFirstItem(!anyUserStreaming);
        }
    }

    private void processItemsForPhoneLayout() {
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

        listView.setAdapter(new MindcrackFrontListAdapter(getActivity(), listItems, false));

        if (anyUserStreaming != null){
            MindcrackFrontListAdapter adapter = (MindcrackFrontListAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
            adapter.setUseMarginInFirstItem(!anyUserStreaming);
        }
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

    private void showVideoFragment(String mindcrackerId, String videoId){
        //TODO: Use interface in MainActivity to open video

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.container, MindcrackerVideoFragment.newInstance(mindcrackerId, videoId, true))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    public void setIsStreamingHeaderVisible(boolean headerVisible) {

        if (listView.getAdapter() == null){
            anyUserStreaming = headerVisible;
            return;
        }

        if (tabletMode){
            MindcrackFrontListTabletAdapter adapter = (MindcrackFrontListTabletAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
            adapter.setUseMarginInFirstItem(!headerVisible);
        } else {
            MindcrackFrontListAdapter adapter = (MindcrackFrontListAdapter) ((HeaderViewListAdapter)listView.getAdapter()).getWrappedAdapter();
            adapter.setUseMarginInFirstItem(!headerVisible);
        }
    }

}
