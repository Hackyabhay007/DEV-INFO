package com.oruphones.oruphones_deviceinfo;

import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.ColorSpace;
import android.graphics.Point;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.hardware.fingerprint.FingerprintManager;
import android.hardware.usb.UsbAccessory;
import android.hardware.usb.UsbManager;
import android.location.LocationManager;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaDrm;
import android.media.MediaRecorder;
import android.media.UnsupportedSchemeException;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.nfc.NfcAdapter;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.opengl.GLES30;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.MemoryFile;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.UserManager;
import android.os.Vibrator;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.provider.MediaStore;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Range;
import android.util.Size;
import android.util.SizeF;
import android.view.Display;
import android.view.InputDevice;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.security.Provider;
import java.security.Security;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class fetchDeviceDetailsHelper {
    static Context context;
    static String TAG = "fetchDevicedetails";

    private static final String CAMERA_FRONT = "1";

    String chargingAmp;
    private BroadcastReceiver chargingReceiver;
    static deviceDetailsTypeStructure deviceDetailsTypeStructure;
    static HashMap<String, String> batterydata = new HashMap<>();
    static BroadcastReceiver battery_receiver;
    static IntentFilter filter;

    static ArrayList<deviceDetailsTypeStructure> deviceDetails = new ArrayList<>();

    public fetchDeviceDetailsHelper(Context context) {
        this.context = context;

    }

    public ArrayList<deviceDetailsTypeStructure> getAllDetails() throws InterruptedException {
        //Abhay
        addSimDetails();
        addSimDefault();
        addAllPartitions();
        addOtherDetails();
        addMemoryDetails();
        addProcessorDetails();
        addStorageDetails();
        addIdentifiers();
        addMobileDetails();
        addMobileConnectionDetails();
        addSensorDetails();
        addInputDevices();
        addGPUDetails();
        addDisplayDetails();
        addAudioDetails();
        addDeviceDetails();
        addOSDetails();
        addBluetoothDetails();
        addWideVineDetails();
        addWifiDetails();
        addConnectionDetails();
        getAppInfo();
        getCameraInfo();
        getFrontCameraInfo();
        addVideoCaptureDetails();


        return deviceDetails;
    }

    void getAppInfo() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("Apps");
        deviceDetailsTypeStructure.addDetail("Apps", "");
        deviceDetails.add(deviceDetailsTypeStructure);
    }


    void addInputDevices() {
        String[] inputDeviceNames = getAllInputDeviceNames(context);
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("INPUT DEVICES");
        if (inputDeviceNames != null) {
            for (int i = 0; i < inputDeviceNames.length; i++) {
                String deviceName = inputDeviceNames[i];
                deviceDetailsTypeStructure.addDetail(String.valueOf((i + 1)), deviceName);
            }
        }
        deviceDetails.add(deviceDetailsTypeStructure);
    }

    void addSensorDetails() {
        SensorDataRetriever sensorDataRetriever = new SensorDataRetriever(context);
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("SENSOR DETAILS");
        deviceDetailsTypeStructure.addDetail("Accelerometer", sensorDataRetriever.isAccelerometerAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("BMI120 Accelerometer", sensorDataRetriever.isBmi120AccAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Magnetic Field", sensorDataRetriever.isMagneticFieldAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Orientation", sensorDataRetriever.isOrientationAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Gyroscope", sensorDataRetriever.isGyroscopeAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Light", sensorDataRetriever.isLightAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Proximity", sensorDataRetriever.isProximityAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Gravity", sensorDataRetriever.isGravityAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Linear Acceleration", sensorDataRetriever.isLinearAccelerationAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Rotation Vector", sensorDataRetriever.isRotationVectorAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Game Rotation Vector", sensorDataRetriever.isGameRotationVectorAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Step Detector", sensorDataRetriever.isStepDetectorAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Significant Motion", sensorDataRetriever.isSignificantMotionAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Geomagnetic Rotation Vector", sensorDataRetriever.isGeomagneticRotationVectorAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Stationary Detect", sensorDataRetriever.isStationaryDetectAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Motion Detect", sensorDataRetriever.isMotionDetectAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Ambient Temperature", sensorDataRetriever.isAmbientTemperatureAvailable() ? "Supported" : "Not Supported");
        deviceDetailsTypeStructure.addDetail("Pressure Sensor", sensorDataRetriever.isPressureSensorAvailable() ? "Supported" : "Not Supported");

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    void addMobileConnectionDetails() {
        try {
            HashMap<String, String> conData;
            conData = getMobileConnectionDetails();
            deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
            deviceDetailsTypeStructure.setCategory("MOBILE CONNECTION");
            deviceDetailsTypeStructure.addDetail("TYPE", conData.get("CONNECTION_TYPE"));
            deviceDetailsTypeStructure.addDetail("OPERATOR", conData.get("OPERATOR_NAME"));
            deviceDetailsTypeStructure.addDetail("Strength", conData.get("dBm") + "dBm");
            deviceDetailsTypeStructure.addDetail("Level", conData.get("Level") + "%");
            deviceDetails.add(deviceDetailsTypeStructure);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }


    void addIdentifiers() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("IDENTIFIERS");
        deviceDetailsTypeStructure.addDetail("GooglePlayServicesVersion", googlePlayServicesVersion());
        //   deviceDetailsTypeStructure.addDetail("GoogleServicesFrameworkId",getGoogleServicesFrameworkId(context));
        deviceDetailsTypeStructure.addDetail("Device id", getDeviceId(context));

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    void addMobileDetails() {
        getMobileDetails();
    }
    //  calling sim detail functions

    void addSimDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("SIM 1");
        HashMap<String, String> sim1Data = new HashMap<>();
        sim1Data = getSim1Details();
        if (!sim1Data.isEmpty()) {

            deviceDetailsTypeStructure.setCategory("SIM 1");
            deviceDetailsTypeStructure.addDetail("State", sim1Data.get("State"));
            deviceDetailsTypeStructure.addDetail("Operator", sim1Data.get("Operator"));
            deviceDetailsTypeStructure.addDetail("Operator Code", sim1Data.get("OperatorCode"));
            deviceDetailsTypeStructure.addDetail("Country", sim1Data.get("Country"));
            deviceDetailsTypeStructure.addDetail("Roaming", sim1Data.get("Roaming"));
            deviceDetailsTypeStructure.addDetail("Network type", sim1Data.get("Networktype"));
            deviceDetails.add(deviceDetailsTypeStructure);
        }

        HashMap<String, String> sim2data = new HashMap<>();
        sim2data = getSim2Details();
        if (!sim2data.isEmpty()) {
            deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
            deviceDetailsTypeStructure.setCategory("SIM 2");

            deviceDetailsTypeStructure.addDetail("State", sim2data.get("State"));
            deviceDetailsTypeStructure.addDetail("Operator", sim2data.get("Operator"));
            deviceDetailsTypeStructure.addDetail("Operator Code", sim2data.get("OperatorCode"));
            deviceDetailsTypeStructure.addDetail("Country", sim2data.get("Country"));
            deviceDetailsTypeStructure.addDetail("Roaming", sim2data.get("Roaming"));
            deviceDetailsTypeStructure.addDetail("Network type", sim2data.get("Networktype"));
            deviceDetails.add(deviceDetailsTypeStructure);
        }
    }

    void addSimDefault() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("DEFAULTS NETWORK FOR SIM");
        deviceDetailsTypeStructure.addDetail("DATA", getDefaultDataSim(context));
        deviceDetailsTypeStructure.addDetail("VOICE", getDefaultVoiceSim(context));
        deviceDetailsTypeStructure.addDetail("SMS", getDefaultSmsSim(context));
        deviceDetails.add(deviceDetailsTypeStructure);
    }

    void addAllPartitions() {
        //  Toast.makeText(context, String.valueOf(getPartitions()), Toast.LENGTH_SHORT).show();
    }


    void addProcessorDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("PROCESSOR");
        deviceDetailsTypeStructure.addDetail("PROCESSOR NAME", getProcessorName());
        deviceDetailsTypeStructure.addDetail("Frequencies", getCpuFrequencies());
        deviceDetailsTypeStructure.addDetail("SUPPORTED ABIs", Arrays.toString(getSupportedABIs()));
        deviceDetailsTypeStructure.addDetail("Cores", String.valueOf(getCpuCoresCount()));
        deviceDetailsTypeStructure.addDetail("BOOTLOADER", getBootloader());
        deviceDetailsTypeStructure.addDetail("CPU", getProcessorDetails());
        deviceDetailsTypeStructure.addDetail("/proc/cpuinfo", getCpuInfo());
        deviceDetails.add(deviceDetailsTypeStructure);
    }

    void addMemoryDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("MEMORY");
        //deviceDetailsTypeStructure.addDetail("RAM TYPE", getProcessorName());
        RAMInfoRetriever.getRAMInfo(context).forEach((k,v)->{
            deviceDetailsTypeStructure.addDetail(k, v + " GB");
        });


//        ZRAM
        String zramUsedSpace = String.valueOf(((getZRAMUsedSpace() / 1024))) + " MB";
        String zramFreeSpace = String.valueOf(((getZRAMFreeSpace() / 1024))) + " MB";
        String total = String.valueOf((getZRAMUsedSpace() + getZRAMFreeSpace()) / 1024) + " MB";
        deviceDetailsTypeStructure.addDetail("ZRAM TOTAL", total);
        deviceDetailsTypeStructure.addDetail("ZRAM FREE", zramFreeSpace);
        deviceDetailsTypeStructure.addDetail("ZRAM USED", zramUsedSpace);
        deviceDetails.add(deviceDetailsTypeStructure);
    }


    //    battery common functions
    static HashMap<String, String> AllBatteryData = new HashMap<>();

    static void setBatteryData(HashMap<String, String> data) {
        //        unregister battery broadcast receiver
        if (battery_receiver != null) {
            context.unregisterReceiver(battery_receiver);
            battery_receiver = null;
        }

        AllBatteryData = data;
    }

    public static HashMap<String, String> getBatterydata() {
        //        unregister battery broadcast receiver
        if (battery_receiver != null) {
            context.unregisterReceiver(battery_receiver);
            battery_receiver = null;
        }
        return AllBatteryData;
    }

    public static void getBatteryExtraInfo() {
        battery_receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                boolean isPresent = intent.getBooleanExtra("present", false);
                String technology = intent.getStringExtra("technology");
                int plugged = intent.getIntExtra("plugged", -1);
                int scale = intent.getIntExtra("scale", -1);
                int health = intent.getIntExtra("health", 0);
                int status = intent.getIntExtra("status", 0);
                int rawlevel = intent.getIntExtra("level", -1);
                int voltageInMillivolts = intent.getIntExtra("voltage", 0);
                double voltageInVolts = voltageInMillivolts / 1000.0;
                int temperature = intent.getIntExtra("temperature", 0);
                temperature /= 10;
                int level = 0;
                int chargingCurrent = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0);
                Bundle bundle = intent.getExtras();
                Log.i("BatteryLevel", bundle.toString());
                if (isPresent) {
                    if (rawlevel >= 0 && scale > 0) {
                        level = (rawlevel * 100) / scale;
                    }
                    HashMap<String, String> batteryInfoData;
                    batteryInfoData = new HashMap<>();
                    batteryInfoData.put("LEVEL", String.valueOf(getBatteryLevel()) + "%");
                    batteryInfoData.put("Technology", technology);
                    batteryInfoData.put("Plugged", getPlugTypeString(plugged));
                    batteryInfoData.put("Health", getHealthString(health));
                    batteryInfoData.put("Status", getStatusString(status));
                    batteryInfoData.put("Voltage", String.valueOf(voltageInVolts + " V"));
                    batteryInfoData.put("Temperature", String.valueOf(temperature) + "\u00B0" + "C");
                      batteryInfoData.put("Capacity(Reported By System)", String.valueOf(getBatteryCapacity(context) + " mah"));
                    if (getTimeUntilFull() == 0)
                        batteryInfoData.put("TimeUntilFull", ("Full Charged"));
                    else batteryInfoData.put("TimeUntilFull", (getTimeUntilFull() + " Minutes"));
                    setBatteryData(batteryInfoData);

                } else {
                    Toast.makeText(context, "Battery not present!!!", Toast.LENGTH_SHORT).show();
                }
            }
        };
        filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        //    Toast.makeText(context, "registered", Toast.LENGTH_SHORT).show();
        context.registerReceiver(battery_receiver, filter);
    }

    public static long getTimeUntilFull() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPercentage = (level / (float) scale) * 100;

        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;

        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean isUSBCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean isACCharging = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        long timeUntilFullMinutes = 0;
        if (isCharging && (isUSBCharging || isACCharging)) {
            int levelMax = 100 - level;
            float chargingRate = 1.0f;

            timeUntilFullMinutes = (long) (levelMax / chargingRate);
            // Use the estimated time until full
            // ...
        } else {

        }

        return timeUntilFullMinutes;
    }

    public static int getBatteryLevel() {
        try {
            Intent batteryIntent = context.registerReceiver(null, new IntentFilter(
                    Intent.ACTION_BATTERY_CHANGED));

            int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL,
                    -1);

            return level;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static String getHealthString(int health) {
        String healthString = "Unknown";

        switch (health) {
            case BatteryManager.BATTERY_HEALTH_DEAD:
                healthString = "Dead";
                break;
            case BatteryManager.BATTERY_HEALTH_GOOD:
                healthString = "Good";
                break;
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                healthString = "Over Voltage";
                break;
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                healthString = "Over Heat";
                break;
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                healthString = "Failure";
                break;
        }

        return healthString;
    }

    private static String getStatusString(int status) {
        String statusString = "NO";

        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                statusString = "Charging";
                break;
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                statusString = "Discharging";
                break;
            case BatteryManager.BATTERY_STATUS_FULL:
                statusString = "Full";
                break;
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                statusString = "Not Charging";
                break;
        }

        return statusString;
    }

    private static String getPlugTypeString(int plugged) {
        String plugType = "NO";

        switch (plugged) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                plugType = "AC";
                break;
            case BatteryManager.BATTERY_PLUGGED_USB:
                plugType = "USB";
                break;
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                plugType = "WIRELESS";
                break;
        }

        return plugType;
    }

    @SuppressLint("PrivateApi")
    public static double getBatteryCapacity(Context context) {
        Object powerProfile;
        double batteryCapacity = 0;
        final String powerProfileClass = "com.android.internal.os.PowerProfile";
        try {
            powerProfile = Class.forName(powerProfileClass)
                    .getConstructor(Context.class)
                    .newInstance(context);
            batteryCapacity = (double) Class
                    .forName(powerProfileClass)
                    .getMethod("getBatteryCapacity")
                    .invoke(powerProfile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return batteryCapacity;
    }


    public String bytesToGB(long bytes) {
        double gb = (double) bytes / (1024 * 1024 * 1024);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(gb) + "GB";
    }

    public String bytesToMB(long bytes) {
        double mb = (double) bytes / (1024 * 1024);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(mb) + "MB";
    }

    void addStorageDetails() {
        deviceDetails.add(insertData("Storage", getStorageInfo()));
    }

    static deviceDetailsTypeStructure insertData(String category, HashMap<String, String> data) {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory(category);
        for (String key : data.keySet()) {
            deviceDetailsTypeStructure.addDetail(key, data.get(key));
        }
        return deviceDetailsTypeStructure;
    }

    public HashMap<String, String> getStorageInfo() {

        ContentResolver contentResolver = context.getContentResolver();
        HashMap<String, String> storageInfoMap = new HashMap<>();
        HashMap<String, Long> mediaSize = calculateMediaSize(contentResolver);

        for (Map.Entry<String, Long> entry : mediaSize.entrySet()) {
            String key = entry.getKey();
            String value = formatSize(entry.getValue());
            storageInfoMap.put(key, value);
        }
        // Storage Type
        storageInfoMap.put("Storage Type", getStorageType(context));

        // Total Storage
        long totalStorage = getTotalInternalMemorySize();
        storageInfoMap.put("Total Storage", convertToNearestGreaterCategory(totalStorage) + " GB");

        // Free Storage
        long freeStorage = getAvailableInternalMemorySize();
        storageInfoMap.put("Free Storage", formatSize(freeStorage));

        // Used Storage
        long usedStorage = totalStorage - freeStorage;
        storageInfoMap.put("Storage Used", formatSize(usedStorage));


        return storageInfoMap;
    }


    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return totalBlocks * blockSize;
    }
    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        return availableBlocks * blockSize;
    }

    public static HashMap<String, Long> calculateMediaSize(ContentResolver contentResolver) {
        HashMap<String, Long> mediaSizeMap = new HashMap<>();

        String[] projection = {
                MediaStore.Files.FileColumns.MEDIA_TYPE,
                MediaStore.Files.FileColumns.SIZE
        };

        String selection = MediaStore.Files.FileColumns.MEDIA_TYPE + " IN (?, ?, ?)";
        String[] selectionArgs = {
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
                String.valueOf(MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO)
        };

        Uri mediaUri = MediaStore.Files.getContentUri("external");

        Cursor cursor = contentResolver.query(
                mediaUri,
                projection,
                selection,
                selectionArgs,
                null
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int mediaType = cursor.getInt(cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE));
                @SuppressLint("Range") long fileSize = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE));

                String category = getCategory(mediaType);

                mediaSizeMap.put(category, mediaSizeMap.getOrDefault(category, 0L) + fileSize);
            }
            cursor.close();
        } else {
            Log.d("MediaSizeCalculator", "Cursor is null. NO media files found.");
        }

        Log.d("MediaSizeCalculator", "Media Size Map: " + mediaSizeMap.toString());

        return mediaSizeMap;
    }

    private static String getCategory(int mediaType) {
        switch (mediaType) {
            case MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE:
                return "Images";
            case MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO:
                return "Videos";
            case MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO:
                return "Audio";
            default:
                return "Other";
        }
    }


    public static long getSystemStorageUsed() {
        long systemStorageUsed = 0;

        try {
            String path = Environment.getDataDirectory().getAbsolutePath();
            StatFs statFs = new StatFs(path);

            long blockSize = statFs.getBlockSizeLong();
            long totalBlocks = statFs.getBlockCountLong();
            long availableBlocks = statFs.getAvailableBlocksLong();
            long freeBlocks = statFs.getFreeBlocksLong();

            long totalSize = totalBlocks * blockSize;
            long availableSize = availableBlocks * blockSize;
            long freeSize = freeBlocks * blockSize;

            systemStorageUsed = totalSize - availableSize;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return systemStorageUsed;
    }



    private static long getAppDataStorage() {
        File path = new File("/data/data");
        long totalSize = getFolderSize(path);
        return totalSize;
    }

    private static long getFolderSize(File folder) {
        if (!folder.exists()) {
            return 0;
        }
        if (folder.isFile()) {
            return folder.length();
        }
        long size = 0;
        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                size += getFolderSize(file);
            }
        }
        return size;
    }

    private static long getDataFolderSize() {
        File path = new File("/data/data");
        long totalSize = getFolderSize(path);
        return totalSize;
    }

    void addOtherDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("OTHER DETAILS");


        PackageManager packageManager = context.getPackageManager();
        boolean hasInfraredBlaster = packageManager.hasSystemFeature(PackageManager.FEATURE_CONSUMER_IR);
        if (hasInfraredBlaster) {
            deviceDetailsTypeStructure.addDetail("Infrared Blaster", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("Infrared Blaster", "NOT SUPPORTED");
        }

        boolean isUSBHostSupported = isUSBHostSupported(context.getApplicationContext());
        if (isUSBHostSupported) {
            deviceDetailsTypeStructure.addDetail("USB HOST", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("USB HOST", "NOT SUPPORTED");
        }

        boolean isUSBAccessorySupported = isUSBAccessorySupported(context.getApplicationContext());
        if (isUSBAccessorySupported) {
            deviceDetailsTypeStructure.addDetail("USB Accessory", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("USB Accessory", "NOT SUPPORTED");
        }

        boolean isHighVRModeSupported = isHighVRModeSupported(context.getApplicationContext());
        if (isHighVRModeSupported) {
            deviceDetailsTypeStructure.addDetail("HIGH VR MODE", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("HIGH VR MODE", "NOT SUPPORTED");
        }
        boolean isVRModeSupported = isVRModeSupported(context.getApplicationContext());
        if (isVRModeSupported) {
            deviceDetailsTypeStructure.addDetail("VR MODE ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail(" VR MODE", "NOT SUPPORTED");
        }

        boolean isSIPSupported = isSIPSupported(context.getApplicationContext());
        if (isSIPSupported) {
            deviceDetailsTypeStructure.addDetail("SIP Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("SIP Supported ", "NOT SUPPORTED");
        }

        boolean isEthernetSupported = isEthernetSupported(context.getApplicationContext());
        if (isEthernetSupported) {
            deviceDetailsTypeStructure.addDetail("Ethernet Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail(" Ethernet Supported ", "NOT SUPPORTED");
        }

        boolean isRemovableStorageAvailable = isRemovableStorageAvailable(context.getApplicationContext());
        if (isRemovableStorageAvailable) {
            deviceDetailsTypeStructure.addDetail("Removable Storage Available  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail(" Removable Storage Available ", "NOT SUPPORTED");
        }
        boolean isMultitouchSupported = isMultitouchSupported(context);
        if (isMultitouchSupported) {
            deviceDetailsTypeStructure.addDetail("Multitouch Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail(" Multitouch Supported ", "NOT SUPPORTED");
        }

        boolean isMultipleUsersSupported = isMultipleUsersSupported(context);
        if (isMultipleUsersSupported) {
            deviceDetailsTypeStructure.addDetail("Multiple Users Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail(" Multiple Users Supported ", "NOT SUPPORTED");
        }
        boolean isNfcSupported = isNFCSupported(context);
        if (isNfcSupported) {
            deviceDetailsTypeStructure.addDetail("Nfc Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("Nfc Supported  ", "NOT SUPPORTED");
        }

        boolean isSecureNfcSupported = isSecureNfcSupported();
        if (isSecureNfcSupported) {
            deviceDetailsTypeStructure.addDetail("Secure Nfc Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("Secure Nfc Supported  ", "NOT SUPPORTED");
        }

        boolean isVibrationAvailable = isVibrationAvailable(context);
        if (isVibrationAvailable) {
            deviceDetailsTypeStructure.addDetail("Vibration Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("Vibration Supported  ", "NOT SUPPORTED");
        }

        boolean isFingerprintSensorAvailable = isFingerprintSensorAvailable(context);
        if (isFingerprintSensorAvailable) {
            deviceDetailsTypeStructure.addDetail("Fingerprint Supported  ", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("Fingerprint Supported  ", "NOT SUPPORTED");
        }

        boolean isGPSSupported = isGPSSupported(context);
        if (isGPSSupported) {
            deviceDetailsTypeStructure.addDetail("GPS Supported", "SUPPORTED");
        } else {
            deviceDetailsTypeStructure.addDetail("GPS Supported ", "NOT SUPPORTED");
        }

        boolean isGPSEnabled = isGPSEnabled(context);
        if (isGPSEnabled) {
            deviceDetailsTypeStructure.addDetail("GPS Status", "ON");
        } else {
            deviceDetailsTypeStructure.addDetail("GPS Status ", "OFF");
        }


        deviceDetails.add(deviceDetailsTypeStructure);
    }

    //    input devices
    public String[] getAllInputDeviceNames(Context context) {
        int[] deviceIds = InputDevice.getDeviceIds();
        List<String> deviceNames = new ArrayList<>();

        for (int deviceId : deviceIds) {
            InputDevice inputDevice = InputDevice.getDevice(deviceId);
            if (inputDevice != null) {
                deviceNames.add(inputDevice.getName());
            }
        }

        return deviceNames.toArray(new String[deviceNames.size()]);
    }

    //    connection details
    public static HashMap<String, String> getMobileConnectionDetails() {
        try {
            HashMap<String, String> connData = new HashMap<>();
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            String connectionType = networkInfo.getTypeName();

            String operatorName = telephonyManager.getNetworkOperatorName();

            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            }
            int networkType = telephonyManager.getNetworkType();
            String networkTypeName = "";

            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_1xRTT:
                    networkTypeName = "1xRTT";
                    break;
                case TelephonyManager.NETWORK_TYPE_CDMA:
                    networkTypeName = "CDMA";
                    break;
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    networkTypeName = "EDGE";
                    break;
                // Add more cases for other network types
            }

// Get dBm value
            int dbm = 0;
            int perc = 0;
            if (networkInfo.isConnected()) {
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // Wi-Fi signal strength
                    WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
                    dbm = wifiManager.getConnectionInfo().getRssi();

//                   mobile sim signal strength
                } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        for (android.telephony.CellSignalStrength signalStrength : telephonyManager.getSignalStrength().getCellSignalStrengths()) {
                            int rsrpValue = signalStrength.getDbm();
                            perc = signalStrength.getAsuLevel() + 16;
                            dbm = rsrpValue;
                        }
                    }
                }
            }


            String nettype = getNetworkType(telephonyManager);
// Log network information
            connData.put("CONNECTION_TYPE", connectionType);
            connData.put("OPERATOR_NAME", operatorName);
            connData.put("dBm", String.valueOf(dbm));
            connData.put("Level", String.valueOf(perc));
            Log.d("Network", "Connection Type: " + connectionType);
            Log.d("Network", "Operator Name: " + operatorName + " " + nettype);
            Log.d("Network", "dBm" + dbm);
            return connData;
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return null;
    }

//    identifiers

    String googlePlayServicesVersion() {
        String googlePlayServicesVersion;
        PackageManager packageManager = context.getPackageManager();
        String googlePlayServicesPackageName = "com.google.android.gms";
        PackageInfo packageInfo = null;
        try {
            packageInfo = packageManager.getPackageInfo(googlePlayServicesPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageInfo != null) {
            googlePlayServicesVersion = packageInfo.versionName;
            return googlePlayServicesVersion;
        }
        return "NOT FOUND";
    }

    public static String getGoogleServicesFrameworkId(Context context) {
        String gsfId = null;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://com.google.android.gsf.gservices"), null, null, new String[]{"android_id"}, null);
        if (cursor != null && cursor.moveToFirst()) {
            gsfId = cursor.getString(0);
            cursor.close();
        }
        return gsfId;
    }


    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    //    mobile details
    public static void getMobileDetails() {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        boolean isSimAvailable = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY;
        boolean isDualSim = telephonyManager.getPhoneCount() > 1;
        String phoneType = "";
        switch (telephonyManager.getPhoneType()) {
            case TelephonyManager.PHONE_TYPE_GSM:
                phoneType = "GSM";
                break;
            case TelephonyManager.PHONE_TYPE_CDMA:
                phoneType = "CDMA";
                break;
            case TelephonyManager.PHONE_TYPE_NONE:
                phoneType = "NONE";
                break;
        }
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

        }
        boolean isESIMSupported = isESIMSupported();
        // Method to check if eSIM is supported on the device
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("MOBILE");
        deviceDetailsTypeStructure.addDetail("SIM Available:", isSimAvailable ? "YES" : "NO");
        deviceDetailsTypeStructure.addDetail("Dual Sim", isDualSim ? "YES" : "NO");
        deviceDetailsTypeStructure.addDetail("Phone Type", phoneType);
        deviceDetailsTypeStructure.addDetail("eSIM Supported:", isESIMSupported ? "YES" : "NO");
        deviceDetails.add(deviceDetailsTypeStructure);

    }


    public static boolean isESIMSupported() {
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        if (subscriptionManager != null) {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            final int activeSubscriptionsCount = subscriptionManager.getActiveSubscriptionInfoCount();
            for (int i = 0; i < activeSubscriptionsCount; i++) {
                SubscriptionInfo subscriptionInfo = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(i);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (subscriptionInfo != null && subscriptionInfo.isEmbedded()) {
                        // If an eSIM is found, return true
                        return true;
                    }
                }
            }
        }
        return false;
    }


    public String getPartitions() {
        String line = null;
        String output = null;
        try {
            Process process = Runtime.getRuntime().exec("cat /proc/partitions");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = reader.readLine()) != null) {
                output += line;
            }
        } catch (IOException e) {

        }
        return output;
    }

    //    sim default settings
    private String getDefaultDataSim(Context context) {
        try {
            SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            int defaultDataId = SubscriptionManager.getDefaultDataSubscriptionId();
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "NO SIM";
            }
            SubscriptionInfo info = manager.getActiveSubscriptionInfo(defaultDataId);
            if (info != null) {
                String operatorName = info.getCarrierName().toString();
                String simName = info.getDisplayName().toString();
                int simSlot = info.getSimSlotIndex() + 1;
                return simName + " " + "(" + operatorName + ")";
            }
            return "NO SIM";
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "NO SIM";
    }

    //    sims details
    public HashMap<String, String> getSim1Details() {
        HashMap<String, String> sim1DetailsMap = new HashMap<>();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
        }
        String state = telephonyManager.getSimState() == TelephonyManager.SIM_STATE_READY ? "Connected" : "Disconnected";
        String operator = telephonyManager.getNetworkOperatorName();
        String operatorCode = telephonyManager.getNetworkOperator();
        String country = telephonyManager.getNetworkCountryIso();
        String roamingStatus = getRoamingStatus(telephonyManager);
        String networkType = getNetworkType(telephonyManager);
        sim1DetailsMap.put("State", state);
        sim1DetailsMap.put("Operator", operator);
        sim1DetailsMap.put("OperatorCode", operatorCode);
        sim1DetailsMap.put("Country", country);
        sim1DetailsMap.put("Roaming", roamingStatus);
        sim1DetailsMap.put("Networktype", networkType);
        return sim1DetailsMap;
    }

    private String getDefaultVoiceSim(Context context) {
        try {
            SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            int defaultVoiceId = SubscriptionManager.getDefaultVoiceSubscriptionId();
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "NO SIM ";
            }
            SubscriptionInfo info = manager.getActiveSubscriptionInfo(defaultVoiceId);
            if (info != null) {
                String simName = info.getDisplayName().toString();
                int simSlot = info.getSimSlotIndex() + 1;
                String operatorName = info.getCarrierName().toString();
                return simName + " " + "(" + operatorName + ")";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "NO SIM ";
    }

    private String getDefaultSmsSim(Context context) {
        try {
            SubscriptionManager manager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
            int defaultSmsId = SubscriptionManager.getDefaultSmsSubscriptionId();
            if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                return "PERMISSION NEEDED";
            }
            SubscriptionInfo info = manager.getActiveSubscriptionInfo(defaultSmsId);
            if (info != null) {
                String simName = info.getDisplayName().toString();
                int simSlot = info.getSimSlotIndex() + 1;
                String operatorName = info.getCarrierName().toString();
                return simName + " " + "(" + operatorName + ")";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "NO SIM";
    }


    public HashMap<String, String> getSim2Details() {
        HashMap<String, String> sim2DetailsMap = new HashMap<>();
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, handle accordingly (e.g., request permission)
        }
        SubscriptionManager subscriptionManager = SubscriptionManager.from(context);
        int simCount = subscriptionManager.getActiveSubscriptionInfoCount();
        if (simCount >= 2) {
            SubscriptionInfo subscriptionInfo2 = subscriptionManager.getActiveSubscriptionInfoForSimSlotIndex(1);
            if (subscriptionInfo2 != null) {
                TelephonyManager telephonyManager2 = telephonyManager.createForSubscriptionId(subscriptionInfo2.getSubscriptionId());
                String state2 = telephonyManager2.getSimState() == TelephonyManager.SIM_STATE_READY ? "Connected" : "Disconnected";
                String operator2 = telephonyManager2.getNetworkOperatorName();
                String operatorCode2 = telephonyManager2.getNetworkOperator();
                String country2 = telephonyManager2.getNetworkCountryIso();
                String roamingStatus2 = getRoamingStatus(telephonyManager2);
                String networkType2 = getNetworkType(telephonyManager2);
                sim2DetailsMap.put("State", state2);
                sim2DetailsMap.put("Operator", operator2);
                sim2DetailsMap.put("OperatorCode", operatorCode2);
                sim2DetailsMap.put("Country", country2);
                sim2DetailsMap.put("Roaming", roamingStatus2);
                sim2DetailsMap.put("Networktype", networkType2);
            }
        }
        return sim2DetailsMap;
    }

    private String getRoamingStatus(TelephonyManager telephonyManager) {
        if (telephonyManager.isNetworkRoaming())
            return "ENABLED";
        else
            return "DISABLED";
    }

    private static String getNetworkType(TelephonyManager telephonyManager) {
        if (context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            return "PERMISSION NEEDED";
        }
        try {
            int networkType = telephonyManager.getNetworkType();
            switch (networkType) {
                case TelephonyManager.NETWORK_TYPE_GPRS:
                    return "GPRS";
                case TelephonyManager.NETWORK_TYPE_EDGE:
                    return "EDGE";
                case TelephonyManager.NETWORK_TYPE_UMTS:
                    return "UMTS";
                case TelephonyManager.NETWORK_TYPE_HSDPA:
                    return "HSDPA";
                case TelephonyManager.NETWORK_TYPE_HSPAP:
                    return "HSPA+";
                case TelephonyManager.NETWORK_TYPE_HSUPA:
                    return "HSUPA";
                case TelephonyManager.NETWORK_TYPE_LTE:
                    return "LTE";
                default:
                    return "Unknown";
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "Unknown";
    }


    public boolean isNFCSupported(Context context) {
        try {
            NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
            return nfcAdapter != null;
        } catch (UnsupportedOperationException e) {
            // NFC is not supported on this device
            return false;
        }
    }

    public boolean isFingerprintSensorAvailable(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
                if (fingerprintManager != null && fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints()) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static boolean isVibrationAvailable(Context context) {

        try {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null && vibrator.hasVibrator()) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static boolean isGPSEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isGPSSupported(Context context) {
        try {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                return true;
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return false;
    }

    public static boolean isHighVRModeSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_VR_MODE_HIGH_PERFORMANCE);
    }

    public static boolean isVRModeSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_VR_MODE);
    }

    public static boolean isSIPSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_SIP);
    }

    public static boolean isEthernetSupported(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_ETHERNET;
    }

    public static boolean isRemovableStorageAvailable(Context applicationContext) {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    public static boolean isUSBHostSupported(Context context) {
        UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
        return usbManager != null && usbManager.getDeviceList() != null;
    }

    public static boolean isMultipleUsersSupported(Context context) {
        UserManager userManager = (UserManager) context.getSystemService(Context.USER_SERVICE);
        return userManager != null && UserManager.supportsMultipleUsers();
    }

    public boolean isUSBAccessorySupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        boolean hasUSBFeature = packageManager.hasSystemFeature(PackageManager.FEATURE_USB_ACCESSORY);

        if (hasUSBFeature) {
            UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
            UsbAccessory[] accessories = usbManager.getAccessoryList();
            return accessories != null && accessories.length > 0;
        }
        return false;
    }

    public static boolean isMultitouchSupported(Context context) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH);
    }

    public boolean isSecureNfcSupported() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (NfcAdapter.getDefaultAdapter(context).isSecureNfcSupported()) {
                    return true;
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;

    }


//    network functions

    public Map<String, String> getSimInfo(Context context) {
        Map<String, String> simInfo = new HashMap<>();
        TelephonyManager telephonyManager = (TelephonyManager) fetchDeviceDetailsHelper.this.context.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);

        String simName = telephonyManager.getNetworkOperatorName();


        // Store the SIM names and default data SIM in the result map
        simInfo.put("SIM_NAME", simName);
        simInfo.put("OPERATOR_NAME", telephonyManager.getNetworkOperatorName());
        simInfo.put("DEFAULT_SIM", String.valueOf(""));

        return simInfo;
    }


    SubscriptionInfo getSubscriptionInfoForSimSlot(SubscriptionManager subscriptionManager, int simSlot) {
        if (simSlot >= 0) {
            if (context.checkSelfPermission(android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    contextCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for contextCompat#requestPermissions for more details.
                return null;
            }
            for (SubscriptionInfo info : subscriptionManager.getActiveSubscriptionInfoList()) {
                if (info.getSimSlotIndex() == simSlot) {
                    return info;
                }
            }
        }
        return null;
    }

    private static String getStateString(int simState) {
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT:
                return "Absent";
            case TelephonyManager.SIM_STATE_PIN_REQUIRED:
                return "PIN Required";
            case TelephonyManager.SIM_STATE_PUK_REQUIRED:
                return "PUK Required";
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
                return "Network Locked";
            case TelephonyManager.SIM_STATE_READY:
                return "Ready";
            case TelephonyManager.SIM_STATE_UNKNOWN:
                return "Unknown";
            default:
                return "N/A";
        }
    }

    private boolean isRoamingEnabled(SubscriptionInfo subscriptionInfo) {
        return subscriptionInfo.getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE;
    }


    //    STORAGE FUNCTIONS


    public String getStorageType(Context context) {
        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        StorageVolume primaryVolume = storageManager.getPrimaryStorageVolume();
        String description = primaryVolume.getDescription(context);
        return extractStorageType(description);
    }

    private String extractStorageType(String description) {
        if (description != null && !description.isEmpty()) {
            if (description.toLowerCase().contains("ufs")) {
                return "UFS";
            } else if (description.toLowerCase().contains("emmc")) {
                return "eMMC";
            } else {
                return description;
            }
        }
        return "Unknown";
    }


    public String[] getInternalStorageData(Context context) {
        String[] data = new String[5];
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);

        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long totalSize = blockSize * totalBlocks;
        long availableBlocks = statFs.getAvailableBlocksLong();
        long freeSize = blockSize * availableBlocks;
        long usedSize = totalSize - freeSize;

        String total = formatSize(totalSize);
        String used = formatSize(usedSize);
        String free = formatSize(freeSize);

        long appDataSize = getAllAppDataSize(context);
        long systemSize = usedSize - appDataSize;

        String usedBySystem = formatSize(systemSize);
        String usedByAppData = String.valueOf(getTotalAppUsage(context));


        data[0] = total;
        data[1] = used;
        data[2] = free;
        data[3] = usedBySystem;
        data[4] = usedByAppData;

        return data;
    }


    public HashMap<String, String> getFileSystem(String path) {
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSizeLong();
        long totalBlocks = statFs.getBlockCountLong();
        long availableBlocks = statFs.getAvailableBlocksLong();
        long freeBlocks = statFs.getFreeBlocksLong();
        long totalSize = totalBlocks * blockSize;
        long availableSize = availableBlocks * blockSize;
        long freeSize = freeBlocks * blockSize;

        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String totalSizeGB = decimalFormat.format(totalSize / (1024.0 * 1024.0 * 1024.0));
        String availableSizeGB = decimalFormat.format(availableSize / (1024.0 * 1024.0 * 1024.0));
        String freeSizeGB = decimalFormat.format(freeSize / (1024.0 * 1024.0 * 1024.0));

        HashMap<String, String> data = new HashMap<>();
        data.put("Total", totalSizeGB + " GB");
        data.put("Available ", availableSize + "GB");
        data.put("Free ", freeSize + "GB");

        return data;
    }

    public double getTotalAppUsage(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<ApplicationInfo> applicationInfoList = packageManager.getInstalledApplications(0);
        long totalSize = 0;

        for (ApplicationInfo applicationInfo : applicationInfoList) {
            try {
                File file = new File(applicationInfo.publicSourceDir);
                totalSize += file.length();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        double totalSizeGB = (double) totalSize / (1024 * 1024 * 1024);
        return totalSizeGB;
    }

    private long getAllAppDataSize(Context context) {
        PackageManager packageManager = context.getPackageManager();
        long totalSize = 0;

        try {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            totalSize += getDirSize(applicationInfo.dataDir);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        // Get the list of installed applications
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo appInfo : installedApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                // Exclude system apps
                totalSize += getDirSize(appInfo.dataDir);
            }
        }

        return totalSize;
    }

    private long getDirSize(String dirPath) {
        File dir = new File(dirPath);
        if (!dir.exists()) {
            return 0;
        }

        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                } else if (file.isDirectory()) {
                    size += getDirSize(file.getAbsolutePath());
                }
            }
        }
        return size;
    }


    //    MEMORY
    public long getZRAMUsedSpace() {
        long totalSwap = 0;
        long freeSwap = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("SwapTotal:")) {
                    String[] splitLine = line.split("\\s+");
                    totalSwap = Long.parseLong(splitLine[1]);
                } else if (line.startsWith("SwapFree:")) {
                    String[] splitLine = line.split("\\s+");
                    freeSwap = Long.parseLong(splitLine[1]);
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (totalSwap - freeSwap);
    }

    public long getZRAMFreeSpace() {
        long freeSwap = 0;

        try {
            BufferedReader br = new BufferedReader(new FileReader("/proc/meminfo"));
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("SwapFree:")) {
                    String[] splitLine = line.split("\\s+");
                    freeSwap = Long.parseLong(splitLine[1]);
                    break;
                }
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return (freeSwap);
    }

    private static long bytesToMegabytes(long bytes) {
        return bytes / (1024 * 1024);
    }


    public static int convertToNearestGreaterCategory(long sizeInBytes) {
        // Convert bytes to gigabytes
        double sizeInGB = (double) sizeInBytes / (1024 * 1024 * 1024);

        // Round up the gigabyte value to the nearest greater category
        int roundedSize = (int) Math.ceil(sizeInGB);

        // Determine the nearest greater category
        int[] categories = {8, 16, 32, 64, 128, 256};
        int nearestCategory = 0;
        for (int category : categories) {
            if (roundedSize <= category) {
                nearestCategory = category;
                break;
            }
        }

        return nearestCategory;
    }

    private static String formatSize(long size) {
        if (size <= 0) {
            return "0 MB";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        if (digitGroups >= units.length) {
            digitGroups = units.length - 1;
        }


        return String.format("%.2f %s", size / Math.pow(1024, digitGroups), units[digitGroups]);
    }


    public static class RAMInfoRetriever {

        public static HashMap<String, Double> getRAMInfo(Context context) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
            activityManager.getMemoryInfo(memoryInfo);

            double totalRAMGB = bytesToGB(memoryInfo.totalMem);
            double nonceiledTotal=totalRAMGB;
            totalRAMGB = Math.ceil(totalRAMGB); // Round up the total RAM to the next integer

            // Calculate used RAM and available RAM using ceiled total RAM
            double usedRAMGB = nonceiledTotal - bytesToGB(memoryInfo.availMem);
            double availableRAMGB = nonceiledTotal - usedRAMGB;

            HashMap<String, Double> ramInfoMap = new HashMap<>();
            ramInfoMap.put("Total RAM", totalRAMGB);
            ramInfoMap.put("Used RAM", roundToTwoDecimals(usedRAMGB));
            ramInfoMap.put("Available RAM", roundToTwoDecimals(availableRAMGB));

            return ramInfoMap;
        }

        private static double bytesToGB(long bytes) {
            return bytes / (1024.0 * 1024.0 * 1024.0);
        }

        // Round a double value to two decimal places
        private static double roundToTwoDecimals(double value) {
            DecimalFormat df = new DecimalFormat("#.##");
            return Double.parseDouble(df.format(value));
        }
    }

    public String getTotalRAM(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
            long totalBytes = memoryInfo.totalMem;
            double totalGB = (double) totalBytes / (1024 * 1024 * 1024); // Convert bytes to GB

            // Calculate the ceil value in GB
            int ceilGB = (int) Math.ceil(totalGB);

            // Format the result as GB
            return ceilGB + "GB";
        }

        return "Unknown";
    }


    public String getUsedMemory(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        long usedMemoryBytes = memoryInfo.totalMem - memoryInfo.availMem;
        double usedMemory;
        String memoryUnit;

        if (usedMemoryBytes >= 1024 * 1024 * 1024) {
            usedMemory = usedMemoryBytes / (1024.0 * 1024 * 1024);
            memoryUnit = "GB";
        } else {
            usedMemory = usedMemoryBytes / (1024.0 * 1024);
            memoryUnit = "MB";
        }

        return String.format("%.2f %s", usedMemory, memoryUnit);
    }

    public String getFreeRAM(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();

        if (activityManager != null) {
            activityManager.getMemoryInfo(memoryInfo);
            long totalBytes = memoryInfo.totalMem;
            double totalGB = (double) totalBytes / (1024 * 1024 * 1024); // Convert bytes to GB

            // Calculate the ceil value in GB for total RAM
            int ceilTotalGB = (int) Math.ceil(totalGB);

            // Format the total RAM result as GB
            String totalRAM = ceilTotalGB + "GB";

            // Now, calculate the free RAM using the ceiled total RAM
            long freeBytes = memoryInfo.availMem;
            double freeGB = (double) freeBytes / (1024 * 1024 * 1024); // Convert bytes to GB

            // Format the free RAM result as GB
            String freeRAM = String.format("%.2fGB", freeGB);

            return freeRAM;
        }

        return "Unknown";
    }

    public boolean isZRAMEnabled() {
        String EnabledOrNot = "";
        try {
            Method method = MemoryFile.class.getDeclaredMethod("isSwappingEnabled");
            method.setAccessible(true);
            if ((boolean) method.invoke(null)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

//    PROCESSOR


    public static List<Integer> getLiveCpuFrequencies() {
        List<Integer> coreFrequencies = new ArrayList<>();
        int numOfCores = Runtime.getRuntime().availableProcessors();

        for (int i = 0; i < numOfCores; i++) {
            int frequency = readCpuFrequency(i);
            coreFrequencies.add(frequency);
        }

        return coreFrequencies;
    }

    private static int readCpuFrequency(int coreIndex) {
        int frequency = 0;
        BufferedReader reader = null;

        try {
            File cpuFreqFile = new File("/sys/devices/system/cpu/cpu" + coreIndex + "/cpufreq/scaling_cur_freq");
            reader = new BufferedReader(new FileReader(cpuFreqFile));
            String line;
            if ((line = reader.readLine()) != null) {
                frequency = Integer.parseInt(line) / 1000; // Convert to MHz
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return frequency;
    }


    public static class CpuInfo {
        private int cpuUsage;
        private List<Integer> coreFrequencies;

        public int getCpuUsage() {
            return cpuUsage;
        }

        public void setCpuUsage(int cpuUsage) {
            this.cpuUsage = cpuUsage;
        }

        public List<Integer> getCoreFrequencies() {
            return coreFrequencies;
        }

        public void setCoreFrequencies(List<Integer> coreFrequencies) {
            this.coreFrequencies = coreFrequencies;
        }
    }

    public String getProcessorDetails() {
        String hardware = Build.HARDWARE;
        int coreCount = getCpuCoresCount();
        String clockSpeed = "";
        String processorDescription = "";
        switch (coreCount) {
            case 1:
                processorDescription = "Single-core";
                break;
            case 2:
                processorDescription = "Dual-core";
                break;
            case 4:
                processorDescription = "Quad-core";
                break;
            case 6:
                processorDescription = "Hexa-core";
                break;
            case 8:
                processorDescription = "Octa-core";
                break;
            default:
                processorDescription = "Unknown";
                break;
        }

        // Combine the information into a description string

        String description = processorDescription + " " + getClockSpeed();

        return description;
    }

    public int getCpuCoresCount() {
        return Runtime.getRuntime().availableProcessors();
    }

    public String getClockSpeed() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu0/cpufreq/scaling_cur_freq"));
            String line = reader.readLine();
            reader.close();

            if (line != null) {
                int currentFreq = Integer.parseInt(line);
                return (currentFreq / 1000) + " MHz";
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }

        return "Unknown";
    }

    public static String getBootloader() {
        return Build.BOOTLOADER;
    }

    public static String getCpuInfo() {
        StringBuilder cpuInfoBuilder = new StringBuilder();
        try {
            BufferedReader reader = new BufferedReader(new FileReader("/proc/cpuinfo"));
            String line;
            while ((line = reader.readLine()) != null) {
                cpuInfoBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cpuInfoBuilder.toString();
    }

    public static String[] getSupportedABIs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            // For older versions, fallback to single ABI
            return new String[]{Build.CPU_ABI};
        }
    }


    public static String getProcessorName() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return Build.SOC_MANUFACTURER + " " + Build.SOC_MODEL;
        }
        return Build.HARDWARE;
    }



    private String getCpuFrequencies() {
        int numCores = Runtime.getRuntime().availableProcessors();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < numCores; i++) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader("/sys/devices/system/cpu/cpu" + i + "/cpufreq/scaling_available_frequencies"));
                String line = reader.readLine();
                reader.close();

                String[] frequenciesStr = line.split("\\s+");
                int[] frequencies = new int[frequenciesStr.length];

                for (int j = 0; j < frequenciesStr.length; j++) {
                    frequencies[j] = Integer.parseInt(frequenciesStr[j]) / 1000; // Convert to MHz
                }

                if (frequencies.length == 0) {
                    sb.append("CPU").append(i).append(": N/A\n");
                } else {
                    int minFrequency = frequencies[0];
                    int maxFrequency = frequencies[frequencies.length - 1];
                    sb.append("CPU").append(i).append(" Frequencies: ").append(minFrequency).append(" MHz - ").append(maxFrequency).append(" MHz\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
                sb.append("CPU").append(i).append(": N/A\n");
            }
        }

        return sb.toString();
    }

    private static String[] readCpuFrequencies() {
        String[] frequencies = new String[getNumCpuCores()];

        try {
            for (int i = 0; i < frequencies.length; i++) {
                String fileName = String.format("/sys/devices/system/cpu/cpu%d/cpufreq/scaling_cur_freq", i);
                BufferedReader br = new BufferedReader(new FileReader(fileName));
                frequencies[i] = br.readLine();
                br.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return frequencies;
    }



    private static String formatFrequency(String frequency) {
        long freq = Long.parseLong(frequency);
        return freq / 1000000 + " MHz";
    }

    private static int getNumCpuCores() {
        return Runtime.getRuntime().availableProcessors();
    }

    void addConnectionDetails() {

        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("Connection");

        deviceDetailsTypeStructure.addDetail("RADIO BANDS Status", isRadioBandsConnected(context));
       // deviceDetailsTypeStructure.addDetail("Gateway", getGatewayIp(wifiManager));
        deviceDetailsTypeStructure.addDetail("DNS1", getDNS1(context));
        deviceDetailsTypeStructure.addDetail("IPv6", getIPv6Address());

        deviceDetailsTypeStructure.addDetail("IP Address", "SHOW");

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    String getIPv6Address() {
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress.getHostAddress().contains(":")) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getGatewayIp(WifiManager w) {
        return ipAddressToString(w.getDhcpInfo().gateway);
    }

    public static String ipAddressToString(int ipAddress) {

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddress = Integer.reverseBytes(ipAddress);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

        String ipAddressString;
        try {
            ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
        } catch (UnknownHostException ex) {
            Log.e("WIFI_IP", "Unable to get host address.");
            ipAddressString = "NaN";
        }

        return ipAddressString;
    }
    private static String getGatewayFromWifi(Context context, NetworkCapabilities capabilities) {
        try {
            Class<?> wifiManagerClass = Class.forName("android.net.wifi.WifiManager");
            Object wifiManager = context.getSystemService(WIFI_SERVICE);

            Method getWifiServiceMethod = wifiManagerClass.getDeclaredMethod("getWifiService");
            getWifiServiceMethod.setAccessible(true);
            Object wifiService = getWifiServiceMethod.invoke(wifiManager);

            Method getWifiStateMachineMethod = wifiManagerClass.getDeclaredMethod("getWifiStateMachine");
            getWifiStateMachineMethod.setAccessible(true);
            Object wifiStateMachine = getWifiStateMachineMethod.invoke(wifiManager);

            Class<?> wifiStateMachineClass = wifiStateMachine.getClass();
            Method getCurrentWifiInfoMethod = wifiStateMachineClass.getDeclaredMethod("getCurrentWifiInfo");
            getCurrentWifiInfoMethod.setAccessible(true);
            Object wifiInfo = getCurrentWifiInfoMethod.invoke(wifiStateMachine);

            if (wifiInfo != null) {
                int gatewayInt = (int) wifiInfo.getClass().getMethod("getIpAddress").invoke(wifiInfo);
                return formatIpAddress(gatewayInt);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String formatIpAddress(int ipAddress) {
        return ((ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16) & 0xFF) + "." + (ipAddress >> 24 & 0xFF));
    }




    String getDNS1(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        if (wifiManager != null) {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            String dns1 = intToIp(wifiManager.getDhcpInfo().dns1);
            return dns1;
        }
        return null;
    }

    private static String intToIp(int ipAddress) {
        return ((ipAddress & 0xFF) + "." +
                ((ipAddress >> 8) & 0xFF) + "." +
                ((ipAddress >> 16) & 0xFF) + "." +
                (ipAddress >> 24 & 0xFF));
    }


    String isRadioBandsConnected(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonyManager != null) {
                PhoneStateListener phoneStateListener = new PhoneStateListener() {
                    @Override
                    public void onServiceStateChanged(ServiceState serviceState) {
                        // Check the service state for connectivity
                        int state = serviceState.getState();
                        if (state == ServiceState.STATE_IN_SERVICE) {
                            // Radio bands are connected
                            // You can handle the connected state here or trigger any desired actions
                        } else {
                            // Radio bands are disconnected
                            // You can handle the disconnected state here or trigger any desired actions
                        }
                    }
                };

                // Register the PhoneStateListener to listen for service state changes
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);

                // Retrieve the current service state
                if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

                }
                ServiceState serviceState = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    serviceState = telephonyManager.getServiceState();
                }
                if (serviceState != null) {
                    int state = serviceState.getState();
                    return state == ServiceState.STATE_IN_SERVICE ? "Connected" : "Disconnected";
                }
            }
            if (telephonyManager != null) {
                PhoneStateListener phoneStateListener = new PhoneStateListener() {
                    @Override
                    public void onServiceStateChanged(ServiceState serviceState) {
                        // Check the service state for connectivity
                        int state = serviceState.getState();
                        if (state == ServiceState.STATE_IN_SERVICE) {
                            // Radio bands are connected
                            // You can handle the connected state here or trigger any desired actions
                        } else {
                            // Radio bands are disconnected
                            // You can handle the disconnected state here or trigger any desired actions
                        }
                    }
                };

                // Register the PhoneStateListener to listen for service state changes
                telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_SERVICE_STATE);

                // Retrieve the current service state
                if (context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || context.checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return "";
                }
                ServiceState serviceState = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    serviceState = telephonyManager.getServiceState();
                }
                if (serviceState != null) {
                    int state = serviceState.getState();
                    return state == ServiceState.STATE_IN_SERVICE ? "Connected" : "Disconnected";
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return "Disconnected";
    }


    void addWifiDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("wifi");

        deviceDetailsTypeStructure.addDetail("Status", isWifiConnected(context.getApplicationContext()));
        deviceDetailsTypeStructure.addDetail("WIFI DIRECT", "");

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    String isWifiConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) fetchDeviceDetailsHelper.this.context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo wifiNetworkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (wifiNetworkInfo != null) {
                return wifiNetworkInfo.isConnected() ? "CONNECTED" : "NOT CONNECTED";
            }
        }
        return "NOT CONNECTED";
    }


    void addWideVineDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("WideVine");

       // deviceDetailsTypeStructure.addDetail("Vendor", Build.MANUFACTURER);
         deviceDetailsTypeStructure.addDetail("Vendor", "GOOGLE");
        deviceDetailsTypeStructure.addDetail("Version Algorithms", printAlgorithmVersions());
        deviceDetailsTypeStructure.addDetail("AES/CBC/NoPadding", isAlgorithmSupported(("AES/CBC/NoPadding")));
        deviceDetailsTypeStructure.addDetail("HmacSHA256", isAlgorithmSupported(("HmacSHA256")));

        deviceDetailsTypeStructure.addDetail("Device ID", Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID));
        deviceDetailsTypeStructure.addDetail("Security level", getWidevineSecurityLevel());
        deviceDetailsTypeStructure.addDetail("Max HDCP level", getMaxHdcpLevel());

        deviceDetails.add(deviceDetailsTypeStructure);
    }


     String getWidevineSecurityLevel() {
         UUID WIDEVINE_UUID = new UUID(0xEDEF8BA979D64ACEL, 0xA3C827DCD51D21EDL);
         String widevineSecurityLevel = null;
         try {
             MediaDrm mediaDrm = new MediaDrm(WIDEVINE_UUID);
             widevineSecurityLevel = mediaDrm.getPropertyString("securityLevel");

             Log.d("Widevine Security Level", widevineSecurityLevel);

         } catch (UnsupportedSchemeException e) {
             Log.e("Error: ", "Widevine DRM is not supported on this device.");
         }

         return  widevineSecurityLevel;
     }

    String getMaxHdcpLevel() {
        try {
            UUID widevineUuid = new UUID(-0x121074568629b532L, -0x5c37d8232ae2de13L);
            MediaDrm mediaDrm = new MediaDrm(widevineUuid);
            int maxHdcpLevel = 0;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                maxHdcpLevel = mediaDrm.getMaxHdcpLevel();
            }
            mediaDrm.release();

            switch (maxHdcpLevel) {
                case MediaDrm.HDCP_LEVEL_UNKNOWN:
                    return "HDCP UNKNOWN";
                case MediaDrm.HDCP_V2:
                    return "HDCP UNKNOWN 2";
                case MediaDrm.HDCP_V1:
                    return "HDCP 1";
                case MediaDrm.HDCP_V2_1:
                    return "HDCP 2.1";
                case MediaDrm.HDCP_V2_2:
                    return "HDCP 2.2";
                case MediaDrm.HDCP_V2_3:
                    return "HDCP 2.3";
                case MediaDrm.HDCP_NONE:
                    return "HDCP NONE";
                default:
                    return "Unknown HDCP Level";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "HDCP Level Retrieval Failed";
    }




    String isAlgorithmSupported(String algorithm) {
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            Set<Provider.Service> services = provider.getServices();

            for (Provider.Service service : services) {
                String supportedAlgorithm = service.getAlgorithm();

                if (supportedAlgorithm.equals(algorithm)) {
                    return "Supported";
                }
            }
        }

        return "Not Supported";
    }

    String getWidevineVendor() {
        try {
            MediaDrm mediaDrm = new MediaDrm(UUID.fromString("edef8ba9-79d6-4ace-a3c8-27dcd51d21ed"));
            byte[] widevineVendorBytes = mediaDrm.getPropertyByteArray(MediaDrm.PROPERTY_VENDOR);
            mediaDrm.release();

            if (widevineVendorBytes != null) {
                return new String(Base64.decode(widevineVendorBytes, Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String printAlgorithmVersions() {
        String returnThis = "";
        Provider[] providers = Security.getProviders();

        for (Provider provider : providers) {
            String providerName = provider.getName();
            Set<Provider.Service> services = provider.getServices();

            for (Provider.Service service : services) {
                String algorithm = service.getAlgorithm();
                returnThis = algorithm + " " + providerName + " ";
                //System.out.println("Algorithm: " + algorithm + " (Provider: " + providerName + ", Version: " + version + ")");
            }
        }

        return returnThis;
    }

    void addOSDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("OS");

        deviceDetailsTypeStructure.addDetail("Android Version", Build.VERSION.RELEASE);
        deviceDetailsTypeStructure.addDetail("Security patch", Build.VERSION.SECURITY_PATCH);
        deviceDetailsTypeStructure.addDetail("Fingerprint", Build.FINGERPRINT);
        if ( Build.VERSION.SDK_INT == Build.VERSION_CODES.S)deviceDetailsTypeStructure.addDetail("Build", Build.ID);
        else deviceDetailsTypeStructure.addDetail("Build", Build.DISPLAY);
        deviceDetailsTypeStructure.addDetail("MIUI", getMIUIVersion());
        deviceDetailsTypeStructure.addDetail("Build Date", convertBuildTime(Build.TIME));
        deviceDetailsTypeStructure.addDetail("Google Play Services", getGooglePlayServicesVersion(context.getApplicationContext()));
        deviceDetailsTypeStructure.addDetail("Root access", hasRootAccess());
        deviceDetailsTypeStructure.addDetail("Toybox", isToyboxInstalled());
        deviceDetailsTypeStructure.addDetail("Timezone", getCurrentTimezone());
        deviceDetailsTypeStructure.addDetail("USB Debugging Kernel", (isUsbDebuggingEnabled(context) ? "Enabled/" : "Disabled/") + getKernelInformation());
        deviceDetailsTypeStructure.addDetail("Language", getCurrentLanguage());
        deviceDetailsTypeStructure.addDetail("Java VM SSL version", System.getProperty("java.vm.version"));
        deviceDetailsTypeStructure.addDetail("Treble", Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? "YES" : "NO");
        deviceDetailsTypeStructure.addDetail("Architecture Instruction sets", isInstructionSetSupported("arm64"));
        deviceDetailsTypeStructure.addDetail("API", String.valueOf(Build.VERSION.SDK_INT));
        deviceDetails.add(deviceDetailsTypeStructure);
    }

    private boolean isUsbDebuggingEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ADB_ENABLED, 0) == 1;
    }

    private String getKernelInformation() {
        String output = "";
        try {
            Process process = Runtime.getRuntime().exec("uname -a");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                output += line + "\n";
            }
            reader.close();
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return output;
    }

    private String convertBuildTime(long buildTimeInMillis) {
        // Create a SimpleDateFormat with the desired format
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy - HH:mm:ss", Locale.getDefault());

        // Convert the build time from milliseconds to Date object
        Date buildDate = new Date(buildTimeInMillis);

        // Format the Date object to a human-readable string
        return dateFormat.format(buildDate);
    }

    String getCurrentTimezone() {
        TimeZone currentTimezone = TimeZone.getDefault();
        return currentTimezone.getID();
    }

    String getCurrentLanguage() {
        Locale currentLocale = Locale.getDefault();
        return currentLocale.getLanguage();
    }

    String isToyboxInstalled() {
        try {
            Process process = Runtime.getRuntime().exec("which toybox");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.readLine();
            reader.close();

            if (output != null) {
                return "INSTALLED";
            } else {
                return "NOT INSTALLED";
            }
        } catch (Exception e) {
            // Exception occurred, indicating Toybox is not installed
        }

        return "NOT INSTALLED";
    }

    String getGooglePlayServicesVersion(Context context) {
        try {
            PackageInfo packageInfo = fetchDeviceDetailsHelper.this.context.getApplicationContext().getPackageManager().getPackageInfo("com.google.android.gms", 0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    String hasRootAccess() {
        try {
            Process process = Runtime.getRuntime().exec("su");
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String output = reader.readLine();
            reader.close();

            if (output != null && output.toLowerCase().contains("uid")) {
                return "ROOTED";
            }
        } catch (Exception e) {
            // Exception occurred, indicating no root access
        }

        return "NOT ROOTED";
    }

    String isInstructionSetSupported(String instructionSet) {
        String[] supportedInstructionSets = Build.SUPPORTED_ABIS;

        for (String supportedSet : supportedInstructionSets) {
            if (supportedSet.equalsIgnoreCase(instructionSet)) {
                return "arm64";
            }
        }

        return TextUtils.join(", ", supportedInstructionSets);
    }

    String getMIUIVersion() {
        String buildDisplay = Build.DISPLAY;
        String miuiVersion = "Not MIUI";

        if (buildDisplay != null && buildDisplay.toLowerCase().contains("miui")) {
            String[] parts = buildDisplay.split(" ");
            for (String part : parts) {
                if (part.toLowerCase().contains("miui")) {
                    miuiVersion = part;
                    break;
                }
            }
        }

        return miuiVersion;
    }

    String isApiEnabled(Context context, String featureOrPermission) {
        PackageManager packageManager = fetchDeviceDetailsHelper.this.context.getApplicationContext().getPackageManager();

        // Check if the feature or permission is enabled on the device
        if (packageManager.hasSystemFeature(featureOrPermission) ||
                packageManager.checkPermission(featureOrPermission, fetchDeviceDetailsHelper.this.context.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
            return "ENABLED";
        }

        return "DISABLED";
    }


    void addDeviceDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("Device");

        deviceDetailsTypeStructure.addDetail("Model", Build.MODEL);
        deviceDetailsTypeStructure.addDetail("Product", Build.PRODUCT);
        deviceDetailsTypeStructure.addDetail("Platform", "ANDROID");
        deviceDetailsTypeStructure.addDetail("Board", Build.BOARD);
        deviceDetailsTypeStructure.addDetail("Manufacture", Build.MANUFACTURER);
        deviceDetailsTypeStructure.addDetail("Radio", "FM Radio");

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    void addBluetoothDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("BLUETOOTH");

        deviceDetailsTypeStructure.addDetail("Paired devices", "");
        deviceDetailsTypeStructure.addDetail("NearBy devices", "");
        // deviceDetailsTypeStructure.addDetail("Bluetooth 4 features", isBluetooth4Supported());
        deviceDetailsTypeStructure.addDetail("Bluetooth LE (Low Energy)", isBluetoothLESupported(context.getApplicationContext()));
        deviceDetailsTypeStructure.addDetail("Multiple advertisement", isMultipleAdvertisementSupported());
        deviceDetailsTypeStructure.addDetail("Offloaded filtering", isOffloadedFilteringSupported());
        deviceDetailsTypeStructure.addDetail("Offloaded scan batching", isOffloadedScanBatchingSupported());

        deviceDetailsTypeStructure.addDetail("LE Periodic Advertising", isLEPeriodicAdvertisingSupported());
        deviceDetailsTypeStructure.addDetail("LE Extended Advertising", isLEExtendedAdvertisingSupported());

        //deviceDetailsTypeStructure.addDetail("BLUETOOTH ON / OFF", );

        deviceDetailsTypeStructure.addDetail("LE 2M PHY (high speed)", isLE2MPhySupported());
        deviceDetailsTypeStructure.addDetail("LE Coded PHY (long range)", isLECodedPhySupported());
        deviceDetailsTypeStructure.addDetail("LE Audio support", isLEAudioSupported(context.getApplicationContext()));

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    String isLEAudioSupported(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (bluetoothManager != null) {
                BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
                if (bluetoothAdapter != null && bluetoothAdapter.isLe2MPhySupported()) {
                    // LE Audio (LE 2M PHY) is supported
                    // You can now enable features or make adjustments accordingly
                    return "YES";
                } else {
                    // LE Audio (LE 2M PHY) is not supported
                    // Handle the situation accordingly
                    return "NO";
                }
            }
        }
        return "NO";
    }

    String isLECodedPhySupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return bluetoothAdapter.isLeCodedPhySupported() ? "YES" : "NO";
            }
        }
        return "NO";
    }

    String isLE2MPhySupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return bluetoothAdapter.isLe2MPhySupported() ? "YES" : "NO";
            }
        }
        return "NO";
    }

    public static long getDeviceUptimeInMillis() {
        return SystemClock.elapsedRealtime();
    }

    @SuppressLint("DefaultLocale")
    public static String getDeviceUptimeFormatted() {
        long uptimeInMillis = getDeviceUptimeInMillis();
        long seconds = uptimeInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        return String.format("%dd, %dh, %dm, %ds",
                days, hours % 24, minutes % 60, seconds % 60);
    }

    public static String getAndroidVersionName() {
        switch (Build.VERSION.SDK_INT) {
            case 1:
                return "Android 1.0";
            case 2:
                return "Android 1.1";
            case 3:
                return "Android 1.5 (Cupcake)";
            case 4:
                return "Android 1.6 (Donut)";
            case 5:
                return "Android 2.0 (Eclair)";
            case 6:
                return "Android 2.0.1 (Eclair)";
            case 7:
                return "Android 2.1 (Eclair)";
            case 8:
                return "Android 2.2 (Froyo)";
            case 9:
                return "Android 2.3 (Gingerbread)";
            case 10:
                return "Android 2.3.3 (Gingerbread)";
            case 11:
                return "Android 3.0 (Honeycomb)";
            case 12:
                return "Android 3.1 (Honeycomb)";
            case 13:
                return "Android 3.2 (Honeycomb)";
            case 14:
                return "Android 4.0 (Ice Cream Sandwich)";
            case 15:
                return "Android 4.0.3 (Ice Cream Sandwich)";
            case 16:
                return "Android 4.1 (Jelly Bean)";
            case 17:
                return "Android 4.2 (Jelly Bean)";
            case 18:
                return "Android 4.3 (Jelly Bean)";
            case 19:
                return "Android 4.4 (KitKat)";
            case 20:
                return "Android 4.4W (KitKat Wear)";
            case 21:
                return "Android 5.0 (Lollipop)";
            case 22:
                return "Android 5.1 (Lollipop)";
            case 23:
                return "Android 6.0 (Marshmallow)";
            case 24:
                return "Android 7.0 (Nougat)";
            case 25:
                return "Android 7.1 (Nougat)";
            case 26:
                return "Android 8.0 (Oreo)";
            case 27:
                return "Android 8.1 (Oreo)";
            case 28:
                return "Android 9 (Pie)";
            case 29:
                return "Android 10 (Q)";
            case 30:
                return "Android 11 (R)";
            case 31:
                return "Android 12 (S)";
            case 32:
                return "Android 12 (S)";
            case 33:
                return "Android 13";
            case 34:
                return "Android 14";
            default:
                return "Unknown"; // Return "Unknown" if the version is not explicitly handled.
        }
    }

    String isLEExtendedAdvertisingSupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return bluetoothAdapter.isLeExtendedAdvertisingSupported() ? "YES" : "NO";
            }
        }
        return "NO";
    }

    String isOffloadedScanBatchingSupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isOffloadedScanBatchingSupported() ? "YES" : "NO";
        }
        return "NO";
    }

    String isLEPeriodicAdvertisingSupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return bluetoothAdapter.isLePeriodicAdvertisingSupported() ? "YES" : "NO";
            }
        }
        return "NO";
    }

    String isOffloadedFilteringSupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isOffloadedFilteringSupported() ? "YES" : "NO";
        }
        return "NO";
    }

    String isMultipleAdvertisementSupported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            return bluetoothAdapter.isMultipleAdvertisementSupported() ? "YES" : "NO";
        }
        return "NO";
    }

    String isBluetooth4Supported() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter != null) {
            int bluetoothVersion = bluetoothAdapter.getBluetoothLeAdvertiser() != null ? 4 : 0;
            return bluetoothVersion >= 4 ? "YES" : "NO";
        }
        return "NO";
    }

    String isBluetoothLESupported(Context context) {
        PackageManager packageManager = fetchDeviceDetailsHelper.this.context.getApplicationContext().getPackageManager();
        return packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE) ? "YES" : "NO";
    }


    public int getNumberOfNearbyDevices(Context context) {
        int numberOfNearbyDevices = 0;
        NearbyDevicesCounter devicesCounter = new NearbyDevicesCounter(context);
        devicesCounter.startDiscoveryAndCountDevices();
        // Add some delay to allow the discovery process to complete
        try {
            Thread.sleep(5000); // Wait for 5 seconds (adjust this value as needed)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        numberOfNearbyDevices = devicesCounter.getNumberOfNearbyDevices();
        devicesCounter.stopDiscovery();
        return numberOfNearbyDevices;
    }

    public class NearbyDevicesCounter {

        private Context context;
        private BluetoothAdapter bluetoothAdapter;
        private int numberOfNearbyDevices;
        private boolean isDiscovering;

        public NearbyDevicesCounter(Context context) {
            this.context = context;
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }

        public void startDiscoveryAndCountDevices() {
            if (bluetoothAdapter == null) {
                // Device does not support Bluetooth, handle this case as needed.
                return;
            }

            // Register a BroadcastReceiver to listen for discovered devices
            context.registerReceiver(discoveryReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            isDiscovering = true;

            // Start discovery
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            bluetoothAdapter.startDiscovery();
        }

        public int getNumberOfNearbyDevices() {
            return numberOfNearbyDevices;
        }

        public void stopDiscovery() {
            if (isDiscovering) {
                // Stop discovery and unregister the BroadcastReceiver
                bluetoothAdapter.cancelDiscovery();
                context.unregisterReceiver(discoveryReceiver);
                isDiscovering = false;
            }
        }

        // BroadcastReceiver to count the number of discovered devices
        private final BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    numberOfNearbyDevices++;
                }
            }
        };
    }


    //CALLING GPU FUNCTIONS
    void addGPUDetails()
    {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("GPU");

        EGL10 egl = (EGL10) EGLContext.getEGL();
        EGLDisplay display = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);
        int[] version = new int[2];
        egl.eglInitialize(display, version);
        int[] configAttribs = {
                EGL10.EGL_RENDERABLE_TYPE, EGL14.EGL_OPENGL_ES2_BIT,
                EGL10.EGL_SURFACE_TYPE, EGL10.EGL_PBUFFER_BIT,
                EGL10.EGL_NONE
        };
        EGLConfig[] configs = new EGLConfig[1];
        int[] numConfigs = new int[1];
        egl.eglChooseConfig(display, configAttribs, configs, 1, numConfigs);
        if (numConfigs[0] == 0) {

        }
        EGLConfig config = configs[0];
        EGLContext contexts = egl.eglCreateContext(display, config, EGL10.EGL_NO_CONTEXT, null);
        EGLSurface surface = egl.eglCreatePbufferSurface(display, config, new int[]{EGL10.EGL_WIDTH, 1, EGL10.EGL_HEIGHT, 1, EGL10.EGL_NONE});
        egl.eglMakeCurrent(display, surface, surface, contexts);

        String renderer = GLES20.glGetString(GLES20.GL_RENDERER);

        //String versionStr = GLES20.glGetString(GLES20.GL_VERSION);
        String vendor = GLES20.glGetString(GLES20.GL_VENDOR);
        String ext = GLES20.glGetString(GLES20.GL_EXTENSIONS);

        deviceDetailsTypeStructure.addDetail("Vendor", vendor);
        deviceDetailsTypeStructure.addDetail("GPU Name", renderer);
        deviceDetailsTypeStructure.addDetail("OpenGL", getOpenGLDetails());
        deviceDetailsTypeStructure.addDetail("Extensions", ext);
        deviceDetailsTypeStructure.addDetail("Vulkan Support", isVulkanSupported(context) ? "YES" : "NO");

        egl.eglDestroySurface(display, surface);
        egl.eglDestroyContext(display, contexts);
        egl.eglTerminate(display);


        deviceDetails.add(deviceDetailsTypeStructure);
    }

    boolean isVulkanSupported(Context activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            PackageManager packageManager = activity.getPackageManager();
            return packageManager.hasSystemFeature(PackageManager.FEATURE_VULKAN_HARDWARE_LEVEL);
        } else {
            return false;
        }
    }

    public static String getOpenGLDetails() {
        // Check if the device supports OpenGL ES 3.0
        boolean isOpenGLES30Supported = EGL14.eglGetCurrentContext() != null && GLES30.glGetString(GLES30.GL_VERSION) != null;

        StringBuilder detailsBuilder = new StringBuilder();

        // Get OpenGL version
        if (isOpenGLES30Supported) {
            detailsBuilder.append("OpenGL ES 3.0 or above\n");
            detailsBuilder.append("OpenGL Version: ").append(GLES30.glGetString(GLES30.GL_VERSION)).append("\n");
        } else {
            detailsBuilder.append("OpenGL ES 2.0\n");
            detailsBuilder.append("OpenGL Version: ").append(GLES20.glGetString(GLES20.GL_VERSION)).append("\n");
        }

        // Get vendor information
        detailsBuilder.append("Vendor: ").append(GLES20.glGetString(GLES20.GL_VENDOR)).append("\n");

        // Get renderer information
        detailsBuilder.append("Renderer: ").append(GLES20.glGetString(GLES20.GL_RENDERER)).append("\n");

        // Get extensions
        detailsBuilder.append("Extensions: ").append(GLES20.glGetString(GLES20.GL_EXTENSIONS)).append("\n");

        // Get shading language version (only applicable to ES 2.0)
        if (!isOpenGLES30Supported) {
            detailsBuilder.append("Shading Language Version: ").append(GLES20.glGetString(GLES20.GL_SHADING_LANGUAGE_VERSION)).append("\n");
        }

        return detailsBuilder.toString();
    }


    void addAudioDetails()
    {

        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("AUDIO");



        deviceDetailsTypeStructure.addDetail("Low Latency Audio", latency());
        deviceDetailsTypeStructure.addDetail("Pro Audio Support", proSupport());
        deviceDetailsTypeStructure.addDetail("Microphone Available", isMicrophoneAvailable(context.getApplicationContext()));
        deviceDetailsTypeStructure.addDetail("MIDI", isMidiSupported(context.getApplicationContext()));
        deviceDetailsTypeStructure.addDetail("Speaker MODE", isPhoneInSpeakerMode(context.getApplicationContext()) ? "YES" : "NO");
        deviceDetailsTypeStructure.addDetail("Codecs", getSupportedAudioCodecs());
        deviceDetailsTypeStructure.addDetail("Encoder", getSupportedAudioEncoders());
        deviceDetailsTypeStructure.addDetail("Decoder", getSupportedAudioDecoders());

        deviceDetails.add(deviceDetailsTypeStructure);

    }

    boolean isPhoneInSpeakerMode(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

        // Check if the audio focus is currently set to AUDIOFOCUS_GAIN_TRANSIENT or AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK.
        int audioFocus = audioManager.getMode();
        return audioFocus == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT || audioFocus == AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK;
    }


    String latency() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // Low latency audio is supported starting from Android 8.0 (API level 26)
            return "NO";
        }

        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build();

        int bufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);

        AudioTrack audioTrack = new AudioTrack.Builder()
                .setAudioAttributes(audioAttributes)
                .setBufferSizeInBytes(bufferSize)
                .build();

        // Check if the audio track initialization was successful
        if (audioTrack.getState() == AudioTrack.STATE_INITIALIZED) {
            return "YES";
        }

        return "NO";
    }

    String proSupport() {
        PackageManager packageManager = context.getApplicationContext().getPackageManager();

        // Check if the feature is supported on the device
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_AUDIO_PRO)) {
            return "YES";
        }

        return "NO";
    }

    String isMicrophoneAvailable(Context context) {
        PackageManager packageManager = fetchDeviceDetailsHelper.this.context.getApplicationContext().getPackageManager();

        // Check if the feature is supported on the device
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)) {
            return "YES";
        }
        return "NO";
    }

    String isMidiSupported(Context context) {
        PackageManager packageManager = fetchDeviceDetailsHelper.this.context.getApplicationContext().getPackageManager();

        // Check if the feature is supported on the device
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_MIDI)) {
            return "YES";
        }

        return "NO";
    }

    String speaker(Context context) {

        AudioManager audioManager = (AudioManager) fetchDeviceDetailsHelper.this.context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        int mode = audioManager.getMode();

        switch (mode) {
            case AudioManager.MODE_NORMAL:
                return "Normal audio mode";
            case AudioManager.MODE_RINGTONE:
                return "Ringtone audio mode";
            case AudioManager.MODE_IN_CALL:
                return "In-call audio mode";
            case AudioManager.MODE_IN_COMMUNICATION:
                return "In-communication audio mode";
            default:
                return "Speaker mode";
        }

    }


    String getSupportedAudioCodecs() {
        List<String> supportedCodecs = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecList codecList = new MediaCodecList(MediaCodecList.REGULAR_CODECS);

            for (MediaCodecInfo codecInfo : codecList.getCodecInfos()) {
                if (codecInfo.isEncoder()) {
                    continue;
                }

                String[] supportedTypes = codecInfo.getSupportedTypes();

                for (String type : supportedTypes) {
                    if (type.startsWith("audio/")) {
                        supportedCodecs.add(codecInfo.getName());
                        break;
                    }
                }
            }
        }

        return TextUtils.join(", ", supportedCodecs);
    }

    String getSupportedAudioEncoders() {
        return TextUtils.join(", ", getSupportedCodecs(true));
    }

    String getSupportedAudioDecoders() {
        return TextUtils.join(", ", getSupportedCodecs(false));
    }

    private static List<String> getSupportedCodecs(boolean isEncoder) {
        List<String> supportedCodecs = new ArrayList<>();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            MediaCodecList codecList = new MediaCodecList(MediaCodecList.ALL_CODECS);

            for (MediaCodecInfo codecInfo : codecList.getCodecInfos()) {
                if (codecInfo.isEncoder() == isEncoder) {
                    String[] supportedTypes = codecInfo.getSupportedTypes();
                    for (String type : supportedTypes) {
                        if (type.startsWith("audio/")) {
                            supportedCodecs.add(codecInfo.getName());
                            break;
                        }
                    }
                }
            }
        }

        return supportedCodecs;
    }

    //CALLING DISPLAY FUNCTIONS
    void addDisplayDetails()
    {

        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("DISPLAY");
        String hdrAvailiable;
        if (isHdrDisplaySupported())
        {
            hdrAvailiable = "YES";
        }
        else {
            hdrAvailiable = "NO";
        }

        deviceDetailsTypeStructure.addDetail("HDR Support",hdrAvailiable);
        deviceDetailsTypeStructure.addDetail("HDR capabilities",hdrCapibilities());
        deviceDetailsTypeStructure.addDetail("Resolution",resolution());
        deviceDetailsTypeStructure.addDetail("Screen density (dpi)",dpi()+" dpi");
        deviceDetailsTypeStructure.addDetail("Screen density (ppi)",""+getScreenDensityPPI(context)+" ppi");
          deviceDetailsTypeStructure.addDetail("Screen size (Estimated)", screenSize()+" Inches");
        deviceDetailsTypeStructure.addDetail("Aspect ratio",getScreenRatio());
        deviceDetailsTypeStructure.addDetail("Refresh rate",refresh());
        deviceDetailsTypeStructure.addDetail("Wide color gamut",isWideColorGamutSupported(context) ? "YES" : "NO");
        deviceDetails.add(deviceDetailsTypeStructure);

    }


    public String getScreenHeightInInches() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getRealSize(size);

        double widthInches = size.x / getScreenDensityDpi(context);
        double heightInches = size.y / getScreenDensityDpi(context);
        double diagonalInches = Math.sqrt(Math.pow(widthInches, 2) + Math.pow(heightInches, 2));

        return String.format("%.2f inches", diagonalInches);
    }
    private static float getScreenDensityDpi(Context context) {
        return context.getResources().getDisplayMetrics().densityDpi;
    }

    public static String resolution() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }

        int widthPixels = size.x;
        int heightPixels = size.y;

        return widthPixels + " x " + heightPixels;
    }

    boolean isWideColorGamutSupported(Context context) {

        // Check if the device is running Android 8.0 (API level 26) or higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // Get the display of the device
            Display display = getWindowManager(context).getDefaultDisplay();

            // Check if the display supports the Display.P3 (wide color gamut) color space
            if (display != null) {
                ColorSpace colorSpace = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    colorSpace = display.getPreferredWideGamutColorSpace();
                }
                if (colorSpace != null && colorSpace.isWideGamut()) {
                    return true;
                } else {
                    // Wide color gamut is not supported
                    // Handle the situation accordingly
                    return false;
                }
            }
        }

        return false;

    }

    public static String refresh() {
        DisplayManager displayManager = (DisplayManager) context.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        float refreshRate = display.getRefreshRate();
        return refreshRate + " Hz";
    }

