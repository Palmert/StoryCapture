package com.palmer.thestoryteller.activities;

import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Display;
import android.view.WindowManager;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

import java.util.List;

/**
 * Created by Thom on 11/12/2014.
 */
public class ImageDetailActivity extends FragmentActivity {
    public static final String BOOK_ID = "bookId";

    private ImagePagerAdapter mAdapter;
    private ViewPager mPager;
    private BooksDataSource data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager); // Contains just a ViewPager
        if (getIntent().hasExtra(BOOK_ID)) {
            data = new BooksDataSource(this);
            data.open();

            mAdapter = new ImagePagerAdapter(getSupportFragmentManager(),
                    data.findAllPages(getIntent().getExtras().getLong(BOOK_ID)), this);
            mPager = (ViewPager) findViewById(R.id.view_pager);
            mPager.setAdapter(mAdapter);
        }
    }

    public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final List<Page> pages;
        private Context mContext;

        public ImagePagerAdapter(FragmentManager fm, List<Page> pages, Context mContext) {
            super(fm);
            this.pages = pages;
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return this.pages.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            ImageDetailFragment f = ImageDetailFragment.newInstance();
            f.setPage(pages.get(position));
            f.setScaledBitmapCache(new ScaledBitmapCache(mContext,
                    ScaledBitmapCache.createFixedDirectoryLocator(
                            FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE, mContext).toString())));
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point displaySize = new Point();
            display.getSize(displaySize);
            f.setImageWidth(displaySize.x);
            f.setImageHeight(displaySize.y);
            return f;
        }
    }
}