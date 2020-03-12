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

    private static final UriMatcher sUriMatcher;

    private static final int NOTES = 1;

    private static final int NOTES_ID = 2;

    private static HashMap<String, String> notesProjectionMap;

    public class DatabaseHelper extends SQLiteOpenHelper {

        // Constructor
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + NOTES_TABLE_NAME + " (" + Note.Notes.NOTE_ID
                    + " INTEGER PRIMARY KEY AUTOINCREMENT," + Note.Notes.TITLE + " VARCHAR(255)," + Note.Notes.TEXT + " LONGTEXT" + ");");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE_NAME);
            onCreate(db);
        }
    }

    private DatabaseHelper dbHelper;


    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        return true;
    }

//    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(NOTES_TABLE_NAME);
        qb.setProjectionMap(notesProjectionMap);

        switch (sUriMatcher.match(uri)) {
            case NOTES:
                break;
            case NOTES_ID:
                selection = selection + "_id = " + uri.getLastPathSegment();
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

//    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (sUriMatcher.match(uri) != NOTES) {
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;
        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        long rowId = db.insert(NOTES_TABLE_NAME, Note.Notes.TEXT, values);
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(Note.Notes.CONTENT_URI, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

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

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case NOTES:
                count = db.update(NOTES_TABLE_NAME, values, where, whereArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME, NOTES);
        sUriMatcher.addURI(AUTHORITY, NOTES_TABLE_NAME + "/#", NOTES_ID);

        notesProjectionMap = new HashMap<String, String>();
        notesProjectionMap.put(Note.Notes.NOTE_ID, Note.Notes.NOTE_ID);
        notesProjectionMap.put(Note.Notes.TITLE, Note.Notes.TITLE);
        notesProjectionMap.put(Note.Notes.TEXT, Note.Notes.TEXT);
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
