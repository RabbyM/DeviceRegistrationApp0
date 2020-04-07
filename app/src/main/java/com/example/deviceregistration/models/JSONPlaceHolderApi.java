package com.example.deviceregistration.models;

import android.database.Cursor;

import com.example.deviceregistration.activities.RegisterActivity;
import com.example.deviceregistration.providers.NotesContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONPlaceHolderApi {

    public String JSONObjectLogin(String username, String password, Cursor cursor) {
        // Store username and password into the front of the the JSON array
        JSONObject rowObject = new JSONObject();
        JSONArray resultArray = new JSONArray();
        try {
            rowObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultArray.put(rowObject);
        rowObject = new JSONObject(); //create a new object for every row
        try {
            rowObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultArray.put(rowObject);

        // Iterate through each row of the SQLite DB and store into JSON
        cursor.moveToFirst(); //ensure the rows starts from the beginning
        while (cursor.moveToNext()) {
            rowObject = new JSONObject(); //create a new object for every row
            try {
                rowObject.put(cursor.getColumnName(1), cursor.getString(1)); //obtain title
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                rowObject.put(cursor.getColumnName(0), cursor.getString(0)); //obtain text
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resultArray.put(rowObject); //store each JSON row object into a JSON array
        }

        // Store the JSON array into an outer JSON object, label it, and convert it into a string
        JSONObject returnObject = new JSONObject();
        try {
            returnObject.put("Login", resultArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject.toString();
    }

    public String JSONObject(String username, String password, String cPassword, String email) {
        // Store username and password into the front of the the JSON array
        JSONObject rowObject = new JSONObject();
        JSONArray resultArray = new JSONArray();

        // Username
        try {
            rowObject.put("username", username);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultArray.put(rowObject);

        // E-mail
        rowObject = new JSONObject(); //create a new object for every row
        try {
            rowObject.put("email", email);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultArray.put(rowObject);

        // Password
        rowObject = new JSONObject(); //create a new object for every row
        try {
            rowObject.put("password_1", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultArray.put(rowObject);

        // Confirmed password
        rowObject = new JSONObject(); //create a new object for every row
        try {
            rowObject.put("password_2", cPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        resultArray.put(rowObject);

        // Store the JSON array into an outer JSON object, label it, and convert it into a string
        JSONObject returnObject = new JSONObject();
        try {
            returnObject.put("Register", resultArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnObject.toString();
    }
}
