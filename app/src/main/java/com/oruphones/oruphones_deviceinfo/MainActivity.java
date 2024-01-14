package com.oruphones.oruphones_deviceinfo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.AppsFragment;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.BatteryFragment;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.CameraFragment;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.DashBoard;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.HardwareFragment;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.NetworkFragment;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.SensorsFragment;
import com.oruphones.oruphones_deviceinfo.DeviceInfoTabs.SystemFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.S)
public class MainActivity extends AppCompatActivity {

    // Add other variables and initialization as needed
    String chargingAmp;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    ArrayList<String> permissionsList;
    String[] permissionsStr = {
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_WIFI_STATE,


    };

    boolean batteryInflated;
    int permissionsCount = 0;
    ActivityResultLauncher<String[]> permissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    new ActivityResultCallback<Map<String, Boolean>>() {
                        @Override
                        public void onActivityResult(Map<String, Boolean> result) {
                            ArrayList<Boolean> list = new ArrayList<>(result.values());
                            permissionsList = new ArrayList<>();
                            permissionsCount = 0;
                            for (int i = 0; i < list.size(); i++) {
                                if (shouldShowRequestPermissionRationale(permissionsStr[i])) {
                                    permissionsList.add(permissionsStr[i]);
                                } else if (!hasPermission(MainActivity.this, permissionsStr[i])) {
                                    Toast.makeText(activity, permissionsStr[i], Toast.LENGTH_SHORT).show();
                                    permissionsCount++;
                                }
                            }
                            if (permissionsList.size() > 0) {
                                askForPermissions(permissionsList);
                            } else if (permissionsCount > 0) {
                                showPermissionDialog();
                            } else {
                                viewPager.setAdapter(new TabAdapter(getSupportFragmentManager()));
                                tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
                                tabLayout.setupWithViewPager(viewPager);
                            }
                        }
                    });

    private boolean hasPermission(Context context, String permissionStr) {
        return ContextCompat.checkSelfPermission(context, permissionStr) == PackageManager.PERMISSION_GRANTED;
    }

    AlertDialog alertDialog;

    private void showPermissionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission required")
                .setMessage("Some permissions are needed to be allowed to use this app without any problems.")
                .setPositiveButton("Ok", (dialog, which) -> {
                    dialog.dismiss();
                });
        if (alertDialog == null) {
            alertDialog = builder.create();
            if (!alertDialog.isShowing()) {
                alertDialog.show();
            }
        }
    }

    private void askForPermissions(ArrayList<String> permissionsList) {
        if (Build.VERSION.SDK_INT >= 30) {
            if (!Environment.isExternalStorageManager()) {
                requestManageAllFilesAccessPermission();
            }
        }
        String[] newPermissionStr = new String[permissionsList.size()];
        for (int i = 0; i < newPermissionStr.length; i++) {
            newPermissionStr[i] = permissionsList.get(i);
        }
        if (newPermissionStr.length > 0) {
            permissionsLauncher.launch(newPermissionStr);
        } else {
            /* User has pressed 'Deny & Don't ask again' so we have to show the enable permissions dialog
            which will lead them to app details page to enable permissions from there. */
            showPermissionDialog();
        }
    }


    Activity activity;
    Context context;
    LinearLayout mainScrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity = MainActivity.this;
        context = getApplicationContext();

        permissionsList = new ArrayList<>();
        permissionsList.addAll(Arrays.asList(permissionsStr));
        askForPermissions(permissionsList);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

    }
    private void requestManageAllFilesAccessPermission() {
        Snackbar.make(findViewById(android.R.id.content), "Allow All Files Access For OruPhones to get more Accurate data ", Snackbar.LENGTH_INDEFINITE)
                .setAction("Settings", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Uri uri = Uri.parse("package:" + getPackageName());
                            Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                            startActivity(intent);
                        } catch (Exception ex) {
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                            startActivity(intent);
                        }
                    }
                })
                .show();
    }


}



     class TabAdapter extends FragmentStatePagerAdapter {

        public TabAdapter(FragmentManager fm) {
            super(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new DashBoard();
                case 1:
                    return new HardwareFragment();
                case 2:
                    return new SystemFragment();
                case 3:
                    return new BatteryFragment();
                case 4:
                    return new NetworkFragment();
                case 5:
                    return new AppsFragment();
                case 6:
                    return new CameraFragment();
                case 7:
                    return new SensorsFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 8; // Number of tabs
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Dashboard";
                case 1:
                    return "Hardware";
                case 2:
                    return "System";
                case 3:
                    return "Battery";
                case 4:
                    return "Network";
                case 5:
                    return "Apps";
                case 6:
                    return "Camera";
                case 7:
                    return "Sensors";
                default:
                    return null;
            }
        }
    }

