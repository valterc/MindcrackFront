package com.valterc.mindcrackfront.app.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Random;

/**
 * Created by Valter on 18/05/2014.
 */
public class Settings {
    private static final String TAG = "Settings";
    /*

    1w5M0D
    5fEInK
    w3BGtc
    0qBlx9
    wQvPGy
    qtCOPU
    o1NzKK
    DwQ5VG
    jkBVbt
    6xYgHa

     */

    private static final String SETTINGS_FILE_NAME = "MDF_settings";
    private static final String KEY_DUMMY_VALUE = "1pE9rN";
    private static final String KEY_HASH_VALUES = "IMkel0";
    private static final String KEY_SHOW_ADS = "g98uMr";
    private static final String KEY_SHOW_SPLASH = "ZQyqaO";
    private static final String KEY_FIRST_RUN_TIMESTAMP = "1kBKnc";

    private SharedPreferences sharedPreferences;

    /* Settings */
    private Boolean showAds;
    private Boolean showSplashScreen;
    private long firstRunTimeStamp;
    private Boolean autoLikeVideos;

    public Settings(Context context) {
        sharedPreferences = context.getSharedPreferences(SETTINGS_FILE_NAME, Context.MODE_PRIVATE);

        if (!sharedPreferences.contains(KEY_DUMMY_VALUE)) {
            setDefaultValues();
        } else {
            loadExistingValues();
        }
    }

    private void loadExistingValues() {

        Boolean showAds = sharedPreferences.getBoolean(KEY_SHOW_ADS, true);
        Boolean showSplash = sharedPreferences.getBoolean(KEY_SHOW_SPLASH, true);
        long firstRunTimeStamp = sharedPreferences.getLong(KEY_FIRST_RUN_TIMESTAMP, -1);

        String hash = sharedPreferences.getString(KEY_HASH_VALUES, "");

        this.showAds = showAds;
        this.showSplashScreen = showSplash;
        this.firstRunTimeStamp = firstRunTimeStamp;

        String localHash = ComputeHash(hashCode() + "");

        if (hash.equals(localHash)){
            Log.d(TAG, "Setting values loaded successfully!");
        } else {
            Log.d(TAG, "Error loading setting values, corrupted data! ");
            setDefaultValues();
        }

    }

    private void setDefaultValues() {
        this.showAds = true;
        this.showSplashScreen = true;
        this.firstRunTimeStamp = Calendar.getInstance().getTimeInMillis();
        Log.d(TAG, "Default setting values loaded!");
    }

    private String ComputeHash(String key){
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
            messageDigest.update(key.getBytes());
            byte[] bytes = messageDigest.digest();

            StringBuilder hashedKey = new StringBuilder(64);
            for (int i=0; i < bytes.length; i++) {
                hashedKey.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            return hashedKey.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void storeValues(){
        boolean dummyValue = Math.random() > .5;

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DUMMY_VALUE, dummyValue);

        editor.putBoolean(KEY_SHOW_ADS, getShowAds());
        editor.putBoolean(KEY_SHOW_SPLASH, getShowSplashScreen());
        editor.putLong(KEY_FIRST_RUN_TIMESTAMP, getFirstRunTimeStamp());

        editor.putString(KEY_HASH_VALUES, ComputeHash(hashCode() + ""));

        boolean result = editor.commit();

        Log.d(TAG, "Values stored, result: " + result);
    }

    public void dispose() {
       storeValues();
    }

    public Boolean getShowAds() {
        //Only show ads after a day from the first run of the app
        return showAds && Calendar.getInstance().getTimeInMillis() - getFirstRunTimeStamp() > 3600 * 1000 * 24;
    }

    public void setShowAds(Boolean showAds) {
        this.showAds = showAds;
    }

    public Boolean getShowSplashScreen() {
        return showSplashScreen;
    }

    public void setShowSplashScreen(Boolean showSplashScreen) {
        this.showSplashScreen = showSplashScreen;
    }

    public long getFirstRunTimeStamp() {
        return firstRunTimeStamp;
    }

    public void setFirstRunTimeStamp(long firstRunTimeStamp) {
        this.firstRunTimeStamp = firstRunTimeStamp;
    }

    public Boolean getAutoLikeVideos() {
        return autoLikeVideos;
    }

    public void setAutoLikeVideos(Boolean autoLikeVideos) {
        this.autoLikeVideos = autoLikeVideos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Settings settings = (Settings) o;

        if (firstRunTimeStamp != settings.firstRunTimeStamp) return false;
        if (!autoLikeVideos.equals(settings.autoLikeVideos)) return false;
        if (!showAds.equals(settings.showAds)) return false;
        if (!showSplashScreen.equals(settings.showSplashScreen)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = showAds.hashCode();
        result = 31 * result + showSplashScreen.hashCode();
        result = 31 * result + (int) (firstRunTimeStamp ^ (firstRunTimeStamp >>> 32));
        result = 31 * result + autoLikeVideos.hashCode();
        return result;
    }
}
