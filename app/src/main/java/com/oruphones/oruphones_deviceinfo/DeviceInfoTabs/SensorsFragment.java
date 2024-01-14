package com.oruphones.oruphones_deviceinfo.DeviceInfoTabs;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.oruphones.oruphones_deviceinfo.R;
import com.oruphones.oruphones_deviceinfo.SensorDataPopUp;
import com.oruphones.oruphones_deviceinfo.SensorDataRetriever;
import com.oruphones.oruphones_deviceinfo.deviceDetailsTypeStructure;
import com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SensorsFragment extends Fragment {
    private final String[] sensorValue = new String[1];
    public static boolean isDialogShowing = false;
    private SensorDataRetriever sensorDataRetriever;
    private LinearLayout mainScrollView;
    private final Map<String, String> sensorNamesMap = new HashMap<>();
    private String currentSensorName;
    private Handler handler;

    public SensorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sensors, container, false);

        fetchDeviceDetailsHelper fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(v.getContext());
        mainScrollView = v.findViewById(R.id.device_details_scroll_view);

        Toast.makeText(getContext(), "Tap Sensor Name To See Readings", Toast.LENGTH_SHORT).show();
        sensorDataRetriever = new SensorDataRetriever(requireContext());
        sensorDataRetriever.startListening();

        initializeSensorNamesMap();
        createSensorViews(fetchDeviceDetailsHelper);

        return v;
    }

    private void initializeSensorNamesMap() {
        // Populate the sensor names and their corresponding detail names here
        sensorNamesMap.put("Accelerometer", "AccelerometerValues");
        sensorNamesMap.put("BMI120 Accelerometer", "Bmi120AccValues");
        sensorNamesMap.put("Magnetic Field", "MagneticFieldValues");
        sensorNamesMap.put("Orientation", "OrientationValues");
        sensorNamesMap.put("Gyroscope", "GyroscopeValues");
        sensorNamesMap.put("Light", "LightValue");
        sensorNamesMap.put("Proximity", "ProximityValue");
        sensorNamesMap.put("Gravity", "GravityValues");
        sensorNamesMap.put("Linear Acceleration", "LinearAccelerationValues");
        sensorNamesMap.put("Rotation Vector", "RotationVectorValues");
        sensorNamesMap.put("Game Rotation Vector", "GameRotationVectorValues");
        sensorNamesMap.put("Step Detector", "StepDetectorValue");
        sensorNamesMap.put("Significant Motion", "SignificantMotionValue");
        sensorNamesMap.put("Geomagnetic Rotation Vector", "GeomagneticRotationVectorValues");
        sensorNamesMap.put("Stationary Detect", "StationaryDetectValue");
        sensorNamesMap.put("Motion Detect", "MotionDetectValue");
        sensorNamesMap.put("Ambient Temperature", "AmbientTemperatureValue");
        sensorNamesMap.put("Pressure Sensor", "PressureSensorValue");
        // Add other sensor names and detail names as needed
    }

    private void createSensorViews(fetchDeviceDetailsHelper fetchDeviceDetailsHelper) {
        try {
            for (deviceDetailsTypeStructure deviceDetails : fetchDeviceDetailsHelper.getAllDetails()) {
                if (deviceDetails.getCategory().equals("SENSOR DETAILS")) {
                    boolean categoryViewAdded = false;
                    for (int i = 0; i < mainScrollView.getChildCount(); i++) {
                        View childView = mainScrollView.getChildAt(i);
                        TextView categoryHeading = childView.findViewById(R.id.device_details_category_heading);
                        if (categoryHeading != null && categoryHeading.getText().toString().equals(deviceDetails.getCategory())) {
                            categoryViewAdded = true;
                            break;
                        }
                    }

                    if (!categoryViewAdded) {
                        View categoryHolder = getLayoutInflater().inflate(R.layout.device_details_tile, mainScrollView, false);
                        TextView categoryHeading = categoryHolder.findViewById(R.id.device_details_category_heading);
                        LinearLayout containers = categoryHolder.findViewById(R.id.detailsContainer);
                        categoryHeading.setText(deviceDetails.getCategory());
                        int leftMargin = 16;
                        int topMargin = 8;
                        int rightMargin = 16;
                        int bottomMargin = 8;
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );
                        layoutParams.setMargins(leftMargin, topMargin, rightMargin, bottomMargin);
                        categoryHolder.setLayoutParams(layoutParams);

                        for (String detailName : deviceDetails.getAllDetails().keySet()) {
                            View dataTile = getLayoutInflater().inflate(R.layout.device_detail_data_tile, containers, false);
                            TextView detailNameTextView = dataTile.findViewById(R.id.detailNameTextView);
                            TextView detailValueTextView = dataTile.findViewById(R.id.valueTextView);
                            ImageView yesImg = dataTile.findViewById(R.id.yes);
                            ImageView noImg = dataTile.findViewById(R.id.no);
                            detailNameTextView.setText(detailName);

                            detailValueTextView.setVisibility(View.VISIBLE);
                            if (deviceDetails.getDetailValue(detailName).equals("Supported") ||
                                    deviceDetails.getDetailValue(detailName).equalsIgnoreCase("YES")) {
                                yesImg.setVisibility(View.VISIBLE);
                            } else if (deviceDetails.getDetailValue(detailName).equals("Not Supported") || deviceDetails.getDetailValue(detailName).equalsIgnoreCase("NO")) {
                                noImg.setVisibility(View.VISIBLE);
                            }

                            detailNameTextView.setOnClickListener(view -> {
                                if (!isDialogShowing) {
                                    currentSensorName = detailName;
                                    showSensorDialog(detailName);
                                }
                            });

                            containers.addView(dataTile);
                        }

                        mainScrollView.addView(categoryHolder);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void showSensorDialog(String sensorName) {
        if (isDialogShowing) {
            SensorDataPopUp.dismissDialog();
        }
        isDialogShowing = true;

        SensorDataPopUp.showDialog(getContext(), getSensorData(sensorName));
        SensorDataPopUp.setHeading(sensorName);
        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                SensorDataPopUp.updateDialogValue(getSensorData(currentSensorName));
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        new Handler().postDelayed(() -> {
            isDialogShowing = false;
          //  SensorDataPopUp.dismissDialog();

        }, 3000);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        sensorDataRetriever.startListening();
    }

    private String getSensorData(String sensorName) {
        String detailName = sensorNamesMap.get(sensorName);
        if (detailName != null) {
            switch (detailName) {
                case "AccelerometerValues":
                    return Arrays.toString(sensorDataRetriever.getAccelerometerValues());
                case "Bmi120AccValues":
                    return Arrays.toString(sensorDataRetriever.getBmi120AccValues());
                case "MagneticFieldValues":
                    return Arrays.toString(sensorDataRetriever.getMagneticFieldValues());
                case "OrientationValues":
                    return Arrays.toString(sensorDataRetriever.getOrientationValues());
                case "GyroscopeValues":
                    return Arrays.toString(sensorDataRetriever.getGyroscopeValues());
                case "LightValue":
                    return Float.toString(sensorDataRetriever.getLightValue());
                case "ProximityValue":
                    return Float.toString(sensorDataRetriever.getProximityValue());
                case "GravityValues":
                    return Arrays.toString(sensorDataRetriever.getGravityValues());
                case "LinearAccelerationValues":
                    return Arrays.toString(sensorDataRetriever.getLinearAccelerationValues());
                case "RotationVectorValues":
                    return Arrays.toString(sensorDataRetriever.getRotationVectorValues());
                case "GameRotationVectorValues":
                    return Arrays.toString(sensorDataRetriever.getGameRotationVectorValues());
                case "StepDetectorValue":
                    return Float.toString(sensorDataRetriever.getStepDetectorValue());
                case "SignificantMotionValue":
                    return Float.toString(sensorDataRetriever.getSignificantMotionValue());
                case "GeomagneticRotationVectorValues":
                    return Arrays.toString(sensorDataRetriever.getGeomagneticRotationVectorValues());
                case "StationaryDetectValue":
                    return Float.toString(sensorDataRetriever.getStationaryDetectValue());
                case "MotionDetectValue":
                    return Float.toString(sensorDataRetriever.getMotionDetectValue());
                case "AmbientTemperatureValue":
                    return Float.toString(sensorDataRetriever.getAmbientTemperatureValue());
                case "PressureSensorValue":
                    return Float.toString(sensorDataRetriever.getPressureSensorValue());
                // Add other cases for other sensor names
                default:
                    break;
            }
        }
        return "";
    }
}
