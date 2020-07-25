package com.riguz.opencv_helper.cv;


import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class OpenCVBinding {
    static {
        System.loadLibrary("opencv_helper");
    }

    public native String getVersion();

    public native byte[] resize(String source, int width, int height);
}
