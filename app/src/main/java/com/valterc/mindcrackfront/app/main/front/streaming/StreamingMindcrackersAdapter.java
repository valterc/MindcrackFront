package com.valterc.mindcrackfront.app.main.front.streaming;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.valterc.WebImageView;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;

/**
 * Created by Valter on 08/12/2014.
 */
public class StreamingMindcrackersAdapter extends BaseAdapter {

    private Context context;
    private String[] items;

    public StreamingMindcrackersAdapter(Context context, String[] users){
        this.context = context;
        this.items = users;
    }

    @Override
    public int getCount() {
        return items.length;
    }

    @Override
    public Object getItem(int position) {
        return items[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        if (view == null) {
            view = View.inflate(context, R.layout.grid_streaming_mindcracker, null);
        }

        Mindcracker user = MindcrackFrontApplication.getDataManager().getMindcrackerTwitchId((String) getItem(position));

        ImageView imaegView = (ImageView) view.findViewById(R.id.imageViewStreamingUserLogo);
        imaegView.setImageResource(user.getImageResourceId());

        return view;
    }
}
