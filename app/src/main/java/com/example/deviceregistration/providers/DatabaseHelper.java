// SQLite Content Provider
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


package com.example.deviceregistration.providers;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

//Modify an existing SQL Class to communicate with SQL database
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "register.db";
    public static final String TABLE_NAME = "register";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "FirstName";
    public static final String COL_3 = "LastName";
    public static final String COL_4 = "Password";
    public static final String COL_5 = "Email";
    public static final String COL_6 = "Phone";

    // Constructor
    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE" + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTO INCREMENT, FirstName TEXT, LastName TEXT, Password TEXT, Email TEXT, Phone TEXT");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //db.exec
    }
}
