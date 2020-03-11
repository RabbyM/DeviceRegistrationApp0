package com.example.deviceregistration;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


//        Char[18] 	BLE MAC address // “AA:BB:CC:DD:EE:FF”
//        Char[22] 	Machine Serial Number
//        Int16		Temperature
//        Uint32 	Cycle Count
//        Int8 		Battery Level

public class SerialNumberActivity extends AppCompatActivity {

    EditText enterSerialEditText;
    public Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Inflate layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serial_number);

        //todo handle names in addition to already handling mac addresses

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

    // Method that executes upon pressing button on main page
    public void serialNumberClick(View view) {
        Log.i("SerialNumberActivity", "serialNumberClick pressed!");

        // Obtain vibrator object and cast to vibrator type
        vibrator = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(50);

        // Find handles for text fields
        enterSerialEditText = findViewById(R.id.enterSerialEditText); //resources.id.tag name

        // Convert login credentials to strings
        String enterSerialString = enterSerialEditText.getText().toString();
        Log.i(  "Serial Number: ", enterSerialString);

        // Create confirmation dialog - yes means go back to main activity
        AlertDialog.Builder builder = new AlertDialog.Builder(SerialNumberActivity.this);
        builder.setTitle("Confirm");
        builder.setMessage(Html.fromHtml("<font color='#ffffff'>Are you sure you want to pair with this device?</font>"));
        builder.setIcon(android.R.drawable.ic_dialog_alert);

        // If user agrees, navigate back to the main activity
        builder.setPositiveButton(Html.fromHtml("<font color='#E41E1E'>OK</font>"), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int yes) {
                //todo microcontroller communications go here
                startActivity(new Intent(SerialNumberActivity.this, CheckmarkActivity.class));
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


    }
}
