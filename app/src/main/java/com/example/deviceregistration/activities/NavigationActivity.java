package com.example.deviceregistration.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.deviceregistration.R;

public class NavigationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);


//        // Confirmation dialog
//        new AlertDialog.Builder(this)
//                .setTitle("Confirm")
//                .setMessage("Would you like to pair another device?")
//                .setIcon(android.R.drawable.ic_dialog_alert)
//
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int yes) {
//                        Toast.makeText(CheckmarkActivity.this, "Pairing devices...", Toast.LENGTH_SHORT).show();
//                        startActivity(new Intent(CheckmarkActivity.this, MainBluetoothActivity.class));
//                    }
//                })
//                .setNegativeButton(android.R.string.no, null)
//                .show();
    }
}
