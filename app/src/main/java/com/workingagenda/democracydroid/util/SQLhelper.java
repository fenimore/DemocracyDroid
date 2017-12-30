package com.workingagenda.democracydroid.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by fen on 1/20/16.
 */
@SuppressWarnings("DefaultFileTemplate")
class SQLhelper extends SQLiteOpenHelper {

    public static final String TABLE_DOWNLOADS = "downloads";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_TITLE = "title";

    private static final String DATABASE_NAME = "downloads.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_DOWNLOADS + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_PATH
            + " text not null, " + COLUMN_TITLE + "text not null);";

    public SQLhelper(Context context){
        super(context,DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLhelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOWNLOADS);
        onCreate(db);
    }
}
