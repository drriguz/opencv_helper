package com.riguz.opencv_helper.camera;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.flutter.plugin.common.PluginRegistry;

public class CameraPermissions {
    private static final int CAMERA_REQUEST_ID = 9527;

    private boolean ongoing = false;

    public interface PermissionsRegistry {
        void addListener(PluginRegistry.RequestPermissionsResultListener handler);
    }

    public interface ResultCallback {
        void onResult(String errorCode, String errorDescription);
    }

    public void requestPermissions(Activity activity,
                                   PermissionsRegistry permissionsRegistry,
                                   final ResultCallback callback) {
        if (ongoing) {
            callback.onResult("cameraPermission", "Camera permission request ongoing");
            return;
        }
        if (!hasCameraPermission(activity)) {
            permissionsRegistry.addListener(new CameraRequestPermissionsListener(new ResultCallback() {
                @Override
                public void onResult(String errorCode, String errorDescription) {
                    ongoing = false;
                    callback.onResult(errorCode, errorDescription);
                }
            }));
            ongoing = true;
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_ID);
        } else
            callback.onResult(null, null);
    }

    private boolean hasCameraPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasAudioPermission(Activity activity) {
        return ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO)
                == PackageManager.PERMISSION_GRANTED;
    }

    static class CameraRequestPermissionsListener
            implements PluginRegistry.RequestPermissionsResultListener {
        private final ResultCallback resultCallback;
        // There's no way to unregister permission listeners in the v1 embedding, so we'll be called
        // duplicate times in cases where the user denies and then grants a permission. Keep track of if
        // we've responded before and bail out of handling the callback manually if this is a repeat
        // call.
        boolean alreadyCalled = false;


        CameraRequestPermissionsListener(ResultCallback resultCallback) {
            this.resultCallback = resultCallback;
        }

        @Override

        public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            if (alreadyCalled || requestCode != CAMERA_REQUEST_ID)
                return false;

            alreadyCalled = true;
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                resultCallback.onResult("cameraPermission", "MediaRecorderCamera permission not granted");
            } else if (grantResults.length > 1 && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                resultCallback.onResult("cameraPermission", "MediaRecorderAudio permission not granted");
            } else {
                resultCallback.onResult(null, null);
            }
            return true;
        }
    }
}
