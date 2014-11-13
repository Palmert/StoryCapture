package com.palmer.thestoryteller.helpers;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.widget.ImageView;

/**
 * Created by Thom on 11/9/2014.
 */
public class ImageHelpers {

    public static final String ANDROID_RESOURCE = "android.resource://";
    public static final String FORESLASH = "/";

    public static void loadImageIntoViewAsync(final ScaledBitmapCache bitmapCache,
                                              Uri imageUri, ImageView imageView,
                                              final int width, final int height,
                                              Resources resources) {
        // check in-memory cache, if found no need for an AsyncTask
        Bitmap bitmap = bitmapCache.getInMemoryScaledBitmap(imageUri, width, height);
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            return;
        }
        // read from storage with AsyncTask
        if (BitmapWorkerTask.cancelPotentialWork(imageUri, imageView)) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageView, bitmapCache, width, height);
            AsyncDrawable asyncDrawable = new AsyncDrawable(resources, task);
            imageView.setImageDrawable(asyncDrawable);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            task.execute(imageUri);
        }

    }

    public static Uri resIdToUri(Context context, int resId) {
        return Uri.parse(ANDROID_RESOURCE + context.getPackageName() + FORESLASH + resId);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId,
                                                         int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

}
