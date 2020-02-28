package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import javax.crypto.Mac;


//        Char[18] 	BLE MAC address // “AA:BB:CC:DD:EE:FF”
//        Char[22] 	Machine Serial Number
//        Int16		Temperature
//        Uint32 	Cycle Count
//        Int8 		Battery Level

public class SerialNumberActivity extends AppCompatActivity {

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
        String MACAddress = stringbuilder.toString();

        // Display the mac address that was selected
        Log.d("onCreate", "MACAddress: " + MACAddress);
        TextView MACAddressTextView = findViewById(R.id.MACAddressTextView);
        MACAddressTextView.setText(MACAddress);
    }

    // Method that executes upon pressing button on main page
    public void serialNumberClick(View view) {
        Log.i("SerialNumberActivity", "serialNumberClick pressed!");

        // Confirmation dialog
        new AlertDialog.Builder(this)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to pair with this device?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(SerialNumberActivity.this, "Pairing devices...", Toast.LENGTH_SHORT).show();

            }})
                .setNegativeButton(android.R.string.no, null).show();

        // Find handles for text fields
        EditText enterSerialEditText = findViewById(R.id.enterSerialEditText); //resources.id.tag name

        // Convert login credentials to strings
        String enterSerialString = enterSerialEditText.getText().toString();
        Log.i(  "Serial Number: ", enterSerialString);







    }
}
