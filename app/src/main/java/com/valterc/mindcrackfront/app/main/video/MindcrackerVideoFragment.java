package com.valterc.mindcrackfront.app.main.video;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.services.youtube.model.Video;
import com.mopub.mobileads.MoPubView;
import com.valterc.IFragmentBack;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.youtube.YoutubeManager;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideoAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideoAsyncTask.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Valter on 24/05/2014.
 */
public class MindcrackerVideoFragment extends Fragment implements IFragmentBack, YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener, GetVideoAsyncTask.GetVideoListener {

    private static final String MOPUB_VIDEO_AD_ID = "486c4437924d44519385a9818634916e";

    private static final String PARAM_MINDCRACKER_ID = "mindcrackerId";
    private static final String PARAM_VIDEO_ID = "videoId";
    private static final String PARAM_SET_AB_LOGO = "setActionBarLogo";


    private String mindrackerId;
    private String videoId;
    private Typeface typefaceLight;
    private Typeface typefaceNormal;
    private boolean fullscreen;
    private SimpleDateFormat dateFormat;
    private Boolean liked;

    private View playerView;
    private View adViewWrapper;
    private MoPubView adView;
    private View viewLoading;
    private View viewErrorLoading;

    private TextView textViewVideoTitle;
    private TextView textViewVideoPublishDate;
    private TextView textViewVideoViewCount;
    private TextView textViewVideoDescription;
    private LinearLayout linearLayoutLike;
    private LinearLayout linearLayoutDislike;
    private View viewLoadingRating;

    public static MindcrackerVideoFragment newInstance(String mindcrackerId, String videoId, boolean setActionBarLogo) {
        MindcrackerVideoFragment fragment = new MindcrackerVideoFragment();

        Bundle args = new Bundle();
        args.putString(PARAM_MINDCRACKER_ID, mindcrackerId);
        args.putString(PARAM_VIDEO_ID, videoId);
        fragment.setArguments(args);

        return fragment;
    }

