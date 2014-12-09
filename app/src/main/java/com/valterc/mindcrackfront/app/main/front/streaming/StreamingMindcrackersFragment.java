package com.valterc.mindcrackfront.app.main.front.streaming;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.twitch.GetUsersStreamingAsyncTask;
import com.valterc.mindcrackfront.app.twitch.GetUsersStreamingAsyncTask.*;
import com.valterc.mindcrackfront.app.twitch.TwitchAPI;
import com.valterc.views.ExpandableGridView;
import com.vcutils.IntentUtils;

/**
 * Created by Valter on 07/12/2014.
 */
public class StreamingMindcrackersFragment extends Fragment implements GetUsersStreamingListener {

    private View view;
    private ExpandableGridView gridView;

    public StreamingMindcrackersFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_streaming, null);

        TextView textView = (TextView) view.findViewById(R.id.textViewStreaming);
        textView.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf"));

        gridView = (ExpandableGridView) view.findViewById(R.id.expandableGridViewStreaming);
        gridView.setExpanded(true);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TwitchAPI.OpenTwitchStream(getActivity(), (String) gridView.getAdapter().getItem(position));
            }
        });

        view.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new GetUsersStreamingAsyncTask().execute(new GetUsersStreamingInfo(this));
    }

    @Override
    public void onGetUsersStreamingComplete(String[] users) {
        if (users != null && users.length > 0){
            gridView.setAdapter(new StreamingMindcrackersAdapter(getActivity(), users));
            view.setVisibility(View.VISIBLE);
        } else {
            gridView.setAdapter(null);
            getView().setVisibility(View.GONE);

            //Test code
            //gridView.setAdapter(new StreamingMindcrackersAdapter(getActivity(), new String[]{"sethbling", "ethotv", "sevadus", "anderzel"}));
            //view.setVisibility(View.VISIBLE);
        }
    }
}
