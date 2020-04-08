package com.example.deviceregistration.models;

import android.content.Context;
import android.database.Cursor;

import com.example.deviceregistration.providers.NotesContentProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.*;

public class JSONPlaceHolderApiTest {

    private String username = "Ramir", password = "Ramirissexy", email = "fazlay_rabby@hotmail.com";

    //todo NEED TO ASK TEJ ON HOW TO GIVE CONTEXT TO UNIT TEST
//    // Query database through content provider and superimpose un + pw onto json object and compares
//    // it to a known json string to see if function worked
//    @Test
//    public void jsonFileForLoginTest() throws Exception {
//        JSONPlaceHolderApi jsonPlaceHolderApi = new JSONPlaceHolderApi();
//        Cursor cursor = getInfo(); //row iterator for SQLiteDB through content provider
//        String jString = jsonPlaceHolderApi.JSONObjectLogin(username, password, cursor);
//
//    }

    @Test
    public void jsonFileForRegisterTest() throws Exception {
        String compareString = "{\"Register\":[{\"username\":\"Ramir\"},{\"email\":\"fazlay_rabby@hotmail.com\"},{\"password_1\":\"Ramirissexy\"},{\"password_2\":\"Ramirissexy\"}]}";
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