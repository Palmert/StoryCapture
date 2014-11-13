package com.palmer.thestoryteller.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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
import android.widget.GridView;

import com.palmer.thestoryteller.R;
import com.palmer.thestoryteller.adapters.ImageAdapter;
import com.palmer.thestoryteller.data.Book;
import com.palmer.thestoryteller.data.BooksDataSource;
import com.palmer.thestoryteller.helpers.FileHelpers;

public class BookshelfActivity extends Activity {

    private static boolean canEdit;
    private Button addBookBtn;
    private MenuItem viewMenuItem;
    private MenuItem manageMenuItem;
    private Intent intent;
    private Uri fileUri;
    private long bookToDelete;
    private BooksDataSource booksDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBookBtn = (Button) findViewById(R.id.addBook);
        addBookBtn.setVisibility(canEdit ? View.VISIBLE : View.INVISIBLE);

        booksDataSource = new BooksDataSource(this);
        booksDataSource.open();
        GridView bookshelfGrid = (GridView) findViewById(R.id.bookshelfGrid);
        bookshelfGrid.setAdapter(new ImageAdapter(this, booksDataSource.findAllBooks()));

        bookshelfGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                //TODO open view pager in child mode pass it page collection
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        viewMenuItem = menu.findItem(R.id.action_view);
        viewMenuItem.setVisible(canEdit);

        manageMenuItem = menu.findItem(R.id.action_manage);
        manageMenuItem.setVisible(!canEdit);
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
            canEdit = false;
            viewMenuItem.setVisible(false);
            manageMenuItem.setVisible(!canEdit);
            addBookBtn.setVisibility(canEdit ? View.VISIBLE : View.INVISIBLE);
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
                    canEdit = true;
                } else {
                    canEdit = false;
                }
                addBookBtn.setVisibility(canEdit ? View.VISIBLE : View.INVISIBLE);
                manageMenuItem.setVisible(!canEdit);
                viewMenuItem.setVisible(canEdit);
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
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = FileHelpers.getOutputMediaFileUri(FileHelpers.MEDIA_TYPE_IMAGE, this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
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
                    photoPickerIntent.setDataAndType(fileUri, "image/*");
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(photoPickerIntent, FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
                }
                break;
            }
            case FileHelpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE: {
                Book book = new Book(fileUri.toString());
                booksDataSource.open();
                book = booksDataSource.create(book);
                Intent newStoryCapture = new Intent(getApplicationContext(), CaptureStoryActivity.class);
                newStoryCapture.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                newStoryCapture.putExtra("bookId", book.getId());
                startActivity(newStoryCapture);
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        booksDataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        booksDataSource.close();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        if (canEdit) {
            bookToDelete = (Long) v.getTag();
            inflater.inflate(R.menu.book_long_press, menu);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.action_deleteBook: {
                booksDataSource.deleteBook(bookToDelete);
                this.recreate();
            }
            default:
                return super.onContextItemSelected(item);
        }
    }
}
