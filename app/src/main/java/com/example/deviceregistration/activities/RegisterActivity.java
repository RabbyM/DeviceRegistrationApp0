package com.example.deviceregistration.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.example.deviceregistration.R;
import com.example.deviceregistration.providers.NotesContentProvider;
import com.example.deviceregistration.utils.BackgroundWorker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

import static com.example.deviceregistration.activities.LoginActivity.sha256;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started");

        // Return to main page if registered
        TextView rLoginTextView = findViewById(R.id.rLoginTextView);
        rLoginTextView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
    }

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method that executes upon pressing button on main page
    public void registerClick(View view) {

        // Checks for both network availability and ACTUAL connection
        if (!(isNetworkAvailable() || isOnline())) {
            makeToast("Network off.");
            return;
        }

        // Find handles for text fields
        EditText rUsernameEditText = findViewById(R.id.rUsernameEditText);
        EditText rPasswordEditText = findViewById(R.id.rPasswordEditText);
        EditText rConfirmPasswordEditText = findViewById(R.id.rConfirmPasswordEditText);
        TextView alertTextView = findViewById(R.id.alertTextView);

        // Convert login credentials to strings
        String username = rUsernameEditText.getText().toString();
        String password = rPasswordEditText.getText().toString();
        String confirmPassword = rConfirmPasswordEditText.getText().toString();
        String type = "login";

        // Display information on info log
        Log.i("Info", "Login button pressed!");            //display a message when button is pressed
        Log.i("Values", username); //grab information entered by username
        Log.i("Values", password); //and pw
        Log.i("Values", confirmPassword); //and pw

        // Ensure user enters the desired password
        if (password != confirmPassword) {
            return;
        }

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

        //todo add new database for registering user
        // Iterate through each row of the SQLite DB and store into JSON
        Cursor cursor = getInfo(); //row iterator for SQLiteDB through content provider
        cursor.moveToFirst(); //ensure the rows starts from the beginning
        while (cursor.moveToNext()) {
            rowObject = new JSONObject(); //create a new object for every row
            try {
                rowObject.put(cursor.getColumnName(2), cursor.getString(2)); //obtain title
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                rowObject.put(cursor.getColumnName(1), cursor.getString(1)); //obtain text
            } catch (JSONException e) {
                e.printStackTrace();
            }
            resultArray.put(rowObject); //store each JSON row object into a JSON array
        }

        // Store the JSON array into an outer JSON object and convert it into a string
        JSONObject returnObject = new JSONObject();
        try {
            returnObject.put("Register", resultArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jString = returnObject.toString();

        // Hash 256
        String hashedPassword = sha256(password);
        Log.d("Info", "registerClick: " + hashedPassword);

        // Perform logging in (networking operations) in background
        BackgroundWorker backgroundWorker = new BackgroundWorker(this, alertTextView);
        backgroundWorker.execute(type, username, password, jString);


    }//loginClick

    // Toast user
    private void makeToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }


    // Prevent the user from pressing back button
    @Override
    public void onBackPressed() {
        Toast.makeText(this, "Restart the app", Toast.LENGTH_SHORT).show();
    }


    // Check if network is available
    public Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }


    // Checks for ACTUAL connection
    // Uses ICMP protocol to ping google DNS - guaranteed available
    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8"); // google DNS
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
}
