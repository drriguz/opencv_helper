package com.riguz.opencv_helper;

import android.app.Activity;
import android.os.Build;

import androidx.annotation.NonNull;

import com.riguz.opencv_helper.handler.MethodCallHandlerImpl;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.view.TextureRegistry;

/**
 * OpencvHelperPlugin
 */
public class OpenCVHelperPlugin implements FlutterPlugin, ActivityAware {
    private FlutterPluginBinding flutterPluginBinding;
    private MethodCallHandlerImpl methodCallHandler;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        register(flutterPluginBinding.getBinaryMessenger());
        this.flutterPluginBinding = flutterPluginBinding;
    }

    // This static function is optional and equivalent to onAttachedToEngine. It supports the old
    // pre-Flutter-1.12 Android projects. You are encouraged to continue supporting
    // plugin registration via this function while apps migrate to use the new Android APIs
    // post-flutter-1.12 via https://flutter.dev/go/android-project-migration.
    //
    // It is encouraged to share logic between onAttachedToEngine and registerWith to keep
    // them functionally equivalent. Only one of onAttachedToEngine or registerWith will be called
    // depending on the user's project. onAttachedToEngine or registerWith must both be defined
    // in the same class.
    public static void registerWith(Registrar registrar) {
        register(registrar.messenger());
    }

    private static void register(BinaryMessenger messenger) {

    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        this.flutterPluginBinding = null;
    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        maybeStartListening(binding.getActivity(),
                flutterPluginBinding.getBinaryMessenger(),
                flutterPluginBinding.getTextureRegistry());
    }

    @Override
    public void onDetachedFromActivity() {
        if (methodCallHandler == null) {
            // Could be on too low of an SDK to have started listening originally.
            return;
        }

        methodCallHandler.stopListening();
        methodCallHandler = null;
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        onDetachedFromActivity();
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        onAttachedToActivity(binding);
    }


    private void maybeStartListening(
            Activity activity,
            BinaryMessenger messenger,
            TextureRegistry textureRegistry) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            // If the sdk is less than 21 (min sdk for Camera2) we don't register the plugin.
            return;
        }

        methodCallHandler = new MethodCallHandlerImpl(activity, messenger, textureRegistry);
    }
}
