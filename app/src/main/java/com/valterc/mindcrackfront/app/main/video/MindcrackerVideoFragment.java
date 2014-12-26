package com.valterc.mindcrackfront.app.main.video;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.Video;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubView;
import com.valterc.IFragmentBack;
import com.valterc.data.download.DownloadImageAsyncTask;
import com.valterc.data.download.DownloadImageListener;
import com.valterc.data.download.DownloadImageRequest;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.main.MainActivity;
import com.valterc.mindcrackfront.app.main.actionbar.MindcrackActionBarContextHolder;
import com.valterc.mindcrackfront.app.main.actionbar.MindcrackActionBarFragment;
import com.valterc.mindcrackfront.app.utils.DateFormatter;
import com.valterc.mindcrackfront.app.youtube.AuthenticationResult;
import com.valterc.mindcrackfront.app.youtube.YoutubeManager;
import com.valterc.mindcrackfront.app.youtube.tasks.DislikeVideoAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.GetChannelAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideoAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideoAsyncTask.*;
import com.valterc.mindcrackfront.app.youtube.tasks.GetVideoRatingAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.LikeVideoAsyncTask;
import com.valterc.mindcrackfront.app.youtube.tasks.RateVideoAsyncTask;
import com.vcutils.IntentUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Valter on 24/05/2014.
 */
public class MindcrackerVideoFragment extends Fragment implements IFragmentBack, YouTubePlayer.OnInitializedListener, YouTubePlayer.OnFullscreenListener, YouTubePlayer.PlayerStateChangeListener, GetVideoListener, GetVideoRatingAsyncTask.GetVideoRatingListener, LikeVideoAsyncTask.LikeVideoListener, DislikeVideoAsyncTask.DislikeVideoListener, RateVideoAsyncTask.RateVideoListener, MindcrackActionBarContextHolder {

    private static final String MOPUB_VIDEO_AD_ID = "486c4437924d44519385a9818634916e";
    private static final String MOPUB_VIDEO_BANNER_AD_ID = "b2e649962b144b02913b7e9991f791ee";

    private static final String PARAM_MINDCRACKER_ID = "mindcrackerId";
    private static final String PARAM_VIDEO_ID = "videoId";
    private static final String PARAM_SET_AB_LOGO = "setActionBarLogo";


    private String mindrackerId;
    private String videoId;
    private boolean setActionBarLogo;
    private Typeface typefaceLight;
    private Typeface typefaceNormal;
    private boolean fullscreen;
    private String rating;
    private Video video;
    private YouTubePlayer youTubePlayer;

    private View playerView;
    private View adViewWrapper;
    private MoPubView adView;
    private MoPubView adViewBanner;
    private View viewLoading;
    private View viewErrorLoading;
    private MindcrackActionBarFragment mindcrackActionBarFragment;

    private TextView textViewVideoTitle;
    private TextView textViewVideoPublishDate;
    private TextView textViewVideoViewCount;
    private TextView textViewVideoDescription;
    private LinearLayout linearLayoutLike;
    private LinearLayout linearLayoutDislike;
    private View viewLoadingRating;
    private View viewTopSpace;
    private View viewYoutubeApp;

    public static MindcrackerVideoFragment newInstance(String mindcrackerId, String videoId, boolean setActionBarLogo) {
        MindcrackerVideoFragment fragment = new MindcrackerVideoFragment();

        Bundle args = new Bundle();
        args.putString(PARAM_MINDCRACKER_ID, mindcrackerId);
        args.putString(PARAM_VIDEO_ID, videoId);
        args.putBoolean(PARAM_SET_AB_LOGO, setActionBarLogo);
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
            setActionBarLogo = getArguments().getBoolean(PARAM_SET_AB_LOGO);
        } else if (savedInstanceState != null) {
            mindrackerId = savedInstanceState.getString(PARAM_MINDCRACKER_ID);
            videoId = savedInstanceState.getString(PARAM_VIDEO_ID);
            setActionBarLogo = savedInstanceState.getBoolean(PARAM_SET_AB_LOGO);

            if (mindrackerId == null || videoId == null) {
                OnBackKeyPressed();
            }
        }

        typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
        typefaceNormal = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Regular.ttf");

        this.mindcrackActionBarFragment = (MindcrackActionBarFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMindcrackActionBar);
        mindcrackActionBarFragment.setContextHolder(this);
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
        adViewBanner = (MoPubView) view.findViewById(R.id.adview_banner);

        viewTopSpace = view.findViewById(R.id.relativeLayoutTopSpace);

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

