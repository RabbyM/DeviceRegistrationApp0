package com.example.deviceregistration;

import android.database.Cursor;

import com.example.deviceregistration.activities.LoginActivity;
import com.example.deviceregistration.models.JSONPlaceHolderApi;
import com.example.deviceregistration.providers.NotesContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import static org.junit.Assert.*;

public class JSONPlaceHolderApiTest {

    private String username = "Ramir", password = "Ramirissexy";

    @Test
    public void jsonFile() throws Exception {
        JSONPlaceHolderApi jsonPlaceHolderApi = new JSONPlaceHolderApi();
        Cursor cursor = getInfo(); //row iterator for SQLiteDB through content provider
        String jString = jsonPlaceHolderApi.JSONObjectLogin(username, password, cursor);
    }

    // Queries the database to obtain the SN + MAC address pairs
    public Cursor getInfo() {
        Cursor cursor = null;// = getContentResolver().query(NotesContentProvider.Note.Notes.CONTENT_URI,
              //  null, null, null, null);

        if (cursor != null) {
            cursor.moveToNext();
        }
        return cursor;
    }



}