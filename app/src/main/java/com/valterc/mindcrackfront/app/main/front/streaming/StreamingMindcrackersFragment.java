package com.valterc.mindcrackfront.app.main.front.streaming;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.valterc.mindcrackfront.app.R;
import com.valterc.views.ExpandableGridView;

/**
 * Created by Valter on 07/12/2014.
 */
public class StreamingMindcrackersFragment extends Fragment {

    public StreamingMindcrackersFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_streaming, null);

        ExpandableGridView gridView = (ExpandableGridView) view.findViewById(R.id.expandableGridViewStreaming);

        gridView.setExpanded(true);

        gridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return 50;
            }

            @Override
            public Object getItem(int position) {
                return position;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {

                return View.inflate(getActivity(), R.layout.grid_streaming_mindcracker, null);
            }
        });


        return view;
    }
}
