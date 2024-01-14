package com.oruphones.oruphones_deviceinfo.DeviceInfoTabs;

import android.annotation.SuppressLint;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.opengl.EGL14;
import android.opengl.GLES20;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.oruphones.oruphones_deviceinfo.CustomViewUi.CPUCircularView;
import com.oruphones.oruphones_deviceinfo.R;
import com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;

public class DashBoard extends Fragment {

    private CPUCircularView cpuCircularView;
    private TextView cpuPercentage ,cpuFreqTextView,uptime,deepsleep,deviceOs,processor , device_name;
    LinearLayout leftContainer,rightContainer;
    private Handler handler;
    Runnable updatorinflateDeviceInfo,updatorinflateBatteryData,updatorinflateNetworkData,updatorinflateApps,updatorinflateDisplayData,updatorinflateRamData,updatorinflateStorageData;
    private Runnable cpuUpdateRunnable;
    private static long screenOffTimeMillis = -1L;
    com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper fetchDeviceDetailsHelper;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    View vi;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fetchDeviceDetailsHelper = new fetchDeviceDetailsHelper(getContext());
        View rootView = inflater.inflate(R.layout.fragment_dash_board, container, false);

        vi = rootView;

        initUi(rootView);

        return rootView;
    }

    void initUi(View rootView)
    {
        cpuCircularView = rootView.findViewById(R.id.cpuCircularView);
        cpuPercentage = rootView.findViewById(R.id.cpu_percentage);
        leftContainer = rootView.findViewById(R.id.leftDashBoard);
        cpuFreqTextView = rootView.findViewById(R.id.cpuusage);
        rightContainer=rootView.findViewById(R.id.rightDashBoard);
        uptime = rootView.findViewById(R.id.uptime_device);
        deepsleep = rootView.findViewById(R.id.sleep_device);
        deviceOs = rootView.findViewById(R.id.operating_system_v);
        device_name = rootView.findViewById(R.id.device_name);
        processor = rootView.findViewById(R.id.processor_name);
        handler = new Handler(Looper.getMainLooper());

        startCpuFluctuation();
        fetchDeviceDetailsHelper.getBatteryExtraInfo();
        inflateBatteryData();
        inflateNetworkData();
        inflateDisplayData();
        inflateRamData();
        inflateStorageData();
        inflateApps();
        inflateDeviceInfo();
    }


    private void startCpuFluctuation() {

        fetchDeviceDetailsHelper.getLiveCpuFrequencies();
        cpuUpdateRunnable = new Runnable() {
            @Override
            public void run() {

                List<Integer> cpuFrequencies = fetchDeviceDetailsHelper.getLiveCpuFrequencies();
                int maxFrequency = 0;
                int totalFrequency = 0;
                // Find the maximum frequency and total frequency from the cpuFrequencies list
                for (int frequency : cpuFrequencies) {
                    if (frequency > maxFrequency) {
                        maxFrequency = frequency;
                    }
                    totalFrequency += frequency;
                }

                int overallPercentage = (int) ((totalFrequency / (float) (cpuFrequencies.size() * maxFrequency)) * 100);
                StringBuilder cpuFreqStringBuilder = new StringBuilder();
                for (int i = 0; i < cpuFrequencies.size(); i++) {
                    int frequency = cpuFrequencies.get(i);
                    int percentage = (int) ((frequency / (float) maxFrequency) * 100);
                    cpuFreqStringBuilder.append(percentage).append("% ").append(frequency).append(" MHz\n");
                }

                cpuFreqTextView.setText(cpuFreqStringBuilder.toString());
                cpuPercentage.setText(overallPercentage +"%");
                cpuCircularView.setCpuUsagePercentage(overallPercentage);
                handler.postDelayed(this, 500);
            }
        };
        handler.post(cpuUpdateRunnable);
    }



    @SuppressLint("SetTextI18n")
    void inflateDeviceInfo()
    {
        device_name.setText(Build.BRAND.toUpperCase()+" "+ Build.MODEL);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            processor.setText(com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper.getProcessorName());
        }
        deviceOs.setText(com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper.getAndroidVersionName());
         updatorinflateDeviceInfo = new Runnable() {
            @Override
            public void run() {

                uptime.setText("Uptime: "+ com.oruphones.oruphones_deviceinfo.fetchDeviceDetailsHelper.getDeviceUptimeFormatted());
//                deepsleep.setText("DeepSleep:"+formatMillis(getDeepSleepTimeMillis()));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updatorinflateDeviceInfo);
    }



    void inflateBatteryData() {

        View categoryHolder = getLayoutInflater().inflate(R.layout.dashboard_cards, null);
        TextView categoryHeading = categoryHolder.findViewById(R.id.device_info_category_name);
        TextView uppervalue = categoryHolder.findViewById(R.id.deviceinfo_uppervalue);
        TextView lowervalue = categoryHolder.findViewById(R.id.deviceinfo_lowervalue);
        categoryHeading.setText("BATTERY");

         updatorinflateBatteryData = new Runnable() {
            @Override
            public void run() {

                HashMap<String,String> batterydata =  fetchDeviceDetailsHelper.getBatterydata();
                uppervalue.setText(batterydata.get("LEVEL")+" "+batterydata.get("Temperature"));
                lowervalue.setText("Charging " +batterydata.get("Plugged"));
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updatorinflateBatteryData);
        leftContainer.addView(categoryHolder);

    };

    void inflateNetworkData()
    {
        View categoryHolder = getLayoutInflater().inflate(R.layout.dashboard_cards, null);
        TextView categoryHeading = categoryHolder.findViewById(R.id.device_info_category_name);
        TextView uppervalue = categoryHolder.findViewById(R.id.deviceinfo_uppervalue);
        TextView lowervalue = categoryHolder.findViewById(R.id.deviceinfo_lowervalue);
        ImageView drawable = categoryHolder.findViewById(R.id.category_drawable);
        categoryHeading.setText("NETWORK");
        drawable.setImageResource(R.drawable.network_cellular_signal);
         updatorinflateNetworkData = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                HashMap<String,String> data =  fetchDeviceDetailsHelper.getMobileConnectionDetails();
                int randomPercentage = new Random().nextInt(100) + 1;
                uppervalue.setText(data.get("OPERATOR_NAME"));
                lowervalue.setText(data.get("Level")+"%" +" "+data.get("dBm")+"dBm");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updatorinflateNetworkData);
        leftContainer.addView(categoryHolder);
    }

    void inflateApps()
    {
        View categoryHolder = getLayoutInflater().inflate(R.layout.dashboard_cards, null);
        TextView categoryHeading = categoryHolder.findViewById(R.id.device_info_category_name);
        TextView uppervalue = categoryHolder.findViewById(R.id.deviceinfo_uppervalue);
        TextView lowervalue = categoryHolder.findViewById(R.id.deviceinfo_lowervalue);
        ImageView drawable = categoryHolder.findViewById(R.id.category_drawable);
        drawable.setImageResource(R.drawable.apps2_device);
        categoryHeading.setText("APPS");
         updatorinflateApps = new Runnable() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void run() {
                uppervalue.setText(""+getTotalInstalledApps());
                lowervalue.setText(String.format("%.2f", getTotalInstalledAppsSizeInGB()) + "GB");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updatorinflateApps);
        rightContainer.addView(categoryHolder);
    }

    private int getTotalInstalledApps() {
        PackageManager packageManager = vi.getContext().getPackageManager();
        return packageManager.getInstalledApplications(0).size();
    }

    private double getTotalInstalledAppsSizeInGB() {
        long totalSize = 0;
        PackageManager packageManager = vi.getContext().getPackageManager();
        for (ApplicationInfo applicationInfo : packageManager.getInstalledApplications(0)) {
            totalSize += new java.io.File(applicationInfo.sourceDir).length();
        }
        // Convert bytes to gigabytes (GB)
        double totalSizeInGB = (double) totalSize / (1024 * 1024 * 1024);
        return totalSizeInGB;
    }

    void inflateDisplayData()
    {
        View categoryHolder = getLayoutInflater().inflate(R.layout.dashboard_cards, null);
        TextView categoryHeading = categoryHolder.findViewById(R.id.device_info_category_name);
        TextView uppervalue = categoryHolder.findViewById(R.id.deviceinfo_uppervalue);
        TextView lowervalue = categoryHolder.findViewById(R.id.deviceinfo_lowervalue);
        ImageView drawable = categoryHolder.findViewById(R.id.category_drawable);
        drawable.setImageResource(R.drawable.display_device);
        categoryHeading.setText("Display");
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

                uppervalue.setText(renderer);

                egl.eglDestroySurface(display, surface);
                egl.eglDestroyContext(display, contexts);
                egl.eglTerminate(display);
                lowervalue.setText(fetchDeviceDetailsHelper.resolution()+" "+fetchDeviceDetailsHelper.refresh().substring(0,2));

        rightContainer.addView(categoryHolder);
    }

    void inflateRamData()
    {
        View categoryHolder = getLayoutInflater().inflate(R.layout.dashboard_cards, null);
        TextView categoryHeading = categoryHolder.findViewById(R.id.device_info_category_name);
        TextView uppervalue = categoryHolder.findViewById(R.id.deviceinfo_uppervalue);
        TextView lowervalue = categoryHolder.findViewById(R.id.deviceinfo_lowervalue);
        ImageView drawable = categoryHolder.findViewById(R.id.category_drawable);
        drawable.setImageResource(R.drawable.ram_device);
        categoryHeading.setText("RAM");
         updatorinflateRamData = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {
                uppervalue.setText(fetchDeviceDetailsHelper.getUsedMemory(getContext())+" USED");
                lowervalue.setText(fetchDeviceDetailsHelper.getTotalRAM(getContext())+" TOTAL");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updatorinflateRamData);
        rightContainer.addView(categoryHolder);
    }

    void inflateStorageData()
    {
        View categoryHolder = getLayoutInflater().inflate(R.layout.dashboard_cards, null);
        TextView categoryHeading = categoryHolder.findViewById(R.id.device_info_category_name);
        TextView uppervalue = categoryHolder.findViewById(R.id.deviceinfo_uppervalue);
        TextView lowervalue = categoryHolder.findViewById(R.id.deviceinfo_lowervalue);
        ImageView drawable = categoryHolder.findViewById(R.id.category_drawable);
        drawable.setImageResource(R.drawable.storage_circle);
        categoryHeading.setText("STORAGE");
         updatorinflateStorageData = new Runnable() {
            @SuppressLint("SetTextI18n")
            @Override
            public void run() {

                HashMap<String,String> data =  fetchDeviceDetailsHelper.getStorageInfo();
                uppervalue.setText(data.get("Storage Used") +" Used");
                lowervalue.setText(data.get("Total Storage")+" Total ");
                handler.postDelayed(this, 1000);
            }
        };
        handler.post(updatorinflateStorageData);
        leftContainer.addView(categoryHolder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (handler != null) {
            handler.removeCallbacks(cpuUpdateRunnable);
            handler.removeCallbacks(updatorinflateDeviceInfo);
            handler.removeCallbacks(updatorinflateBatteryData);
            handler.removeCallbacks(updatorinflateNetworkData);
            handler.removeCallbacks(updatorinflateApps);
            handler.removeCallbacks(updatorinflateDisplayData);
            handler.removeCallbacks(updatorinflateRamData);
            handler.removeCallbacks(updatorinflateStorageData);
        }
    }
}

