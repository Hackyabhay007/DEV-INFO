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

import java.util.Objects;

public class CameraFragment extends Fragment {

    LinearLayout mainScrollView;

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_camera, container, false);

        fetchDeviceDetailsHelper fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(v.getContext());
        mainScrollView = v.findViewById(R.id.device_details_scroll_view);

        try {
            for (deviceDetailsTypeStructure deviceDetails : fetchDeviceDetailsHelper.getAllDetails()) {

                if (deviceDetails.getCategory().equals("Rear Camera") || deviceDetails.getCategory().equals("Front Camera")
                        || deviceDetails.getCategory().equals("Video Capture")) {

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

                            if (detailname.equals("WIFI DIRECT")) {

                            } else if (detailname.equals("Apps")) {
                                

                            }

                            else if (detailname.equals("Output Sizes")  ||detailname.equals("Profiles") || detailname.equals("Codecs") || detailname.equals("Encoder") || detailname.equals("Decoder") || detailname.equals("Extensions") || detailname.equals("Shutter Speed Range") ||
                                    detailname.equals("Fingerprint") || detailname.equals("Google Play Services") || detailname.equals("Architecture Instruction sets") || detailname.equals("Capabilities") || detailname.equals("Exposure Modes") ||
                                    detailname.equals("Color Effects") || detailname.equals("Scene Modes") || detailname.equals("White Balance Modes") || detailname.equals("Autofocus Modes") || detailname.equals("/proc/cpuinfo")) {
                                detailValue.setText("Show");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    ScrollViewDialog.showDialog(v.getContext(), deviceDetails.getDetailValue(detailname));
                                });
                            } else {
                                if (Objects.equals(deviceDetails.getDetailValue(detailname), "YES")) {
                                    yesImg.setVisibility(View.VISIBLE);
                                } else if (Objects.equals(deviceDetails.getDetailValue(detailname), "NO")){
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
