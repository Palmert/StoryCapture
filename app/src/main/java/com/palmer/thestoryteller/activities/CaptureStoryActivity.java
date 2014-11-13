package com.palmer.thestoryteller.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.ImageHelpers;
import com.palmer.thestoryteller.helpers.SystemUiHider;

import java.io.IOException;

import static com.palmer.thestoryteller.R.drawable;
import static com.palmer.thestoryteller.R.id;
import static com.palmer.thestoryteller.R.layout;
import static com.palmer.thestoryteller.R.string;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class CaptureStoryActivity extends Activity {


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
     * The flags to pass to {@link SystemUiHider#getInstance}.
     */
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private static String audioPath = null;
    private Handler mHideHandler = new Handler();
    private Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * The instance of the {@link SystemUiHider} for this activity.
     */
    private SystemUiHider mSystemUiHider;

    private ImageView imageView;
    private Intent intent;
    private Uri fileUri;
    private Page page;
    private Book book;
    private long bookId;
    private int pageIndex;
    private boolean isExistingPage;
    private BooksDataSource booksDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(layout.activity_story_capture);
        setupActionBar();
        booksDataSource = new BooksDataSource(this);
        booksDataSource.open();
        final View controlsView = findViewById(id.fullscreen_content_controls);
        final View contentView = findViewById(id.imageView);

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

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(id.addPage).setOnTouchListener(mDelayHideTouchListener);


        Uri imageUri = null;

        if (getIntent().hasExtra("bookId")) {
            bookId = getIntent().getExtras().getLong("bookId");
            booksDataSource.open();

            book = booksDataSource.findBookById(bookId);
            imageUri = book.getImageUri();
        }

        if (getIntent().hasExtra("pageIndex")) {
            pageIndex = getIntent().getExtras().getInt("pageIndex");
        }

        if (pageIndex < book.getPageList().size()) {
            Button addPage = (Button) findViewById(id.addPage);
            addPage.setCompoundDrawablesWithIntrinsicBounds(0, drawable.ic_action_copy, 0, 0);
            addPage.setText(string.next_page);
            page = book.getPageList().get(pageIndex);
            imageUri = Uri.parse(page.getImagePath());
            isExistingPage = true;
        }

        if (getIntent().hasExtra("fileUri")) {
            imageUri = (Uri) getIntent().getExtras().get("fileUri");
            page = new Page(book.getId(), imageUri.toString());
        }

        if (page == null) {
            Button addAudioBtn = (Button) findViewById(id.addAudio);
            addAudioBtn.setVisibility(View.INVISIBLE);
            Button saveButton = (Button) findViewById(id.savePage);
            saveButton.setVisibility(View.INVISIBLE);
        }
        ++pageIndex;

        imageView = (ImageView) findViewById(id.imageView);

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_story_capture, menu);
        if (!isExistingPage) {
            MenuItem deletePage = menu.findItem(R.id.action_delete);
            deletePage.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setupActionBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            // Show the Up button in the action bar.
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        } else if (id == R.id.action_delete) {
            booksDataSource.open();
            booksDataSource.deletePage(page.getId());
            --pageIndex;
            this.recreate();
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

    public void addPage(View v) {
        moveToNextPage();
    }

    public void moveToNextPage() {
        booksDataSource.open();
        if (pageIndex < book.getPageList().size() - 1) {
            if (page != null) {
                booksDataSource.update(page);
            }
            intent = new Intent(getApplicationContext(), CaptureStoryActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("pageIndex", pageIndex);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            if (page != null) {
                page = booksDataSource.create(page);
            }
            captureImage();
        }
    }

    public void captureImage() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE, this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FileHelpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_EDIT);
                    photoPickerIntent.setDataAndType(fileUri, "image/*");
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(photoPickerIntent, FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
                }
                break;
            }
            case FileHelpers.CAPTURE_PAGE_AUDIO_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    page.setAudioPath(data.getData().toString());
                }
                break;
            }
            case FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE: {
                Intent newStoryCapture = new Intent(getApplicationContext(), CaptureStoryActivity.class);
                newStoryCapture.putExtra("bookId", bookId);
                newStoryCapture.putExtra("fileUri", fileUri);
                newStoryCapture.putExtra("pageIndex", pageIndex);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(newStoryCapture);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        booksDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        booksDataSource.close();
    }

    public void savePage(View view) {
        booksDataSource.open();
        if (pageIndex < book.getPageList().size() - 1) {
            if (page != null) {
                booksDataSource.update(page);
            }
            intent = new Intent(getApplicationContext(), CaptureStoryActivity.class);
            intent.putExtra("bookId", bookId);
            intent.putExtra("pageIndex", pageIndex);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        } else {
            if (page != null) {
                page = booksDataSource.create(page);
            }
        }
        NavUtils.navigateUpFromSameTask(this);
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                moveToNextPage();
                return true; // Right to left
            } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                pageIndex--;
                recreate();
                return true; // Left to right
            }

            return false;
        }
    }

}
