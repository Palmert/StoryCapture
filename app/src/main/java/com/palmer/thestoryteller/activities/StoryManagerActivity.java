package com.palmer.thestoryteller.activities;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ListView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.adapters.StoryListViewAdapter;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.fragments.PageDisplayFragment;
import com.palmer.thestoryteller.helpers.DepthPageTransformer;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

import java.util.List;

/**
 * Created by Thom on 11/12/2014.
 */
public class StoryManagerActivity extends FragmentActivity {
    public static final String BOOK_ID = "bookId";

    private ImagePagerAdapter mAdapter;
    private StoryListViewAdapter listAdapter;
    private ViewPager mPager;
    private ListView pageList;
    private DrawerLayout drawerLayout;
    private BooksDataSource data;
    private ScaledBitmapCache scaledBitmapCache;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_manager); // Contains just a ViewPager
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

            mAdapter = new ImagePagerAdapter(getSupportFragmentManager(),
                    selectedBook.getPageList(), this, scaledBitmapCache);
            mPager = (ViewPager) findViewById(R.id.view_pager);
            mPager.setPageTransformer(true, new DepthPageTransformer());
            mPager.setAdapter(mAdapter);

            listAdapter = new StoryListViewAdapter(this, R.layout.item_page,
                    selectedBook.getPageList(), scaledBitmapCache);
            pageList = (ListView) findViewById(R.id.book_navigation);
            pageList.setAdapter(listAdapter);

            drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerToggle = new ActionBarDrawerToggle(
                    this,                  /* host Activity */
                    drawerLayout,         /* DrawerLayout object */
                    R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                    R.string.drawer_open,  /* "open drawer" description */
                    R.string.drawer_close  /* "close drawer" description */
            ) {

                /** Called when a drawer has settled in a completely closed state. */
                public void onDrawerClosed(View view) {
                    super.onDrawerClosed(view);

                    getActionBar().setTitle(R.string.drawer_open);
                }

                /** Called when a drawer has settled in a completely open state. */
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    getActionBar().setTitle(R.string.drawer_close);
                }
            };

            // Set the drawer toggle as the DrawerListener
            drawerLayout.setDrawerListener(mDrawerToggle);

            getActionBar().setDisplayHomeAsUpEnabled(true);
            getActionBar().setHomeButtonEnabled(true);

        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }


    public static class ImagePagerAdapter extends FragmentStatePagerAdapter {
        private final List<Page> pages;
        private Context mContext;
        private ScaledBitmapCache scaledBitmapCache;

        public ImagePagerAdapter(FragmentManager fm, List<Page> pages, Context mContext,
                                 ScaledBitmapCache bitmapCache) {
            super(fm);
            this.pages = pages;
            this.mContext = mContext;
            this.scaledBitmapCache = bitmapCache;
        }

        @Override
        public int getCount() {
            return this.pages.size();
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {

            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point displaySize = new Point();
            display.getSize(displaySize);

            PageDisplayFragment f = PageDisplayFragment.newInstance();

            f.setImageWidth(displaySize.x);
            f.setImageHeight(displaySize.y);
            f.setPage(pages.get(position));
            f.setScaledBitmapCache(scaledBitmapCache);

            return f;
        }
    }
}