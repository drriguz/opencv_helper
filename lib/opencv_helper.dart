import 'dart:async';
import 'dart:typed_data';

import 'package:flutter/services.dart';

class OpencvHelper {
  static const MethodChannel _channel = const MethodChannel('opencv_helper');

  static Future<String> version() async {
    return await _channel.invokeMethod('version', {});
  }

  static Future<Uint8List> resize(String sourceImagePath, int width, int height) async {
    return await _channel.invokeMethod('resize', {
      "source": sourceImagePath,
      "width": width,
      "height": height,
    });
  }
}
