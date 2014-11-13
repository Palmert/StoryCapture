package com.palmer.thestoryteller.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.ImageHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

/**
 * Created by Thom on 11/12/2014.
 */
public class ImageDetailFragment extends Fragment {

    private ImageView mImageView;
    private Page page;
    private ScaledBitmapCache scaledBitmapCache;
    private int imageWidth;
    private int imageHeight;

    // Empty constructor, required as per Fragment docs
    public ImageDetailFragment() {
    }

    static ImageDetailFragment newInstance() {
        ImageDetailFragment f = new ImageDetailFragment();
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // image_detail_fragment.xml contains just an ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageHelpers.loadImageIntoViewAsync(scaledBitmapCache, page.getImageUri(),
                mImageView, imageWidth, imageHeight, getResources());
    }

    public void setPage(Page page) {
        this.page = page;
    }

    public void setScaledBitmapCache(ScaledBitmapCache scaledBitmapCache) {
        this.scaledBitmapCache = scaledBitmapCache;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }
}