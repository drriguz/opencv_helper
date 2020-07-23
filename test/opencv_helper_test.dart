import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:opencv_helper/opencv_helper.dart';

void main() {
  const MethodChannel channel = MethodChannel('opencv_helper');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });
}
