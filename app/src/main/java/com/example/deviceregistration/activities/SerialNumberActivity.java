package com.example.deviceregistration.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.deviceregistration.R;

public class SerialNumberActivity extends AppCompatActivity {

    private static final String TAG = "SerialNumberActivity";

    EditText enterSerialEditText;
    TextView macAddressTextView;
    Vibrator vibrator;
    String enterSerialString;
    String macAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_number);
        Log.d(TAG, "onCreate: called.");

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Register Your Machine");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); //back button
            getSupportActionBar().setTitle("Registration");
            getSupportActionBar().setSubtitle("Insert your machine serial number");
        }

        // Remove everything after acquired MAC address
        String masterString = getIntent().getStringExtra("MAC"); //access intent from previous activity
        String targetString = "\n";                                    //RSSI label comes after this
        int startIndex = masterString.indexOf(targetString);           //start at /n
        int stopIndex = masterString.length();                         //end of string
        StringBuilder stringbuilder = new StringBuilder(masterString); //declare a new stringbuilder class that allows deleting parts of string via indicies
        stringbuilder.delete(startIndex, stopIndex);                   //delete starting from /n to the end of the string

        // Remove everything before acquired MAC address
        startIndex = 0;
        stopIndex = stringbuilder.toString().indexOf(":") + 2;         //this is where the first number starts
        stringbuilder.delete(startIndex, stopIndex);
        String macAddress = stringbuilder.toString();

        // Display the mac address that was selected
        Log.d("onCreate", "MACAddress: " + macAddress);
        TextView macAddressTextView = findViewById(R.id.macAddressTextView);
        TextView ccdTextView = findViewById(R.id.ccdTextView);
        macAddressTextView.setText(macAddress);
        macAddressTextView.setPaintFlags(macAddressTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG); //underline text
        ccdTextView.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // return to previous page
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // Method that executes upon pressing button on main page
    public void serialNumberClick(View view) {
        Log.i(TAG, "serialNumberClick: pressed!");

        // Vibrate the phone when prompting
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE); //Obtain vibrator object and cast to vibrator type
        vibrator.vibrate(50);

        // Display serial number of machine and MAC address of cycle counter on the LogCat
        enterSerialEditText = findViewById(R.id.enterSerialEditText); //resources.id.tag name
        macAddressTextView = findViewById(R.id.macAddressTextView);
        enterSerialString = enterSerialEditText.getText().toString();
        macAddress = macAddressTextView.getText().toString();
        Log.i(  "Serial Number: ", enterSerialString);
        Log.i(  "MAC: ", macAddress);

        // Create confirmation dialog - yes means go back to main activity
        AlertDialog.Builder builder = new AlertDialog.Builder(SerialNumberActivity.this);
        builder.setTitle("Confirm");
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to pair with this device?</font>"));
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // If user agrees, add to the database
        builder.setPositiveButton(Html.fromHtml("<font color='#E41E1E'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int yes) {
                // Create intent getting the context of activity and store values into the database activity
                Intent intent = new Intent(SerialNumberActivity.this, DatabaseActivity.class);
                intent.putExtra("SN", enterSerialString);
                intent.putExtra("MAC", macAddress);
                SerialNumberActivity.this.startActivity(intent);
            }
        });

        // If user disagrees, stay on same page
        builder.setNegativeButton(Html.fromHtml("<font color='#ffffff'>Cancel</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }//end serialNumberClick
}//end SerialNumberActivity
