package com.oruphones.oruphones_deviceinfo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppActivity extends AppCompatActivity {

    private List<ApplicationInfo> installedApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        installedApps = getInstalledApps();

        ListView listView = findViewById(R.id.app_list);

        List<String> appNames = getInstalledAppNames();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, appNames);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((adapterView, view, position, id) -> {
            ApplicationInfo app = installedApps.get(position);
            showAppInfoDialog(app);
        });

    }

    private List<ApplicationInfo> getInstalledApps() {
        PackageManager packageManager = getPackageManager();
        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
    }

    private List<String> getInstalledAppNames() {
        PackageManager packageManager = getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        List<String> appNames = new ArrayList<>();
        for (ApplicationInfo app : installedApps) {
            appNames.add(app.loadLabel(packageManager).toString());
        }
        return appNames;
    }
    private void showAppInfoDialog(ApplicationInfo app) {
        PackageManager packageManager =  getPackageManager();

        StringBuilder appInfo = new StringBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appInfo.append("Category: ").append(getCategoryName(app.category)).append("\n");

        }
        appInfo.append("Package name: ").append(app.packageName).append("\n");
        try {
            appInfo.append("Version: ").append(packageManager.getPackageInfo(app.packageName, 0).versionName).append("\n");
        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException(e);
        }
        appInfo.append("Target SDK: ").append(app.targetSdkVersion).append("\n");
        appInfo.append("Minimum SDK: ").append(app.minSdkVersion).append("\n");
        appInfo.append("Installer type: ").append(packageManager.getInstallerPackageName(app.packageName)).append("\n");
        try {
            appInfo.append("Installed: ").append(packageManager.getPackageInfo(app.packageName, 0).firstInstallTime).append("\n");
        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException(e);
        }
        try {
            appInfo.append("Updated: ").append(packageManager.getPackageInfo(app.packageName, 0).lastUpdateTime).append("\n");
        } catch (PackageManager.NameNotFoundException e) {

            throw new RuntimeException(e);
        }
        appInfo.append("Size: ").append(getAppSize(app)).append(" MB").append("\n");
        appInfo.append("UID: ").append(app.uid).append("\n");
        appInfo.append("Permissions: \n").append(getAppPermissions(app.packageName));


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(app.loadLabel(packageManager));
        builder.setMessage(appInfo.toString());
        builder.setPositiveButton("OK", null);
        builder.create().show();
    }

    private double getAppSize(ApplicationInfo app) {
        try {
            String sourceDir = app.sourceDir;
            long sizeBytes = new File(sourceDir).length();
            return sizeBytes / (1024.0 * 1024.0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    private String getCategoryName(int category) {
        switch (category) {
            case ApplicationInfo.CATEGORY_GAME:
                return "Game";
            case ApplicationInfo.CATEGORY_AUDIO:
                return "Audio";
            case ApplicationInfo.CATEGORY_VIDEO:
                return "Video";
            case ApplicationInfo.CATEGORY_IMAGE:
                return "Image";
            case ApplicationInfo.CATEGORY_SOCIAL:
                return "Social";
            case ApplicationInfo.CATEGORY_NEWS:
                return "News";
            case ApplicationInfo.CATEGORY_MAPS:
                return "Maps";
            case ApplicationInfo.CATEGORY_PRODUCTIVITY:
                return "Productivity";
            default:
                return "Unknown";
        }
    }

    private String getAppPermissions(String packageName) {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);
            String[] permissions = packageInfo.requestedPermissions;
            if (permissions != null && permissions.length > 0) {
                StringBuilder permissionList = new StringBuilder();
                for (String permission : permissions) {
                    permissionList.append(permission).append("\n");
                }
                return permissionList.toString();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "No permissions found.";
    }

}