        viewYoutubeApp = view.findViewById(R.id.relativeLayoutYoutubeApp);
        viewYoutubeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    IntentUtils.actionView(getActivity(), "market://details?id=com.google.android.youtube");
                } catch (Exception ignore) {
                    IntentUtils.actionView(getActivity(), "http://play.google.com/store/apps/details?id=com.google.android.youtube");
                }

            }
        });

        adView.setAdUnitId(MOPUB_VIDEO_AD_ID);
        adViewBanner.setAdUnitId(MOPUB_VIDEO_BANNER_AD_ID);

        adViewBanner.setBannerAdListener(new MoPubView.BannerAdListener() {
            @Override
            public void onBannerLoaded(MoPubView banner) {
                adViewBanner.setVisibility(View.VISIBLE);
            }

            @Override
            public void onBannerFailed(MoPubView banner, MoPubErrorCode errorCode) {
                adViewBanner.setVisibility(View.GONE);
            }

            @Override
            public void onBannerClicked(MoPubView banner) {

            }

            @Override
            public void onBannerExpanded(MoPubView banner) {

            }

            @Override
            public void onBannerCollapsed(MoPubView banner) {

            }
        });

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

        linearLayoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayoutLike.setVisibility(View.GONE);
                linearLayoutDislike.setVisibility(View.GONE);
                viewLoadingRating.setVisibility(View.VISIBLE);

                if (likesVideo()) {
                    new RateVideoAsyncTask().execute(new RateVideoAsyncTask.RateVideoInfo(MindcrackerVideoFragment.this, videoId, "none"));
                } else {
                    new LikeVideoAsyncTask().execute(new LikeVideoAsyncTask.LikeVideoInfo(MindcrackerVideoFragment.this, videoId));
                }
            }
        });

        linearLayoutDislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                linearLayoutLike.setVisibility(View.GONE);
                linearLayoutDislike.setVisibility(View.GONE);
                viewLoadingRating.setVisibility(View.VISIBLE);

                if (dislikesVideo()) {
                    new RateVideoAsyncTask().execute(new RateVideoAsyncTask.RateVideoInfo(MindcrackerVideoFragment.this, videoId, "none"));
                } else {
                    new DislikeVideoAsyncTask().execute(new DislikeVideoAsyncTask.DislikeVideoInfo(MindcrackerVideoFragment.this, videoId));
                }
            }
        });

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        new GetVideoAsyncTask().execute(new GetVideoInfo(videoId, this));
        if (setActionBarLogo) {
            GetChannelAsyncTask.GetChannelInfo channelInfo = new GetChannelAsyncTask.GetChannelInfo(new GetChannelAsyncTask.GetChannelListener() {
                @Override
                public void onGetChannelComplete(Channel response) {
                    if (response == null) {
                        return;
                    }

                    new DownloadImageAsyncTask().execute(new DownloadImageRequest(response.getSnippet().getThumbnails().getMedium().getUrl(), new DownloadImageListener() {
                        @Override
                        public void OnDownloadComplete(String imageUrl, boolean error, Bitmap bitmap) {
                            if (!error) {
                                if (mindcrackActionBarFragment.isCurrentContextHolder(MindcrackerVideoFragment.this)) {
                                    mindcrackActionBarFragment.setCenterImage(bitmap);
                                }
                            }
                        }
                    }));
                }
            }, MindcrackFrontApplication.getDataManager().getMindcracker(mindrackerId).getYoutubeId());
            new GetChannelAsyncTask().execute(channelInfo);
        }
        //new GetVideoRatingAsyncTask().execute(new GetVideoRatingAsyncTask.GetVideoRatingInfo(MindcrackerVideoFragment.this, videoId));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        adView.destroy();

        mindcrackActionBarFragment.show();

        mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
        mindcrackActionBarFragment.setRightImageOnClickListener(null);
        mindcrackActionBarFragment.setRightImageOnLongClickListener(null);
        mindcrackActionBarFragment.setRightImageResource(0);
        if (setActionBarLogo) {
            mindcrackActionBarFragment.resetCenterImage();
        }
        mindcrackActionBarFragment.removeContextHolder(this);

        try {
            YouTubePlayerSupportFragment f = (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.fragmentYoutube);
            if (f != null)
                getFragmentManager().beginTransaction().remove(f).commit();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onGetVideoComplete(Video response) {

        if (isDetached() || getActivity() == null) {
            return;
        }

        viewLoading.setVisibility(View.GONE);

        if (response == null) {
            viewErrorLoading.setVisibility(View.VISIBLE);
            return;
        }

        this.video = response;

        textViewVideoTitle.setText(response.getSnippet().getTitle());
        textViewVideoPublishDate.setText(DateFormatter.format(new Date(response.getSnippet().getPublishedAt().getValue())));

        if (response.getStatistics() != null && response.getStatistics().getViewCount() != null) {
            textViewVideoViewCount.setText(response.getStatistics().getViewCount().toString() + " views");
        } else {
            textViewVideoViewCount.setText("No views");
        }

        textViewVideoDescription.setText(response.getSnippet().getDescription());

        setUpActionBarButton();
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean wasRestored) {

        if (isDetached() || getActivity() == null) {
            return;
        }

        if (viewYoutubeApp.getVisibility() != View.GONE) {
            viewYoutubeApp.setVisibility(View.GONE);
        }

        this.youTubePlayer = youTubePlayer;

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

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            youTubePlayer.setFullscreen(true);
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

        if (isDetached()) {
            return;
        }

        if (youTubeInitializationResult == YouTubeInitializationResult.SERVICE_MISSING) {
            viewYoutubeApp.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFullscreen(boolean b) {
        fullscreen = b;
        if (!b) {
            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
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
            mindcrackActionBarFragment.hide();
            viewTopSpace.setVisibility(View.GONE);
        } else {
            playerParams.width = RelativeLayout.LayoutParams.MATCH_PARENT;
            playerParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
            mindcrackActionBarFragment.show();
            viewTopSpace.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public boolean OnBackKeyPressed() {
        if (fullscreen) {
            youTubePlayer.setFullscreen(false);
            return true;
        }
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
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
        if (MindcrackFrontApplication.getSettings().getShowAds()) {
            adViewWrapper.setVisibility(View.VISIBLE);
            adView.loadAd();
            adViewBanner.loadAd();
        }

        //TODO: Like video if auto like is on
    }

    @Override
    public void onError(YouTubePlayer.ErrorReason errorReason) {
        Log.d(getClass().getSimpleName(), "onError " + errorReason.toString());
    }

    @Override
    public void onGetVideoRatingComplete(String response) {
        if (TextUtils.isEmpty(response)) {
            if (!MindcrackFrontApplication.getYoutubeManager().isAuthenticated()) {
                AuthenticationResult authenticationResult = MindcrackFrontApplication.getYoutubeManager().authenticate();
                if (authenticationResult.getIntentChooseAccount() != null) {
                    getActivity().startActivityForResult(authenticationResult.getIntentChooseAccount(), MainActivity.REQUEST_CODE_SELECT_ACCOUNT);
                }
            }
            return;
        }

        rating = response;
        if (rating.equals("none")) {
            linearLayoutLike.setAlpha(1f);
            linearLayoutDislike.setAlpha(1f);
        } else if (rating.equals("like")) {
            linearLayoutLike.setAlpha(1f);
            linearLayoutDislike.setAlpha(.3f);
        } else {
            linearLayoutLike.setAlpha(.3f);
            linearLayoutDislike.setAlpha(1f);
        }
    }

    @Override
    public void onLikeVideoComplete(String videoId) {
        viewLoadingRating.setVisibility(View.GONE);
        linearLayoutLike.setVisibility(View.VISIBLE);
        linearLayoutDislike.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(videoId) && videoId.equals(this.videoId)) {
            this.rating = "like";
            linearLayoutLike.setAlpha(1);
            linearLayoutDislike.setAlpha(.3f);
        }
    }

    @Override
    public void onDislikeVideoComplete(String videoId) {
        viewLoadingRating.setVisibility(View.GONE);
        linearLayoutLike.setVisibility(View.VISIBLE);
        linearLayoutDislike.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(videoId) && videoId.equals(this.videoId)) {
            this.rating = "dislike";
            linearLayoutLike.setAlpha(.3f);
            linearLayoutDislike.setAlpha(1);
        }
    }

    private boolean likesVideo() {
        return !TextUtils.isEmpty(rating) && rating.equals("like");
    }

    private boolean dislikesVideo() {
        return !TextUtils.isEmpty(rating) && rating.equals("dislike");
    }

    private void setUpActionBarButton() {
        if (video == null) {
            return;
        }

        mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
        mindcrackActionBarFragment.setRightImageResource(R.drawable.share);
        mindcrackActionBarFragment.setRightImageOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(video.getSnippet().getTitle());
                stringBuilder.append(": http://youtu.be/");
                stringBuilder.append(video.getId());

                if (stringBuilder.length() + " via http://j.mp/mindcapp".length() <= 140)
                    stringBuilder.append(" via http://j.mp/mindcapp");

                IntentUtils.shareText(getActivity(), stringBuilder.toString());
            }
        });

        mindcrackActionBarFragment.setRightImageOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Share video", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void forceDestroy() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getFragmentManager().beginTransaction().remove(this).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commitAllowingStateLoss();
    }

    @Override
    public void onRateVideoComplete(String videoId, String rate) {
        viewLoadingRating.setVisibility(View.GONE);
        linearLayoutLike.setVisibility(View.VISIBLE);
        linearLayoutDislike.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(rate) && rate.equals("none")) {
            rating = rate;
            linearLayoutLike.setAlpha(1);
            linearLayoutDislike.setAlpha(1);
        }
    }

    @Override
    public void restoreContext(MindcrackActionBarFragment actionBarFragment) {
        setUpActionBarButton();
    }

    @Override
    public void contextLost(MindcrackActionBarFragment actionBarFragment) {
        mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
        mindcrackActionBarFragment.setRightImageOnClickListener(null);
        mindcrackActionBarFragment.setRightImageOnLongClickListener(null);
        mindcrackActionBarFragment.setRightImageResource(0);
        if (setActionBarLogo) {
            mindcrackActionBarFragment.resetCenterImage();
        }
    }
}
