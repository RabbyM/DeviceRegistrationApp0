//todo this was using only SLQite DB and not content provider, no longer used
package com.example.deviceregistration.models;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.deviceregistration.providers.NotesContentProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

//            1) Initilize an ArrayList
//            2) Retrieve all the data from your database and store it in the ArrayList
//            3) Use gson.toJson(NameOfArrayList);
//            4) DONE. You now have a string containing all the data from your database.
public class JSONPlaceHolderApi {


    private ArrayList<String> machineInfo = new ArrayList<>();

    private JSONArray getResults(String TABLE_NAME, String path) {
        String searchQuery = "SELECT * FROM " + TABLE_NAME; //SQL select all rows

        String myPath = path;

        SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        Cursor cursor = myDataBase.rawQuery(searchQuery, null);

        JSONArray resultSet = new JSONArray();

        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();

            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        if (cursor.getString(i) != null) {
                            Log.d(TAG, "getResults: " + cursor.getString(i));
                            rowObject.put(cursor.getColumnName(i), cursor.getString(i));
                        } else {
                            rowObject.put(cursor.getColumnName(i), "");
                        }
                    } catch (JSONException e) {
                        Log.d(TAG, "getResults: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }
        Log.d(TAG, "getResults: " + resultSet.toString());
        return resultSet;
    }
}
