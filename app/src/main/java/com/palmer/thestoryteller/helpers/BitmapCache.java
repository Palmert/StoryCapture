package com.palmer.thestoryteller.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.LruCache;

/**
 * Created by Thom on 11/13/2014.
 */
public class BitmapCache {
    private LruCache<Integer, Bitmap> mMemoryCache;

    public BitmapCache() {
        // Get max available VM memory, exceeding this amount will throw an
        // OutOfMemory exception. Stored in kilobytes as LruCache takes an
        // int in its constructor.
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        // Use 1/8th of the available memory for this memory cache.
        final int cacheSize = maxMemory / 8;

        mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(Integer key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes rather than
                // number of items.
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(Integer key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            mMemoryCache.put(key, bitmap);
        }
    }


    public Bitmap getBitmapFromMemCache(Integer key) {
        return mMemoryCache.get(key);
    }

    public Bitmap getScaledBitmap(Integer resId, Resources resources, int width, int height) {

        Bitmap bitmap = getBitmapFromMemCache(resId);
        if (bitmap == null) {
            Bitmap nMutableBackground = ImageHelpers.decodeSampledBitmapFromResource(
                    resources, resId, width,
                    height);

            bitmap = Bitmap.createScaledBitmap(nMutableBackground,
                    width, height, false);

            addBitmapToMemoryCache(resId, bitmap);
        }
        return bitmap;
    }
}
