package com.riguz.opencv_helper;

import android.util.Log;

public class OpenCVBinding {
    static {
        System.loadLibrary("opencv_helper");
    }

    private native String getVersion();
    private native byte[] resize(String source, int width, int height);

    @Invokable("version")
    public String version() {
        return getVersion();
    }

    @Invokable("resize")
    public byte[] resizeImage(@Param("source") String source,
                              @Param("width") int width,
                              @Param("height") int height)
            throws InvokeException {
        return resize(source, width, height);
    }
}
