package com.palmer.thestoryteller.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thom on 11/8/2014.
 */
public class SoundsTable {
    // Database table
    public static final String TABLE_SOUNDS = "sounds";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_PAGEID = "page_id";
    public static final String COLUMN_AUDIO = "audio";
    public static final String COLUMN_XLOCATION = "x_location";
    public static final String COLUMN_YLOCATION = "y_location";

    public static final String[] allColumns = {
            COLUMN_ID,
            COLUMN_PAGEID,
            COLUMN_AUDIO,
            COLUMN_XLOCATION,
            COLUMN_YLOCATION
    };
    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_SOUNDS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_PAGEID + " INTEGER NOT NULL, "
            + COLUMN_AUDIO + " TEXT NOT NULL, "
            + COLUMN_XLOCATION + " INT NOT NULL, "
            + COLUMN_YLOCATION + " INT NOT NULL, "
            + "FOREIGN KEY (" + COLUMN_PAGEID + ") REFERENCES pages(id)" +
            ");";


    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(SoundsTable.class.getName(), "Upgrading com.palmer.thestoryteller.database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_SOUNDS);
        onCreate(database);
    }

    public static long create(Sound sound, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAGEID, sound.getPageId());
        values.put(COLUMN_AUDIO, sound.getAudioPath());
        values.put(COLUMN_XLOCATION, sound.getxLocation());
        values.put(COLUMN_YLOCATION, sound.getyLocation());
        return db.insert(TABLE_SOUNDS, null, values);
    }

    public static List<Sound> findAll(long pageId, SQLiteDatabase db) {
        List<Sound> soundsList = new ArrayList<Sound>();

        Cursor cursor = db.query(TABLE_SOUNDS, allColumns, COLUMN_PAGEID + " = " + pageId,
                null, null, null, null);

        Log.i(BooksDataSource.LOGTAG, "Sounds findAll returned " + cursor.getCount()
                + " for pageID " + pageId);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Sound sound = new Sound();
                sound.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                sound.setPageId(cursor.getLong(cursor.getColumnIndex(COLUMN_PAGEID)));
                sound.setAudioPath(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO)));
                sound.setxLocation(cursor.getInt(cursor.getColumnIndex(COLUMN_XLOCATION)));
                sound.setyLocation(cursor.getInt(cursor.getColumnIndex(COLUMN_YLOCATION)));
                soundsList.add(sound);
            }
        }
        return soundsList;
    }

    public static void update(Sound sound, SQLiteDatabase booksDB) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_PAGEID, sound.getPageId());
        values.put(COLUMN_AUDIO, sound.getAudioPath());
        values.put(COLUMN_XLOCATION, sound.getxLocation());
        values.put(COLUMN_YLOCATION, sound.getyLocation());

        booksDB.update(TABLE_SOUNDS, values, COLUMN_ID + " = " + sound.getId(), null);
    }
}
