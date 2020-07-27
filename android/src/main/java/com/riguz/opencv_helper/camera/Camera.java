package com.riguz.opencv_helper.camera;

import android.app.Activity;
import android.content.Context;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.media.CamcorderProfile;
import android.media.ImageReader;
import android.os.Build;
import android.util.Size;
import android.view.Surface;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.TextureRegistry;

import static com.riguz.opencv_helper.camera.CameraUtils.computeBestPreviewSize;
import static com.riguz.opencv_helper.camera.CameraUtils.getBestAvailableCamcorderProfileForResolutionPreset;

public class Camera {
    private final TextureRegistry.SurfaceTextureEntry flutterTexture;
    private final CameraManager cameraManager;
    //private final OrientationEventListener orientationEventListener;
    private final boolean isFrontFacing;
    private final int sensorOrientation;
    private final String cameraId;
    private final Size captureSize;
    private final Size previewSize;
    private final boolean enableAudio;
    private final CamcorderProfile recordingProfile;

    private CameraDevice cameraDevice;
    private ImageReader pictureImageReader;
    private CameraCaptureSession cameraCaptureSession;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Camera(
            final Activity activity,
            final TextureRegistry.SurfaceTextureEntry flutterTexture,
            final String cameraId,
            final String resolutionPreset,
            final boolean enableAudio) throws CameraAccessException {
        this.cameraId = cameraId;
        this.enableAudio = enableAudio;
        this.flutterTexture = flutterTexture;
        this.cameraManager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);

        ResolutionPreset preset = ResolutionPreset.valueOf(resolutionPreset);
        previewSize = computeBestPreviewSize(cameraId, preset);
        recordingProfile = getBestAvailableCamcorderProfileForResolutionPreset(cameraId, preset);
        captureSize = new Size(recordingProfile.videoFrameWidth, recordingProfile.videoFrameHeight);

        CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
        sensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        isFrontFacing = characteristics.get(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_FRONT;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void open(final MethodChannel.Result result) throws CameraAccessException {
        pictureImageReader = ImageReader.newInstance(captureSize.getWidth(),
                captureSize.getHeight(),
                ImageFormat.JPEG,
                2);

        cameraManager.openCamera(cameraId,
                new CameraDevice.StateCallback() {
                    @Override
                    public void onOpened(@NonNull CameraDevice cameraDevice) {
                        Camera.this.cameraDevice = cameraDevice;

                        try {
                            startPreview();
                        } catch (CameraAccessException e) {
                            result.error("CameraAccess", "Unable to start preview", e);
                            close();
                            return;
                        }
                        Map<String, Object> reply = new HashMap<>();
                        reply.put("textureId", flutterTexture.id());
                        reply.put("previewWidth", previewSize.getWidth());
                        reply.put("previewHeight", previewSize.getHeight());
                        result.success(reply);
                    }

                    @Override
                    public void onDisconnected(@NonNull CameraDevice cameraDevice) {
                        close();
                    }

                    @Override
                    public void onError(@NonNull CameraDevice cameraDevice, int i) {
                        close();
                    }
                }, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startPreview() throws CameraAccessException {
        createCaptureSession(CameraDevice.TEMPLATE_PREVIEW, pictureImageReader.getSurface());
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void createCaptureSession(int templateType, Surface... surfaces) throws CameraAccessException {
        closeCaptureSession();

        SurfaceTexture surfaceTexture = flutterTexture.surfaceTexture();
        surfaceTexture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
        Surface flutterSurface = new Surface(surfaceTexture);

        final CaptureRequest.Builder captureRequestBuilder = cameraDevice.createCaptureRequest(templateType);
        captureRequestBuilder.addTarget(flutterSurface);


        CameraCaptureSession.StateCallback callback =
                new CameraCaptureSession.StateCallback() {
                    @Override
                    public void onConfigured(@NonNull CameraCaptureSession session) {
                        try {
                            if (cameraDevice == null) {
                                return;
                            }
                            cameraCaptureSession = session;
                            captureRequestBuilder.set(
                                    CaptureRequest.CONTROL_MODE, CameraMetadata.CONTROL_MODE_AUTO);
                            cameraCaptureSession.setRepeatingRequest(captureRequestBuilder.build(), null, null);
                        } catch (CameraAccessException | IllegalStateException | IllegalArgumentException e) {
                        }
                    }

                    @Override
                    public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                    }
                };
        List<Surface> surfaceList = Collections.singletonList(flutterSurface);
        // Start the session
        cameraDevice.createCaptureSession(surfaceList, callback, null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void closeCaptureSession() {
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            cameraCaptureSession = null;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void close() {
        closeCaptureSession();

        if (cameraDevice != null) {
            cameraDevice.close();
            cameraDevice = null;
        }
        if (pictureImageReader != null) {
            pictureImageReader.close();
            pictureImageReader = null;
        }
    }
}
