package com.riguz.opencv_helper.handler;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public interface DirectExecutor {
    void execute(MethodCall call, MethodChannel.Result result) throws ExecuteException;
}
