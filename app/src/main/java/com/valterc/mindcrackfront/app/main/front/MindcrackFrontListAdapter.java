package com.valterc.mindcrackfront.app.main.front;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<MindcrackFrontListItem> items;

    public MindcrackFrontListAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
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

    private View getVideoView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    private View getTitleView(int position, View convertView, ViewGroup parent) {
        return null;
    }


}
