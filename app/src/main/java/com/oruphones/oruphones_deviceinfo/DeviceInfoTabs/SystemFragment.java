package com.oruphones.oruphones_deviceinfo.DeviceInfoTabs;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oruphones.oruphones_deviceinfo.R;
import com.oruphones.oruphones_deviceinfo.ScrollViewDialog;
import com.oruphones.oruphones_deviceinfo.deviceDetailsTypeStructure;
import com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper;

public class SystemFragment extends Fragment {

    LinearLayout mainScrollView;

    public SystemFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_system, container, false);

        fetchDeviceDetailsHelper fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(v.getContext());
        mainScrollView = v.findViewById(R.id.device_details_scroll_view);

        try {
            for (deviceDetailsTypeStructure deviceDetails : fetchDeviceDetailsHelper.getAllDetails()) {

                if (deviceDetails.getCategory().equals("Device") || deviceDetails.getCategory().equals("OS")
                        || deviceDetails.getCategory().equals("IDENTIFIERS") || deviceDetails.getCategory().equals("WideVine")) {

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
                        View categoryHolder = inflater.inflate(R.layout.device_details_tile, mainScrollView, false);
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

                            View dataTile = inflater.inflate(R.layout.device_detail_data_tile, containers, false);
                            TextView detailName = dataTile.findViewById(R.id.detailNameTextView);
                            TextView detailValue = dataTile.findViewById(R.id.valueTextView);
                            ImageView yesImg = dataTile.findViewById(R.id.yes);
                            ImageView noImg = dataTile.findViewById(R.id.no);
                            detailName.setText(detailname);
                            if (detailname.equals("WIFI DIRECT")) {
                                // ... (handle the detail view as required)
                            } else if (detailname.equals("Apps")) {
                                // ... (handle the detail view as required)
                            } else if (detailname.equals("Java VM SSL version") ||  detailname.equals("Version Algorithms")  || detailname.equals("Profiles") || detailname.equals("Build Date") || detailname.equals("GooglePlayServicesVersion") || detailname.equals("Build") || detailname.equals("Extensions") || detailname.equals("IPv6") ||
                                    detailname.equals("Fingerprint") || detailname.equals("Google Play Services") || detailname.equals("Architecture Instruction sets") || detailname.equals("Capabilities") || detailname.equals("Exposure Modes") ||
                                    detailname.equals("Color Effects") || detailname.equals("Scene Modes") || detailname.equals("USB Debugging Kernel") || detailname.equals("Autofocus Modes") || detailname.equals("/proc/cpuinfo")) {
                                detailValue.setText("Show");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    ScrollViewDialog.showDialog( v.getContext(), deviceDetails.getDetailValue(detailname));
                                });
                            } else {
                                if (deviceDetails.getDetailValue(detailname).equals("Supported") ||
                                        deviceDetails.getDetailValue(detailname).toUpperCase().equals("YES")) {
                                    yesImg.setVisibility(View.VISIBLE);
                                } else if (deviceDetails.getDetailValue(detailname).equals("Not Supported") || deviceDetails.getDetailValue(detailname).toUpperCase().equals("NO")) {
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
}
