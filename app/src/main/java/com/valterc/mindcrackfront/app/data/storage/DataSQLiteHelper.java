package com.valterc.mindcrackfront.app.data.storage;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.File;
import java.io.IOException;

/**
 * Created by Valter on 18/05/2014.
 */
public class DataSQLiteHelper {


    // ===========================================================
    // Constants
    // ===========================================================

    private final static String DB_FILE_NAME = "data";
    private final static int DB_VERSION = 1;
    private final static String DB_GET_VERSION = "pragma user_version;";
    private final static String DB_SET_VERSION = "pragma user_version = " + DB_VERSION + ";";
    private final static Boolean FORCE_UPDATE = false;

    // ===========================================================
    // Fields
    // ===========================================================

    private SQLiteDatabase database;
    private Context context;
    private String mDataPath;

    // ===========================================================
    // Constructor
    // ===========================================================

    public DataSQLiteHelper(Context c) {
        this.context = c;
        mDataPath = c.getApplicationContext().getFilesDir().getPath() + "/";

        try {
            openDatabaseReadWrite(mDataPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // ===========================================================
    // Methods
    // ===========================================================

    private void openDatabaseReadWrite(String path) throws Exception {
        try {

            if (database != null)
                database.close();

            if (!new File(path + DB_FILE_NAME).exists()){
                Log.d(this.toString(), "Creating database...");
                createDatabase(path);
            }

            database = SQLiteDatabase.openDatabase(path + DB_FILE_NAME, null, SQLiteDatabase.OPEN_READWRITE);

            if (isDatabaseOutdated()) {
                Log.d(this.toString(), "Database is outdated and will be replaced.");
                closeDatabase();
                createDatabase(path);
            }

        } catch (Exception e) {
            if (database != null)
                database.close();
            database = null;
            throw e;
        }
    }

    private boolean isDatabaseOutdated() {

        if (FORCE_UPDATE) return true;

        int localdbversion;

        try {
            Cursor c = database.rawQuery(DB_GET_VERSION, null);
            c.moveToFirst();
            localdbversion = c.getInt(0);
            c.close();
        } catch (Exception e) {
            return true;
        }

        return localdbversion < DB_VERSION;
    }

    private void createDatabase(String path) throws IOException {

        Log.d(this.toString(), "Database will be created");

        deleteDatabaseFile(path);

        File f = new File(path);
        f.mkdirs();

        database =  SQLiteDatabase.openOrCreateDatabase(path + DB_FILE_NAME, null);
        DatabaseCreator.CreateDatabase(context, database);
        database.execSQL(DB_SET_VERSION);


        Log.d(this.toString(), "Database created!");
    }

    private void closeDatabase() {
        if (database == null)
            return;

        try {
            database.close();
        } catch (Exception ignored) {
        }

        database = null;
    }

    private void deleteDatabaseFile(String path) {
        closeDatabase();
        File f = new File(path + DB_FILE_NAME);
        try {
            boolean result = false;
            if (f.exists())
                result = f.delete();

            Log.d(this.toString(), "Delete database file, result: " + result);
        } catch (Exception e) {
            Log.d(this.toString(), "Error deleting database file! : " + e.getMessage());
        }
    }


    public SQLiteDatabase getDatabase(){
        return database;
    }

    public void disposeDatabase(){
        closeDatabase();
        context = null;
    }

}
