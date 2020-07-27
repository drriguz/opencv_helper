import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import 'camera_info.dart';

const MethodChannel _channel = const MethodChannel('com.riguz.opencv_helper');

class OpenCVHelper {
  static Future<String> version() async {
    return _channel.invokeMethod('version', {});
  }

  static Future<Uint8List> resize(String sourceImagePath, int width, int height) async {
    return _channel.invokeMethod('resize', {
      "source": sourceImagePath,
      "width": width,
      "height": height,
    });
  }

  static Future<List<CameraDescription>> getCameras() async {
    final List<Map<dynamic, dynamic>> cameras = await _channel.invokeListMethod<Map<dynamic, dynamic>>('getCameras');
    return cameras.map((Map<dynamic, dynamic> camera) {
      return CameraDescription(
        camera['cameraId'],
        CameraLensDirection.from(camera['lensFacing']),
        camera['sensorOrientation'],
      );
    }).toList();
  }
}

class CameraController extends ValueNotifier<CameraStatus> {
  final CameraDescription cameraDescription;
  final ResolutionPreset resolutionPreset;

  int _textureId;
  Completer<void> _creatingCompleter;

  CameraController(
    this.cameraDescription,
    this.resolutionPreset,
  ) : super(const CameraStatus.uninitialized());

  Future<void> initialize() async {
    _creatingCompleter = Completer<void>();
    final Map<String, dynamic> reply = await _channel.invokeMapMethod("initializeCamera", {
      "cameraId": cameraDescription.cameraId,
      "resolutionPreset": resolutionPreset.toString(),
    });
    _textureId = reply["textureId"];
    value = value.copyWith(
        hasInitialized: true,
        previewSize: Size(
          reply["previewWidth"].toDouble(),
          reply["previewHeight"].toDouble(),
        ));
    _creatingCompleter.complete();
    return _creatingCompleter.future;
  }

  int get textureId => _textureId;
}
