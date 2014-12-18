package com.valterc.mindcrackfront.app.main.video;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

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

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MindcrackerVideoFragment.MindcrackerVideoFragmentListener} interface
 * to handle interaction events.
 * Use the {@link MindcrackerVideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MindcrackerVideoFragment extends Fragment implements IFragmentBack, YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener, GetVideoAsyncTask.GetVideoListener {

    private static final String PARAM_MINDCRACKER_ID = "mindcrackerId";
    private static final String PARAM_VIDEO_ID = "videoId";
    private static final String MOPUB_VIDEO_AD_ID = "486c4437924d44519385a9818634916e";

    private String mindrackerId;
    private String videoId;
    private Typeface typefaceLight;
    private Typeface typefaceNormal;
    private boolean fullscreen;

    private View playerView;
    private View adViewWrapper;
    private MoPubView adView;


    private MindcrackerVideoFragmentListener mListener;


    public static MindcrackerVideoFragment newInstance(String mindcrackerId, String videoId) {
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

        return view;
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MindcrackerVideoFragmentListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onGetVideoComplete(Video response) {

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
        adView.setAdUnitId(MOPUB_VIDEO_AD_ID);
        adView.loadAd();
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.d(getClass().getSimpleName(), "onError " + errorReason.toString());
    }

    public interface MindcrackerVideoFragmentListener {
        public void returnToMindrackerList(String mindcrackerId);
    }

}
