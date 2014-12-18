package com.valterc.mindcrackfront.app.splash;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.valterc.mindcrackfront.app.R;
import com.vcutils.ScalingUtils;
import com.vcutils.VTimer;

/**
 * Created by Valter on 07/12/2014.
 */
public class SplashFragment extends Fragment implements VTimer.TimerExecutor {

    private ImageView imageViewLogoUncracked;
    private ImageView imageViewLogo;
    private TextView textViewTitleMind;
    private TextView textViewTitleCrack;
    private TextView textViewTitleFront;
    private View viewParentFrameLayout;

    private VTimer timer;

    public SplashFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_splash, null);

        viewParentFrameLayout = container;
        viewParentFrameLayout.setVisibility(View.VISIBLE);

        imageViewLogoUncracked = (ImageView) view.findViewById(R.id.imageViewSplashLogoUncracked);
        imageViewLogo = (ImageView) view.findViewById(R.id.imageViewSplashLogo);

        textViewTitleMind = (TextView) view.findViewById(R.id.textViewTitleMind);
        textViewTitleCrack = (TextView) view.findViewById(R.id.textViewTitleCrack);
        textViewTitleFront = (TextView) view.findViewById(R.id.textViewTitleFront);

        Typeface typefaceText = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface typefaceTextBold = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        textViewTitleMind.setTypeface(typefaceTextBold);
        textViewTitleCrack.setTypeface(typefaceTextBold);
        textViewTitleFront.setTypeface(typefaceText);

        new VTimer(new VTimer.TimerExecutor() {
            @Override
            public void execute(VTimer vTimer) {
                viewParentFrameLayout.setVisibility(View.GONE);
                vTimer.dispose();
            }
        }, 3100).start();

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ObjectAnimator animLogo = ObjectAnimator.ofFloat(imageViewLogo, "alpha", 1f);
        ObjectAnimator animLogoUncracked = ObjectAnimator.ofFloat(imageViewLogoUncracked, "alpha", 0f);

        animLogo.setDuration(220);
        animLogoUncracked.setDuration(200);


        float xMind = textViewTitleMind.getX();
        ObjectAnimator animTextMind = ObjectAnimator.ofFloat(textViewTitleMind, "translationX", xMind, xMind - ScalingUtils.convertDpToPixel(35));

        float xCrack = textViewTitleCrack.getX();
        ObjectAnimator animTextCrack = ObjectAnimator.ofFloat(textViewTitleCrack, "translationX", xCrack, xCrack - ScalingUtils.convertDpToPixel(35));

        float xFront = textViewTitleFront.getX();
        ObjectAnimator animTextFront = ObjectAnimator.ofFloat(textViewTitleFront, "translationX", xFront, xFront + ScalingUtils.convertDpToPixel(25));

        animTextMind.setDuration(400);
        animTextCrack.setDuration(300);
        animTextFront.setDuration(400);


        ObjectAnimator animTextCrackAlpha = ObjectAnimator.ofFloat(textViewTitleCrack, "alpha", 1f);
        animTextCrack.setDuration(500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setInterpolator(new LinearInterpolator());

        animatorSet.play(animTextMind).after(500).with(animTextFront).with(animTextCrack);
        animatorSet.play(animTextCrackAlpha).after(900);

        animatorSet.play(animLogo).after(500).with(animLogoUncracked);

        timer = new VTimer(this, 2500);
        timer.start();

        animatorSet.start();

    }

    @Override
    public void execute(VTimer vTimer) {
        getFragmentManager().beginTransaction().remove(SplashFragment.this).setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commitAllowingStateLoss();
        timer.dispose();
    }
}
