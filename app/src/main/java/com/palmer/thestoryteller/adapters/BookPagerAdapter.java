package com.palmer.thestoryteller.adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.Display;
import android.view.WindowManager;

import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.fragments.PageDisplayFragment;
import com.palmer.thestoryteller.fragments.PageEditFragment;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

import java.util.List;

/**
 * Created by Thom on 11/13/2014.
 */
public class BookPagerAdapter extends FragmentStatePagerAdapter {
    public static ScaledBitmapCache scaledBitmapCache;
    private final List<Page> pages;
    private Context mContext;
    private boolean isEditFragment;

    public BookPagerAdapter(FragmentManager fm, List<Page> pages, Context mContext,
                            ScaledBitmapCache scaledBitmapCache, boolean isEditFragment) {
        super(fm);
        this.pages = pages;
        this.mContext = mContext;
        this.scaledBitmapCache = scaledBitmapCache;
        this.isEditFragment = isEditFragment;
    }

    @Override
    public int getCount() {
        return this.pages.size();
    }


    @Override
    public android.support.v4.app.Fragment getItem(int position) {
        WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point displaySize = new Point();
        display.getSize(displaySize);

        if (isEditFragment) {
            PageEditFragment f = PageEditFragment.newInstance();
            f.setImageWidth(displaySize.x);
            f.setImageHeight(displaySize.y);
            f.setPage(pages.get(position));
            f.setScaledBitmapCache(scaledBitmapCache);
            return f;
        } else {
            PageDisplayFragment f = PageDisplayFragment.newInstance();
            f.setImageWidth(displaySize.x);
            f.setImageHeight(displaySize.y);
            f.setPage(pages.get(position));
            f.setScaledBitmapCache(scaledBitmapCache);
            return f;
        }
    }


}