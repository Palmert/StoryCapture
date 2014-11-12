package com.palmer.thestoryteller.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.ImageHelpers;
import com.palmer.thestoryteller.helpers.SystemUiHider;

import java.io.IOException;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see com.palmer.thestoryteller.helpers.SystemUiHider
 */
public class ReadStoryActivity extends Activity {


    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;
    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };
    /**
     * If set, will toggle the system UI visibility upon interaction. Otherwise,
     * will show the system UI visibility upon interaction.
     */
    private static final boolean TOGGLE_ON_CLICK = true;
    /**
     * The flags to pass to {@link com.palmer.thestoryteller.helpers.SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private static final int SWIPE_MIN_DISTANCE = 80;
    private static final int SWIPE_THRESHOLD_VELOCITY = 100;
    private Handler mHideHandler = new Handler();
    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * The instance of the {@link com.palmer.thestoryteller.helpers.SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private ImageView imageView;
    private Intent intent;
    private long bookId;
    private int pageIndex;
    private Page page;
    private Book book;
    private MediaPlayer mPlayer;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read_story);

        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.imageView);

        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        // Set up the user interaction to manually show or hide the system UI.
        contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TOGGLE_ON_CLICK) {
                    mSystemUiHider.toggle();
                } else {
                    mSystemUiHider.show();
                }
            }
        });

        findViewById(R.id.nextPage).setOnTouchListener(mDelayHideTouchListener);

        if (getIntent().hasExtra("bookId")) {
            bookId = (Long) getIntent().getExtras().get("bookId");
            BooksDataSource.data.open();
            book = BooksDataSource.data.findBookById(bookId);
            imageUri = Uri.parse(book.getImagePath());
        }

        if (getIntent().hasExtra("pageIndex")) {
            pageIndex = (Integer) getIntent().getExtras().get("pageIndex");
            if (pageIndex < book.getPageList().size()) {
                page = book.getPageList().get(pageIndex);
                imageUri = Uri.parse(page.getImagePath());
            }
            ++pageIndex;
            if (pageIndex == book.getPageList().size()) {
                Button nextPageButton = (Button) findViewById(R.id.nextPage);
                nextPageButton.setText("Close Book");
            }
        }

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);

        Bitmap thumbnail = null;
        try {
            thumbnail = ImageHelpers.getThumbnail(imageUri, this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageView.setImageBitmap(thumbnail);

        final GestureDetector swipeDetector = new GestureDetector(new GestureListener());
        imageView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View view, final MotionEvent event) {
                swipeDetector.onTouchEvent(event);
                return true;
            }
        });
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (page != null && page.getAudioPath() != null) {
            try {
                mPlayer = new MediaPlayer();
                mPlayer.setDataSource(getApplicationContext(), Uri.parse(page.getAudioPath()));
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                Log.e("Page Audio", "prepare() failed for file " + page.getAudioPath());
            }
            // Trigger the initial hide() shortly after the activity has been
            // created, to briefly hint to the user that UI controls
            // are available.
            delayedHide(100);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override
    protected void onResume() {
        super.onResume();
        BooksDataSource.data.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BooksDataSource.data.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void nextPage(View v) {
        moveToNextPage();
    }

    public void moveToNextPage() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
        }
        if (pageIndex < book.getPageList().size()) {
            intent = new Intent(getApplicationContext(), ReadStoryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("bookId", bookId);
            intent.putExtra("pageIndex", pageIndex);
            startActivity(intent);
        } else {
            if (mPlayer != null && mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                moveToNextPage();
                return true; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                --pageIndex;
                recreate();
                return true; // Left to right
            }

            return false;
        }
    }

}
