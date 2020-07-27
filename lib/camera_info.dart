import 'package:flutter/material.dart';

class CameraLensDirection {
  final String _value;

  const CameraLensDirection._internal(this._value);

  static const FRONT = const CameraLensDirection._internal("front");
  static const BACK = const CameraLensDirection._internal("back");
  static const EXTERNAL = const CameraLensDirection._internal("external");

  factory CameraLensDirection.from(String lensDirection) {
    switch (lensDirection) {
      case 'front':
        return FRONT;
      case 'back':
        return BACK;
      case 'external':
        return EXTERNAL;
    }
    throw ArgumentError('Unknown CameraLensDirection value');
  }

  @override
  String toString() => _value;
}

class ResolutionPreset {
  final String _value;

  const ResolutionPreset._internal(this._value);

  static const LOW = const ResolutionPreset._internal("low"); // 352x288 on iOS, 240p (320x240) on Android
  static const MEDIUM = const ResolutionPreset._internal("medium"); // 480p (640x480 on iOS, 720x480 on Android)
  static const HIGH = const ResolutionPreset._internal("high"); // 720p (1280x720)
  static const VERY_HIGH = const ResolutionPreset._internal("veryHigh"); // 1080p (1920x1080)
  static const ULTRA_HIGH = const ResolutionPreset._internal("ultraHigh"); // 2160p (3840x2160)
  static const MAX = const ResolutionPreset._internal("max"); // The highest resolution available.

  factory ResolutionPreset.from(String lensDirection) {
    switch (lensDirection) {
      case 'low':
        return LOW;
      case 'medium':
        return MEDIUM;
      case 'high':
        return HIGH;
      case 'veryHigh':
        return VERY_HIGH;
      case 'ultraHigh':
        return ULTRA_HIGH;
      case 'max':
        return MAX;
    }
    throw ArgumentError('Unknown CameraLensDirection value');
  }

  @override
  String toString() => _value;
}

class CameraDescription {
  final String cameraId;
  final CameraLensDirection lensDirection;
  final int sensorOrientation;

  CameraDescription(this.cameraId, this.lensDirection, this.sensorOrientation);

  @override
  String toString() {
    return "camera: ${cameraId} ${lensDirection} ${sensorOrientation}";
  }
}

class CameraStatus {
  final bool hasInitialized;
  final bool isTakingPicture;
  final Size previewSize;
  final String errorDescription;

  const CameraStatus({
    this.hasInitialized,
    this.isTakingPicture,
    this.previewSize,
    this.errorDescription,
  });

  const CameraStatus.uninitialized()
      : this(
          hasInitialized: false,
          isTakingPicture: false,
        );

  double get aspectRatio => previewSize.height / previewSize.width;

  bool get hasError => errorDescription != null;

  CameraStatus copyWith({
    bool hasInitialized,
    bool isTackingPicture,
    Size previewSize,
    String errorDescription,
  }) {
    return CameraStatus(
        hasInitialized: hasInitialized ?? this.hasInitialized,
        isTakingPicture: isTackingPicture ?? this.isTakingPicture,
        previewSize: previewSize ?? this.previewSize,
        errorDescription: errorDescription);
  }
}
