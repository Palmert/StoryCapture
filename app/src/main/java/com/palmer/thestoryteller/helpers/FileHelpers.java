package com.palmer.thestoryteller.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Thom on 11/11/2014.
 */
public class FileHelpers {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_AUDIO = 2;
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    public static final int EDIT_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    public static final int CAPTURE_PAGE_AUDIO_REQUEST_CODE = 300;
    public static final int CAPTURE_PAGE_SOUND_REQUEST_CODE = 400;


    public static Uri getOutputMediaFileUri(int type, Context context) {
        return Uri.fromFile(getOutputMediaFile(type, context));
    }

    /**
     * Create a File for saving an image or video
     */
    @SuppressLint("SimpleDateFormat")
    public static File getOutputMediaFile(int type, Context context) {
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = null;
        if (type == MEDIA_TYPE_IMAGE) {
            File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), "StoryCapture");

            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d("StoryCapture", "failed to create directory");
                    return null;
                }
            }
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_AUDIO) {
            String fileName = "AUDIO_" + timeStamp + ".3gp";
            mediaFile = new File(context.getFilesDir(), fileName);
        }
        return mediaFile;
    }
}
