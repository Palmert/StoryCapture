package com.palmer.thestoryteller;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;

import java.io.IOException;
import java.util.List;

import database.Book;
import database.BooksDataSource;

public class BookshelfActivity extends Activity {


    private static final int BOOKS_PER_ROW = 3;
    private static final int SHELVES_PER_SCREEN = 5;
    private static boolean canEdit;
    public List<Book> booksList;
    private Button addBookBtn;
    private MenuItem viewMenuItem;
    private MenuItem manageMenuItem;
    private Intent intent;
    private Uri fileUri;
    private long bookToDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addBookBtn = (Button) findViewById(R.id.addBook);
        addBookBtn.setVisibility(canEdit ? View.VISIBLE : View.INVISIBLE);

        BooksDataSource.data = new BooksDataSource(this);
        buildBookShelf();
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
        fileUri = Helpers.getOutputMediaFileUri(Helpers.MEDIA_TYPE_IMAGE, this);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
        // start the image capture Intent
        startActivityForResult(intent, Helpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Helpers.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    Intent photoPickerIntent = new Intent(Intent.ACTION_EDIT);
                    photoPickerIntent.setDataAndType(fileUri, "image/*");
                    photoPickerIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                    photoPickerIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivityForResult(photoPickerIntent, Helpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE);
                }
                break;
            }
            case Helpers.EDIT_IMAGE_ACTIVITY_REQUEST_CODE: {
                Book book = new Book(fileUri.toString());
                BooksDataSource.data.open();
                book = BooksDataSource.data.create(book);
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
        BooksDataSource.data.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        BooksDataSource.data.close();
    }

    protected void buildBookShelf() {
        BooksDataSource.data.open();
        booksList = BooksDataSource.data.findAllBooks();

        for (int bookIndex = 0, rowIndex = 0; bookIndex < booksList.size() || rowIndex < SHELVES_PER_SCREEN; rowIndex++) {
            TableLayout tblLayout = (TableLayout) findViewById(R.id.tblLayout);

            TableRow tblRow = new TableRow(this);
            tblRow.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT));
            tblRow.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());
            tblRow.setMinimumHeight(getWindowManager().getDefaultDisplay().getHeight() / SHELVES_PER_SCREEN);
            tblRow.setBackgroundResource(R.drawable.bookshelf);

            for (int j = 0; j < BOOKS_PER_ROW && bookIndex < booksList.size(); j++, bookIndex++) {
                Book book = booksList.get(bookIndex);
                Uri imageUri = Uri.parse(book.getImagePath());

                ImageView imageView = new ImageView(this);
                Bitmap thumbnail = null;
                try {
                    thumbnail = Helpers.getThumbnail(imageUri, this);
                    Bitmap standardThumbnail = Bitmap.createScaledBitmap(thumbnail, 185, 275, false);
                    imageView.setImageBitmap(standardThumbnail);
                    imageView.setCropToPadding(true);
                    imageView.setPadding(30, 30, 30, 40);
                    imageView.setHapticFeedbackEnabled(true);
                    imageView.setTag(book.getId());
                    registerForContextMenu(imageView);
                    imageView.setOnClickListener(new OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            long bookId = (Long) v.getTag();
                            if (canEdit) {
                                Intent editStory = new Intent(getApplicationContext(), CaptureStoryActivity.class);
                                editStory.putExtra("bookId", bookId);
                                startActivity(editStory);
                            } else {
                                intent = new Intent(getApplicationContext(), ReadStoryActivity.class);
                                intent.putExtra("bookId", bookId);
                                startActivity(intent);
                            }

                        }
                    });

                    imageView.setOnLongClickListener(new View.OnLongClickListener() {

                        @Override
                        public boolean onLongClick(View v) {
                            return false;
                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }
                tblRow.addView(imageView, j);
            }
            tblLayout.addView(tblRow, rowIndex);
        }
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
                BooksDataSource.data.deleteBook(bookToDelete);
                this.recreate();
            }
            default:
                return super.onContextItemSelected(item);
        }
    }
}
