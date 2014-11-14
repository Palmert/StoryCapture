package com.palmer.thestoryteller.activities;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.adapters.BookPagerAdapter;
import com.palmer.thestoryteller.adapters.StoryListViewAdapter;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.DepthPageTransformer;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

/**
 * Created by Thom on 11/12/2014.
 */
public class StoryManagerActivity extends FragmentActivity {
    public static final String BOOK_ID = "bookId";

    private BookPagerAdapter mAdapter;
    private StoryListViewAdapter listAdapter;
    private ViewPager mPager;
    private ListView pageList;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    //TODO move to model
    private ScaledBitmapCache scaledBitmapCache;
    private BooksDataSource data;
    private Uri imageUri;
    private Book selectedBook;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_manager); // Contains just a ViewPager
        getActionBar().setDisplayShowTitleEnabled(false);

        if (getIntent().hasExtra(BOOK_ID)) {
            data = new BooksDataSource(this);
            data.open();
            selectedBook = data.findBookById(getIntent().getExtras().getLong(BOOK_ID));
            selectedBook.getPageList().add(0,
                    new Page(selectedBook.getId(), selectedBook.getImagePath()));

            scaledBitmapCache = new ScaledBitmapCache(this,
                    ScaledBitmapCache.createFixedDirectoryLocator(
                            FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE, this).toString()));

            mAdapter = new BookPagerAdapter(getSupportFragmentManager(),
                    selectedBook.getPageList(), this, scaledBitmapCache, true);
            mPager = (ViewPager) findViewById(R.id.view_pager);
            mPager.setPageTransformer(true, new DepthPageTransformer());
            mPager.setAdapter(mAdapter);


            listAdapter = new StoryListViewAdapter(this, R.layout.item_page,
                    selectedBook.getPageList(), scaledBitmapCache);
            pageList = (ListView) findViewById(R.id.book_navigation);
            pageList.setAdapter(listAdapter);
            pageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mPager.setCurrentItem(position);
                }
            });


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

    public void savePage(View view) {
        NavUtils.navigateUpFromSameTask(this);
    }

    public void addPage(View v) {
        captureImage();
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        imageUri = FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE,
                this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri); // set the image file name
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // start the image capture Intent
        startActivityForResult(intent, FileHelpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void addAudio(View v) {
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivityForResult(intent, FileHelpers.CAPTURE_PAGE_AUDIO_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case FileHelpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_EDIT);
                    photoPickerIntent.setDataAndType(imageUri, "image/*");
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(photoPickerIntent, FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
                }
                break;
            }
            case FileHelpers.CAPTURE_PAGE_AUDIO_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Page page = selectedBook.getPageList().get(mPager.getCurrentItem());
                    page.setAudioPath(intent.getData().toString());
                    data.open();
                    data.update(page);
                }
                break;
            }
            case FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE: {
                Page page = new Page(selectedBook.getId(), imageUri.toString());
                data.open();
                data.create(page);
                selectedBook.getPageList().add(page);
                mAdapter.notifyDataSetChanged();
                mPager.setCurrentItem(mPager.getCurrentItem() + 1);

                break;
            }
        }
    }


}