package com.palmer.thestoryteller.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.adapters.BookPagerAdapter;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.DepthPageTransformer;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

/**
 * Created by Thom on 11/12/2014.
 */
public class StoryPagerActivity extends FragmentActivity {
    public static final String BOOK_ID = "bookId";

    private BookPagerAdapter mAdapter;
    private ViewPager mPager;
    private BooksDataSource data;
    private ScaledBitmapCache scaledBitmapCache;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_reader); // Contains just a ViewPager
        getActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().hasExtra(BOOK_ID)) {
            data = new BooksDataSource(this);
            data.open();
            Book selectedBook = data.findBookById(getIntent().getExtras().getLong(BOOK_ID));
            selectedBook.getPageList().add(0,
                    new Page(selectedBook.getId(), selectedBook.getImagePath()));

            scaledBitmapCache = new ScaledBitmapCache(this,
                    ScaledBitmapCache.createFixedDirectoryLocator(
                            FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE, this).toString()));

            mAdapter = new BookPagerAdapter(getSupportFragmentManager(),
                    selectedBook.getPageList(), this, scaledBitmapCache);
            mPager = (ViewPager) findViewById(R.id.view_pager);
            mPager.setPageTransformer(true, new DepthPageTransformer());
            mPager.setAdapter(mAdapter);
        }
    }


}