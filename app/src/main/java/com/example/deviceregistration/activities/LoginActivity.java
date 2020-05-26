/** Activity that logins in and sends machine info in a JSON to server */

package com.example.deviceregistration.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.example.deviceregistration.models.JSONPlaceHolderApi;
import com.example.deviceregistration.providers.NotesContentProvider;
import com.example.deviceregistration.utils.BackgroundWorker;
import com.example.deviceregistration.R;
import java.io.IOException;
import java.security.MessageDigest;

// Modify the existing activity template with login page
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

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
        if (!(isNetworkAvailable() && isOnline())) {
            makeToast("Network off.");
            return;
        }

        // Find handles for text fields
        EditText usernameEditText = findViewById(R.id.usernameEditText); //resources.id.tag name
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        TextView alertTextView = findViewById(R.id.alertTextView);
        ProgressBar searchProgressBar = findViewById(R.id.searchProgressBar);
//        Context context = getApplicationContext();

        // Convert login credentials to strings
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String type = "login";

        // Display information on info log
        Log.i(  "Info", "Login button pressed!");            //display a message when button is pressed
        Log.i(  "Username", username); //grab information entered by username
        Log.i(  "Password", password); //and pw
        Log.i(  "Type", type); //and pw

        // Put the username, password, and database contents into a JSON file
        Cursor cursor = getInfo(); //row iterator for SQLiteDB through content provider
        JSONPlaceHolderApi jsonPlaceHolderApi = new JSONPlaceHolderApi();
        String jString = jsonPlaceHolderApi.JSONObjectLogin(username, password, cursor);

        // Hash 256
        String hashedPassword = sha256(password);
        Log.d("Info", "loginClick: " + hashedPassword);

        // Perform logging in (networking operations) in background
        BackgroundWorker backgroundWorker = new BackgroundWorker(this);
        backgroundWorker.execute(type, jString);
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
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // Prevent the user from pressing back button
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Restart the app", Toast.LENGTH_SHORT).show();
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
