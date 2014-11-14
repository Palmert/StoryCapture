package com.palmer.thestoryteller.models;

import android.content.Context;
import android.net.Uri;

import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;

import java.util.List;

/**
 * Created by Thom on 11/14/2014.
 */
public class BookshelfModel {

    private static boolean editable = true;
    private Uri fileUri;
    private long bookToDelete;
    private BooksDataSource booksDataSource;
    private Context context;
    private List<Book> books;

    public BookshelfModel(Context context) {
        this.booksDataSource = new BooksDataSource(context);
        booksDataSource.open();
    }

    public static boolean isEditable() {
        return editable;
    }

    public static void setEditable(boolean editable) {
        BookshelfModel.editable = editable;
    }

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
    }

    public long getBookToDelete() {
        return bookToDelete;
    }

    public void setBookToDelete(long bookToDelete) {
        this.bookToDelete = bookToDelete;
    }

    public BooksDataSource getBooksDataSource() {
        return booksDataSource;
    }

    public void setBooksDataSource(BooksDataSource booksDataSource) {
        this.booksDataSource = booksDataSource;
    }

    public List<Book> getBooks() {
        this.setBooks(booksDataSource.findAllBooks());
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String getFileUriString() {
        return fileUri.toString();
    }

    public void openDataSource() {
        booksDataSource.open();
    }

    public void closeDataSource() {
        booksDataSource.close();
    }
}
