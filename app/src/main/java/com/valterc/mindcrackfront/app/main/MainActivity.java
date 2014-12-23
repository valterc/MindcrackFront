package com.valterc.mindcrackfront.app.main;

import android.accounts.AccountManager;
import android.app.Dialog;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.widget.Toast;

import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.valterc.IFragmentBack;
import com.valterc.mindcrackfront.app.ExceptionHandlerActivity;
import com.valterc.mindcrackfront.app.MindcrackFrontApplication;
import com.valterc.mindcrackfront.app.R;
import com.valterc.mindcrackfront.app.data.Mindcracker;
import com.valterc.mindcrackfront.app.main.front.MindcrackFrontFragment;
import com.valterc.mindcrackfront.app.main.list.mindcracker.MindcrackerListFragment;
import com.valterc.mindcrackfront.app.main.list.mindcracker.ShowVideoListener;
import com.valterc.mindcrackfront.app.main.navigationdrawer.NavigationDrawerFragment;
import com.valterc.mindcrackfront.app.main.navigationdrawer.NavigationDrawerListener;
import com.valterc.mindcrackfront.app.main.navigationdrawer.list.NavigationDrawerListItem;
import com.valterc.mindcrackfront.app.main.settings.SettingsFragment;
import com.valterc.mindcrackfront.app.main.video.MindcrackerVideoFragment;
import com.valterc.mindcrackfront.app.splash.SplashFragment;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerListener, ShowVideoListener, ExceptionHandlerActivity {

    public static final int REQUEST_CODE_SELECT_ACCOUNT = 0x2140;
    public static final int REQUEST_CODE_RECOVER_FROM_AUTH_ERROR = 0x2141;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MindcrackFrontApplication.setExceptionHandlerActivity(this);

        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new MindcrackFrontFragment())
                .commit();

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout));

        //TODO: <debug> Remove false
        if (false && MindcrackFrontApplication.getSettings().getShowSplashScreen()) {
            getSupportFragmentManager().beginTransaction().add(R.id.frameLayoutSplash, new SplashFragment()).commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MindcrackFrontApplication.setExceptionHandlerActivity(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MindcrackFrontApplication.removeExceptionHandlerActivity(this);
    }

    @Override
    public void onNavigationDrawerItemSelected(Mindcracker mindcracker) {

        FragmentManager fragmentManager = getSupportFragmentManager();

        Fragment fragment = fragmentManager.findFragmentById(R.id.container);
        if (fragment != null && fragment instanceof MindcrackerVideoFragment){
            ((MindcrackerVideoFragment)fragment).forceDestroy();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, MindcrackerListFragment.newInstance(mindcracker.getId()))
                .commit();
    }

    @Override
    public void onNavigationDrawerItemSelected(NavigationDrawerListItem item) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, new SettingsFragment())
                .commit();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SELECT_ACCOUNT) {
            if (resultCode == RESULT_OK) {
                String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                MindcrackFrontApplication.getYoutubeManager().authenticate(accountName);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "You need to select an account in order to perform this action", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == REQUEST_CODE_RECOVER_FROM_AUTH_ERROR) {
            if (resultCode == RESULT_OK) {
                MindcrackFrontApplication.getYoutubeManager().authenticate();
            } else {
                MindcrackFrontApplication.getYoutubeManager().clearAuth();
            }
        }

    }

    @Override
    public void handleException(final Exception e) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    GooglePlayServicesAvailabilityException googleException = (GooglePlayServicesAvailabilityException) e;
                    Dialog alert = GooglePlayServicesUtil.getErrorDialog(googleException.getConnectionStatusCode(), MainActivity.this, REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
                    alert.show();
                } else if (e instanceof UserRecoverableAuthException) {
                    UserRecoverableAuthException userRecoverableAuthException = (UserRecoverableAuthException) e;
                    startActivityForResult(userRecoverableAuthException.getIntent(), REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
                } else if (e instanceof UserRecoverableAuthIOException) {
                    UserRecoverableAuthIOException userRecoverableAuthIOException = (UserRecoverableAuthIOException) e;
                    startActivityForResult(userRecoverableAuthIOException.getIntent(), REQUEST_CODE_RECOVER_FROM_AUTH_ERROR);
                }
            }
        });
    }
}
