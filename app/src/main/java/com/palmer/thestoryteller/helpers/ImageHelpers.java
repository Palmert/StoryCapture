package com.palmer.thestoryteller.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by Thom on 11/9/2014.
 */
public class ImageHelpers {

    public static void loadImageIntoViewAsync(final ScaledBitmapCache bitmapCache, Uri imageUri, ImageView imageView,
                                              final int width, final int height, Resources resources) {
        // check in-memory cache, if found no need for an AsyncTask
        Bitmap bitmap = bitmapCache.getInMemoryScaledBitmap(imageUri, width, height);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            return;
        }
        // read from storage with AsyncTask
        if (BitmapWorkerTask.cancelPotentialWork(imageUri, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, bitmapCache, width, height);
            AsyncDrawable asyncDrawable = new AsyncDrawable(resources, task);
            imageView.setImageDrawable(asyncDrawable);
            task.execute(imageUri);
        }
    }

}
