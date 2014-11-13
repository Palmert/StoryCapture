package com.palmer.thestoryteller.helpers;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Thom on 11/12/2014.
 */
public class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {
    WeakReference<ImageView> imageViewReference;
    Uri data = null;
    ScaledBitmapCache bitmapCache;
    int width;
    int height;

    public BitmapWorkerTask(ImageView imageView, ScaledBitmapCache bitmapCache, int width, int height) {
        // Use a WeakReference to ensure the ImageView can be garbage collected
        imageViewReference = new WeakReference(imageView);
        this.bitmapCache = bitmapCache;
        this.width = width;
        this.height = height;
    }

    private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) {
        if (imageView != null) {
            final Drawable drawable = imageView.getDrawable();
            if (drawable instanceof AsyncDrawable) {
                final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
                return asyncDrawable.getBitmapWorkerTask();
            }
        }
        return null;
    }

    public static boolean cancelPotentialWork(Uri uri, ImageView imageView) {
        final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

        if (bitmapWorkerTask != null) {
            final Uri taskUri = bitmapWorkerTask.data;
            if (!uri.equals(taskUri)) {
                // Cancel previous task
                bitmapWorkerTask.cancel(true);
            } else {
                // The same work is already in progress
                return false;
            }
        }
        // No task associated with the ImageView, or an existing task was cancelled
        return true;
    }

    // Decode image in background.
    @Override
    protected Bitmap doInBackground(Uri... args) {
        data = args[0];
        return bitmapCache.getScaledBitmap(args[0], width, height);
    }

    // Once complete, see if ImageView is still around and set bitmap.
    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
            }
        }
    }

}
