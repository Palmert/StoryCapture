package com.palmer.thestoryteller.helpers;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

/**
 * Created by Thom on 11/13/2014.
 */
public class AsyncDrawable extends BitmapDrawable {
    private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

    public AsyncDrawable(Resources res, AsyncTask bitmapWorkerTask) {
        super(res, (Bitmap) null);
        bitmapWorkerTaskReference = new WeakReference(bitmapWorkerTask);
    }

    public BitmapWorkerTask getBitmapWorkerTask() {
        return bitmapWorkerTaskReference.get();
    }
}
