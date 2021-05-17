import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:async';
import 'package:http/http.dart' as http;
import 'package:opencv_helper/opencv_helper.dart';
import 'package:opencv_helper/camera_info.dart';
import 'package:opencv_helper/camera_preview.dart';
import 'package:path/path.dart';
import 'package:path_provider/path_provider.dart';

Future<File> getImageFile() async {
  final rootPath = await getApplicationDocumentsDirectory();
  final imageFile = File(join(rootPath.path, "lena.png"));
  return imageFile;
}

Future<void> prepareResources() async {
  final imageFile = await getImageFile();
  if (!imageFile.existsSync()) {
    print("Saving images to local ");
    http.Response response = await http.get(
      'https://homepages.cae.wisc.edu/~ece533/images/lena.png',
    );
    final Uint8List bytes = response.bodyBytes;
    return imageFile.writeAsBytes(bytes);
  }
}

void main() async {
  WidgetsFlutterBinding.ensureInitialized();
  await prepareResources();

  final cameras = await OpenCVHelper.getCameras();
  runApp(MyApp(cameras));
}

class MyApp extends StatefulWidget {
  final List<CameraDescription> cameras;

  const MyApp(this.cameras, {Key key}) : super(key: key);

  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ImageProvider original = AssetImage("assets/lena.png");
  ImageProvider thumb;
  String version = null;
  CameraController _cameraController;

  _MyAppState();

  @override
  void initState() {
    super.initState();
    OpenCVHelper.version().then((value) => setState(() {
          version = value;
        }));
    final camera = widget.cameras[0];
    print("cameras: ${camera}");
    _cameraController = CameraController(camera, ResolutionPreset.MEDIUM);
    _cameraController.initialize().then((_) {
      if (!mounted) {
        return;
      }
      setState(() {});
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              version == null ? Text("OpenCV loading...") : Text("OpenCV ${version}"),
              thumb == null ? Text("OpenCV loading...") : Image(image: thumb),
              _cameraController.value.hasInitialized
                  ? AspectRatio(
                      aspectRatio: _cameraController.value.aspectRatio,
                      child: CameraPreview(_cameraController),
                    )
                  : Text("Camera is loading...")
            ],
          ),
        ),
        floatingActionButton: FloatingActionButton(
          child: Icon(Icons.add),
          onPressed: handle,
        ),
      ),
    );
  }

  Future<void> handle() async {
    setState(() {
      thumb = null;
    });
    final imageFile = await getImageFile();
    final Uint8List t = await OpenCVHelper.resize(imageFile.path, 100, 100);
    setState(() {
      thumb = MemoryImage(t);
    });
  }
}
