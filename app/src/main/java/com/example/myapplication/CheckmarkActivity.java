package com.example.myapplication;

//todo need to change this activity to start after successful pairing

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class CheckmarkActivity extends AppCompatActivity {

    ImageView checkmarkImageView;
    ImageView circleBackgroundImageView;
    Button serialNumberButton;
    AnimatedVectorDrawableCompat avd;
    AnimatedVectorDrawable avd2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkmark);

        // Obtain handles
        checkmarkImageView = findViewById(R.id.checkmarkImageView);
        serialNumberButton = findViewById(R.id.serialNumberButton);
        circleBackgroundImageView = findViewById(R.id.circleBackgroundImageView);
        Drawable drawable = checkmarkImageView.getDrawable();

        // Activate full screen activity
        this.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        );

//                circleBackgroundImageView.setVisibility(View.VISIBLE);
//                checkmarkImageView.setVisibility(View.VISIBLE);


        // Animate checkmark vectors drawables
        if(drawable instanceof AnimatedVectorDrawableCompat) {
            avd =(AnimatedVectorDrawableCompat) drawable;
            avd.start();
        } else if(drawable instanceof AnimatedVectorDrawable) {
            avd2 = (AnimatedVectorDrawable) drawable;
            avd2.start();
        }

        // Confirmation dialog after some delay
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(CheckmarkActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Would you like to pair another device?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int yes) {
                                Toast.makeText(CheckmarkActivity.this, "Pairing devices...", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(CheckmarkActivity.this, MainBluetoothActivity.class));
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .show();
            }
        }, 1000);



    }//onCreate end
}//class End
