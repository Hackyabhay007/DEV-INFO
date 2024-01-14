package com.oruphones.oruphones_deviceinfo;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

public class NearbyDevicesCounter {

    private Context context;
    private int nearbyDeviceCount;
    private BroadcastReceiver discoveryReceiver;

    public NearbyDevicesCounter(Context context) {
        this.context = context;
    }

    public void startDiscoveryAndCountDevices() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            // Bluetooth is not supported on this device
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            // Bluetooth is not enabled, request the user to enable it
            // or handle the scenario accordingly
            return;
        }

        nearbyDeviceCount = 0;

        discoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        // Increment the nearby device count
                        nearbyDeviceCount++;
                    }
                }
             //   return null;
            }
        };

        // Register the BroadcastReceiver to listen for discovered devices
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        context.registerReceiver(discoveryReceiver, filter);

        // Start the Bluetooth discovery
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

        }else{
            bluetoothAdapter.startDiscovery();
        }
    }

    public int getNumberOfNearbyDevices() {
        return nearbyDeviceCount;
    }

    public void stopDiscovery() {
        if (discoveryReceiver != null) {
            context.unregisterReceiver(discoveryReceiver);
            discoveryReceiver = null;
        }
    }
}
