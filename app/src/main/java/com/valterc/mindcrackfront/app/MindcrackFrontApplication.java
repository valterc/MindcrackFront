package com.valterc.mindcrackfront.app;

import android.app.Application;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;

import com.valterc.data.cache.Cache;
import com.valterc.mindcrackfront.app.data.DataManager;
import com.valterc.mindcrackfront.app.data.Settings;
import com.valterc.mindcrackfront.app.utils.DateFormatter;
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

        DateFormatter.setUp(getApplicationContext());

        super.onCreate();
    }

    public static void handleException(Exception e) {
        if (exceptionHandlerActivity != null){
            exceptionHandlerActivity.handleException(e);
        }
    }


}
