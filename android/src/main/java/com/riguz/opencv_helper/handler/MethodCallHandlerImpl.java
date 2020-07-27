package com.riguz.opencv_helper.handler;

import android.app.Activity;
import android.hardware.camera2.CameraAccessException;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.riguz.opencv_helper.camera.Camera;
import com.riguz.opencv_helper.camera.CameraPermissions;
import com.riguz.opencv_helper.camera.CameraUtils;
import com.riguz.opencv_helper.cv.OpenCVBinding;

import java.util.HashMap;
import java.util.Map;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.view.TextureRegistry;

public class MethodCallHandlerImpl implements MethodChannel.MethodCallHandler {
    private final Activity activity;
    private final BinaryMessenger messenger;
    private final TextureRegistry textureRegistry;
    private final MethodChannel methodChannel;
    private final EventChannel imageStreamChannel;
    private final CameraPermissions.PermissionsRegistry permissionsRegistry;

    private final OpenCVBinding openCVBinding = new OpenCVBinding();
    private final CameraPermissions cameraPermissions = new CameraPermissions();
    private Camera camera;

    private final Map<String, MethodInvoker> invokers = new HashMap<>();

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MethodCallHandlerImpl(Activity activity,
                                 BinaryMessenger messenger,
                                 TextureRegistry textureRegistry,
                                 CameraPermissions.PermissionsRegistry permissionsRegistry) {
        this.activity = activity;
        this.messenger = messenger;
        this.textureRegistry = textureRegistry;
        this.methodChannel = new MethodChannel(messenger, "com.riguz.opencv_helper");
        this.imageStreamChannel = new EventChannel(messenger, "com.riguz.opencv_helper/camera");
        this.permissionsRegistry = permissionsRegistry;

        registerMethodInvokers();

        methodChannel.setMethodCallHandler(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void registerMethodInvokers() {
        invokers.put("version", MethodInvoker.sync(new ResultExecutor() {
            @Override
            public Object execute(MethodCall call) {
                return openCVBinding.getVersion();
            }
        }));
        invokers.put("resize", MethodInvoker.async(new ResultExecutor() {
            @Override
            public Object execute(MethodCall call) {
                String source = call.argument("source");
                Integer width = call.argument("width");
                Integer height = call.argument("height");
                return openCVBinding.resize(source, width, height);
            }
        }));
        invokers.put("getCameras", MethodInvoker.sync(new ResultExecutor() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public Object execute(MethodCall call) throws ExecuteException {
                try {
                    return CameraUtils.getAvailableCameras(activity);
                } catch (CameraAccessException e) {
                    throw new ExecuteException("CameraAccess", "Unable to get cameras", e);
                }
            }
        }));
        invokers.put("initializeCamera", MethodInvoker.direct(new DirectExecutor() {
            @Override
            public void execute(final MethodCall call, final MethodChannel.Result result) throws ExecuteException {
                if (camera != null)
                    camera.close();
                cameraPermissions.requestPermissions(activity, permissionsRegistry, new CameraPermissions.ResultCallback() {
                    @Override
                    public void onResult(String errorCode, String errorDescription) {
                        if (errorCode != null) {
                            result.error(errorCode, errorDescription, null);
                        } else {
                            // permission granted
                            try {
                                initializeCamera(call, result);
                            } catch (CameraAccessException e) {
                                result.error("CameraAccess", "Unable to initialize", e);
                            }
                        }
                    }
                });
            }
        }));
    }

    public void stopListening() {
        methodChannel.setMethodCallHandler(null);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initializeCamera(MethodCall call, MethodChannel.Result result) throws CameraAccessException {
        String cameraId = call.argument("cameraId");
        String resolutionPreset = call.argument("resolutionPreset");
        TextureRegistry.SurfaceTextureEntry flutterSurfaceTexture = textureRegistry.createSurfaceTexture();

        Camera camera = new Camera(activity, flutterSurfaceTexture, cameraId, resolutionPreset, false);
        camera.open(result);
    }

    @Override

    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        MethodInvoker invoker = invokers.get(call.method);
        if (invoker == null)
            result.notImplemented();
        else
            invoker.execute(call, result);
    }
}
