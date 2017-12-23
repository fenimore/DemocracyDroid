package com.workingagenda.democracydroid.Helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.workingagenda.democracydroid.Network.Download;

/**
 * Created by fen on 1/20/16.
 */
public class DownloadsDataSave {

    private SQLiteDatabase database;
    private SQLhelper dbHelper;
    private String[] allColumns = {SQLhelper.COLUMN_ID, SQLhelper.COLUMN_PATH, SQLhelper.COLUMN_TITLE};

    public DownloadsDataSave(Context context){
        dbHelper = new SQLhelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Download createDownload(String path, String title){
        ContentValues values = new ContentValues();
        values.put(SQLhelper.COLUMN_PATH, path);
        values.put(SQLhelper.COLUMN_TITLE, title);
        long insertId = database.insert(SQLhelper.TABLE_DOWNLOADS, null, values);
        Cursor cursor = database.query(SQLhelper.TABLE_DOWNLOADS,
                allColumns, SQLhelper.COLUMN_ID + "=" + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Download newDownload = cursorToDownload(cursor);
        cursor.close();
        return newDownload;
    }

    private Download cursorToDownload(Cursor cursor) {
        Download d = new Download(cursor.getString(1));
        d.setId(cursor.getLong(0));
        d.setTitle(cursor.getString(2));
        return d;
    }

}
