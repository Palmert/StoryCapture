package database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Thom on 11/8/2014.
 */
public class BooksDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "books.db";
    private static final int DATABASE_VERSION = 1;

    public BooksDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        BooksTable.onCreate(db);
        PagesTable.onCreate(db);
        SoundsTable.onCreate(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        BooksTable.onUpgrade(db, oldVersion, newVersion);
        PagesTable.onUpgrade(db, oldVersion, newVersion);
        SoundsTable.onUpgrade(db, oldVersion, newVersion);
    }
}
