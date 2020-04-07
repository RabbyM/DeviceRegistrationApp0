package com.example.deviceregistration.activities;


import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MainBluetoothActivityTest {

    @Rule
    public ActivityTestRule<MainBluetoothActivity> mActivityTestRule = new ActivityTestRule<>(MainBluetoothActivity.class);

    @Test
    public void mainBluetoothActivityTest() {
    }
}
