// Main activity that allows user to login credentials or create new account
//todo this version is where Rabby is working on the UI
//todo make this activity after selecting a device and successfully transferring
package com.example.deviceregistration.activities;

import android.content.Intent;
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

import com.example.deviceregistration.utils.BackgroundWorker;
import com.example.deviceregistration.R;

import java.security.MessageDigest;

// Modify the existing activity template with login page
public class LoginActivity extends AppCompatActivity {

    // Method created on start-up to initialize login page
    @Override //this method already exists in AppCompatActivity and we are adding to it
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //superclass (parent) object
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Go to registration page if not registered
        TextView registerTextView = findViewById(R.id.registerTextView);
        registerTextView.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

//        // Provide phone number or something TBD
//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
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

        // Hash 256
        String hashedPassword = sha256(password);
        Log.d("Info", "loginClick: "+ hashedPassword);

        // Allow background to obtain context and store information
        BackgroundWorker backgroundWorker = new BackgroundWorker(this); // declare, instantiate, initialize
        backgroundWorker.execute(type, username, password);                 // pass user info as strings

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


    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Restart the app", Toast.LENGTH_SHORT).show();
    }


} //LoginActivity class end
