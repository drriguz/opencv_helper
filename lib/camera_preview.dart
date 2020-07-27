import 'package:flutter/material.dart';

import 'opencv_helper.dart';

class CameraPreview extends StatelessWidget {
  const CameraPreview(this.controller);

  final CameraController controller;

  @override
  Widget build(BuildContext context) {
    return controller.value.hasInitialized ? Texture(textureId: controller.textureId) : Container();
  }
}
