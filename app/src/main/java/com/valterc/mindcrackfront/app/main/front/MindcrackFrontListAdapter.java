package com.valterc.mindcrackfront.app.main.front;

import android.content.Context;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mopub.mobileads.MoPubView;
import com.valterc.WebImageView;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.utils.DateFormatter;
import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.TimeZone;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MindcrackFrontListItem> items;
    private Typeface typefaceLight;
    private Boolean useMarginInFirstItem;

    public MindcrackFrontListAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
        typefaceLight = Typeface.createFromAsset(context.getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).type;
    }

    @Override
    public boolean isEnabled(int position) {
        return items.get(position).type == MindcrackFrontListItem.TYPE_VIDEO;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (getItemViewType(position)) {
            case MindcrackFrontListItem.TYPE_TITLE:
                return getTitleView(position, convertView, parent);
            case MindcrackFrontListItem.TYPE_VIDEO:
                return getVideoView(position, convertView, parent);
            case MindcrackFrontListItem.TYPE_AD:
                return getAdView(position, convertView, parent);
        }

        return convertView;
    }

    private View getVideoView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.list_front_video, null);

            VideoItemViewHolder videoItemViewHolder = new VideoItemViewHolder();
            videoItemViewHolder.imageViewUserLogo = (ImageView) view.findViewById(R.id.imageViewUserLogo);
            videoItemViewHolder.webImageViewVideo = (WebImageView) view.findViewById(R.id.webImageViewVideoImage);
            videoItemViewHolder.textViewUserName = (TextView) view.findViewById(R.id.textViewUserName);
            videoItemViewHolder.textViewVideoPublishDate = (TextView) view.findViewById(R.id.textViewVideoPublishDate);
            videoItemViewHolder.textViewVideoTitle = (TextView) view.findViewById(R.id.textViewVideoTitle);

            videoItemViewHolder.textViewVideoTitle.setTypeface(typefaceLight);
            view.setTag(videoItemViewHolder);
        }

        MindcrackFrontListItem item = (MindcrackFrontListItem) getItem(position);
        VideoItemViewHolder videoItemViewHolder = (VideoItemViewHolder) view.getTag();

        videoItemViewHolder.webImageViewVideo.setImageSource(item.video.getThumbnailMediumUrl());
        videoItemViewHolder.imageViewUserLogo.setImageResource(item.video.getMindcracker().getImageResourceId());
        videoItemViewHolder.textViewUserName.setText(item.video.getMindcracker().getName());
        videoItemViewHolder.textViewVideoPublishDate.setText(DateFormatter.format(item.video.getPublishDate()));
        videoItemViewHolder.textViewVideoTitle.setText(item.video.getTitle());

        return view;
    }

    private View getTitleView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.list_front_title, null);

            TextView textView = (TextView) view.findViewById(R.id.textViewTitle);
            textView.setTypeface(typefaceLight);
        }

        MindcrackFrontListItem item = (MindcrackFrontListItem) getItem(position);

        TextView textView = (TextView) view.findViewById(R.id.textViewTitle);
        textView.setText(item.title);

        View viewSpace = view.findViewById(R.id.spaceTitleTop);

        if (position == 0 && useMarginInFirstItem) {
            viewSpace.setVisibility(View.VISIBLE);
        } else {
            viewSpace.setVisibility(View.GONE);
        }

        return view;
    }

    private View getAdView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.list_front_ad, null);

            MoPubView moPubView = (MoPubView) view.findViewById(R.id.adview);
            FrontAdItemViewHolder adViewHolder = new FrontAdItemViewHolder();
            adViewHolder.adView = moPubView;

            view.setTag(adViewHolder);
        }

        FrontAdItemViewHolder adViewHolder = (FrontAdItemViewHolder) view.getTag();

        if (adViewHolder.adView.getAdUnitId() == null) {
            adViewHolder.adView.setAdUnitId("2a8f0f76e9764bf7aecab2c87ea3e187");
            adViewHolder.adView.loadAd();
        } else {
            adViewHolder.adView.forceRefresh();
        }

        return view;
    }


    public void SetItems(ArrayList<MindcrackFrontListItem> items) {
        this.items = items;
        super.notifyDataSetChanged();
    }

    public void setUseMarginInFirstItem(Boolean useMarginInFirstItem) {
        this.useMarginInFirstItem = useMarginInFirstItem;
    }

    private class FrontAdItemViewHolder {
        private MoPubView adView;
    }

    private class VideoItemViewHolder {
        private WebImageView webImageViewVideo;
        private ImageView imageViewUserLogo;
        private TextView textViewUserName;
        private TextView textViewVideoPublishDate;
        private TextView textViewVideoTitle;
    }

}
