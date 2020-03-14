// Searches for bluetooth devices in the vicinity
package com.example.deviceregistration.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deviceregistration.R;
import com.example.deviceregistration.adapters.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.Set;

// Modify the existing activity template with bluetooth features
public class MainBluetoothActivity extends AppCompatActivity {

    private static final String TAG = "MainBluetoothActivity";

    private ArrayList<String> mNames = new ArrayList<>();
    private ArrayList<String> mImageUrls = new ArrayList<>();
    ArrayList<String> bluetoothDevices = new ArrayList<>(); //list of BT devices to pop up
    ArrayList<String> pairedBluetoothDevices = new ArrayList<>(); //list of BT devices to pop up
    TextView statusTextView;
    BluetoothAdapter bluetoothAdapter;

    int REQUEST_ENABLE_BT = 1;

    // Method created on start-up
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Overwrite the current state, set the layout, and change the Action Bar title
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        Log.d(TAG, "onCreate: started.");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Bluetooth Devices");
        }

        // Get tags
        statusTextView = findViewById(R.id.statusTextView);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checks if permission is granted, if not it will default and ask for permission
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        // Device does not support Bluetooth, alert the user
        if (bluetoothAdapter == null) {
            new AlertDialog.Builder(this)
                    .setTitle("Not compatible")
                    .setMessage("Your phone does not support Bluetooth")
                    .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            System.exit(0);
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        // Enable Bluetooth if disabled on device
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        // Get all paired devices as a set (set = unsorted array/list)
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();

        // Set up recylerview and inflate bluetooth devices
        RecyclerView recyclerView = findViewById(R.id.registeredRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, pairedBluetoothDevices, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Add names and addresses of paired devices to array
        if (pairedDevices.size() > 0) {
            // There are paired devices. Get the name and address of each paired device.
            // for loop every element of pairedDevices is passed and temporary copied into "device"
            for (BluetoothDevice device : pairedDevices) {
                String deviceName = device.getName();
                String deviceHardwareAddress = device.getAddress(); // MAC address
                // add items to adapter
//                adapter.add(deviceName + "\n" + deviceHardwareAddress);

                String deviceString = ""; // Init the device string

                // If device has no name, use the address
                if (deviceName == null || deviceName.equals(""))
                {
                    deviceString = "MAC: " + deviceHardwareAddress;
                }

                // Use the name if it is available
                else
                {
                    deviceString = "MAC: " + deviceHardwareAddress + "\n" + deviceName;
                }

                // Make sure that the string is not on the list already to avoid duplicates
                if (!pairedBluetoothDevices.contains(deviceString))
                {
                    // Add the string to the bluetoothDevices string list
                    pairedBluetoothDevices.add(deviceString);
                    String path = "android.resource://" + getPackageName() + "/" + R.drawable.outline_bluetooth_white_36dp; //must be jpg
                    mImageUrls.add(path);

                }
            }

        }

        // If scanning already running, stop
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Add actions for BT states
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        // Register the broadcast handler and link it to the intent filter
        registerReceiver(broadcastReceiver, intentFilter);

        //initImageBitmaps(); //initialize images
    }


    // Create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.bluetooth_settings_menu, menu);
        return true;
    }


    // Links methods to tool bar options buttons
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Search for devices using bluetooth icon button
            case R.id.bluetoothSearchButton:

                // Display to user the bluetooth search is in progress
                Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show();
                statusTextView.setText("Scanning...");
                findViewById(R.id.searchProgressBar).setVisibility(View.VISIBLE);
                findViewById(R.id.bluetoothSearchButton).setClickable(false);// Don't allow button to be pressed again

                // Remove redundancy and start searching
                bluetoothDevices.clear();
                bluetoothAdapter.startDiscovery();
                return true;
            case R.id.item2:
                Toast.makeText(this, "Item 2 selected", Toast.LENGTH_SHORT).show();
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

