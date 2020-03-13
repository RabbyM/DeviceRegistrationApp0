// Custom Content Provider
// Uses SQLite Database for a self sustained serverless storage that can be shared with other apps
// Used to share and access to a structured set of data

// Methods to use in a Content Provider
//        query(Uri, String[], String, String[], String) which returns data to the client request
//        insert(Uri, ContentValues) which inserts new data into the content provider
//        update(Uri, ContentValues, String, String[]) which updates existing data in the content provider
//        delete(Uri, String, String[]) which deletes data from the content provider
//        getType(Uri) which returns the MIME type of data in the content provider

// Content URIs
//        A Content URIs identifies the structured set of data in a provider. The Content URIs have the syntax content://authority/path/id
//        content: Is the scheme portion of the URI and always looks like “content://”
//        authority: Content Provider identifier. All the content URIs for the provider must start with this field . To guarantee a unique authority it is advisable to use the same as the provider class package identifier.
//        path: Optional segments separated by a forward slash (/) that identify some subset of the provider’s data for example this is used to identify some individual data tables.
//        id: A unique numeric identifier for a single row in the subset of data.

//todo close database after using it somehow

package com.example.deviceregistration.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import java.util.HashMap;
import android.content.ContentUris;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.provider.BaseColumns;
import android.util.Log;


public class NotesContentProvider extends ContentProvider {

    private static final String TAG = "NotesContentProvider";

    private static final String DATABASE_NAME = "notes.db";

    private static final int DATABASE_VERSION = 1;

    public static final String NOTES_TABLE_NAME = "notes";

    public static final String AUTHORITY = "com.example.deviceregistration.providers.NotesContentProvider";

    // Codes to return when uri is matched
    private static final int NOTES = 1; // all notes
    private static final int NOTES_ID = 2; // single note

    // Map table columns
    private static HashMap<String, String> notesProjectionMap;

    // Create Uri Matcher which has to be in static block so that it can run first
    private static final UriMatcher sUriMatcher;
    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME, NOTES);
        sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME + "/#", NOTES_ID);

        notesProjectionMap = new HashMap<String, String>();
        notesProjectionMap.put(Note.Notes.NOTE_ID, Note.Notes.NOTE_ID);
        notesProjectionMap.put(Note.Notes.TITLE, Note.Notes.TITLE);
        notesProjectionMap.put(Note.Notes.TEXT, Note.Notes.TEXT);
    }

    private SQLiteDatabase db;
    private DatabaseHelper dbHelper;

    public static class DatabaseHelper extends SQLiteOpenHelper {

        // Constructor
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        // Create new table
        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.d("DatabaseHelper: ", "onCreate called");
            db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + " (" + Note.Notes.NOTE_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + Note.Notes.TITLE
                    + " VARCHAR(255)," + Note.Notes.TEXT + " LONGTEXT" + ");");
        }

        // Drop table if it exists
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
            onCreate(db);
        }
    }//end DatabaseHelper inner class


    // Creates the database if does not already exist
    // Returns false if the database is inaccessible
    @Override
    public boolean onCreate() {
        boolean ret = true;
        dbHelper = new DatabaseHelper(getContext());
        db = dbHelper.getWritableDatabase();

        if (db == null) {
            ret = false;
        }

        if (db.isReadOnly()) {
            db.close();
            db = null;
            ret = false;
        }
        return ret;
    }//end onCreate


    // Used to retrieve the data stored in the database and return a cursor instance
    // A cursor is used to navigate the rows of the database
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder(); //new instance of query builder
        qb.setTables(NOTES_TABLE_NAME); //set the table name
        qb.setProjectionMap(notesProjectionMap);

        // if URI matches
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTES_ID:
//                qb.appendWhere(ID+" = "+uri.getLastPathSegment());
                selection = selection + "_id = " + uri.getLastPathSegment(); //old code
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

//    @Nullable
    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                return Note.Notes.CONTENT_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    // Insert the values into the table
    // Returns the uri of the new record
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        // Check if the uri received matches the uri of the table
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        //todo do i need this?
        //if the values are not null then put them in an instance of content values
        //otherwise create a new empty instance of contentvalues
        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        // Store the values into the table
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(NOTES_TABLE_NAME, Note.Notes.TEXT, values);

        // Return uri if there is a record added to the table
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Note.Notes.CONTENT_URI, rowId); //create new uri
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        // Throw an exception if record is not stored
        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTES_ID:
                where = where + "_id = " + uri.getLastPathSegment();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        int count = db.delete(NOTES_TABLE_NAME, where, whereArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    //    Uri uri: The URI to query. It can be the URI of a single record, or the URI of a table.
    //    ContentValues contentValues: A set of key-value pairs, with the column name as a key and the values to update as values.
    //    String s: A selection to match the rows which are going to be updated.
    //    String[] strings: The arguments of the above rows.

    // Change an existing record of a table in the database
    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) { //check if uri matches a table or record
            case NOTES:
                count = db.update(NOTES_TABLE_NAME, values, where, whereArgs); //pass the same parameters as the method except use table URI
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }



    public static class Note {

        // Constructor
        public Note() {
        }

        // Keeps all columns organized and readily accessible
        public static final class Notes implements BaseColumns {

            // Constructor
            private Notes() {
            }

            public static final Uri CONTENT_URI = Uri.parse("content://" + NotesContentProvider.AUTHORITY + "/notes");

            //todo change this name later
            public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/serialnumber.tasksDB/"+ NOTES_TABLE_NAME;

            public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/serialnumber/tasksDB" + NOTES_TABLE_NAME;

            public static final String NOTE_ID = "_id";

            public static final String TITLE = "title";

            public static final String TEXT = "text";
        }

    }
}//end ContentProvider outer class
