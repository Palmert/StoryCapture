package com.palmer.thestoryteller.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.GridView;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;


/**
 * Created by Thom on 11/12/2014.
 */
public class GridViewBookshelf extends GridView {

    public GridViewBookshelf(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {
        ImageView imageView = (ImageView) getChildAt(0);
        int count = getChildCount();
        int top = count > 0 ? getChildAt(0).getTop() : 0;

        BitmapCache bitmapCache = new BitmapCache();
        Bitmap scaledBookshelfRow = bitmapCache.getScaledBitmap(R.drawable.bookshelf,
                getResources(), getWidth(),
                imageView.getMeasuredHeight());

        for (int y = top; y < getHeight(); y += scaledBookshelfRow.getHeight()) {
            for (int x = 0; x < getWidth(); x += scaledBookshelfRow.getWidth()) {
                canvas.drawBitmap(scaledBookshelfRow, x, y, null);
            }
        }

        super.dispatchDraw(canvas);
    }


}

