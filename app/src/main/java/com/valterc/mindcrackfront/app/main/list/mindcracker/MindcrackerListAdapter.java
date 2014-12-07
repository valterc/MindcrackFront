package com.valterc.mindcrackfront.app.main.list.mindcracker;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.youtube.GetVideoListAsyncTask;
import com.valterc.mindcrackfront.app.youtube.GetVideoListAsyncTask.*;

import java.util.ArrayList;

/**
 * Created by Valter on 28/05/2014.
 */
public class MindcrackerListAdapter extends BaseAdapter implements GetVideoListListener {

    private boolean loading;
    private Context context;
    private ArrayList<MindcrackerListItem> items;
    private Mindcracker mindcracker;
    private String pageToken;

    public MindcrackerListAdapter(Context context, Mindcracker mindcracker) {
        this.context = context;
        this.mindcracker = mindcracker;
        this.items = new ArrayList<>();

        loadMoreVideos();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        if (i < 0 || i > items.size() -1)
            return null;

        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (!loading && i > items.size() - 3)
            if (items.size() != 1 || items.get(0).type != MindcrackerListItem.TYPE_ERROR)
                loadMoreVideos();

        int itemType = getItemViewType(i);

        switch (itemType){
            case MindcrackerListItem.TYPE_AD: return getAdView(i, view, viewGroup);
            case MindcrackerListItem.TYPE_VIDEO: return getVideoView(i, view, viewGroup);
            case MindcrackerListItem.TYPE_LOADING: return getLoadingView(i, view, viewGroup);
            case MindcrackerListItem.TYPE_ERROR: return getErrorView(i, view, viewGroup);
        }

        return null;
    }

    private View getErrorView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(context, R.layout.list_mindcracker_error, null);
        }

        Button button = (Button) view.findViewById(R.id.buttonLoadMoreVideos);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadMoreVideos();
            }
        });

        return view;
    }

    private View getLoadingView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(context, R.layout.list_mindcracker_loading, null);
        }

        return view;
    }

    private View getVideoView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(context, R.layout.list_mindcracker_video, null);

            MindcrackerVideoItemViewHolder viewHolder = new MindcrackerVideoItemViewHolder();
            viewHolder.webImageViewVideoImage = (com.valterc.WebImageView) view.findViewById(R.id.webImageViewVideoImage);
            viewHolder.textViewVideoTitle = (android.widget.TextView) view.findViewById(R.id.textViewVideoTitle);
            view.setTag(viewHolder);
        }

        MindcrackerVideoItemViewHolder viewHolder  = (MindcrackerVideoItemViewHolder) view.getTag();
        SearchResult searchResult = items.get(i).searchResult;

        viewHolder.webImageViewVideoImage.setImageSource(searchResult.getSnippet().getThumbnails().getMedium().getUrl());
        viewHolder.textViewVideoTitle.setText(searchResult.getSnippet().getTitle());

        return view;
    }

    private View getAdView(int i, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(context, R.layout.list_mindcracker_ad, null);

            MindcrackerAdItemViewHolder viewHolder = new MindcrackerAdItemViewHolder();
            viewHolder.adView = (com.mopub.mobileads.MoPubView) view.findViewById(R.id.adview);
            view.setTag(viewHolder);
        }

        MindcrackerAdItemViewHolder viewHolder = (MindcrackerAdItemViewHolder) view.getTag();

        if (viewHolder.adView.getAdUnitId() == null) {
            viewHolder.adView.setAdUnitId("d58184bc052f48b8acebe28345f54bd5");
            viewHolder.adView.loadAd();
        } else {
            viewHolder.adView.forceRefresh();
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {
        return ((MindcrackerListItem)getItem(position)).type;
    }

    @Override
    public boolean isEnabled(int position) {
        return ((MindcrackerListItem)getItem(position)).type == MindcrackerListItem.TYPE_VIDEO;
    }

    @Override
    public void onGetVideoListComplete(SearchListResponse response) {

        if (items.size() > 0 && items.get(items.size() - 1).type == MindcrackerListItem.TYPE_LOADING)
            items.remove(items.size() - 1);

        if (response == null){
            items.add(new MindcrackerListItem(MindcrackerListItem.TYPE_ERROR));
        } else {

            for (int i = 0; i < response.getItems().size(); i++) {
                SearchResult searchResult = response.getItems().get(i);
                items.add(new MindcrackerListItem(searchResult, MindcrackerListItem.TYPE_VIDEO));

                if ((items.size() - items.size() / 5 - 1) % 5 == 0)
                    items.add(new MindcrackerListItem(searchResult, MindcrackerListItem.TYPE_AD));
            }

            this.pageToken = response.getNextPageToken();

        }

        notifyDataSetChanged();
        loading = false;
    }

    private void loadMoreVideos(){

        loading = true;

        if (items.size() > 0 && items.get(items.size() - 1).type == MindcrackerListItem.TYPE_ERROR)
            items.remove(items.size() - 1);

        this.items.add(new MindcrackerListItem(MindcrackerListItem.TYPE_LOADING));
        notifyDataSetChanged();

        GetVideoListInfo videoListInfo = new GetVideoListInfo(this, this.mindcracker.getYoutubeId(), this.pageToken);
        new GetVideoListAsyncTask().execute(videoListInfo);
    }

}
