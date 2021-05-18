import 'dart:io';
import 'dart:typed_data';

import 'package:flutter/material.dart';
import 'dart:async';
import 'package:http/http.dart' as http;
import 'package:opencv_helper/opencv_helper.dart';
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
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  ImageProvider original = AssetImage("assets/lena.png");
  ImageProvider thumb;
  String version = null;

  _MyAppState();

  @override
  void initState() {
    super.initState();
    OpencvHelper.version().then((value) => setState(() {
          version = value;
        }));
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
              thumb == null ? Text("OpenCV loading...") : Image(image: thumb)
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
    final Uint8List t = await OpencvHelper.resize(imageFile.path, 100, 100);
    setState(() {
      thumb = MemoryImage(t);
    });
  }
}
