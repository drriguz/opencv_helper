package com.riguz.opencv_helper.camera;

import android.app.Activity;
import android.content.Context;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.media.CamcorderProfile;
import android.os.Build;
import android.util.Size;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CameraUtils {
    private CameraUtils() {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Size computeBestPreviewSize(String cameraId, ResolutionPreset preset) {
        if (preset.ordinal() > ResolutionPreset.high.ordinal()) {
            preset = ResolutionPreset.high;
        }

        CamcorderProfile profile =
                getBestAvailableCamcorderProfileForResolutionPreset(cameraId, preset);
        return new Size(profile.videoFrameWidth, profile.videoFrameHeight);
    }

    public static CamcorderProfile getBestAvailableCamcorderProfileForResolutionPreset(
            String cameraIdStr, ResolutionPreset preset) {
        int cameraId = Integer.parseInt(cameraIdStr);
        switch (preset) {
            // All of these cases deliberately fall through to get the best available profile.
            case max:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_HIGH)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_HIGH);
                }
            case ultraHigh:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_2160P)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_2160P);
                }
            case veryHigh:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_1080P)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_1080P);
                }
            case high:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_720P)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_720P);
                }
            case medium:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_480P)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_480P);
                }
            case low:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_QVGA)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_QVGA);
                }
            default:
                if (CamcorderProfile.hasProfile(cameraId, CamcorderProfile.QUALITY_LOW)) {
                    return CamcorderProfile.get(cameraId, CamcorderProfile.QUALITY_LOW);
                } else {
                    throw new IllegalArgumentException(
                            "No capture session available for current capture session.");
                }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static List<Map<String, Object>> getAvailableCameras(Activity activity) throws CameraAccessException {
        CameraManager cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        String[] cameraIds = cameraManager.getCameraIdList();

        List<Map<String, Object>> cameras = new ArrayList<>();
        for (String cameraId : cameraIds) {
            Map<String, Object> details = new HashMap<>();
            CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);

            details.put("cameraId", cameraId);
            details.put("sensorOrientation", characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION));
            details.put("lensFacing", getLensFacingName(characteristics.get(CameraCharacteristics.LENS_FACING)));

            cameras.add(details);
        }
        return cameras;
    }

    private static String getLensFacingName(int lensFacing) {
        switch (lensFacing) {
            case CameraMetadata.LENS_FACING_FRONT:
                return "front";
            case CameraMetadata.LENS_FACING_BACK:
                return "back";
            case CameraMetadata.LENS_FACING_EXTERNAL:
                return "external";
            default:
                throw new IllegalArgumentException("Unexpected lens facing:" + lensFacing);
        }
    }
}
