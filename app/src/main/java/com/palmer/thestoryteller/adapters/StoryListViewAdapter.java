package com.palmer.thestoryteller.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.data.Page;
import com.palmer.thestoryteller.helpers.ImageHelpers;
import com.palmer.thestoryteller.helpers.ScaledBitmapCache;

import java.util.List;


/**
 * Created by Thom on 11/13/2014.
 */
public class StoryListViewAdapter extends ArrayAdapter<Page> {

    private ScaledBitmapCache scaledBitmapCache;

    public StoryListViewAdapter(Context context, int resource, List<Page> pages, ScaledBitmapCache scaledBitmapCache) {
        super(context, resource, pages);
        this.scaledBitmapCache = scaledBitmapCache;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Page page = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_page, parent, false);
        }

        ImageView pageThumbnail = (ImageView) convertView.findViewById(R.id.pageThumbnail);
        pageThumbnail.setCropToPadding(true);
        pageThumbnail.setPadding(8, 8, 8, 8);

        ImageHelpers.loadImageIntoViewAsync(scaledBitmapCache,
                page.getImageUri(), pageThumbnail, 64, 64, getContext().getResources());

        ImageView audio = (ImageView) convertView.findViewById(R.id.audio);
        audio.setCropToPadding(true);

        //TODO Write code to view whether a page has audio and play it from thumbnail press
        //audio.setVisibility(page.getAudioPath() == null ? View.INVISIBLE : View.VISIBLE);
        audio.setVisibility(View.INVISIBLE);

        return convertView;
    }
}
