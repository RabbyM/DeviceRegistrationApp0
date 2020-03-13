package com.example.deviceregistration.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;
import android.widget.TextView;

import com.example.deviceregistration.R;
import com.example.deviceregistration.providers.NotesContentProvider;

public class DatabaseActivity extends AppCompatActivity {

    private static final String TAG = "DatabaseActivity";

    private ListView myTaskListView;
    private NotesContentProvider.DatabaseHelper myHelper;
    TextView queryResultTextView;

    private String[] columnProjection = new String[] {
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.Contacts.CONTACT_STATUS,
            ContactsContract.Contacts.HAS_PHONE_NUMBER};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        //todo store onto local database
        // Get the content resolver which will send a message to the content provider
        ContentResolver contentResolver = getContentResolver();

        // cursor iterates over rows of a table
        // contentResolver.query method is equivalent to SELECT SQL statement
        // Args:
        // 1 - Content URI: Use custom content provider as URI - tells us which table to query
        // 2 - Projection: number of columns/particular set of columns that you want to query - string array of names of columns
        // 3 - Selection Clause: equivalent to where clause - condition
        // 4 - Sort Order: Which order you want the query to be sorted
        Cursor cursor = contentResolver.query(NotesContentProvider.Note.Notes.CONTENT_URI, null, null, null, null);

        if ((cursor!=null) && (cursor.getCount()>0)) {
            StringBuilder stringBuilderQueryResult = new StringBuilder("");
            while (cursor.moveToNext()) {
                stringBuilderQueryResult.append(cursor.getString(0)+" , "+cursor.getString(1)+" , "+cursor.getString(2)+"\n");
            }
            queryResultTextView.setText(stringBuilderQueryResult.toString());
        } else {
            queryResultTextView.setText("Invalid");
        }



//        // Show the database
//        myHelper = new NotesContentProvider.DatabaseHelper(this);
//
////        updateUI();
    }


}
