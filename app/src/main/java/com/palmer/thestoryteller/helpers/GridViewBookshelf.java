package com.palmer.thestoryteller.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;


/**
 * Created by Thom on 11/12/2014.
 */
public class GridViewBookshelf extends GridView {

    private Bitmap scaledBackground;

    public GridViewBookshelf(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes() * height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        int count = getChildCount();
        ImageView imageView = (ImageView) getChildAt(0);
        int top = count < 0 ? getChildAt(0).getTop() : 0;
        Bitmap nMutableBackground = BitmapFactory.decodeResource(getResources(), R.drawable.bookshelf);
        Bitmap mutableBackground = convertToMutable(nMutableBackground);
        scaledBackground = Bitmap.createScaledBitmap(mutableBackground,
                getWidth(), imageView.getMeasuredHeight(), false);

        for (int y = top; y < getHeight(); y += scaledBackground.getHeight()) {
            for (int x = 0; x < getWidth(); x += scaledBackground.getWidth()) {
                canvas.drawBitmap(scaledBackground, x, y, null);
            }
        }

        super.dispatchDraw(canvas);
    }
}

