package com.palmer.thestoryteller.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.adapters.ImageAdapter;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.helpers.BitmapCache;
import com.palmer.thestoryteller.helpers.FileHelpers;
import com.palmer.thestoryteller.helpers.GridViewBookshelf;
import com.palmer.thestoryteller.models.BookshelfModel;

public class BookshelfActivity extends Activity {

    private Button addBookBtn;
    private BookshelfModel bookshelfModel;
    private MenuItem viewMenuItem;
    private MenuItem manageMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getActionBar().setDisplayShowTitleEnabled(false);

        bookshelfModel = new BookshelfModel(this);

        addBookBtn = (Button) findViewById(R.id.addBook);
        addBookBtn.setVisibility(bookshelfModel.isEditable() ? View.VISIBLE : View.INVISIBLE);

        GridViewBookshelf bookshelfGrid = (GridViewBookshelf) findViewById(R.id.bookshelfGrid);
        bookshelfGrid.setBitmapCache(new BitmapCache());
        bookshelfGrid.setAdapter(new ImageAdapter(this, bookshelfModel.getBooks()));

        bookshelfGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                final Intent i = new Intent(parent.getContext(), bookshelfModel.isEditable() ?
                        StoryManagerActivity.class : StoryPagerActivity.class);
                i.putExtra(StoryPagerActivity.BOOK_ID, (Long) v.getTag());
                startActivity(i);
            }
        });

        // TODO Find out why getTag is failing in createContextMenu
        //registerForContextMenu(bookshelfGrid);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it ispresent.
        getMenuInflater().inflate(R.menu.main, menu);
        viewMenuItem = menu.findItem(R.id.action_view);
        viewMenuItem.setVisible(bookshelfModel.isEditable());

        manageMenuItem = menu.findItem(R.id.action_manage);
        manageMenuItem.setVisible(!bookshelfModel.isEditable());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_manage) {
            validatePassword();
        } else if (id == R.id.action_view) {
            bookshelfModel.setEditable(false);
            viewMenuItem.setVisible(false);
            manageMenuItem.setVisible(!bookshelfModel.isEditable());
            addBookBtn.setVisibility(bookshelfModel.isEditable() ? View.VISIBLE : View.INVISIBLE);
        }

        return super.onOptionsItemSelected(item);
    }

    private void validatePassword() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Manage Library");
        alert.setMessage("Type 1234");


        // Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Editable value = input.getText();
                if (value != null && value.toString().compareTo("1234") == 0) {
                    bookshelfModel.setEditable(true);
                } else {
                    bookshelfModel.setEditable(false);
                }
                addBookBtn.setVisibility(bookshelfModel.isEditable() ? View.VISIBLE : View.INVISIBLE);
                manageMenuItem.setVisible(!bookshelfModel.isEditable());
                viewMenuItem.setVisible(bookshelfModel.isEditable());
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        alert.show();
    }

    public void addBook(View v) {
        captureImage();
    }

    public void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        bookshelfModel.setFileUri(FileHelpers.getOutputMediaFileUri(
                FileHelpers.MEDIA_TYPE_IMAGE, this));
        intent.putExtra(MediaStore.EXTRA_OUTPUT, bookshelfModel.getFileUri());
        // start the image capture Intent
        startActivityForResult(intent, FileHelpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FileHelpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_EDIT);
                    photoPickerIntent.setDataAndType(bookshelfModel.getFileUri(), "image/*");
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, bookshelfModel.getFileUri());
                    photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(photoPickerIntent, FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
                }
                break;
            }
            case FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE: {
                Book book = new Book(bookshelfModel.getFileUriString());
                bookshelfModel.openDataSource();
                book = bookshelfModel.getBooksDataSource().create(book);
                final Intent i = new Intent(getApplicationContext(), bookshelfModel.isEditable() ?
                        StoryManagerActivity.class : StoryPagerActivity.class);
                i.putExtra(StoryPagerActivity.BOOK_ID, book.getId());
                startActivity(i);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        bookshelfModel.openDataSource();
    }

    @Override
    protected void onPause() {
        super.onPause();
        bookshelfModel.closeDataSource();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if (bookshelfModel.isEditable()) {
            bookshelfModel.setBookToDelete((Long) v.getTag());
            inflater.inflate(R.menu.book_long_press, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_deleteBook: {
                bookshelfModel.getBooksDataSource().deleteBook(bookshelfModel.getBookToDelete());
                this.recreate();
            }
            default:
                return super.onContextItemSelected(item);
        }
    }
}
