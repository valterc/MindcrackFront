package com.valterc.mindcrackfront.app.main.settings;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.valterc.IFragmentBack;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.main.actionbar.MindcrackActionBarFragment;
import com.valterc.mindcrackfront.app.main.front.IShowFrontFragmentListener;

/**
 * Created by Valter on 23/12/2014.
 */
public class SettingsFragment extends Fragment implements IFragmentBack {

    private Typeface typefaceLight;
    private IShowFrontFragmentListener showFrontFragmentListener;

    private ListView listViewSettings;

    public SettingsFragment() {
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

        typefaceLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Roboto-Light.ttf");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, null);

        TextView textViewTitle = (TextView) view.findViewById(R.id.textViewTitle);
        textViewTitle.setTypeface(typefaceLight);

        listViewSettings = (ListView) view.findViewById(R.id.listView);
        setUpSettingListItems();

        return view;
    }

    private void setUpSettingListItems() {

        SettingsListItem settingClearAuth = new SettingsListItem<Void>(new SettingsListItem.SettingsInteractionListener<Void>() {
            @Override
            public void onInteraction(SettingsListItem item, Void newValue) {
            }

            @Override
            public void onClick(SettingsListItem item) {
                MindcrackFrontApplication.getYoutubeManager().clearAuth();
                setUpSettingListItems();
                Toast.makeText(getActivity(), "Authentication data cleared", Toast.LENGTH_SHORT).show();
            }
        }, SettingsListItem.TYPE_BUTTON, "Clear Authentication", "Deletes any account information within the app.");

        if (!MindcrackFrontApplication.getYoutubeManager().isAuthenticated()) {
            settingClearAuth.setEnabled(false);
            settingClearAuth.setSubTitle("No authentication data stored.");
        }

        SettingsListItem settingClearCache = new SettingsListItem<Void>(new SettingsListItem.SettingsInteractionListener<Void>() {
            @Override
            public void onInteraction(SettingsListItem item, Void newValue) {
            }

            @Override
            public void onClick(SettingsListItem item) {
                MindcrackFrontApplication.getCache().Clear();
                Toast.makeText(getActivity(), "Cache cleared", Toast.LENGTH_SHORT).show();
            }
        }, SettingsListItem.TYPE_BUTTON, "Empty Data Cache", "Removes all cache data stored by the app.");

        SettingsListItem<Boolean> settingAutoLike = new SettingsListItem<Boolean>(new SettingsListItem.SettingsInteractionListener<Boolean>() {
            @Override
            public void onInteraction(SettingsListItem item, Boolean newValue) {
                MindcrackFrontApplication.getSettings().setAutoLikeVideos(newValue);
            }

            @Override
            public void onClick(SettingsListItem item) {
            }
        }, SettingsListItem.TYPE_SWITCH, "Auto Like Videos", "Automaticaly like every video that you watch.", MindcrackFrontApplication.getSettings().getAutoLikeVideos());

        if (!MindcrackFrontApplication.getYoutubeManager().isAuthenticated()) {
            settingAutoLike.setEnabled(false);
            settingAutoLike.setSubTitle("You need to be logged in to Auto Like. Like any video to Login.");
            settingAutoLike.setValue(false);
        }

        SettingsListAdapter settingsListAdapter = new SettingsListAdapter(getActivity());

        settingsListAdapter.addItem(settingClearAuth);
        settingsListAdapter.addItem(settingClearCache);
        settingsListAdapter.addItem(settingAutoLike);

        listViewSettings.setAdapter(settingsListAdapter);
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
