package com.riguz.opencv_helper.handler;

import android.app.Activity;

import androidx.annotation.NonNull;

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

    private final OpenCVBinding openCVBinding = new OpenCVBinding();

    private final Map<String, MethodInvoker> invokers = new HashMap<>();

    public MethodCallHandlerImpl(Activity activity,
                                 BinaryMessenger messenger,
                                 TextureRegistry textureRegistry) {
        this.activity = activity;
        this.messenger = messenger;
        this.textureRegistry = textureRegistry;
        this.methodChannel = new MethodChannel(messenger, "com.riguz.opencv_helper");
        this.imageStreamChannel = new EventChannel(messenger, "com.riguz.opencv_helper/camera");

        registerMethodInvokers();

        methodChannel.setMethodCallHandler(this);
    }

    private void registerMethodInvokers() {
        invokers.put("version", MethodInvoker.sync(new ResultExecutor() {
            @Override
            public Object execute(MethodCall call) {
                return openCVBinding.getVersion();
            }
        }));
        invokers.put("resize", MethodInvoker.sync(new ResultExecutor() {
            @Override
            public Object execute(MethodCall call) {
                String source = call.argument("source");
                Integer width = call.argument("width");
                Integer height = call.argument("height");
                return openCVBinding.resize(source, width, height);
            }
        }));
    }

    public void stopListening() {
        methodChannel.setMethodCallHandler(null);
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
