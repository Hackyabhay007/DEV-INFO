package com.oruphones.oruphones_deviceinfo;

import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.TimeUnit;

public class FlashLightManager {
    private CameraManager cameraManager;
    private String cameraId;
    private boolean isFlashLightOn;
    private Handler handler;

    public FlashLightManager(Context context) throws CameraAccessException {
        cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        cameraId = cameraManager.getCameraIdList()[0]; // Usually back camera is at 0 position.
        handler = new Handler();
    }

        public void start() throws CameraAccessException {
            turnOn();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    turnOff();
                    turnOn();
                    try {
                        start();
                    } catch (CameraAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
            },3000);
        }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
        turnOff();
    }

    private void turnOn() {
        Log.e("GHIO","ON");
        try {
            cameraManager.setTorchMode(cameraId, true);
            isFlashLightOn = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void turnOff() {
        Log.e("GHIO","OFF");
        try {
            if (isFlashLightOn) {
                cameraManager.setTorchMode(cameraId, false);
                isFlashLightOn = false;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }
}
