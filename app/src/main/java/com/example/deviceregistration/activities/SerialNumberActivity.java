package com.example.deviceregistration.activities;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.deviceregistration.R;
import com.example.deviceregistration.providers.NotesContentProvider;



//        Char[18] 	BLE MAC address // “AA:BB:CC:DD:EE:FF”
//        Char[22] 	Machine Serial Number
//        Int16		Temperature
//        Uint32 	Cycle Count
//        Int8 		Battery Level

public class SerialNumberActivity extends AppCompatActivity {

    private static final String TAG = "SerialNumberActivity";

    EditText enterSerialEditText;
    Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_number);
        Log.d(TAG, "onCreate: called.");

        //todo handle names in addition to already handling mac addresses and put this inside of a function

        // Remove everything after MAC address
        String masterString = getIntent().getStringExtra("MAC"); //access intent from previous activity
        String targetString = "\n";                                    //RSSI label comes after this
        int startIndex = masterString.indexOf(targetString);           //start at /n
        int stopIndex = masterString.length();                         //end of string
        StringBuilder stringbuilder = new StringBuilder(masterString); //declare a new stringbuilder class that allows deleting parts of string via indicies
        stringbuilder.delete(startIndex, stopIndex);                   //delete starting from /n to the end of the string

        // Remove everything before MAC address
        startIndex = 0;
        stopIndex = stringbuilder.toString().indexOf(":") + 2;         //this is where the first number starts
        stringbuilder.delete(startIndex, stopIndex);
        String macAddress = stringbuilder.toString();

        // Display the mac address that was selected
        Log.d("onCreate", "MACAddress: " + macAddress);
        TextView macAddressTextView = findViewById(R.id.macAddressTextView);
        TextView ccdTextView = findViewById(R.id.ccdTextView);
        macAddressTextView.setText(macAddress);
        ccdTextView.setVisibility(View.VISIBLE);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Method that executes upon pressing button on main page
    public void serialNumberClick(View view) {
        Log.i(TAG, "serialNumberClick: pressed!");

        // Vibrate the phone when prompting
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE); //Obtain vibrator object and cast to vibrator type
        vibrator.vibrate(50);

        //todo need to use this for the data base entry
        // Convert user input to string
        enterSerialEditText = findViewById(R.id.enterSerialEditText); //resources.id.tag name
        String enterSerialString = enterSerialEditText.getText().toString();
        Log.i(  "Serial Number: ", enterSerialString);

        // Create confirmation dialog - yes means go back to main activity
        AlertDialog.Builder builder = new AlertDialog.Builder(SerialNumberActivity.this);
        builder.setTitle("Confirm");
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to pair with this device?</font>"));
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // If user agrees, add to the database
        builder.setPositiveButton(Html.fromHtml("<font color='#E41E1E'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int yes) {
                //todo microcontroller communications go here

                //todo store into database
                // Show the successful pairing
//                startActivity(new Intent(SerialNumberActivity.this, CheckmarkActivity.class));
                startActivity(new Intent(SerialNumberActivity.this, DatabaseActivity.class));
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
