package com.valterc.mindcrackfront.app.main.front;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.api.services.youtube.model.PlaylistItemListResponse;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.youtube.GetVideoPlaylistItemsAsyncTask;
import com.valterc.mindcrackfront.app.youtube.YoutubeManager;
import com.valterc.utils.MultiporpuseAsyncTaskData;
import com.valterc.utils.MultipurposeAsyncTask;

/**
 * Created by Valter on 07/12/2014.
 */
public class MindcrackFrontFragment extends Fragment {

    private ListView listView;
    private View viewStreamingHeader;

    public MindcrackFrontFragment() {

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_front, null);

        listView = (ListView) view.findViewById(R.id.listView);

        viewStreamingHeader = inflater.inflate(R.layout.list_front_header_streaming, null);
        listView.addHeaderView(viewStreamingHeader);

        listView.setAdapter(new MindcrackFrontListAdapter(getActivity()));

        new MultipurposeAsyncTask<Void>(new MultiporpuseAsyncTaskData<Void>() {
            @Override
            public Void runOnBackground() {
                YoutubeManager.Gdata.GetVideosFromUserGdata("UCfhM3yJ8a_sAJj0qOKb40gw");
                return null;
            }

            @Override
            public void onComplete(Void o) {

            }
        }).execute();


        return view;
    }
}
