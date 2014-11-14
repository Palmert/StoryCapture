package com.palmer.thestoryteller.fragments;

import android.net.Uri;
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
public class PageEditFragment extends Fragment {

    private ImageView mImageView;
    private Page page;
    private ScaledBitmapCache scaledBitmapCache;
    private int imageWidth;
    private int imageHeight;
    private Uri imageUri;


    // Empty constructor, required as per Fragment docs
    public PageEditFragment() {
    }

    public static PageEditFragment newInstance() {
        PageEditFragment f = new PageEditFragment();
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
        final View v = inflater.inflate(R.layout.fragment_story_page_edit, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ImageHelpers.loadImageIntoViewAsync(scaledBitmapCache, page.getImageUri(),
                mImageView, imageWidth, imageHeight, getResources());
    }


    //TODO USE ARGUMENTS INSTEAD OF FIELDS
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