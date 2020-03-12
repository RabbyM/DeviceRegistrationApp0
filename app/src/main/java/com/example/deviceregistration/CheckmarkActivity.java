package com.example.deviceregistration;

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
import android.text.Html;
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

        // Create a handler that will delay ui thread
        final Handler handler = new Handler();

        // Confirmation dialog after some delay
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Confirmation dialog - yes means go back to main activity
                AlertDialog.Builder builder = new AlertDialog.Builder(CheckmarkActivity.this);
                builder.setTitle("Confirm");
                builder.setMessage(Html.fromHtml("<font color='#ffffff'>Would you like to pair another device?</font>"));
                builder.setIcon(android.R.drawable.ic_dialog_alert);
                builder.setCancelable(false); //prevent user from closing the dialog unnecessarily
                // If user agrees, navigate back to the main activity
                builder.setPositiveButton(Html.fromHtml("<font color='#E41E1E'>Yes</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //Go back to main activity
                        startActivity(new Intent(CheckmarkActivity.this, MainBluetoothActivity.class));
                    }
                });

                // If user disagrees, continue to the login page
                builder.setNegativeButton(Html.fromHtml("<font color='#ffffff'>No</font>"), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        //Go to login activity
                        startActivity(new Intent(CheckmarkActivity.this, LoginActivity.class));
//                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        }, 2000);


    }//onCreate end

    @Override
    public void onBackPressed() {
        Toast.makeText(this,"Select an option", Toast.LENGTH_SHORT).show();
//        if (!shouldAllowBack()) {
//            doSomething();
//        } else {
//            super.onBackPressed();
//        }
    }
}//class End