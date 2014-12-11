package com.valterc.mindcrackfront.app.main.front;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.valterc.WebImageView;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.youtube.GDataYoutubeVideo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MindcrackFrontListItem> items;
    private Typeface typefaceLight;
    private ArrayList<GDataYoutubeVideo> recommendedVideos;
    private ArrayList<GDataYoutubeVideo> recentVideos;

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

            TextView textView = (TextView) view.findViewById(R.id.textViewVideoTitle);
            textView.setTypeface(typefaceLight);
        }

        MindcrackFrontListItem item = (MindcrackFrontListItem) getItem(position);

        WebImageView webImageView = (WebImageView) view.findViewById(R.id.webImageViewVideoImage);
        TextView textView = (TextView) view.findViewById(R.id.textViewVideoTitle);

        webImageView.setImageSource(item.video.getMediumImageUrl());
        textView.setText(item.video.getTitle());

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

        return view;
    }

    private View getAdView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view = View.inflate(context, R.layout.list_front_title, null);


        }

        //TODO: Ad View

        return view;
    }


    public void SetItems(ArrayList<MindcrackFrontListItem> items){
        this.items = items;
        super.notifyDataSetChanged();
    }

}
