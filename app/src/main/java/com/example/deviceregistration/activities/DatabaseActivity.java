//todo clear the database on app launch

// Used as example to display the database
package com.example.deviceregistration.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.deviceregistration.R;
import com.example.deviceregistration.providers.NotesContentProvider;

public class DatabaseActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "DatabaseActivity";
    private EditText title, content, delete_id;
    private Button add, update, delete, showNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: started");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // Find IDs
        title = (EditText) findViewById(R.id.title);
        content = (EditText) findViewById(R.id.content);
        delete_id = (EditText) findViewById(R.id.delete_id);

        // add Click Listeners
        add = (Button) findViewById(R.id.add);
        add.setOnClickListener(this);
        update = (Button) findViewById(R.id.update);
        update.setOnClickListener(this);
        delete = (Button) findViewById(R.id.delete);
        delete.setOnClickListener(this);
        showNotes = (Button) findViewById(R.id.show_notes);
        showNotes.setOnClickListener(this);

        // Autofill MAC and SN
        String SN = getIntent().getStringExtra("SN"); //access intent from previous activity
        String MAC = getIntent().getStringExtra("MAC"); //access intent from previous activity
        title.setText(SN);
        content.setText(MAC);
        getNotes();

        // Automatically add SN and MAC
        addNote();

    }

    // Title and content required to ADD a note
    void addNote() {
        if (title.getText().toString().length() > 0 && content.getText().toString().length() > 0) {
            // Store a set of values that the ContentResolver can process
            ContentValues contentValues = new ContentValues();
            contentValues.put(NotesContentProvider.Note.Notes.TITLE, title.getText().toString());
            contentValues.put(NotesContentProvider.Note.Notes.TEXT, content.getText().toString());

            // Content resolver queries the content provider and notify success
            getContentResolver().insert(NotesContentProvider.Note.Notes.CONTENT_URI, contentValues);
            Log.d(TAG, "Inserted");
            makeToast("Note Added");
        } else {
            makeToast("Empty Field");
        }
    }

    //
    void deleteNote(String str_id) {
        try {
            int id = Integer.parseInt(str_id);
            Log.i(TAG, "Deleting with id = " + id);
            getContentResolver().delete(NotesContentProvider.Note.Notes.CONTENT_URI,
                    NotesContentProvider.Note.Notes.NOTE_ID + " = " + id, null);
            Log.i(TAG, "Deleted");
            makeToast("Note Deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void updateNote(String str_id) {
        try {
            int id = Integer.parseInt(str_id);
            Log.i(TAG, "Updating with id = " + id);
            ContentValues contentValues = new ContentValues();
            contentValues.put(NotesContentProvider.Note.Notes.TITLE, title.getText().toString());
            contentValues.put(NotesContentProvider.Note.Notes.TEXT, content.getText().toString());
            getContentResolver().update(NotesContentProvider.Note.Notes.CONTENT_URI, contentValues,
                    NotesContentProvider.Note.Notes.NOTE_ID + " = " + id, null);
            makeToast("Note Updated");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void getNotes() {

        // contentResolver.query method is equivalent to SELECT SQL statement
        // Args:
        // 1 - Content URI: Use custom content provider as URI - tells us which table to query
        // 2 - Projection: number of columns/particular set of columns that you want to query - string array of names of columns
        // 3 - Selection Clause: equivalent to where clause - condition
        // 4 - Sort Order: Which order you want the query to be sorted
        Cursor cur = getContentResolver().query(NotesContentProvider.Note.Notes.CONTENT_URI,
                null, null, null, null);

        // Cursor iterates over rows of a table
        if (cur.getCount() > 0) {
            Log.i(TAG, "Showing values.....");
            while (cur.moveToNext()) {
                String ID = cur.getString(cur.getColumnIndex(NotesContentProvider.Note.Notes.NOTE_ID));
                String title = cur.getString(cur.getColumnIndex(NotesContentProvider.Note.Notes.TITLE));
                String text = cur.getString(cur.getColumnIndex(NotesContentProvider.Note.Notes.TEXT));
                System.out.println("ID = " + ID + ", SN : " + title + ", MAC: " + text);
            }
            makeToast("Check the LogCat for Notes");
        } else {
            Log.i(TAG, "No Notes added");
            makeToast("No Notes added");
        }
    }

    // onclick interface for all buttons
    @Override
    public void onClick(View v) {
        if (v == add) {
            // add note with title and content
            addNote();
//            startActivity(new Intent(DatabaseActivity.this, CheckmarkActivity.class));
        }
        if (v == update) {
            // update note with ID
            updateNote(delete_id.getText().toString());
            startActivity(new Intent(DatabaseActivity.this, CheckmarkActivity.class));
        }
        if (v == delete) {
            // delete note with ID
            deleteNote(delete_id.getText().toString());
        }
        if (v == showNotes) {
            // show all
            getNotes();
        }
    }


    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
