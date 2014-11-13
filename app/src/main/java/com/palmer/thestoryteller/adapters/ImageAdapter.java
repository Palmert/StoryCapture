package com.palmer.thestoryteller.adapters;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.helpers.AsyncImageLoader;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

import java.util.List;

/**
 * Created by Thom on 11/12/2014.
 */
public class ImageAdapter extends BaseAdapter {
    private final int cellWidth;
    private final int cellHeight;
    private Context mContext;
    private List<Book> books;
    private AsyncImageLoader asyncImageLoader;
    private ScaledBitmapCache scaledBitmapCache;


    public ImageAdapter(Context mContext, List<Book> books) {
        this.mContext = mContext;
        this.books = books;
        this.asyncImageLoader = new AsyncImageLoader();
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);
        this.cellWidth = displaySize.x / 3;
        this.cellHeight = displaySize.y / 4;
        scaledBitmapCache = new ScaledBitmapCache(mContext,
                ScaledBitmapCache.createFixedDirectoryLocator(
                        FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE, mContext).toString()));
    }

    public int getCount() {
        return books.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(250, 350));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setPadding(35, 50, 35, 35);
            imageView.setTag(books.get(position).getId());
        } else {
            imageView = (ImageView) convertView;
        }

        asyncImageLoader.loadImageIntoViewAsync(scaledBitmapCache,
                books.get(position).getImageUri(), imageView,
                this.cellWidth, this.cellHeight, mContext.getResources());
        return imageView;
    }
}


