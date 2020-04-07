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
import com.example.deviceregistration.models.JSONPlaceHolderApi;
import com.example.deviceregistration.providers.NotesContentProvider;
import com.example.deviceregistration.utils.BackgroundWorker;
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
        EditText emailEditText = findViewById(R.id.emailEditText);
        TextView alertTextView = findViewById(R.id.alertTextView);

        // Convert login credentials to strings
        String username = rUsernameEditText.getText().toString();
        String password = rPasswordEditText.getText().toString();
        String confirmPassword = rConfirmPasswordEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String type = "register";

        // Display information on info log
        Log.i("Info", "Login button pressed!");            //display a message when button is pressed
        Log.i("Values", username); //grab information entered by username
        Log.i("Values", password); //and pw
        Log.i("Values", confirmPassword); //and pw
        Log.i("Values", email); //and pw

        // Ensure user enters the desired password
        if (!(password.equals(confirmPassword))) {
            return;
        }

        // Put the user info into a JSON
        JSONPlaceHolderApi jsonPlaceHolderApi = new JSONPlaceHolderApi();
        String jString = jsonPlaceHolderApi.JSONObject(username, password, confirmPassword, email);

        // Hash 256
        String hashedPassword = sha256(password);
        Log.d("Info", "registerClick: " + hashedPassword);

        // Perform logging in (networking operations) in background
        BackgroundWorker backgroundWorker = new BackgroundWorker(this, alertTextView);
        backgroundWorker.execute(type, jString);


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

}
