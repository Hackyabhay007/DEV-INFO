package com.oruphones.oruphones_deviceinfo.DeviceInfoTabs;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.oruphones.oruphones_deviceinfo.R;
import com.oruphones.oruphones_deviceinfo.ScrollViewDialog;
import com.oruphones.oruphones_deviceinfo.deviceDetailsTypeStructure;
import com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkFragment extends Fragment {

    LinearLayout mainScrollView;

    public NetworkFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_network, container, false);

        fetchDeviceDetailsHelper fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(v.getContext());
        mainScrollView = v.findViewById(R.id.device_details_scroll_view);

        try {
            for (deviceDetailsTypeStructure deviceDetails : fetchDeviceDetailsHelper.getAllDetails()) {

                if ( deviceDetails.getCategory().equals("SIM 1") ||deviceDetails.getCategory().equals("SIM 2") || deviceDetails.getCategory().equals("MOBILE")
                        || deviceDetails.getCategory().equals("DEFAULTS NETWORK FOR SIM") || deviceDetails.getCategory().equals("MOBILE CONNECTION") || deviceDetails.getCategory().equals("Connection")
                        || deviceDetails.getCategory().equals("wifi")) {
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
                                detailValue.setText("Open");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    startActivity(intent);
                                });
                            } else if (detailname.equals("IP Address")) {
                                detailValue.setText("Show");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    Toast.makeText(getContext(), "Retrieving Ip...", Toast.LENGTH_SHORT).show();
                                    new GetPublicIPAddressTask().execute();
                                });
                            } else if (detailname.equals("IPv6")) {
                                detailValue.setText("Show");
                                detailValue.setTextColor(Color.GREEN);
                                detailValue.setOnClickListener(view -> {
                                    ScrollViewDialog.showDialog( v.getContext(), deviceDetails.getDetailValue(detailname));
                                });
                            } else {

                                    detailValue.setText(deviceDetails.getDetailValue(detailname));

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
    private class GetPublicIPAddressTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            try {
                URL url = new URL("https://api.ipify.org?format=text");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }

                bufferedReader.close();
                inputStream.close();

                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error fetching IP address";
            }
        }

        @Override
        protected void onPostExecute(String ipAddress) {
            ScrollViewDialog.showDialog(getContext(),ipAddress);
        }
    }

}
