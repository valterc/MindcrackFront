package com.valterc.mindcrackfront.app.main.front;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.valterc.WebImageView;
import com.valterc.mindcrackfront.app.R;

import java.util.ArrayList;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MindcrackFrontListItem> items;
    private Typeface typefaceLight;

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
        return 2;
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

        webImageView.setImageSource(item.searchResult.getSnippet().getThumbnails().getMedium().getUrl());
        textView.setText(item.searchResult.getSnippet().getTitle());

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


}