//    // Dummy Method for testing recyclerView - contains image names and their source
//    private void initImageBitmaps() {
//        Log.d(TAG, "initImageBitmaps:  preparing bitmaps.");
//
//        mImageUrls.add("https://icobench.com/images/users/vasyl-baryshnikov-1534813977.jpg");
//        mNames.add("Vasyl");
//
//        mImageUrls.add("https://cdn.pixabay.com/photo/2015/10/12/15/01/cat-984097_1280.jpg");
//        mNames.add("Cool Cat");
//
//        mImageUrls.add("https://cdn.pixabay.com/photo/2017/11/13/07/14/cat-eyes-2944820__340.jpg");
//        mNames.add("Dark Cat");
//
//        mImageUrls.add("https://i.imgur.com/ZcLLrkY.jpg");
//        mNames.add("Washington");
//
//        mImageUrls.add("https://i1.sndcdn.com/artworks-000144829194-rz17hz-t500x500.jpg");
//        mNames.add("SS3 Goku");
//
//        mImageUrls.add("https://img2.thejournal.ie/inline/4432819/original/?width=630&version=4432819");
//        mNames.add("You");
//
//        mImageUrls.add("https://cleobuttera.com/wp-content/uploads/2019/06/kk-ice-cream-full-mood-1-768x1152.jpg");
//        mNames.add("Ice Cream");
//
//        mImageUrls.add("https://icatcare.org/app/uploads/2018/07/Thinking-of-getting-a-cat.png");
//        mNames.add("Kitten");
//
//        //initRecyclerView();
//    }
//
//    // Method to set up recycler view - for testing
//    private void initRecyclerView() {
//        Log.d(TAG, "initRecyclerView: init recyclerview.");
//        RecyclerView recyclerView = findViewById(R.id.recyclerView);
//        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, mNames, mImageUrls);
//        recyclerView.setAdapter(adapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//    }

    // Update Bluetooth devices and image sources
    private void refreshBluetooth() {
        Log.d(TAG, "refreshBluetooth:  bluetooth devices refreshed.");
        RecyclerView recyclerView = findViewById(R.id.unregisteredRecyclerView);
        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, bluetoothDevices, mImageUrls);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

//    // Method that executes upon pressing search button
//    public void searchClickFunction(View view) {
//
//        statusTextView.setText("Searching...");     //show text to user
//        searchButton.setEnabled(false);             //turn off button to restrict user
//        bluetoothDevices.clear();                   //remove redundancy in devices
//        bluetoothAdapter.startDiscovery();          //start searching for BT devices
//
//    }

    // Method that contains properties for a broadcast receiver
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i("Action", action); //log info from intent

            // On bluetooth search finish
            if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                Toast.makeText(MainBluetoothActivity.this, "Finished", Toast.LENGTH_SHORT).show();
                statusTextView.setText("Finished");
                ProgressBar searchProgressBar = findViewById(R.id.searchProgressBar);
                searchProgressBar.setVisibility(View.INVISIBLE);
                findViewById(R.id.bluetoothSearchButton).setClickable(true);
            }

            // Discovery has found a device
            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object and its info from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                String address = device.getAddress(); //MAC address
                String rssi = Integer.toString(intent.getShortExtra(BluetoothDevice.EXTRA_RSSI, Short.MIN_VALUE)); //
                Log.i("Device Found", "Name: " + name + " Address: " + address + " RSSI: " + rssi + "dBm"); // show device details

                String deviceString = ""; // Init the device string

                // If device has no name, use the address
                if (name == null || name.equals(""))
                {
                    deviceString = "MAC: " + address + "\nRSSI: " + rssi + "dBm";
                }

                // Use the name if it is available
                else
                {
                    deviceString = "MAC: " + address + "\nRSSI: " + rssi + "dBm" + "\n" + name;
                }

                // Make sure that the string is not on the list already to avoid duplicates
                if (!bluetoothDevices.contains(deviceString))
                {
                    // Add the string to the bluetoothDevices string list
                    bluetoothDevices.add(deviceString);
                    String path = "android.resource://" + getPackageName() + "/" + R.drawable.outline_bluetooth_white_36dp; //must be jpg
                    mImageUrls.add(path);

                }
                // Update bluetooth devices every time another device is found
                refreshBluetooth();
            }

        }
    };

    // Turn off bluetooth and unregister action found when leaving activity
    @Override
    protected void onDestroy() {
        super.onDestroy();

        // if app crashes, disable this
        // If scanning already running, stop
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // Don't forget to unregister the ACTION_FOUND receiver.
        unregisterReceiver(broadcastReceiver);

    }

}
