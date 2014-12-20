package com.valterc.mindcrackfront.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.auth.GoogleAuthException;
import com.valterc.data.cache.Cache;
import com.valterc.mindcrackfront.app.data.DataManager;
import com.valterc.mindcrackfront.app.data.Settings;
import com.valterc.mindcrackfront.app.main.MainActivity;
import com.valterc.mindcrackfront.app.youtube.YoutubeManager;

/**
 * Created by Valter on 18/05/2014.
 */
public class MindcrackFrontApplication extends Application {

    private static DataManager dataManager;
    public static DataManager getDataManager() {
        return dataManager;
    }

    private static MindcrackFrontApplication mContext;
    public static MindcrackFrontApplication getContext() {
        return mContext;
    }

    private static YoutubeManager youtubeManager;
    public static YoutubeManager getYoutubeManager() {
        return youtubeManager;
    }

    private static Settings settings;
    public static  Settings getSettings(){
        return settings;
    }

    private static Cache cache;
    public static Cache getCache(){
        return cache;
    }

    private static ExceptionHandlerActivity exceptionHandlerActivity;
    public static void setExceptionHandlerActivity(ExceptionHandlerActivity handlerActivity){
        exceptionHandlerActivity = handlerActivity;
    }
    public static void removeExceptionHandlerActivity(ExceptionHandlerActivity handlerActivity) {
        if (exceptionHandlerActivity == handlerActivity) {
            exceptionHandlerActivity = null;
        }
    }

    @Override
    public void onCreate() {

        mContext = this;
        dataManager = new DataManager(getApplicationContext());
        youtubeManager = new YoutubeManager(getApplicationContext());
        settings = new Settings(getApplicationContext());
        cache = Cache.getInstance(getApplicationContext());

        super.onCreate();
    }

    @Override
    public void onTerminate() {

        //TODO: Unsync state, do not call dispose on other places,
        // or else we need to init this singletions again or use the object direct singleton if available
        getDataManager().dispose();
        getCache().Dispose();
        getSettings().dispose();

        super.onTerminate();
    }

    public static void handleException(Exception e) {
        if (exceptionHandlerActivity != null){
            exceptionHandlerActivity.handleException(e);
        }
    }


}
