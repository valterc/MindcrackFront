package com.valterc.mindcrackfront.app.main.about;

import android.app.Activity;
import android.app.Application;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.valterc.IFragmentBack;
import com.valterc.mindcrackfront.app.BuildConfig;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.main.MainActivity;
import com.valterc.mindcrackfront.app.main.actionbar.MindcrackActionBarFragment;
import com.valterc.mindcrackfront.app.main.front.IShowFrontFragmentListener;
import com.vcutils.IntentUtils;

/**
 * Created by Valter on 27/12/2014.
 */
public class AboutFragment extends Fragment implements IFragmentBack {

    private IShowFrontFragmentListener showFrontFragmentListener;

    public AboutFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MindcrackActionBarFragment mindcrackActionBarFragment = (MindcrackActionBarFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentMindcrackActionBar);

        mindcrackActionBarFragment.setRightImageInAnimation(android.R.anim.fade_in);
        mindcrackActionBarFragment.setRightImageOnClickListener(null);
        mindcrackActionBarFragment.setRightImageOnLongClickListener(null);
        mindcrackActionBarFragment.setRightImageResource(0);
        mindcrackActionBarFragment.resetCenterImage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, null);

        Typeface typefaceThin = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Thin.ttf");
        Typeface typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");

        TextView textViewTitleMind = (TextView) view.findViewById(R.id.textViewTitleMind);
        TextView textViewTitleCrack = (TextView) view.findViewById(R.id.textViewTitleCrack);
        TextView textViewTitleFront = (TextView) view.findViewById(R.id.textViewTitleFront);
        TextView textViewDevelopedBy = (TextView) view.findViewById(R.id.textViewDevelopedBy);
        TextView textViewDevelopedName = (TextView) view.findViewById(R.id.textViewDeveloperName);
        TextView textViewCopyrightNotice = (TextView) view.findViewById(R.id.textViewCopyrightNotice);
        TextView textViewAppVersion = (TextView) view.findViewById(R.id.textViewAppVersion);

        textViewTitleMind.setTypeface(typefaceLight);
        textViewTitleCrack.setTypeface(typefaceLight);
        textViewTitleFront.setTypeface(typefaceThin);
        textViewDevelopedBy.setTypeface(typefaceThin);
        textViewDevelopedName.setTypeface(typefaceLight);
        textViewCopyrightNotice.setTypeface(typefaceLight);
        textViewAppVersion.setTypeface(typefaceLight);

        textViewAppVersion.setText(BuildConfig.VERSION_NAME);

        LinearLayout linearLayoutDeveloperContact = (LinearLayout) view.findViewById(R.id.linearLayoutDeveloperContact);
        linearLayoutDeveloperContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.sendEmail(getActivity(), "mail@valterc.com", "Mindcrack Front App", "");
            }
        });

        return view;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (getActivity() != null){
            if (getActivity() instanceof MainActivity){
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showAboutFragment();
            }
        }
    }

    @Override
    public boolean OnBackKeyPressed() {
        if (showFrontFragmentListener != null) {
            showFrontFragmentListener.showFrontFragment();
        }
        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof IShowFrontFragmentListener) {
            showFrontFragmentListener = (IShowFrontFragmentListener) activity;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        showFrontFragmentListener = null;
    }
}
