package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.Mac;


//        Char[18] 	BLE MAC address // “AA:BB:CC:DD:EE:FF”
//        Char[22] 	Machine Serial Number
//        Int16		Temperature
//        Uint32 	Cycle Count
//        Int8 		Battery Level

public class SerialNumberActivity extends AppCompatActivity {

    EditText enterSerialEditText;

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

        // Find handles for text fields
        enterSerialEditText = findViewById(R.id.enterSerialEditText); //resources.id.tag name

        // Convert login credentials to strings
        String enterSerialString = enterSerialEditText.getText().toString();
        Log.i(  "Serial Number: ", enterSerialString);

        // Confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to pair with this device?")
                .setIcon(android.R.drawable.ic_dialog_alert)

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int yes) {
                Toast.makeText(SerialNumberActivity.this, "Pairing devices...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(SerialNumberActivity.this, CheckmarkActivity.class));
            }})
                .setNegativeButton(android.R.string.no, null).show();
    }
}
