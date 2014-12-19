package com.valterc.mindcrackfront.app.main;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;

import com.valterc.IFragmentBack;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.main.front.MindcrackFrontFragment;
import com.valterc.mindcrackfront.app.main.list.mindcracker.MindcrackerListFragment;
import com.valterc.mindcrackfront.app.main.list.mindcracker.ShowVideoListener;
import com.valterc.mindcrackfront.app.main.navigationdrawer.NavigationDrawerFragment;
import com.valterc.mindcrackfront.app.main.navigationdrawer.NavigationDrawerListener;
import com.valterc.mindcrackfront.app.main.navigationdrawer.list.NavigationDrawerListItem;
import com.valterc.mindcrackfront.app.main.video.MindcrackerVideoFragment;
import com.valterc.mindcrackfront.app.splash.SplashFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerListener, ShowVideoListener {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new MindcrackFrontFragment())
                .commit();

        //TODO: <debug> Remove false
        if (false && MindcrackFrontApplication.getSettings().getShowSplashScreen()) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutSplash, new SplashFragment()).commit();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(Mindcracker mindcracker) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, MindcrackerListFragment.newInstance(mindcracker.getId()))
                .commit();

    }

    @Override
    public void onNavigationDrawerItemSelected(NavigationDrawerListItem item) {

    }

    @Override
    public void showVideo(String mindcrackerId, String videoId) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .add(R.id.container, MindcrackerVideoFragment.newInstance(mindcrackerId, videoId, false))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }

    @Override
    public void onBackPressed() {

        if (mNavigationDrawerFragment.isDrawerOpen()) {
            mNavigationDrawerFragment.closeDrawer();
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment fragment = fragmentManager.findFragmentById(R.id.container);

        boolean result = false;

        if (fragment instanceof IFragmentBack) {
            result = ((IFragmentBack) fragment).OnBackKeyPressed();
        }

        if (!result) {
            super.onBackPressed();
        }
    }

}