    public MindcrackerVideoFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mindrackerId = getArguments().getString(PARAM_MINDCRACKER_ID);
            videoId = getArguments().getString(PARAM_VIDEO_ID);
        } else if (savedInstanceState != null) {
            mindrackerId = savedInstanceState.getString(PARAM_MINDCRACKER_ID);
            videoId = savedInstanceState.getString(PARAM_VIDEO_ID);

            if (mindrackerId == null || videoId == null) {
                OnBackKeyPressed();
            }
        }

        typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        typefaceNormal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");

        if (DateFormat.is24HourFormat(getActivity())) {
            dateFormat = new SimpleDateFormat("dd' of 'MMMM' at 'HH':'mm");
        } else {
            dateFormat = new SimpleDateFormat("dd' of 'MMMM' at 'hh':'mma");
        }
        dateFormat.setTimeZone(TimeZone.getDefault());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);

        YouTubePlayerSupportFragment youTubePlayerFragment =
                (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.fragmentYoutube);
        youTubePlayerFragment.initialize(YoutubeManager.YOUTUBE_ANDROID_KEY, this);
        playerView = youTubePlayerFragment.getView();

        adViewWrapper = view.findViewById(R.id.frameLayoutAd);
        adView = (MoPubView) view.findViewById(R.id.adview);
        View viewCloseAd = view.findViewById(R.id.linearLayoutCloseAd);

        initVideoInfoViews(view);

        viewLoading = view.findViewById(R.id.relativeLayoutLoading);
        viewErrorLoading = view.findViewById(R.id.relativeLayoutErrorLoading);

        Button buttonTryAgain = (Button) view.findViewById(R.id.buttonTryAgain);
        TextView textViewErrorTitle = (TextView) view.findViewById(R.id.textViewErrorTitle);
        TextView textViewLoading = (TextView) view.findViewById(R.id.textViewLoading);

        textViewErrorTitle.setTypeface(typefaceLight);
        textViewLoading.setTypeface(typefaceLight);

        viewCloseAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adViewWrapper.setVisibility(View.GONE);
            }
        });

        buttonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewErrorLoading.setVisibility(View.GONE);
                viewLoading.setVisibility(View.VISIBLE);
                new GetVideoAsyncTask().execute(new GetVideoInfo(videoId, MindcrackerVideoFragment.this));
            }
        });

        adView.setAdUnitId(MOPUB_VIDEO_AD_ID);

        return view;
    }

    private void initVideoInfoViews(View view) {

        textViewVideoTitle = (TextView) view.findViewById(R.id.textViewVideoTitle);
        textViewVideoPublishDate = (TextView) view.findViewById(R.id.textViewVideoPublishDate);
        textViewVideoViewCount = (TextView) view.findViewById(R.id.textViewVideoViewCount);
        textViewVideoDescription = (TextView) view.findViewById(R.id.textViewVideoDescription);
        TextView textViewLike = (TextView) view.findViewById(R.id.textViewLike);

        textViewVideoTitle.setTypeface(typefaceLight);
        textViewVideoPublishDate.setTypeface(typefaceLight);
        textViewVideoViewCount.setTypeface(typefaceLight);
        textViewVideoDescription.setTypeface(typefaceLight);
        textViewLike.setTypeface(typefaceLight);

        linearLayoutLike = (LinearLayout) view.findViewById(R.id.linearLayoutLikeVideo);
        linearLayoutDislike = (LinearLayout) view.findViewById(R.id.linearLayoutDislikeVideo);
        viewLoadingRating = view.findViewById(R.id.relativeLayoutLoadingRating);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GetVideoAsyncTask().execute(new GetVideoInfo(videoId, this));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adView.destroy();

        try {
            YouTubePlayerSupportFragment f = (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.fragmentYoutube);
            if (f != null)
                getFragmentManager().beginTransaction().remove(f).commit();
        } catch (Exception e) {

        }
    }

    @Override
    public void onGetVideoComplete(Video response) {

        viewLoading.setVisibility(View.GONE);

        if (response == null) {
            viewErrorLoading.setVisibility(View.VISIBLE);
            return;
        }

        textViewVideoTitle.setText(response.getSnippet().getTitle());
        textViewVideoPublishDate.setText(dateFormat.format(new Date(response.getSnippet().getPublishedAt().getValue())));

        if (response.getStatistics() != null && response.getStatistics().getViewCount() != null) {
            textViewVideoViewCount.setText(response.getStatistics().getViewCount().toString() + " views");
        } else {
            textViewVideoViewCount.setText("No views");
        }

        textViewVideoDescription.setText(response.getSnippet().getDescription());
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        youTubePlayer.addFullscreenControlFlag(YouTubePlayer.FULLSCREEN_FLAG_CUSTOM_LAYOUT);
        youTubePlayer.setOnFullscreenListener(this);

        int controlFlags = youTubePlayer.getFullscreenControlFlags();
        controlFlags |= YouTubePlayer.FULLSCREEN_FLAG_ALWAYS_FULLSCREEN_IN_LANDSCAPE;
        youTubePlayer.setFullscreenControlFlags(controlFlags);

        if (!wasRestored) {
            youTubePlayer.cueVideo(videoId);
        }

        youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.DEFAULT);
        youTubePlayer.setPlayerStateChangeListener(this);
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }

    @Override
    public void onFullscreen(boolean b) {
        fullscreen = b;
        updateLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateLayout();
    }


    private void updateLayout() {

        RelativeLayout.LayoutParams playerParams =
                (RelativeLayout.LayoutParams) playerView.getLayoutParams();

        if (fullscreen) {
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = RelativeLayout.LayoutParams.MATCH_PARENT;
        } else {
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
        }

        //TODO: Hide action bar and other UI Elements
    }

    @Override
    public boolean OnBackKeyPressed() {
        getFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commitAllowingStateLoss();
        return true;
    }

    @Override
    public void onLoading() {
        Log.d(getClass().getSimpleName(), "onLoading");
    }

    @Override
    public void onLoaded(String s) {
        Log.d(getClass().getSimpleName(), "onLoaded " + s);
    }

    @Override
    public void onAdStarted() {
        Log.d(getClass().getSimpleName(), "onAdStarted");
    }

    @Override
    public void onVideoStarted() {
        Log.d(getClass().getSimpleName(), "onVideoStarted");
    }

    @Override
    public void onVideoEnded() {
        Log.d(getClass().getSimpleName(), "onVideoEnded");
        adViewWrapper.setVisibility(View.VISIBLE);
        adView.loadAd();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.d(getClass().getSimpleName(), "onError " + errorReason.toString());
    }

}
