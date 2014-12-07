package com.valterc.mindcrackfront.app.data.storage;

import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.valterc.mindcrackfront.app.R;

import java.io.InputStream;

/**
 * Created by Valter on 18/05/2014.
 */
public class DatabaseCreator {

    private static final String CREATE_TABLE_ANDROID_METADATA = "CREATE TABLE IF NOT EXISTS \"android_metadata\" (\"locale\" TEXT DEFAULT 'en_US');";
    private static final String INSERT_TABLE_ANDROID_METADATA = "INSERT INTO \"android_metadata\" VALUES ('en_US');";


    public static void CreateDatabase(Context context, SQLiteDatabase database) {

        String sql = getSqlCode(context);

        String[] statements = sql.split(";");
        for(String statement : statements){
            statement = statement.trim();
            if (statement.length() > 1) {
                database.execSQL(statement);
            }
        }

        database.execSQL(CREATE_TABLE_ANDROID_METADATA);
        database.execSQL(INSERT_TABLE_ANDROID_METADATA);

    }


    private static String getSqlCode(Context context) {

        String sql = null;

        try {
            Resources res = context.getResources();
            InputStream inputStream = res.openRawResource(R.raw.data_sql);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);
            sql = new String(b);
            inputStream.close();
        } catch (Exception e) {
            Log.e("DatabaseCreator", e.getMessage());
        }

        return sql;
    }

}
