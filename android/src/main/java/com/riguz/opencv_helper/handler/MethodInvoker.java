package com.riguz.opencv_helper.handler;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class MethodInvoker {
    private final ResultExecutor resultExecutor;
    private final DirectExecutor directExecutor;
    private final boolean async;
    private final Handler uiThreadHandler = new Handler(Looper.getMainLooper());

    private static final Executor executor;

    static {
        final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
        executor = Executors.newFixedThreadPool(NUMBER_OF_CORES);
    }

    private MethodInvoker(ResultExecutor resultExecutor, DirectExecutor directExecutor, boolean async) {
        this.resultExecutor = resultExecutor;
        this.directExecutor = directExecutor;
        this.async = async;
    }

    public static MethodInvoker sync(ResultExecutor resultExecutor) {
        return new MethodInvoker(resultExecutor, null, false);
    }

    public static MethodInvoker async(ResultExecutor resultExecutor) {
        return new MethodInvoker(resultExecutor, null, true);
    }

    public static MethodInvoker direct(DirectExecutor directExecutor) {
        return new MethodInvoker(null, directExecutor, false);
    }

    public void execute(final MethodCall call, final MethodChannel.Result result) {
        if (directExecutor != null) {
            try {
                directExecutor.execute(call, result);
            } catch (ExecuteException e) {
                result.error(e.getErrorCode(), e.getMessage(), e.getCause());
            }
        } else if (async)
            executeAsync(call, result);
        else
            executeSync(call, result);
    }

    private void executeSync(final MethodCall call, final MethodChannel.Result result) {
        try {
            Object returnValue = resultExecutor.execute(call);
            result.success(returnValue);
        } catch (ExecuteException e) {
            result.error(e.getErrorCode(), e.getMessage(), e.getCause());
        }
    }

    private void executeAsync(final MethodCall call, final MethodChannel.Result result) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final Object returnValue = resultExecutor.execute(call);
                    uiThreadHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            result.success(returnValue);
                        }
                    });
                } catch (ExecuteException e) {
                    result.error(e.getErrorCode(), e.getMessage(), e.getCause());
                }
            }
        });
    }
}
