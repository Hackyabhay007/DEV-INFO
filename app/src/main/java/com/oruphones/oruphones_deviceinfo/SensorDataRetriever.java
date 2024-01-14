package com.oruphones.oruphones_deviceinfo;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorDataRetriever implements SensorEventListener {
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor bmi120Acc;
    private Sensor magneticField;
    private Sensor orientation;
    private Sensor gyroscope;
    private Sensor light;
    private Sensor proximity;
    private Sensor gravity;
    private Sensor linearAcceleration;
    private Sensor rotationVector;
    private Sensor gameRotationVector;
    private Sensor stepDetector;

    private Sensor significantMotion;
    private Sensor geomagneticRotationVector;

    private Sensor stationaryDetect;
    private Sensor motionDetect;
    private Sensor ambientTemperature;

    private Sensor pressureSensor;

    // Accelerometer
    private float[] accelerometerValues = new float[3];

    // BMI120 accelerometer
    private float[] bmi120AccValues = new float[3];

    // Magnetic field
    private float[] magneticFieldValues = new float[3];

    // Orientation - Deprecated since API level 8, consider using SensorManager.getRotationMatrix() and SensorManager.getOrientation() instead.
    private float[] orientationValues = new float[3];

    // Gyroscope
    private float[] gyroscopeValues = new float[3];

    // Light
    private float lightValue;

    // Proximity
    private float proximityValue;

    // Gravity
    private float[] gravityValues = new float[3];

    // Linear acceleration
    private float[] linearAccelerationValues = new float[3];

    // Rotation vector
    private float[] rotationVectorValues = new float[3];

    // Game rotation vector
    private float[] gameRotationVectorValues = new float[3];

    // Step detector
    private float stepDetectorValue;

    // Significant motion
    private float significantMotionValue;

    // Geomagnetic rotation vector
    private float[] geomagneticRotationVectorValues = new float[3];

    // Stationary detect
    private float stationaryDetectValue;

    // Motion detect
    private float motionDetectValue;

    // Ambient temperature
    private float ambientTemperatureValue;

    // Pressure sensor
    private float pressureSensorValue;

    // Getters and Setters for each sensor value


    public SensorDataRetriever(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        // Initialize sensors
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        bmi120Acc = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magneticField = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        orientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        light = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        linearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        rotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        gameRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR);
        stepDetector = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        significantMotion = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION);
        geomagneticRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR);
        stationaryDetect = sensorManager.getDefaultSensor(Sensor.TYPE_STATIONARY_DETECT);
        motionDetect = sensorManager.getDefaultSensor(Sensor.TYPE_MOTION_DETECT);
        ambientTemperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
        pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
    }

    // Getters and Setters for each sensor value

    public float[] getAccelerometerValues() {
        return accelerometerValues;
    }

    public void setAccelerometerValues(float x, float y, float z) {
        accelerometerValues[0] = x;
        accelerometerValues[1] = y;
        accelerometerValues[2] = z;
    }

    public float[] getBmi120AccValues() {
        return bmi120AccValues;
    }

    public void setBmi120AccValues(float x, float y, float z) {
        bmi120AccValues[0] = x;
        bmi120AccValues[1] = y;
        bmi120AccValues[2] = z;
    }

    public float[] getMagneticFieldValues() {
        return magneticFieldValues;
    }

    public void setMagneticFieldValues(float x, float y, float z) {
        magneticFieldValues[0] = x;
        magneticFieldValues[1] = y;
        magneticFieldValues[2] = z;
    }

    public float[] getOrientationValues() {
        return orientationValues;
    }

    public void setOrientationValues(float x, float y, float z) {
        orientationValues[0] = x;
        orientationValues[1] = y;
        orientationValues[2] = z;
    }

    public float[] getGyroscopeValues() {
        return gyroscopeValues;
    }

    public void setGyroscopeValues(float x, float y, float z) {
        gyroscopeValues[0] = x;
        gyroscopeValues[1] = y;
        gyroscopeValues[2] = z;
    }

    public float getLightValue() {
        return lightValue;
    }

    public void setLightValue(float lightValue) {
        this.lightValue = lightValue;
    }

    public float getProximityValue() {
        return proximityValue;
    }

    public void setProximityValue(float proximityValue) {
        this.proximityValue = proximityValue;
    }

    public float[] getGravityValues() {
        return gravityValues;
    }

    public void setGravityValues(float x, float y, float z) {
        gravityValues[0] = x;
        gravityValues[1] = y;
        gravityValues[2] = z;
    }

    public float[] getLinearAccelerationValues() {
        return linearAccelerationValues;
    }

    public void setLinearAccelerationValues(float x, float y, float z) {
        linearAccelerationValues[0] = x;
        linearAccelerationValues[1] = y;
        linearAccelerationValues[2] = z;
    }

    public float[] getRotationVectorValues() {
        return rotationVectorValues;
    }

    public void setRotationVectorValues(float x, float y, float z) {
        rotationVectorValues[0] = x;
        rotationVectorValues[1] = y;
        rotationVectorValues[2] = z;
    }

    public float[] getGameRotationVectorValues() {
        return gameRotationVectorValues;
    }

    public void setGameRotationVectorValues(float x, float y, float z) {
        gameRotationVectorValues[0] = x;
        gameRotationVectorValues[1] = y;
        gameRotationVectorValues[2] = z;
    }

    public float getStepDetectorValue() {
        return stepDetectorValue;
    }

    public void setStepDetectorValue(float stepDetectorValue) {
        this.stepDetectorValue = stepDetectorValue;
    }

    public float getSignificantMotionValue() {
        return significantMotionValue;
    }

    public void setSignificantMotionValue(float significantMotionValue) {
        this.significantMotionValue = significantMotionValue;
    }

    public float[] getGeomagneticRotationVectorValues() {
        return geomagneticRotationVectorValues;
    }

    public void setGeomagneticRotationVectorValues(float x, float y, float z) {
        geomagneticRotationVectorValues[0] = x;
        geomagneticRotationVectorValues[1] = y;
        geomagneticRotationVectorValues[2] = z;
    }

    public float getStationaryDetectValue() {
        return stationaryDetectValue;
    }

    public void setStationaryDetectValue(float stationaryDetectValue) {
        this.stationaryDetectValue = stationaryDetectValue;
    }

    public float getMotionDetectValue() {
        return motionDetectValue;
    }

    public void setMotionDetectValue(float motionDetectValue) {
        this.motionDetectValue = motionDetectValue;
    }

    public float getAmbientTemperatureValue() {
        return ambientTemperatureValue;
    }

    public void setAmbientTemperatureValue(float ambientTemperatureValue) {
        this.ambientTemperatureValue = ambientTemperatureValue;
    }

    public float getPressureSensorValue() {
        return pressureSensorValue;
    }

    public void setPressureSensorValue(float pressureSensorValue) {
        this.pressureSensorValue = pressureSensorValue;
    }

    public boolean isSensorAvailable(Sensor sensor) {
        return sensor != null;
    }


    public boolean isAccelerometerAvailable() {
        return isSensorAvailable(accelerometer);
    }

    public boolean isBmi120AccAvailable() {
        return isSensorAvailable(bmi120Acc);
    }

    public boolean isMagneticFieldAvailable() {
        return isSensorAvailable(magneticField);
    }

    public boolean isOrientationAvailable() {
        return isSensorAvailable(orientation);
    }

    public boolean isGyroscopeAvailable() {
        return isSensorAvailable(gyroscope);
    }

    public boolean isLightAvailable() {
        return isSensorAvailable(light);
    }

    public boolean isProximityAvailable() {
        return isSensorAvailable(proximity);
    }

    public boolean isGravityAvailable() {
        return isSensorAvailable(gravity);
    }

    public boolean isLinearAccelerationAvailable() {
        return isSensorAvailable(linearAcceleration);
    }

    public boolean isRotationVectorAvailable() {
        return isSensorAvailable(rotationVector);
    }

    public boolean isGameRotationVectorAvailable() {
        return isSensorAvailable(gameRotationVector);
    }

    public boolean isStepDetectorAvailable() {
        return isSensorAvailable(stepDetector);
    }

    public boolean isSignificantMotionAvailable() {
        return isSensorAvailable(significantMotion);
    }

    public boolean isGeomagneticRotationVectorAvailable() {
        return isSensorAvailable(geomagneticRotationVector);
    }

    public boolean isStationaryDetectAvailable() {
        return isSensorAvailable(stationaryDetect);
    }

    public boolean isMotionDetectAvailable() {
        return isSensorAvailable(motionDetect);
    }

    public boolean isAmbientTemperatureAvailable() {
        return isSensorAvailable(ambientTemperature);
    }



    public boolean isPressureSensorAvailable() {
        return isSensorAvailable(pressureSensor);
    }



    public void startListening() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, bmi120Acc, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magneticField, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, orientation, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, linearAcceleration, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, rotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gameRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, stepDetector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, significantMotion, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, geomagneticRotationVector, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, stationaryDetect, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, motionDetect, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, ambientTemperature, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    public void stopListening() {
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = event.sensor.getType();

        if (sensorType == Sensor.TYPE_ACCELEROMETER) {
            setAccelerometerValues(event.values[0], event.values[1], event.values[2]);
            // Process the accelerometer data as required
        }  else if (sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
            setMagneticFieldValues(event.values[0], event.values[1], event.values[2]);
            // Process the magnetic field data as required
        } else if (sensorType == Sensor.TYPE_ORIENTATION) {
            setOrientationValues(event.values[0], event.values[1], event.values[2]);
            // Process the orientation data as required
        } else if (sensorType == Sensor.TYPE_GYROSCOPE) {
            setGyroscopeValues(event.values[0], event.values[1], event.values[2]);
            // Process the gyroscope data as required
        } else if (sensorType == Sensor.TYPE_LIGHT) {
            setLightValue(event.values[0]);
            // Process the light sensor data as required
        } else if (sensorType == Sensor.TYPE_PROXIMITY) {
            setProximityValue(event.values[0]);
            // Process the proximity sensor data as required
        } else if (sensorType == Sensor.TYPE_GRAVITY) {
            setGravityValues(event.values[0], event.values[1], event.values[2]);
            // Process the gravity data as required
        } else if (sensorType == Sensor.TYPE_LINEAR_ACCELERATION) {
            setLinearAccelerationValues(event.values[0], event.values[1], event.values[2]);
            // Process the linear acceleration data as required
        } else if (sensorType == Sensor.TYPE_ROTATION_VECTOR) {
            setRotationVectorValues(event.values[0], event.values[1], event.values[2]);
            // Process the rotation vector data as required
        } else if (sensorType == Sensor.TYPE_GAME_ROTATION_VECTOR) {
            setGameRotationVectorValues(event.values[0], event.values[1], event.values[2]);
            // Process the game rotation vector data as required
        } else if (sensorType == Sensor.TYPE_STEP_DETECTOR) {
            setStepDetectorValue(event.values[0]);
            // Process the step detector data as required
        } else if (sensorType == Sensor.TYPE_SIGNIFICANT_MOTION) {
            setSignificantMotionValue(event.values[0]);
            // Process the significant motion data as required
        } else if (sensorType == Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR) {
            setGeomagneticRotationVectorValues(event.values[0], event.values[1], event.values[2]);
            // Process the geomagnetic rotation vector data as required
        } else if (sensorType == Sensor.TYPE_STATIONARY_DETECT) {
            setStationaryDetectValue(event.values[0]);
            // Process the stationary detect data as required
        } else if (sensorType == Sensor.TYPE_MOTION_DETECT) {
            setMotionDetectValue(event.values[0]);
            // Process the motion detect data as required
        } else if (sensorType == Sensor.TYPE_AMBIENT_TEMPERATURE) {
            setAmbientTemperatureValue(event.values[0]);
            // Process the ambient temperature data as required
        } else if (sensorType == Sensor.TYPE_PRESSURE) {
            setPressureSensorValue(event.values[0]);
            // Process the pressure sensor data as required
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
