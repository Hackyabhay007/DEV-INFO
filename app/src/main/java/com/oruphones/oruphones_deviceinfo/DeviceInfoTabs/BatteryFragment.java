package com.oruphones.oruphones_deviceinfo.DeviceInfoTabs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oruphones.oruphones_deviceinfo.R;
import com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper;

import java.util.HashMap;

public class BatteryFragment extends Fragment {

    public BatteryFragment() {
    }

    LinearLayout mainScrollView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_battery, container, false);
        //fetchDeviceDetailsHelper.getBatteryExtraInfo();
        fetchDeviceDetailsHelper fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(v.getContext());
        mainScrollView = v.findViewById(R.id.device_details_scroll_view);

                        View categoryHolder = getLayoutInflater().inflate(R.layout.device_details_tile, null);
                        TextView categoryHeading = categoryHolder.findViewById(R.id.device_details_category_heading);
                        LinearLayout containers = categoryHolder.findViewById(R.id.detailsContainer);
                        categoryHeading.setText("BATTERY");
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

                             HashMap<String,String> batterydata =  fetchDeviceDetailsHelper.getBatterydata();
                        for (String detailname : batterydata.keySet()) {
                            View dataTile = getLayoutInflater().inflate(R.layout.device_detail_data_tile, null);
                            TextView detailName = dataTile.findViewById(R.id.detailNameTextView);
                            TextView detailValue = dataTile.findViewById(R.id.valueTextView);
                            ImageView yesImg = dataTile.findViewById(R.id.yes);
                            ImageView noImg = dataTile.findViewById(R.id.no);
                            detailName.setText(detailname);
                            detailValue.setText(batterydata.get(detailname));
                            containers.addView(dataTile);
                        }
                        mainScrollView.addView(categoryHolder);

        return v;

    }
}
