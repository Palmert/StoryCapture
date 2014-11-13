package com.palmer.thestoryteller.activities;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.adapters.StoryListViewAdapter;
import com.palmer.thestoryteller.data.BooksDataSource;

public class StoryManagerListActivity extends Activity {
    private ListView listView;
    private BooksDataSource booksDataSource;
    private StoryListViewAdapter storyListViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_manager);

        booksDataSource = new BooksDataSource(this);
        booksDataSource.open();

        listView = (ListView) findViewById(R.id.storyList);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_story_manager, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
