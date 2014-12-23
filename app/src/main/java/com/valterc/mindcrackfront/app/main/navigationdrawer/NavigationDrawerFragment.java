package com.valterc.mindcrackfront.app.main.navigationdrawer;

import android.graphics.Color;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.main.navigationdrawer.list.NavigationDrawerAdapter;
import com.valterc.mindcrackfront.app.main.navigationdrawer.list.NavigationDrawerListItem;

import java.util.ArrayList;


public class NavigationDrawerFragment extends ListFragment {


    private static final String STATE_SELECTED_LIST_POSITION = "selected_navigation_drawer_list_position";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private NavigationDrawerListener mListener;
    private NavigationDrawerStateListener mStateListener;

    private DrawerLayout mDrawerLayout;
    private View mFragmentContainerView;

    private int mCurrentSelectedListPosition = 0;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedListPosition = savedInstanceState.getInt(STATE_SELECTED_LIST_POSITION);
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        setListAdapter(new NavigationDrawerAdapter(getActivity()));
        selectItem(mCurrentSelectedListPosition);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_navigation_drawer, null);
        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        selectItem(position);

        NavigationDrawerListItem item = (NavigationDrawerListItem) getListAdapter().getItem(position);
        if (item.mindcracker != null) {
            mListener.onNavigationDrawerItemSelected(item.mindcracker);
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        } else {
            mListener.onNavigationDrawerItemSelected(item);
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        }
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        mDrawerLayout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                if (mStateListener != null) {
                    mStateListener.onOpen();
                }
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (!isAdded()) {
                    return;
                }
                if (mStateListener != null) {
                    mStateListener.onClose();
                }
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });

    }

    private void selectItem(int position) {
        ((NavigationDrawerAdapter) getListAdapter()).setSelection(position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (NavigationDrawerListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_LIST_POSITION, mCurrentSelectedListPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public void setStateListener(NavigationDrawerStateListener stateListener) {
        this.mStateListener = stateListener;
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawer(mFragmentContainerView);
    }

    public void openDrawer() {
        mDrawerLayout.openDrawer(mFragmentContainerView);
    }
}
