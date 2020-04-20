package com.example.deviceregistration.models;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import androidx.test.InstrumentationRegistry;

import com.example.deviceregistration.providers.NotesContentProvider;
import org.junit.Test;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class JSONPlaceHolderApiTest {
    private static final String TAG = "JSONPlaceHolderApiTest";


    private String username = "Ramir", password = "Ramirissexy", email = "fazlay_rabby@hotmail.com";

    // Query database through content provider and superimpose un + pw onto json object and compares
    // it to a known json string to see if function worked
    @Test
    public void jsonFileForLoginTest() throws Exception {
        String compareString = "{\"Login\":[{\"username\":\"Ramir\"},{\"password\":\"Ramirissexy\"}]}";
        JSONPlaceHolderApi jsonPlaceHolderApi = new JSONPlaceHolderApi();
        Context context = InstrumentationRegistry.getTargetContext();
        Cursor cursor = getInfo(context); //row iterator for SQLiteDB through content provider
        String jString = jsonPlaceHolderApi.JSONObjectLogin(username, password, cursor);
        assertEquals(compareString, jString);
    }

    @Test
    public void jsonFileForRegisterTest() throws Exception {

        String compareString = "{\"Register\":[{\"username\":\""+username+"\"},{\"email\":\""+email+"\"},{\"password_1\":\""+password+"\"},{\"password_2\":\""+password+"\"}]}";
        JSONPlaceHolderApi json = new JSONPlaceHolderApi();

        String jString = json.JSONObject(username, password, password, email);

        assertEquals(compareString, jString);
    }

    // Queries the database to obtain the SN + MAC address pairs
    public Cursor getInfo(Context context) {
        Cursor cursor = context.getContentResolver().query(NotesContentProvider.Note.Notes.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToNext();
        }
        return cursor;
    }



}