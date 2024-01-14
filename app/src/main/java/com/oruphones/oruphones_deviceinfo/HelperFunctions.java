package com.oruphones.oruphones_deviceinfo;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.SizeF;

public class HelperFunctions {

    private static int findGCD(int a, int b) {
        if (b == 0) {
            return a;
        }
        return findGCD(b, a % b);
    }

    // Method to convert a double value to a ratio in the form of "p/q"
    public static String convertToRatio(double value) {
        final double EPSILON = 1.0E-6; // Adjust this value for desired precision

        int p0 = 0, p1 = 1;
        int q0 = 1, q1 = 0;

        double x = value;
        do {
            int integerPart = (int) x;
            double fractionalPart = x - integerPart;

            int p2 = integerPart * p1 + p0;
            int q2 = integerPart * q1 + q0;

            p0 = p1;
            p1 = p2;
            q0 = q1;
            q1 = q2;

            x = 1.0 / fractionalPart;

        } while (Math.abs(value - (double) p1 / q1) > EPSILON);

        int gcd = findGCD(p1, q1);
        int numerator = p1 / gcd;
        int denominator = q1 / gcd;

        return numerator + "/" + denominator;
    }






    // Method to extract the crop factor of the camera's sensor
    public static float getSensorCropFactor(Context context, String cameraId) throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

        // Get the sensor's physical size in millimeters
        float sensorPhysicalWidthMM = getSensorPhysicalWidthMM(characteristics);
        float sensorPhysicalHeightMM = getSensorPhysicalHeightMM(characteristics);

        // Calculate the crop factor by comparing the sensor size to a full-frame 35mm sensor
        float cropFactor = get35mmEquivalentCropFactor(sensorPhysicalWidthMM, sensorPhysicalHeightMM);

        return cropFactor;
    }

    // Method to get the sensor's physical width in millimeters
    private static float getSensorPhysicalWidthMM(CameraCharacteristics characteristics) {
        // Get the sensor size as a SizeF object (width x height) in millimeters
        SizeF sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        return sensorSize != null ? sensorSize.getWidth() : 0.0f;
    }

    // Method to get the sensor's physical height in millimeters
    private static float getSensorPhysicalHeightMM(CameraCharacteristics characteristics) {
        // Get the sensor size as a SizeF object (width x height) in millimeters
        SizeF sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        return sensorSize != null ? sensorSize.getHeight() : 0.0f;
    }

    // Method to calculate the crop factor based on the sensor size
    private static float get35mmEquivalentCropFactor(float sensorPhysicalWidthMM, float sensorPhysicalHeightMM) {
        // Assuming a full-frame 35mm sensor size is approximately 36x24mm
        float fullFrameWidthMM = 36.0f;
        float fullFrameHeightMM = 24.0f;
        float cropFactorWidth = fullFrameWidthMM / sensorPhysicalWidthMM;
        float cropFactorHeight = fullFrameHeightMM / sensorPhysicalHeightMM;
        // Take the smaller crop factor (considering width and height separately) to avoid distortion
        return Math.min(cropFactorWidth, cropFactorHeight);
    }



    public static float calculateHorizontalFOV(Context context ,float folcalLength, String frontCameraId) throws CameraAccessException {

        if (frontCameraId == null) {
            throw new CameraAccessException(CameraAccessException.CAMERA_ERROR);
        }
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(frontCameraId);

        // Get the sensor's physical size in millimeters
        SizeF sensorSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        if (sensorSize != null) {
            float sensorWidth = sensorSize.getWidth();

            // Calculate the horizontal FOV using the sensor's width
            float horizontalFOV = (float) Math.toDegrees(2 * Math.atan(sensorWidth / (2 * folcalLength)));
            return horizontalFOV;
        }

        throw new CameraAccessException(CameraAccessException.CAMERA_ERROR);
    }
}
