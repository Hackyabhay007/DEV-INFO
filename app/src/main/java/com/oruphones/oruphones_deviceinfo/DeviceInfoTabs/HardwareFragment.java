package com.oruphones.oruphones_deviceinfo.DeviceInfoTabs;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.oruphones.oruphones_deviceinfo.R;
import com.oruphones.oruphones_deviceinfo.ScrollViewDialog;
import com.oruphones.oruphones_deviceinfo.deviceDetailsTypeStructure;
import com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper;

import java.util.Set;

public class HardwareFragment extends Fragment {

    LinearLayout mainScrollView;
    BluetoothAdapter btAdapter;
    StringBuilder bluetoothPairedDevicesData;
    private static final int REQUEST_ENABLE_BT = 1;

    public HardwareFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hardware, container, false);

        fetchDeviceDetailsHelper fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(v.getContext());
        mainScrollView = v.findViewById(R.id.device_details_scroll_view);
        bluetoothPairedDevicesData = new StringBuilder();
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        // Clear the mainScrollView before adding new views
        mainScrollView.removeAllViews();

        try {
            for (deviceDetailsTypeStructure deviceDetails : fetchDeviceDetailsHelper.getAllDetails()) {

                if (deviceDetails.getCategory().equals("PROCESSOR") || deviceDetails.getCategory().equals("GPU")
                        || deviceDetails.getCategory().equals("DISPLAY") || deviceDetails.getCategory().equals("MEMORY")
                        || deviceDetails.getCategory().equals("Storage") || deviceDetails.getCategory().equals("BLUETOOTH")
                        || deviceDetails.getCategory().equals("AUDIO") || deviceDetails.getCategory().equals("OTHER DETAILS") || deviceDetails.getCategory().equals("INPUT DEVICES")) {

                    // Check if the category view is already added
                    boolean categoryViewAdded = false;
                    int childCount = mainScrollView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childView = mainScrollView.getChildAt(i);
                        TextView categoryHeading = childView.findViewById(R.id.device_details_category_heading);
                        if (categoryHeading != null && categoryHeading.getText().toString().equals(deviceDetails.getCategory())) {
                            categoryViewAdded = true;
                            break;
                        }
                    }

                    if (!categoryViewAdded) {
                        View categoryHolder = getLayoutInflater().inflate(R.layout.device_details_tile, null);
                        TextView categoryHeading = categoryHolder.findViewById(R.id.device_details_category_heading);
                        LinearLayout containers = categoryHolder.findViewById(R.id.detailsContainer);

                        categoryHeading.setText(deviceDetails.getCategory());
                        int leftMargin = 16;  // in pixels
                        int topMargin = 8;    // in pixels
                        int rightMargin = 16; // in pixels
                        int bottomMargin = 8; // in pixels
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                        categoryHolder.setLayoutParams(layoutParams);

                        for (String detailname : deviceDetails.getAllDetails().keySet()) {

                            View dataTile = getLayoutInflater().inflate(R.layout.device_detail_data_tile, null);
                            TextView detailName = dataTile.findViewById(R.id.detailNameTextView);
                            TextView detailValue = dataTile.findViewById(R.id.valueTextView);
                            ImageView yesImg = dataTile.findViewById(R.id.yes);
                            ImageView noImg = dataTile.findViewById(R.id.no);
                            detailName.setText(detailname);


                            if (detailname.equals("Paired devices")) {
                                detailValue.setText("Show");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    CheckBluetoothState();

                                });

                            } else if (detailname.equals("NearBy devices")) {

                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setText("Show");
                                detailValue.setOnClickListener(view -> {

                                    ScrollViewDialog.showDialog(v.getContext(), "NEARBY DEVICES " + 0);
                                });


                            } else if ( detailname.equals("Frequencies") || detailname.equals("OpenGL") || detailname.equals("SUPPORTED ABIs") || detailname.equals("Version Algorithms") || detailname.equals("Codecs") || detailname.equals("Encoder") || detailname.equals("Decoder") || detailname.equals("Extensions") || detailname.equals("IPv6") ||
                                    detailname.equals("Fingerprint") || detailname.equals("Google Play Services") || detailname.equals("Architecture Instruction sets") || detailname.equals("Capabilities") || detailname.equals("Exposure Modes") ||
                                    detailname.equals("Color Effects") || detailname.equals("Scene Modes") || detailname.equals("White Balance Modes") || detailname.equals("Autofocus Modes") || detailname.equals("/proc/cpuinfo")) {
                                detailValue.setText("Show");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    ScrollViewDialog.showDialog(v.getContext(), deviceDetails.getDetailValue(detailname));
                                });
                            } else {
                                if (deviceDetails.getDetailValue(detailname).toUpperCase().equals("SUPPORTED") ||
                                        deviceDetails.getDetailValue(detailname).toUpperCase().equals("YES")) {
                                    yesImg.setVisibility(View.VISIBLE);
                                } else if (deviceDetails.getDetailValue(detailname).toUpperCase().equals("NOT SUPPORTED") || deviceDetails.getDetailValue(detailname).toUpperCase().equals("NO")) {
                                    noImg.setVisibility(View.VISIBLE);
                                } else {
                                    detailValue.setText(deviceDetails.getDetailValue(detailname));
                                }
                            }
                            containers.addView(dataTile);
                        }
                        mainScrollView.addView(categoryHolder);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            CheckBluetoothState();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void CheckBluetoothState() {

        bluetoothPairedDevicesData = new StringBuilder();
        if (btAdapter == null) {
            bluetoothPairedDevicesData.append("\nBluetooth NOT supported. Aborting.");
            return;
        } else {
            if (btAdapter.isEnabled()) {
              // bluetoothPairedDevicesData.append("\nBluetooth is enabled...");
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.BLUETOOTH_CONNECT}, 12123);
                    Toast.makeText(getContext(), "Allow Permissions and Try Again", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    bluetoothPairedDevicesData.append("Paired Devices are:");
                    Set<BluetoothDevice> devices = btAdapter.getBondedDevices();
                    for (BluetoothDevice device : devices) {
                        bluetoothPairedDevicesData.append("\nDevice: " + device.getName() + ", " + device);
                        ScrollViewDialog.showDialog(getContext(),bluetoothPairedDevicesData.toString());
                    }
                }
            } else {
                //Prompt user to turn on Bluetooth
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

        }
        ScrollViewDialog.showDialog(getContext(),bluetoothPairedDevicesData.toString());
    }
}
