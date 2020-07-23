//
//  OpenCVWrapper.h
//  opencv_helper
//
//  Created by Haifeng Li on 2020/7/21.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface OpenCVWrapper : NSObject

+ (NSString *)openCVVersionString;

+ (NSData *)resize: (const NSString *) sourceFile andPar: (unsigned int) width andPar: (unsigned int) height;

@end

NS_ASSUME_NONNULL_END
