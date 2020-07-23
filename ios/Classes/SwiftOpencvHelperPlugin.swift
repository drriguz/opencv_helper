import Flutter
import UIKit

public class SwiftOpencvHelperPlugin: NSObject, FlutterPlugin {
    let dispQueue = DispatchQueue(label: "com.riguz.opencv_helper");
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "opencv_helper", binaryMessenger: registrar.messenger())
        let instance = SwiftOpencvHelperPlugin()
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        let args = call.arguments as! [String: Any];
        switch call.method {
        case "version":
            result(OpenCVWrapper.openCVVersionString());
            break;
        case "resize":
            let source = args["source"] as! String;
            let width = args["width"] as! UInt32;
            let height = args["height"] as! UInt32;
            dispQueue.async {
                do {
                    let data:Data = OpenCVWrapper.resize(source, andPar: width, andPar: height);
                    DispatchQueue.main.async {
                        result(data);
                    }
                } catch let err as NSError{
                    
                    let error = FlutterError(code: "500", message:err.localizedDescription, details: err.description)
                    result(error)
                };
            }
        default:
            result(FlutterMethodNotImplemented);
        }
    }
}
