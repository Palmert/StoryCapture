package com.palmer.thestoryteller.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.data.Page;

/**
 * Created by Thom on 11/12/2014.
 */
public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_PATH = "imagePath";
    private static final String AUDIO_PATH = "audioPath";
    private static final String PAGE_ID = "pageId";
    private ImageView mImageView;
    private Page page;

    // Empty constructor, required as per Fragment docs
    public ImageDetailFragment() {
    }

    static ImageDetailFragment newInstance(Page page, int pageId) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putInt(PAGE_ID, pageId);
        args.putString(IMAGE_PATH, page.getImagePath());
        args.putString(AUDIO_PATH, page.getAudioPath());
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = new Page();
        page.setImagePath(getArguments().getString(IMAGE_PATH));
        page.setAudioPath(getArguments().getString(AUDIO_PATH));
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
        mImageView.setImageURI(page.getImageUri());
    }
}