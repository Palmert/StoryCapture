package database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Thom on 11/8/2014.
 */
public class PagesTable {
    // Database table
    public static final String TABLE_PAGES = "pages";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_BOOKID = "book_id";
    public static final String COLUMN_AUDIO = "audio";
    public static final String COLUMN_IMAGE = "image";

    private static final String[] allColumns = {
            COLUMN_ID,
            COLUMN_BOOKID,
            COLUMN_AUDIO,
            COLUMN_IMAGE
    };

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_PAGES + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_BOOKID + " INTEGER NOT NULL, "
            + COLUMN_AUDIO + " TEXT,"
            + COLUMN_IMAGE + " TEXT NOT NULL,"
            + "FOREIGN KEY (" + COLUMN_BOOKID + ") REFERENCES books(id)"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(PagesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PAGES);
        onCreate(database);
    }

    public static long create(Page page, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKID, page.getBookId());
        values.put(COLUMN_AUDIO, page.getAudioPath());
        values.put(COLUMN_IMAGE, page.getImagePath());
        return db.insert(TABLE_PAGES, null, values);
    }

    public static List<Page> findAll(long bookId, SQLiteDatabase db) {
        List<Page> pagesList = new ArrayList<Page>();

        Cursor cursor = db.query(TABLE_PAGES, allColumns, COLUMN_BOOKID + " = " + bookId,
                null, null, null, null);

        Log.i(BooksDataSource.LOGTAG, "Pages findAll returned " + cursor.getCount()
                + " for bookId " + bookId);

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Page page = new Page();
                page.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                page.setBookId(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKID)));
                page.setAudioPath(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO)));
                page.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                page.setSoundList(SoundsTable.findAll(page.getId(), db));
                pagesList.add(page);
            }
        }
        return pagesList;
    }

    public static Page findById(long pageId, SQLiteDatabase db) {
        Page page = null;

        Cursor cursor = db.query(TABLE_PAGES, allColumns, null,
                null, null, null, null);

        Log.i(BooksDataSource.LOGTAG, "Pages findById found " + cursor.getCount()
                + " page");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                page = new Page();
                page.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                page.setBookId(cursor.getLong(cursor.getColumnIndex(COLUMN_BOOKID)));
                page.setAudioPath(cursor.getString(cursor.getColumnIndex(COLUMN_AUDIO)));
                page.setImagePath(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                page.setSoundList(SoundsTable.findAll(page.getId(), db));
            }
        }
        return page;
    }

    public static void update(Page page, SQLiteDatabase booksDB) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_BOOKID, page.getBookId());
        values.put(COLUMN_AUDIO, page.getAudioPath());
        values.put(COLUMN_IMAGE, page.getImagePath());

        booksDB.update(TABLE_PAGES, values, COLUMN_ID + " = " + page.getId(), null);
    }

    public static void delete(long id, SQLiteDatabase booksDB) {
        booksDB.delete(TABLE_PAGES, COLUMN_ID + " = " + id, null);
    }
}
