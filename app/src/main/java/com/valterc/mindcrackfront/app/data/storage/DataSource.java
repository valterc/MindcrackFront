package com.valterc.mindcrackfront.app.data.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.valterc.mindcrackfront.app.data.Mindcracker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

/**
 * Created by Valter on 18/05/2014.
 */
public class DataSource {

    private DataSQLiteHelper mSqliteHelper;

    public DataSource(Context c){
        mSqliteHelper = new DataSQLiteHelper(c);
    }

    private SQLiteDatabase getDatabase(){
        return mSqliteHelper.getDatabase();
    }

    private Mindcracker cursorToMindcracker(Cursor c) {
        Mindcracker mindcracker = null;

        String id = null;
        try {
            id = c.getString(c.getColumnIndex("id"));
        } catch (Exception e) {
            return null;
        }

        String name = c.getString(c.getColumnIndex("name"));
        String youtubeName = c.getString(c.getColumnIndex("youtube_name"));
        String youtubeId = c.getString(c.getColumnIndex("youtube_id"));
        String youtubePlaylistId = c.getString(c.getColumnIndex("youtube_plist_id"));
        String twitchId = c.getString(c.getColumnIndex("twitch_id"));
        int showTitleOnList = c.getInt(c.getColumnIndex("show_title_on_list"));
        int notifications = c.getInt(c.getColumnIndex("notifications"));
        int unseenVideoCount = c.getInt(c.getColumnIndex("unseen_video_count"));
        long hits = c.getLong(c.getColumnIndex("hits"));
        String lastVideoId = c.getString(c.getColumnIndex("last_video_id"));
        String lastVideoDateString = c.getString(c.getColumnIndex("last_video_date"));

        Date lastVideoDate  = null;
        if (lastVideoDateString != null) {
            try {                                    //2014-04-24T16:01:15.000Z
                lastVideoDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.ENGLISH).parse(lastVideoDateString);
            } catch (ParseException e) {
                lastVideoDate = GregorianCalendar.getInstance().getTime();
            }
        }

        mindcracker = new Mindcracker(id, name, youtubeName, youtubeId, youtubePlaylistId, twitchId, showTitleOnList == 1, notifications == 1, unseenVideoCount, hits, lastVideoId, lastVideoDateString == null ? null : lastVideoDate);

        return mindcracker;
    }


    public ArrayList<Mindcracker> getMindcrackers(){

        ArrayList<Mindcracker> mindcrackers = new ArrayList<Mindcracker>();

        Cursor c = getDatabase().query(
                "mindcrackers",
                new String[] { "id",
                        "name",
                        "youtube_name",
                        "youtube_id",
                        "youtube_plist_id",
                        "twitch_id",
                        "show_title_on_list",
                        "notifications",
                        "unseen_video_count",
                        "hits",
                        "last_video_id",
                        "last_video_date"
                }, null, null, null, null, "name COLLATE NOCASE", null);

        if (c.moveToFirst()) {
            do {
                Mindcracker m = cursorToMindcracker(c);
                if (m != null)
                    mindcrackers.add(m);
            } while (c.moveToNext());
        }

        c.close();

        return mindcrackers;
    }

    public ArrayList<Mindcracker> getFavoriteMindcrackers(){
        ArrayList<Mindcracker> mindcrackers = new ArrayList<Mindcracker>();

        Cursor c = getDatabase().rawQuery("SELECT * FROM mindcrackers m JOIN favorites f ON m.id = f.mindcracker_id", null);

        if (c.moveToFirst()) {
            do {
                Mindcracker m = cursorToMindcracker(c);
                if (m != null)
                    mindcrackers.add(m);
            } while (c.moveToNext());
        }

        c.close();

        return mindcrackers;
    }

    public Boolean updateMindcrackers(ArrayList<Mindcracker> mindcrackers){

        Boolean result = true;

        SQLiteStatement statement = getDatabase().compileStatement("UPDATE mindcrackers SET " +
                "name = ?, " +
                "youtube_name = ?, " +
                "youtube_id = ?, " +
                "youtube_plist_id = ?, " +
                "twitch_id = ?, " +
                "show_title_on_list = ?, " +
                "notifications = ?, " +
                "unseen_video_count = ?, " +
                "hits = ?, " +
                "last_video_id = ?, " +
                "last_video_date = ? " +
                "WHERE id = ?");


        try {

            for (int i = 0; i < mindcrackers.size(); i++) {
                 Mindcracker m = mindcrackers.get(i);

                statement.clearBindings();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX", Locale.ENGLISH);

                int index = 1;
                statement.bindString(index++, m.getName());
                statement.bindString(index++, m.getYoutubeName());
                statement.bindString(index++, m.getYoutubeId());
                statement.bindString(index++, m.getYoutubePlaylistId());
                statement.bindString(index++, m.getTwitchId());
                statement.bindLong(index++, m.getShowTitleOnList() ? 1 : 0);
                statement.bindLong(index++, m.getNotificationsEnabled() ? 1 : 0);
                statement.bindLong(index++, m.getUnseenVideoCount());
                statement.bindLong(index++, m.getHits());
                statement.bindString(index++, m.getLastVideoId());
                statement.bindString(index++, m.getLastVideoDate() == null ? null : sdf.format(m.getLastVideoDate()));
                statement.bindString(index++, m.getId());


                if (android.os.Build.VERSION.SDK_INT >= 11)
                    statement.executeUpdateDelete();
                else
                    statement.execute();
            }

        }catch (Exception e){
            Log.e("DataSource", e.getMessage());
            result = false;
        }


        return result;
    }

    public Boolean updateFavoriteMindcrackers(ArrayList<Mindcracker> favoriteMindcrackers){

        Boolean result = true;

        SQLiteStatement deleteStatement = getDatabase().compileStatement("DELETE FROM favorites");

        if (android.os.Build.VERSION.SDK_INT >= 11)
            deleteStatement.executeUpdateDelete();
        else
            deleteStatement.execute();

        deleteStatement.close();

        SQLiteStatement statement = getDatabase().compileStatement("INSERT INTO favorite VALUES (?, ?)");

        try {

            for (int i = 0; i < favoriteMindcrackers.size(); i++) {
                Mindcracker m = favoriteMindcrackers.get(i);

                int index = 1;
                statement.bindLong(index++, i);
                statement.bindString(index++, m.getId());

                statement.executeInsert();
            }

            statement.close();

        }catch (Exception e){
            Log.e("DataSource", e.getMessage());
            result = false;
        }

        return result;
    }

    public void dispose(){
        mSqliteHelper.disposeDatabase();
    }

}
