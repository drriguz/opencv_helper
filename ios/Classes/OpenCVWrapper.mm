//
//  OpenCVWrapper.m
//  opencv_helper
//
//  Created by Haifeng Li on 2020/7/21.
//

/**
    refer :
    https://www.timpoulsen.com/2019/using-opencv-in-an-ios-app.html
    https://github.com/flutter/flutter/issues/17978#issuecomment-577270917
    https://stackoverflow.com/questions/51704635/opencv-ios-expected-identifier-macro
 
        do not need to set "allow include non modular....",
                just make the OpenCVWrapper.h file public
 */

#import "OpenCVWrapper.h"
#import <opencv2/opencv.hpp>

@implementation OpenCVWrapper

+ (NSString *)openCVVersionString {
    return [NSString stringWithFormat:@"%s",  CV_VERSION];
}

+ (NSData *)resize: (const NSString *) sourceFile andPar: (unsigned int) width andPar: (unsigned int) height {
    const std::string fileNameStr = std::string([sourceFile UTF8String]);

    /*
        todo: can we directly pass image bytes?
        https://github.com/shimat/opencvsharp/issues/173#issuecomment-173042030
        
        cv::imdecode(cv::InputArray(*imgData), cv::ImreadModes::IMREAD_UNCHANGED);
        however throws:
     libc++abi.dylib: terminating with uncaught exception of type cv::Exception: OpenCV(4.4.0) /Volumes/build-storage/build/master_iOS-mac/opencv/modules/core/src/matrix_wrap.cpp:124: error: (-213:The function/feature is not implemented) Unknown/unsupported array type in function 'getMat_'
     */
    
    cv::Mat image = cv::imread(fileNameStr);
    cv::Mat thumb = cv::Mat();
    cv::resize(image, thumb, cv::Size(width, height), 0, 0, cv::INTER_AREA);
    
    std::vector<uchar> encoded;
    cv::imencode(".jpg", thumb, encoded);
    

    NSData *imgData = [[NSData alloc] initWithBytesNoCopy:encoded.data() length:encoded.size() freeWhenDone:false];
    return imgData;
}

@end
