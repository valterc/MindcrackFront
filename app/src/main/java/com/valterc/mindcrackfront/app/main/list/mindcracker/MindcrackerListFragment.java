package com.valterc.mindcrackfront.app.main.list.mindcracker;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HeaderViewListAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.api.services.youtube.model.Channel;
import com.valterc.WebImageView;
import com.valterc.data.download.DownloadImageAsyncTask;
import com.valterc.data.download.DownloadImageListener;
import com.valterc.data.download.DownloadImageRequest;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.main.actionbar.MindcrackActionBarFragment;
import com.valterc.mindcrackfront.app.youtube.tasks.GetChannelAsyncTask;

import static com.valterc.mindcrackfront.app.youtube.tasks.GetChannelAsyncTask.*;

/**
 * Created by Valter on 24/05/2014.
 */
public class MindcrackerListFragment extends Fragment implements GetChannelListener, AdapterView.OnItemClickListener {

    private static final String PARAM_MINDCRACKER_ID = "mindcrackerId";

    public static MindcrackerListFragment newInstance(String mindcrackerId) {
        MindcrackerListFragment f = new MindcrackerListFragment();

        Bundle args = new Bundle();
        args.putString(PARAM_MINDCRACKER_ID, mindcrackerId);
        f.setArguments(args);

        return f;
    }

    private MindcrackActionBarFragment mindcrackActionBarFragment;
    private MindcrackerListListener listener;
    private String mindcrackerId;
    private Mindcracker mindcracker;
    private Channel channel;
    private Typeface typefaceLight;

    private ListView listView;
    private View viewLoading;
    private View viewErrorLoading;
    private View headerView;

    public MindcrackerListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mindcracker, null);

        listView = (ListView) view.findViewById(R.id.listView);
        viewLoading = view.findViewById(R.id.relativeLayoutLoading);
        viewErrorLoading = view.findViewById(R.id.relativeLayoutErrorLoading);

        typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        TextView loadingText = (TextView) view.findViewById(R.id.textViewLoading);
        TextView errorLoadingText = (TextView) view.findViewById(R.id.textViewErrorText);
        TextView errorLoadingTitle = (TextView) view.findViewById(R.id.textViewErrorTitle);
        Button buttonTryAgain = (Button) view.findViewById(R.id.buttonTryAgain);

        loadingText.setTypeface(typefaceLight);
        errorLoadingText.setTypeface(typefaceLight);
        errorLoadingTitle.setTypeface(typefaceLight);
        buttonTryAgain.setTypeface(typefaceLight);

        buttonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewErrorLoading.setVisibility(View.GONE);
                viewLoading.setVisibility(View.VISIBLE);

                HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listView.getAdapter();
                MindcrackerListAdapter mindcrackerListAdapter = (MindcrackerListAdapter) headerViewListAdapter.getWrappedAdapter();
                mindcrackerListAdapter.RetryLoadVideos();

                GetChannelInfo channelInfo = new GetChannelInfo(MindcrackerListFragment.this, mindcracker.getYoutubeId());
                new GetChannelAsyncTask().execute(channelInfo);
            }
        });

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(PARAM_MINDCRACKER_ID)) {
            mindcrackerId = savedInstanceState.getString(PARAM_MINDCRACKER_ID);
        } else {
            if (getArguments().containsKey(PARAM_MINDCRACKER_ID)) {
                mindcrackerId = getArguments().getString(PARAM_MINDCRACKER_ID);
            } else {
                Log.e(MindcrackerListFragment.class.getSimpleName(), "No MincrackerId param!");
                //TODO: Show main fragment
                return;
            }
        }

        this.mindcrackActionBarFragment = (MindcrackActionBarFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMindcrackActionBar);
        this.mindcracker = MindcrackFrontApplication.getDataManager().getMindcracker(mindcrackerId);

        GetChannelInfo channelInfo = new GetChannelInfo(this, this.mindcracker.getYoutubeId());
        new GetChannelAsyncTask().execute(channelInfo);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PARAM_MINDCRACKER_ID, mindcrackerId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.headerView = getLayoutInflater(savedInstanceState).inflate(R.layout.list_mindcracker_header, null);

        listView.addHeaderView(headerView);
        listView.setAdapter(new MindcrackerListAdapter(getActivity(), this.mindcracker));
        listView.setOnItemClickListener(this);
    }

    @Override
    public void onGetChannelComplete(Channel response) {

        viewLoading.setVisibility(View.GONE);

        if (response == null) {
            viewErrorLoading.setVisibility(View.VISIBLE);
            return;
        }

        setUpFavoriteButton();

        this.channel = response;

        String bannerImageUrl = this.channel.getBrandingSettings().getImage().getBannerMobileHdImageUrl();

        if (bannerImageUrl == null) {
            bannerImageUrl = this.channel.getBrandingSettings().getImage().getBannerImageUrl();
        }

        WebImageView webImageView = (WebImageView) this.headerView.findViewById(R.id.webImageViewHeader);
        webImageView.setImageSource(bannerImageUrl);

        new DownloadImageAsyncTask().execute(new DownloadImageRequest(this.channel.getSnippet().getThumbnails().getMedium().getUrl(), new DownloadImageListener() {
            @Override
            public void OnDownloadComplete(String imageUrl, boolean error, Bitmap bitmap) {
                if (!error){
                    mindcrackActionBarFragment.setCenterImage(bitmap);
                }
            }
        }));

        TextView textViewName = (TextView) this.headerView.findViewById(R.id.textViewName);
        textViewName.setText(this.channel.getSnippet().getTitle());

    }

    private void setUpFavoriteButton() {

        mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);

        if (MindcrackFrontApplication.getDataManager().isMindcrackerFavorite(mindcracker)){
            mindcrackActionBarFragment.setRightImageResource(R.drawable.heart);
            mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
        } else {
            mindcrackActionBarFragment.setRightImageResource(R.drawable.heart_sad);
            mindcrackActionBarFragment.setRightImageInAnimation(R.anim.beat_fade_in);
        }

        mindcrackActionBarFragment.setRightImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MindcrackFrontApplication.getDataManager().isMindcrackerFavorite(mindcracker)){
                    mindcrackActionBarFragment.setRightImageResource(R.drawable.heart_sad);
                    mindcrackActionBarFragment.setRightImageInAnimation(R.anim.beat_fade_in);
                    MindcrackFrontApplication.getDataManager().removeFavorite(mindcracker);
                } else {
                    MindcrackFrontApplication.getDataManager().addFavorite(mindcracker);
                    mindcrackActionBarFragment.setRightImageResource(R.drawable.heart, true);
                    mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
                }
            }
        });

        mindcrackActionBarFragment.setRightImageOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Favorite", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }

    @Override
    public void onDetach() {
        mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
        mindcrackActionBarFragment.setRightImageOnClickListener(null);
        mindcrackActionBarFragment.setRightImageOnLongClickListener(null);
        mindcrackActionBarFragment.setRightImageResource(0);
        mindcrackActionBarFragment.resetCenterImage();
        super.onDetach();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        listener = (MindcrackerListListener) activity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ListAdapter adapter = listView.getAdapter();
        MindcrackerListItem item = (MindcrackerListItem) adapter.getItem(position);

        if (item != null && listener != null) {
            listener.onVideoSelected(mindcrackerId, item.playlistItem.getSnippet().getResourceId().getVideoId());
        }
    }
}
