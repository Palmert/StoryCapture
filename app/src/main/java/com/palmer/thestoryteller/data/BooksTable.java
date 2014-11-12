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
public class BooksTable {
    // Database table
    public static final String TABLE_BOOKS = "books";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_IMAGE = "image";

    private static final String[] allColumns = {
            COLUMN_ID,
            COLUMN_IMAGE
    };

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "CREATE TABLE "
            + TABLE_BOOKS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COLUMN_IMAGE + " TEXT NOT NULL);";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(BooksTable.class.getName(), "Upgrading com.palmer.thestoryteller.database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKS);
        onCreate(database);
    }

    public static long create(Book book, SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE, book.getImagePath());
        return db.insert(TABLE_BOOKS, null, values);
    }

    public static List<Book> findAll(SQLiteDatabase db) {
        List<Book> booksList = new ArrayList<Book>();

        Cursor cursor = db.query(TABLE_BOOKS, allColumns,
                null, null, null, null, null);

        Log.i(BooksDataSource.LOGTAG, "Books findAll returned " + cursor.getCount());

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Book book = new Book(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                book.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                book.setPageList(PagesTable.findAll(book.getId(), db));
                booksList.add(book);
            }
        }
        return booksList;
    }

    public static Book findById(long bookId, SQLiteDatabase db) {
        Book book = null;
        Cursor cursor = db.query(TABLE_BOOKS, allColumns, COLUMN_ID + " = " + bookId,
                null, null, null, null);

        Log.i(BooksDataSource.LOGTAG, "Book findById found " + cursor.getCount() + " book");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                book = new Book(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
                book.setId(cursor.getLong(cursor.getColumnIndex(COLUMN_ID)));
                book.setPageList(PagesTable.findAll(book.getId(), db));
            }
        }
        return book;
    }

    public static void update(Book book, SQLiteDatabase booksDB) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, book.getId());
        values.put(COLUMN_IMAGE, book.getImagePath());

        booksDB.update(TABLE_BOOKS, values, COLUMN_ID + " = " + book.getId(), null);
    }

    public static void delete(long id, SQLiteDatabase booksDB) {
        booksDB.delete(TABLE_BOOKS, COLUMN_ID + " = " + id, null);
    }
}
