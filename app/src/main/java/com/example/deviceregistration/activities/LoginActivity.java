// Main activity that allows user to login credentials or create new account
package com.example.deviceregistration.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.deviceregistration.providers.NotesContentProvider;
import com.example.deviceregistration.utils.BackgroundWorker;
import com.example.deviceregistration.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;

// Modify the existing activity template with login page
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Only used to retrieve data, consider creating a manager class if more functions needed
    private SQLiteDatabase sqLiteDatabase;
    private SQLiteOpenHelper sqLiteOpenHelper;

    // Method created on start-up to initialize login page
    @Override //this method already exists in AppCompatActivity and we are adding to it
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //superclass (parent) object
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(TAG, "onCreate: started");

        // Go to registration page if not registered
        TextView registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });
    } //onCreate

    // Method that initializes settings tab
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    // Checks which item was clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Toast.makeText(this, "Item 1 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show();
                // Enter code here to go another activity
                return true;
            case R.id.item3:
                Toast.makeText(this, "Item 3 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem2_1:
                Toast.makeText(this, "Sub Item 1 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem2_2:
                Toast.makeText(this, "Sub Item 2 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem2_3:
                Toast.makeText(this, "Sub Item 3 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem3_1:
                Toast.makeText(this, "Sub Item 1 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem3_2:
                Toast.makeText(this, "Sub Item 2 selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.subitem3_3:
                Toast.makeText(this, "Sub Item 3 selected", Toast.LENGTH_SHORT).show();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    // Method that executes upon pressing button on main page
    public void loginClick(View view) {

        // Checks for both network availability and ACTUAL connection
        if (!(isNetworkAvailable() || isOnline())) {
            makeToast("Network off.");
            return;
        }

//        getNotes();
        // Find handles for text fields
        EditText usernameEditText = findViewById(R.id.usernameEditText); //resources.id.tag name
        EditText passwordEditText = findViewById(R.id.passwordEditText);

        // Display information on info log
        Log.i(  "Info", "Login button pressed!");            //display a message when button is pressed
        Log.i(  "Values", usernameEditText.getText().toString()); //grab information entered by username
        Log.i(  "Values", passwordEditText.getText().toString()); //and pw

        // Convert login credentials to strings
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String type = "login";

        // Obtain table values of serial numbers and MAC addresses
        Cursor cursor = getInfo(); //cursor holds all rows of data
        JSONObject rowObject;
        JSONArray resultArray = new JSONArray();
        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            rowObject = new JSONObject();
            try {
                rowObject.put(cursor.getColumnName(2), cursor.getString(2));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                rowObject.put(cursor.getColumnName(1), cursor.getString(1));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resultArray.put(rowObject);
        }
        JSONObject returnObject = new JSONObject();
        try {
            returnObject.put("data", resultArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String jString = returnObject.toString();
//        String SN = cursor.getString(1);
//        String MAC = cursor.getString(2);

        // Hash 256
        String hashedPassword = sha256(password);
        Log.d("Info", "loginClick: "+ hashedPassword);

        // Allow background to obtain context and store information
        BackgroundWorker backgroundWorker = new BackgroundWorker(this); // declare, instantiate, initialize
        backgroundWorker.execute(type, username, password, jString);                 // pass user info as strings

    }//loginClick


    public static String sha256(String base) {
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(base.getBytes("UTF-8"));
            StringBuffer hexString = new StringBuffer();

            for (int i = 0; i < hash.length; i++) {
                String hex = Integer.toHexString(0xff & hash[i]);
                if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch(Exception ex){
            throw new RuntimeException(ex);
        }
    }


    // Toast user
    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_LONG).show();
    }


    // Prevent the user from pressing back button
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Restart the app", Toast.LENGTH_SHORT).show();
    }


    // Check if network is available
    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

//    public Boolean isOnline() {
//        try {
//            Process p1 = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
//            int returnVal = p1.waitFor();
//            boolean reachable = (returnVal==0);
//            return reachable;
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return false;
//    }

    // Checks for ACTUAL connection
    // Uses ICMP protocol to ping google DNS - guaranteed available
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8"); // google DNS
            int     exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        }
        catch (IOException e)          { e.printStackTrace(); }
        catch (InterruptedException e) { e.printStackTrace(); }
        return false;
    }

    // Queries the database to obtain the SN + MAC address pairs
    public Cursor getInfo() {
        Cursor cursor = getContentResolver().query(NotesContentProvider.Note.Notes.CONTENT_URI,
                null, null, null, null);

        if (cursor != null) {
            cursor.moveToNext();
        }
        return cursor;
    }
} //LoginActivity class end