//    String ppi() {
//        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
//        float density = displayMetrics.density;
//        int densityDpi = displayMetrics.densityDpi;
//
//        float ppi = densityDpi / density;
//
//        return String.valueOf(ppi);
//    }

    int getScreenDensityPPI(Context activity) {
        WindowManager windowManager = getWindowManager(activity);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        float xInches = metrics.widthPixels / metrics.xdpi;
        float yInches = metrics.heightPixels / metrics.ydpi;
        double diagonalInches = Math.sqrt(xInches * xInches + yInches * yInches);
        int screenWidthPixels = metrics.widthPixels;
        int screenHeightPixels = metrics.heightPixels;
        int ppi = (int) (Math.sqrt(screenWidthPixels * screenWidthPixels + screenHeightPixels * screenHeightPixels) / diagonalInches);
        return ppi;
    }

    public static WindowManager getWindowManager(Context context) {
        return (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }

    public String getScreenRatio() {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;

        // Calculate the screen ratio
        float ratio = (float) screenHeight / screenWidth * 9;

        // Format the output string to one decimal point
        String formattedRatio = String.format("%.1f", ratio);

        return formattedRatio + ":9";
    }
    private int gcd(int a, int b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }
    String screenSize() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(context).getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        double wi = (double) width / (double) dm.xdpi;
        double hi = (double) height / (double) dm.ydpi;
        double x = Math.pow(wi, 2);
        double y = Math.pow(hi, 2);

        double screenSizeInches = Math.sqrt(x + y);
        return String.format("%.2f inches", screenSizeInches);
    }

    String dpi() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        if (windowManager != null) { 
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);

            int densityDpi = displayMetrics.densityDpi;

            return String.valueOf(densityDpi);
        } else {
            return "";
        }
    }

    boolean isHdrDisplaySupported() {
        DisplayManager displayManager = (DisplayManager) context.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);

        if (display != null) {
            Display.HdrCapabilities hdrCapabilities = display.getHdrCapabilities();

            if (hdrCapabilities != null) {
                if (hdrCapabilities.getSupportedHdrTypes().length > 0) {
                    // HDR is supported
                    Log.d("HDR Support", "HDR is supported on this display");
                    return true;
                } else {
                    // HDR is not supported
                    Log.d("HDR Support", "HDR is not supported on this display");
                }
            } else {
                // HDR capabilities not available
                Log.d("HDR Support", "HDR capabilities not available");
            }
        }


        return false;
    }

    String hdrCapibilities() {
        DisplayManager displayManager = (DisplayManager) context.getApplicationContext().getSystemService(Context.DISPLAY_SERVICE);
        Display display = displayManager.getDisplay(Display.DEFAULT_DISPLAY);
        Display.HdrCapabilities hdrCapabilities = display.getHdrCapabilities();
        StringBuilder supportedHdrTypesStringBuilder = new StringBuilder();

        if (hdrCapabilities != null) {
            int[] supportedHdrTypes = hdrCapabilities.getSupportedHdrTypes();

            for (int hdrType : supportedHdrTypes) {
                if (hdrType == Display.HdrCapabilities.HDR_TYPE_DOLBY_VISION) {
                    supportedHdrTypesStringBuilder.append("Dolby Vision, ");
                } else if (hdrType == Display.HdrCapabilities.HDR_TYPE_HDR10) {
                    supportedHdrTypesStringBuilder.append("HDR10, ");
                } else if (hdrType == Display.HdrCapabilities.HDR_TYPE_HDR10_PLUS) {
                    supportedHdrTypesStringBuilder.append("HDR10+, ");
                }
            }

            String supportedHdrTypesString = supportedHdrTypesStringBuilder.toString();
            if (!supportedHdrTypesString.isEmpty()) {
                // Remove trailing comma and space
                supportedHdrTypesString = supportedHdrTypesString.substring(0, supportedHdrTypesString.length() - 2);
            } else {
                supportedHdrTypesString = "NO HDR types supported";
            }
            return supportedHdrTypesString;
        }
        return "NOT SUPPORTED";
    }


    void getFrontCameraInfo() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("Front Camera");

        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraIds[Integer.parseInt(CAMERA_FRONT)]);

            // Retrieve the desired camera details
            float aperture = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)[0];
            float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
            float focalLength = focalLengths[0];
            float cropFactor = HelperFunctions.getSensorCropFactor(context,cameraIds[Integer.parseInt(CAMERA_FRONT)]);
            Range<Long> shutterSpeedRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

            double minShutterSpeedSec = shutterSpeedRange.getLower()/1_000_000_000.0; // Minimum supported shutter speed in seconds
            double maxShutterSpeedSec = shutterSpeedRange.getUpper()/1_000_000_000.0; // Maximum supported shutter speed in seconds


            Range<Integer> isoRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
            boolean isFlashAvailable = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
            boolean isVideoStabilizationSupported = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES).length > 0;
            boolean isAutoExposureLockSupported = characteristics.get(CameraCharacteristics.CONTROL_AE_LOCK_AVAILABLE);
            boolean isWhiteBalanceLockSupported = characteristics.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE);
            int maxFaceCount = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
            boolean isFaceDetectionSupported = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES).length > 0;
            boolean isCamera2ApiSupported = cameraIds.length > 1;

            String capabilities = getCapabilities(characteristics);
            String exposureModes = getExposureModeNames(characteristics);
            String autofocusModes = getAutofocusModes(characteristics);
            String whiteBalanceModes = getWhiteBalanceModes(characteristics);
            String sceneModes = getSceneModes(characteristics);
            String colorEffects = getColorEffects(characteristics);

            float FOV_horizontal =HelperFunctions.calculateHorizontalFOV(context,focalLength,cameraIds[Integer.parseInt(CAMERA_FRONT)]);
            DecimalFormat myFormatter = new DecimalFormat("###.#");
            String FOV_string = myFormatter.format(FOV_horizontal);
