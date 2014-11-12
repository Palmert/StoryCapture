package com.palmer.thestoryteller.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.List;

/**
 * Created by Thom on 11/9/2014.
 */
public class BooksDataSource {

    public static final String LOGTAG = "BOOKS DB";
    public static BooksDataSource data;
    private SQLiteOpenHelper booksDbHelper;
    private SQLiteDatabase booksDB;

    public BooksDataSource(Context context) {
        booksDbHelper = new BooksDatabaseHelper(context);
    }

    public void open() {
        booksDB = booksDbHelper.getWritableDatabase();
        Log.i(LOGTAG, "Database opened");
    }

    public void close() {
        booksDB.close();
        Log.i(LOGTAG, "Database closed");
    }


    public Book create(Book book) {
        long bookId = BooksTable.create(book, booksDB);
        book.setId(bookId);
        return book;
    }

    public List<Book> findAllBooks() {
        return BooksTable.findAll(booksDB);
    }

    public Book findBookById(long bookId) {
        return BooksTable.findById(bookId, booksDB);
    }

    public void update(Book book) {
        BooksTable.update(book, booksDB);
    }

    public void deleteBook(long id) {
        BooksTable.delete(id, booksDB);
    }


    public Page create(Page page) {
        long pageId = PagesTable.create(page, booksDB);
        page.setId(pageId);
        return page;
    }

    public List<Page> findAllPages(long bookId) {
        return PagesTable.findAll(bookId, booksDB);
    }

    public Page findPageById(long pageId) {
        return PagesTable.findById(pageId, booksDB);
    }

    public void update(Page page) {
        PagesTable.update(page, booksDB);
    }

    public void deletePage(long id) {
        PagesTable.delete(id, booksDB);
    }


    public Sound create(Sound sound) {
        long pageAudioId = SoundsTable.create(sound, booksDB);
        sound.setId(pageAudioId);
        return sound;
    }

    public List<Sound> findAllSounds(long pageId) {
        return SoundsTable.findAll(pageId, booksDB);
    }

    public void update(Sound sound) {
        SoundsTable.update(sound, booksDB);
    }
}