//            Math.round((FOV_horizontal*100.0)/100.0)

            // Print the retrieved camera details
            deviceDetailsTypeStructure.addDetail("Aperture: " ,""+"f/"+aperture);
            deviceDetailsTypeStructure.addDetail("Focal Length: " ,""+ focalLength);
            deviceDetailsTypeStructure.addDetail("MegaPixels",getCamerasMegaPixel(characteristics)+"MP");
           // deviceDetailsTypeStructure.addDetail("35mm Equivalent Focal Length: " ,""+ focalLength * cropFactor);
         //   deviceDetailsTypeStructure.addDetail("Crop Factor: " ,""+ cropFactor);
            deviceDetailsTypeStructure.addDetail("Field of View: " ,""+FOV_string+"°"+ "\"Horizontal");
            deviceDetailsTypeStructure.addDetail("Shutter Speed Range (s): " ,""+ HelperFunctions.convertToRatio(minShutterSpeedSec) +" - " +HelperFunctions.convertToRatio(maxShutterSpeedSec));
            deviceDetailsTypeStructure.addDetail("ISO Sensitivity Range: " ,""+ isoRange.toString());
            deviceDetailsTypeStructure.addDetail("Flash Available: " ,isFlashAvailable ? "YES" : "NO");
            deviceDetailsTypeStructure.addDetail("Video Stabilization Supported: " ,isVideoStabilizationSupported ? "YES" : "NO");
            deviceDetailsTypeStructure.addDetail("AE Lock Supported: " ,isAutoExposureLockSupported ? "YES" : "NO");
            deviceDetailsTypeStructure.addDetail("WB Lock Supported: " ,isWhiteBalanceLockSupported ? "YES" : "NO");
            deviceDetailsTypeStructure.addDetail("Capabilities", ""+capabilities);
            deviceDetailsTypeStructure.addDetail("Exposure Modes", ""+exposureModes);
            deviceDetailsTypeStructure.addDetail("Autofocus Modes", ""+autofocusModes);
            deviceDetailsTypeStructure.addDetail("White Balance Modes", ""+whiteBalanceModes);
            deviceDetailsTypeStructure.addDetail("Scene Modes", ""+sceneModes);
            deviceDetailsTypeStructure.addDetail("Color Effects", ""+colorEffects);
            deviceDetailsTypeStructure.addDetail("Max Face Count", ""+maxFaceCount);
            deviceDetailsTypeStructure.addDetail("Max Face Count: " ,""+ maxFaceCount);
            deviceDetailsTypeStructure.addDetail("Face Detection Supported: " ,isFaceDetectionSupported ? "YES" : "NO");
            deviceDetailsTypeStructure.addDetail("Camera2 API Supported: " ,isCamera2ApiSupported ? "YES" : "NO");

            deviceDetails.add(deviceDetailsTypeStructure);
        } catch (CameraAccessException e) {
            throw new RuntimeException(e);
        }
    }





    private void getCameraInfo() {

        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("Rear Camera");

        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = cameraManager.getCameraIdList();
            if (cameraIds.length > 0) {
                String rearCameraId = cameraIds[0]; // Assuming the first camera is the rear camera

                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(rearCameraId);
                int width = 0;
                int height = 0;

                Size rearCameraResolution = getRearCameraResolution(context);
                if (rearCameraResolution != null) {
                    width = rearCameraResolution.getWidth();
                    height = rearCameraResolution.getHeight();
                    // Use the width and height values as needed
                }
                float sensorSize = getSensorSize(characteristics);
                float pixelSize = getPixelSize(characteristics);
                String filterColorArrangement = getFilterColorArrangement(characteristics);
                float aperture = getAperture(characteristics);
                float focalLength = getFocalLength(characteristics);
                float equivalentFocalLength = getEquivalentFocalLength(characteristics, HelperFunctions.getSensorCropFactor(context, cameraIds[0]));

                float fieldOfView = HelperFunctions.calculateHorizontalFOV(context, focalLength, cameraIds[0]);
                DecimalFormat myFormatter = new DecimalFormat("###.#");
                String FOV_string_0 = myFormatter.format(fieldOfView);

                Range<Long> shutterSpeedRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_EXPOSURE_TIME_RANGE);

                double minShutterSpeedSec = 0;
                double maxShutterSpeedSec = 0;
                try {

                    minShutterSpeedSec = shutterSpeedRange.getLower() / 1_000_000_000.0;
                    maxShutterSpeedSec = shutterSpeedRange.getUpper() / 1_000_000_000.0;
                } catch (Exception e) {
                    Log.d(TAG, "getCameraInfo: " + e.getMessage());
                }
                String isoRange = getIsoRange(characteristics);

                boolean hasFlash = hasFlash(characteristics);
                boolean hasVideoStabilization = hasVideoStabilization(characteristics);
                boolean hasAELock = hasAELock(characteristics);
                boolean hasWBLock = hasWBLock(characteristics);
                String capabilities = getCapabilities(characteristics);
                String exposureModes = getExposureModeNames(characteristics);
                String autofocusModes = getAutofocusModes(characteristics);
                String whiteBalanceModes = getWhiteBalanceModes(characteristics);
                String sceneModes = getSceneModes(characteristics);
                String colorEffects = getColorEffects(characteristics);
                int maxFaceCount = getMaxFaceCount(characteristics);
                String faceDetectMode = getFaceDetectCategory(characteristics);
                String getCamera2ApiLevel = getCamera2ApiLevel(characteristics);

                deviceDetailsTypeStructure.addDetail("Megapixels", getCamerasMegaPixel(characteristics) + " MP");
                //deviceDetailsTypeStructure.addDetail("Effective megapixels", getCamerasMegaPixel(context) + " MP");
                //deviceDetailsTypeStructure.addDetail("Supported", context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY) ? "YES" : "NO" );
                deviceDetailsTypeStructure.addDetail("Resolutions", "" + width + "x" + height);
                // deviceDetailsTypeStructure.addDetail("Sensor Size", ""+sensorSize);
                //deviceDetailsTypeStructure.addDetail("Pixel Size", ""+pixelSize);
                deviceDetailsTypeStructure.addDetail("Filter Color Arrangement", "" + filterColorArrangement);
                deviceDetailsTypeStructure.addDetail("Aperture", "" + "f/" + aperture);

                deviceDetailsTypeStructure.addDetail("Focal Length", "" + focalLength);
                //  deviceDetailsTypeStructure.addDetail("35mm Equivalent Focal Length", ""+equivalentFocalLength);
                //  deviceDetailsTypeStructure.addDetail("Crop Factor", ""+HelperFunctions.getSensorCropFactor(context,cameraIds[0]));
                deviceDetailsTypeStructure.addDetail("Field Of View", "" + FOV_string_0 + "°" + "\"Horizontal");
                deviceDetailsTypeStructure.addDetail("Shutter Speed Range", "" + "" + HelperFunctions.convertToRatio(minShutterSpeedSec) + " - " + HelperFunctions.convertToRatio(maxShutterSpeedSec));
                deviceDetailsTypeStructure.addDetail("ISORange", isoRange);

                deviceDetailsTypeStructure.addDetail("Flash", hasFlash ? "YES" : "NO");
                deviceDetailsTypeStructure.addDetail("VideoStabilization", hasVideoStabilization ? "YES" : "NO");
                deviceDetailsTypeStructure.addDetail("AELock", hasAELock ? "YES" : "NO");
                deviceDetailsTypeStructure.addDetail("WBLock", hasWBLock ? "YES" : "NO");
                deviceDetailsTypeStructure.addDetail("Capabilities", "" + capabilities);
                deviceDetailsTypeStructure.addDetail("Exposure Modes", "" + exposureModes);

                deviceDetailsTypeStructure.addDetail("Autofocus Modes", "" + autofocusModes);
                deviceDetailsTypeStructure.addDetail("White Balance Modes", "" + whiteBalanceModes);
                deviceDetailsTypeStructure.addDetail("Scene Modes", "" + sceneModes);
                deviceDetailsTypeStructure.addDetail("Color Effects", "" + colorEffects);
                deviceDetailsTypeStructure.addDetail("Max Face Count", "" + maxFaceCount);
                deviceDetailsTypeStructure.addDetail("Face Detect Mode", "" + faceDetectMode);
                deviceDetailsTypeStructure.addDetail("Camera 2Api ", getCamera2ApiLevel);


                deviceDetails.add(deviceDetailsTypeStructure);

            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }



    public String getCamerasMegaPixel(CameraCharacteristics characteristics) throws CameraAccessException {

        StringBuilder sb = new StringBuilder();

            Size pixelArraySize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);

            if (pixelArraySize != null) {
                int width = pixelArraySize.getWidth();
                int height = pixelArraySize.getHeight();
                double megapixels = (width * height) / 1e6; // Calculate megapixels
                megapixels = Math.ceil(megapixels); // Ceil the value
                sb.append(String.format("%.0f", megapixels));
            }

        return sb.toString();
    }








    public int calculateMegaPixel(float width, float height) {
        return  Math.round((width * height) / 1024000);
    }


    public static Size getRearCameraResolution(Context context) {
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        try {
            String rearCameraId = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                for (String cameraId : cameraManager.getCameraIdList()) {
                    CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                    Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                    if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                        rearCameraId = cameraId;
                        break;
                    }
                }
            }

            if (rearCameraId != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(rearCameraId);
                Size[] sizes = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                        .getOutputSizes(android.graphics.ImageFormat.JPEG);
                if (sizes != null && sizes.length > 0) {
                    // Choose the highest resolution size
                    Size largestSize = sizes[0];
                    for (Size size : sizes) {
                        if (size.getWidth() * size.getHeight() > largestSize.getWidth() * largestSize.getHeight()) {
                            largestSize = size;
                        }
                    }
                    return largestSize;
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    private float getSensorSize(CameraCharacteristics characteristics) {
        SizeF physicalSize = characteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        if (physicalSize != null) {
            float sensorWidth = physicalSize.getWidth();
            float sensorHeight = physicalSize.getHeight();
            float sensorSize = (float) Math.sqrt(sensorWidth * sensorWidth + sensorHeight * sensorHeight);
            return sensorSize;
        }
        return 0f;
    }

    private float getPixelSize(CameraCharacteristics characteristics) {
        Size pixelSizeMicrons = characteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        if (pixelSizeMicrons != null) {
            float sensorWidth = pixelSizeMicrons.getWidth();
            float sensorHeight = pixelSizeMicrons.getHeight();
            float pixelSize = (sensorWidth * sensorHeight) / (getResolutionWidth(characteristics) * getResolutionHeight(characteristics));
            return pixelSize;
        }
        return 0f;
    }

    private int getResolutionWidth(CameraCharacteristics characteristics) {
        StreamConfigurationMap configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (configMap != null) {
            Size[] sizes = configMap.getOutputSizes(android.graphics.ImageFormat.JPEG);
            if (sizes != null && sizes.length > 0) {
                return sizes[0].getWidth();
            }
        }
        return 0;
    }

    private int getResolutionHeight(CameraCharacteristics characteristics) {
        StreamConfigurationMap configMap = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
        if (configMap != null) {
            Size[] sizes = configMap.getOutputSizes(android.graphics.ImageFormat.JPEG);
            if (sizes != null && sizes.length > 0) {
                return sizes[0].getHeight();
            }
        }
        return 0;
    }


    private String getFilterColorArrangement(CameraCharacteristics characteristics) {
        Integer filterArrangement = characteristics.get(CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT);
        if (filterArrangement != null) {
            switch (filterArrangement) {
                case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_RGGB:
                    return "RGGB";
                case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GRBG:
                    return "GRBG";
                case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_GBRG:
                    return "GBRG";
                case CameraCharacteristics.SENSOR_INFO_COLOR_FILTER_ARRANGEMENT_BGGR:
                    return "BGGR";
            }
        }
        return "Unknown";
    }

    private float getAperture(CameraCharacteristics characteristics) {
        float[] apertureRange = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES);
        if (apertureRange != null && apertureRange.length > 0) {
            return apertureRange[0];
        }
        return 0f;
    }

    private float getFocalLength(CameraCharacteristics characteristics) {
        float[] focalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        if (focalLengths != null && focalLengths.length > 0) {
            return focalLengths[0];
        }
        return 0f;
    }

    private float getEquivalentFocalLength(CameraCharacteristics characteristics,float cropFactor)  {
        float[] equivalentFocalLengths = characteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        if (equivalentFocalLengths != null && equivalentFocalLengths.length > 0) {
            return equivalentFocalLengths[0] * cropFactor;
        }
        return 0f;
    }

    private float getCropFactor(CameraCharacteristics characteristics) {
        float sensorWidth = getSensorSize(characteristics);
        float cropFactor = sensorWidth / 35f; // Assuming 35mm is the full-frame equivalent
        return cropFactor;
    }

    private float getFieldOfView(CameraCharacteristics characteristics) {
        float focalLength = getFocalLength(characteristics);
        float cropFactor = getCropFactor(characteristics);
        float fieldOfView = (float) Math.toDegrees(2 * Math.atan(8.8f / (2 * focalLength * cropFactor)));
        return fieldOfView;
    }


    String getIsoRange(CameraCharacteristics characteristics) {
        Range<Integer> isoRange = characteristics.get(CameraCharacteristics.SENSOR_INFO_SENSITIVITY_RANGE);
        if (isoRange != null) {
            int minIso = isoRange.getLower();
            int maxIso = isoRange.getUpper();
            return ""+minIso+"-"+maxIso;
        }
        return "";
    }

    private boolean hasFlash(CameraCharacteristics characteristics) {
        Boolean hasFlash = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
        return hasFlash != null && hasFlash;
    }

    private boolean hasVideoStabilization(CameraCharacteristics characteristics) {
        int[] videoStabilizationModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES);
        if (videoStabilizationModes != null) {
            for (int mode : videoStabilizationModes) {
                if (mode == CameraCharacteristics.CONTROL_VIDEO_STABILIZATION_MODE_ON) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean hasAELock(CameraCharacteristics characteristics) {
        Boolean hasAELock = characteristics.get(CameraCharacteristics.CONTROL_AE_LOCK_AVAILABLE);
        return hasAELock != null && hasAELock;
    }

    private boolean hasWBLock(CameraCharacteristics characteristics) {
        Boolean hasWBLock = characteristics.get(CameraCharacteristics.CONTROL_AWB_LOCK_AVAILABLE);
        return hasWBLock != null && hasWBLock;
    }

    private String getCapabilities(CameraCharacteristics characteristics) {
        int[] capabilities = characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES);
        StringBuilder capabilityBuilder = new StringBuilder();
        if (capabilities != null) {
            for (int capability : capabilities) {
                capabilityBuilder.append(capabilityToString(capability)).append(", ");
            }
        }
        return capabilityBuilder.toString().trim().replaceAll(",$", "");
    }

    private String capabilityToString(int capability) {
        switch (capability) {
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE:
                return "Backward Compatible";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_SENSOR:
                return "Manual Sensor";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MANUAL_POST_PROCESSING:
                return "Manual Post Processing";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_RAW:
                return "Raw";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_PRIVATE_REPROCESSING:
                return "Private Reprocessing";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_READ_SENSOR_SETTINGS:
                return "Read Sensor Settings";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BURST_CAPTURE:
                return "Burst Capture";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_YUV_REPROCESSING:
                return "YUV Reprocessing";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_DEPTH_OUTPUT:
                return "Depth Output";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_CONSTRAINED_HIGH_SPEED_VIDEO:
                return "Constrained High-Speed Video";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MOTION_TRACKING:
                return "Motion Tracking";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_LOGICAL_MULTI_CAMERA:
                return "Logical Multi-Camera";
            case CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_MONOCHROME:
                return "Monochrome";
        }
        return "Unknown";
    }

    private String getExposureModeNames(CameraCharacteristics characteristics) {
        int[] exposureModes = characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES);
        // Retrieve the available exposure modes from the CameraCharacteristics object

        if (exposureModes != null && exposureModes.length > 0) {
            String[] modeNames = new String[exposureModes.length];
            for (int i = 0; i < exposureModes.length; i++) {
                switch (exposureModes[i]) {
                    case CameraMetadata.CONTROL_AE_MODE_OFF:
                        modeNames[i] = "Manual";
                        break;
                    case CameraMetadata.CONTROL_AE_MODE_ON:
                        modeNames[i] = "Auto";
                        break;
                    case CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH:
                        modeNames[i] = "Flash";
                        break;
                    case CameraMetadata.CONTROL_AE_MODE_ON_EXTERNAL_FLASH:
                        modeNames[i] = "EXTERNAL Flash";
                        break;
                    case CameraMetadata.CONTROL_AE_MODE_ON_AUTO_FLASH_REDEYE:
                        modeNames[i] = "Auto Flash REDEYE";
                        break;
                    default:
                        modeNames[i] = "Unknown"; // Fallback for unrecognized exposure modes
                        break;
                }
            }
            return TextUtils.join(", ", modeNames);
        }

        return ""; // If exposureModes is null or empty, return an empty string
    }

    String getAutofocusModes(CameraCharacteristics characteristics) {
        int[] exposureModes = characteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES);
        // Retrieve the available exposure modes from the CameraCharacteristics object

        if (exposureModes != null && exposureModes.length > 0) {
            String[] modeNames = new String[exposureModes.length];
            for (int i = 0; i < exposureModes.length; i++) {
                switch (exposureModes[i]) {
                    case CameraMetadata.CONTROL_AF_MODE_OFF:
                        modeNames[i] = "Manual";
                        break;
                    case CameraMetadata.CONTROL_AF_MODE_AUTO:
                        modeNames[i] = "Auto";
                        break;
                    case CameraMetadata.CONTROL_AF_MODE_MACRO:
                        modeNames[i] = "MACRO";
                        break;
                    case CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_VIDEO:
                        modeNames[i] = "CONTINUOUS  VIDEO";
                        break;
                    case CameraMetadata.CONTROL_AF_MODE_CONTINUOUS_PICTURE:
                        modeNames[i] = "CONTINUOUS PICTURE";
                        break;
                    case CameraMetadata.CONTROL_AF_MODE_EDOF:
                        modeNames[i] = "EDOF";
                        break;
                    // Add more cases for other exposure mode values if needed
                    default:

                        break;
                }
            }
            return TextUtils.join(", ", modeNames);
        }

        return "";

    }

    String getWhiteBalanceModes(CameraCharacteristics characteristics) {
        int[] exposureModes = characteristics.get(CameraCharacteristics.CONTROL_AWB_AVAILABLE_MODES);
        // Retrieve the available exposure modes from the CameraCharacteristics object

        if (exposureModes != null && exposureModes.length > 0) {
            String[] modeNames = new String[exposureModes.length];
            for (int i = 0; i < exposureModes.length; i++) {
                switch (exposureModes[i]) {
                    case CameraMetadata.CONTROL_AWB_MODE_AUTO:
                        modeNames[i] = "AUTO";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_DAYLIGHT:
                        modeNames[i] = "DAYLIGHT";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_CLOUDY_DAYLIGHT:
                        modeNames[i] = "CLOUDY DAYLIGHT";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_SHADE:
                        modeNames[i] = "HADE";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_FLUORESCENT:
                        modeNames[i] = "FLUORESCENT";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_INCANDESCENT:
                        modeNames[i] = "INCANDESCENT";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_TWILIGHT:
                        modeNames[i] = "TWILIGHT";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_WARM_FLUORESCENT:
                        modeNames[i] = "WARM FLUORESCENT";
                        break;
                    case CameraMetadata.CONTROL_AWB_MODE_OFF:
                        modeNames[i] = "Manual";
                        break;
                    // Add more cases for other exposure mode values if needed
                    default:

                        break;
                }
            }
            return TextUtils.join(", ", modeNames);
        }

        return "";
    }

    String getSceneModes(CameraCharacteristics characteristics) {
        int[] exposureModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
        // Retrieve the available exposure modes from the CameraCharacteristics object

        if (exposureModes != null && exposureModes.length > 0) {
            String[] modeNames = new String[exposureModes.length];
            for (int i = 0; i < exposureModes.length; i++) {
                switch (exposureModes[i]) {
                    case CameraMetadata.CONTROL_SCENE_MODE_ACTION:
                        modeNames[i] = "ACTION";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_BARCODE:
                        modeNames[i] = "BARCODE";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_CANDLELIGHT:
                        modeNames[i] = "CANDLELIGHT";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_DISABLED:
                        modeNames[i] = "DISABLED";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_THEATRE:
                        modeNames[i] = "THEATRE";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_BEACH:
                        modeNames[i] = "BEACH";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_FACE_PRIORITY:
                        modeNames[i] = "FACE_PRIORITY";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_FIREWORKS:
                        modeNames[i] = "FIREWORKS";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_HDR:
                        modeNames[i] = "HDR";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_NIGHT:
                        modeNames[i] = "NIGHT";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_NIGHT_PORTRAIT:
                        modeNames[i] = "NIGHT PORTRAIT";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_LANDSCAPE:
                        modeNames[i] = "LANDSCAPE";
                        break;

                    case CameraMetadata.CONTROL_SCENE_MODE_SUNSET:
                        modeNames[i] = "SUNSET";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_STEADYPHOTO:
                        modeNames[i] = "STEADYPHOTO";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_SPORTS:
                        modeNames[i] = "SPORTS";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_SNOW:
                        modeNames[i] = "SNOW";
                        break;
                    case CameraMetadata.CONTROL_SCENE_MODE_PORTRAIT:
                        modeNames[i] = "PORTRAIT";
                        break;
                    default:
                        modeNames[i] = "UNKNOWN"; // Provide a fallback value for unrecognized modes
                        break;
                }
            }
            return TextUtils.join(", ", modeNames);
        }
        return "";
    }
    String getColorEffects(CameraCharacteristics characteristics) {
        int[] exposureModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        boolean colorEffectsEnabled = getColorEffectsEnabled(characteristics);
            if (exposureModes != null && exposureModes.length > 0) {
                String[] modeNames = new String[exposureModes.length];
                for (int i = 0; i < exposureModes.length; i++) {
                    switch (exposureModes[i]) {
                        case CameraMetadata.CONTROL_EFFECT_MODE_AQUA:
                            modeNames[i] = "AQUA";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_BLACKBOARD:
                            modeNames[i] = "BLACKBOARD";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_MONO:
                            modeNames[i] = "MONO";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_OFF:
                            modeNames[i] = "OFF";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_NEGATIVE:
                            modeNames[i] = "NEGATIVE";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_POSTERIZE:
                            modeNames[i] = "POSTERIZE";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_SEPIA:
                            modeNames[i] = "SEPIA";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_SOLARIZE:
                            modeNames[i] = "SOLARIZE";
                            break;
                        case CameraMetadata.CONTROL_EFFECT_MODE_WHITEBOARD:
                            modeNames[i] = "WHITEBOARD";
                            break;
                        default:
                            break;
                    }
                }
                return TextUtils.join(", ", modeNames);
            }
            return "";
    }

    private boolean getColorEffectsEnabled(CameraCharacteristics characteristics) {
        int[] colorEffectModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_EFFECTS);
        if (colorEffectModes != null) {
            for (int mode : colorEffectModes) {
                if (mode == CameraMetadata.CONTROL_EFFECT_MODE_OFF) {
                    return false; // Color effects are disabled
                }
            }
        }
        return true; // Color effects are enabled
    }

    private int getMaxFaceCount(CameraCharacteristics characteristics) {
        Integer maxFaceCount = characteristics.get(CameraCharacteristics.STATISTICS_INFO_MAX_FACE_COUNT);
        return maxFaceCount != null ? maxFaceCount : 0;
    }

    private String getFaceDetectCategory(CameraCharacteristics characteristics) {
        int[] faceDetectModes = characteristics.get(CameraCharacteristics.STATISTICS_INFO_AVAILABLE_FACE_DETECT_MODES);

        if (faceDetectModes != null) {
            for (int mode : faceDetectModes) {
                if (mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_SIMPLE) {
                    return "SIMPLE";
                } else if (mode == CameraCharacteristics.STATISTICS_FACE_DETECT_MODE_FULL) {
                    return "FULL";
                }
            }
        }

        return "NOT_SUPPORTED"; // Face detection not supported or unknown category
    }



    private String getCamera2ApiLevel(CameraCharacteristics characteristics) {
        int deviceLevel = characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL);

        if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_LIMITED) {
            return "LEVEL "+2; // Camera2 API level 2
        } else if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3) {
            return "LEVEL "+3; // Camera2 API level 3
        } else if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_EXTERNAL) {
            return "LEVEL "+4; // Camera2 API level "EXTERNAL"
        } else if (deviceLevel == CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_FULL) {
            return "FULL";
        } else {
            return "NOT SUPPORTED"; // Camera2 API not supported or unknown level
        }
    }

    void addVideoCaptureDetails() {
        deviceDetailsTypeStructure = new deviceDetailsTypeStructure();
        deviceDetailsTypeStructure.setCategory("Video Capture");

                 String cameraId = "0";
        VideoCaptureProfilesInfo profilesInfo = new VideoCaptureProfilesInfo(context);
        Map<String, String> videoCaptureInfo = profilesInfo.getVideoCaptureProfilesInfo(cameraId);


        String outputSizes = videoCaptureInfo.get("OutputSizes");
        String outputFrameRates = videoCaptureInfo.get("OutputFrameRates");
        String isHighSpeedVideoSupported = videoCaptureInfo.get("IsHighSpeedVideoSupported");
        String isVideoStabilizationSupported = videoCaptureInfo.get("IsVideoStabilizationSupported");
        String isHdr10Supported = videoCaptureInfo.get("IsHdr10Supported");
        String videoProfiles = videoCaptureInfo.get("VideoProfiles");

        Log.d("VideoCaptureInfo", "Output Sizes: " + outputSizes);
        Log.d("VideoCaptureInfo", "Output Frame Rates: " + outputFrameRates);
        Log.d("VideoCaptureInfo", "High-Speed Video Supported: " + isHighSpeedVideoSupported);
        Log.d("VideoCaptureInfo", "Video Stabilization Supported: " + isVideoStabilizationSupported);
        Log.d("VideoCaptureInfo", "HDR10 Supported: " + isHdr10Supported);
        Log.d("VideoCaptureInfo", "Video Profiles: " + videoProfiles);

        deviceDetailsTypeStructure.addDetail("Output Sizes", outputSizes);
        //deviceDetailsTypeStructure.addDetail("Output Frame Rates:", outputFrameRates);
      //  deviceDetailsTypeStructure.addDetail("Profiles", videoProfiles);
     //   deviceDetailsTypeStructure.addDetail("Max Frame Rate", videoCaptureInfo.get("Max Frame Rate"));
        deviceDetailsTypeStructure.addDetail("Video stabilization", Objects.equals(isVideoStabilizationSupported, "true") ? "YES": "NO");
        deviceDetailsTypeStructure.addDetail("High speed video", Objects.equals(isHighSpeedVideoSupported, "true") ? "YES": "NO");
        deviceDetailsTypeStructure.addDetail("Hdr10 Supported", Objects.equals(isHdr10Supported, "true") ? "YES": "NO");
      // deviceDetailsTypeStructure.addDetail("Max frame rate", ""+getMaxVideoCaptureFrameRate(context)+" Hz");

        deviceDetails.add(deviceDetailsTypeStructure);
    }

    public class VideoCaptureProfilesInfo {

        private Context context;

        public VideoCaptureProfilesInfo(Context context) {
            this.context = context;
        }

        public Map<String, String> getVideoCaptureProfilesInfo(String cameraId) {
            Map<String, String> videoCaptureInfo = new HashMap<>();

            CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

            try {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                StreamConfigurationMap configs = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);

                // Get all available video sizes and frame rates
                Size[] outputSizes = configs.getOutputSizes(MediaRecorder.class);
                Range<Integer>[] outputFrameRates = configs.getHighSpeedVideoFpsRanges();

                // Check if high-speed video is supported
                boolean isHighSpeedVideoSupported = outputFrameRates != null && outputFrameRates.length > 0;

                // Check if video stabilization is supported
                boolean isVideoStabilizationSupported = characteristics.get(
                        CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES) != null;

                // Check if HDR10 is supported
                boolean isHdr10Supported = false;
                int[] sceneModes = characteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_SCENE_MODES);
                for (int sceneMode : sceneModes) {
                    if (sceneMode == CameraCharacteristics.CONTROL_SCENE_MODE_HDR) {
                        isHdr10Supported = true;
                        break;
                    }
                }

                // Convert data to strings and store it in the map
                videoCaptureInfo.put("OutputSizes", convertToString(outputSizes));
                videoCaptureInfo.put("OutputFrameRates", convertToString(outputFrameRates));
                videoCaptureInfo.put("IsHighSpeedVideoSupported", String.valueOf(isHighSpeedVideoSupported));
                videoCaptureInfo.put("IsVideoStabilizationSupported", String.valueOf(isVideoStabilizationSupported));
                videoCaptureInfo.put("IsHdr10Supported", String.valueOf(isHdr10Supported));

              try{
                  // Get video capture profiles with example values
                  List<VideoProfile> videoProfiles = new ArrayList<>();
                  videoProfiles.add(new VideoProfile("720p", outputSizes[0], outputFrameRates[0], outputFrameRates[1], outputFrameRates[2]));
                  videoProfiles.add(new VideoProfile("1080p", outputSizes[1], outputFrameRates[0], outputFrameRates[1], outputFrameRates[2]));
                  videoProfiles.add(new VideoProfile("4K", outputSizes[2], outputFrameRates[0]));
                  videoCaptureInfo.put("Max Frame Rate", String.valueOf(videoProfiles.get(0).maxFrameRate3)+"Mhz");
                  videoCaptureInfo.put("VideoProfiles", convertVideoProfilesToString(videoProfiles));
              }
              catch (Exception e)
              {
                  Log.e(TAG,e.getMessage());
              }



            } catch (CameraAccessException e) {
                e.printStackTrace();
            }

            return videoCaptureInfo;
        }

        // Custom class to store video profile details
        private  class VideoProfile {
            String resolution;
            Size size;
            int maxFrameRate1;
            int maxFrameRate2;
            int maxFrameRate3;

            public VideoProfile(String resolution, Size size, Range<Integer> frameRate1, Range<Integer> frameRate2, Range<Integer> frameRate3) {
                this.resolution = resolution;
                this.size = size;
                this.maxFrameRate1 = frameRate1.getUpper();
                this.maxFrameRate2 = frameRate2.getUpper();
                this.maxFrameRate3 = frameRate3.getUpper();
            }

            public VideoProfile(String resolution, Size size, Range<Integer> frameRate) {
                this.resolution = resolution;
                this.size = size;
                this.maxFrameRate1 = frameRate.getUpper();
            }

            // Methods to get max frame rates in Hz format
            public String getMaxFrameRate1Hz() {
                return maxFrameRate1 + " Hz";
            }

            public String getMaxFrameRate2Hz() {
                return maxFrameRate2 + " Hz";
            }

            public String getMaxFrameRate3Hz() {
                return maxFrameRate3 + " Hz";
            }
        }

        // Convert Size array to a string
        private String convertToString(Size[] sizes) {
            StringBuilder builder = new StringBuilder();
            for (Size size : sizes) {
                builder.append(size.toString()).append(", ");
            }
            return builder.toString();
        }

        // Convert Range array to a string
        private String convertToString(Range<Integer>[] ranges) {
            StringBuilder builder = new StringBuilder();
            for (Range<Integer> range : ranges) {
                builder.append(range.toString()).append(", ");
            }
            return builder.toString();
        }

        // Convert VideoProfile list to a string
        private String convertVideoProfilesToString(List<VideoProfile> videoProfiles) {
            StringBuilder builder = new StringBuilder();
            for (VideoProfile profile : videoProfiles) {
                builder.append(profile.resolution).append(": ")
                        .append("").append(profile.getMaxFrameRate1Hz()).append(", ")
                        .append("").append(profile.getMaxFrameRate2Hz()).append(", ")
                        .append("").append(profile.getMaxFrameRate3Hz()).append("\n");
            }
            return builder.toString();
        }

    }}




