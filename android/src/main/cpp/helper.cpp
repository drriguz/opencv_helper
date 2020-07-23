#include <jni.h>
#include <string>

#include <opencv2/opencv.hpp>
#include <android/log.h>

extern "C" JNIEXPORT jstring JNICALL
Java_com_riguz_opencv_1helper_OpenCVBinding_getVersion(
        JNIEnv *env,
        jobject instance){
    std::string version(CV_VERSION);
    return env->NewStringUTF(version.c_str());
};

extern "C" JNIEXPORT jbyteArray JNICALL
Java_com_riguz_opencv_1helper_OpenCVBinding_resize(
        JNIEnv *env,
        jobject instance,
        jstring source,
        jint width,
        jint height){
    const char *sourcePath = env->GetStringUTFChars(source , NULL ) ;
    cv::Mat sourceImage = cv::imread(sourcePath);
    cv::Mat thumbImage;

    /*
        https://stackoverflow.com/questions/17533101/resize-a-matrix-after-created-it-in-opencv
    */
    cv::resize(sourceImage, thumbImage, cv::Size((int)width, (int)height), 0, 0, cv::INTER_AREA);
    std::vector<uchar> thumbImageBytes;
    cv::imencode(".jpg", thumbImage, thumbImageBytes);
    uchar *bytes = new uchar[thumbImageBytes.size()];
    std::copy(thumbImageBytes.begin(), thumbImageBytes.end(), bytes);

    // __android_log_write(ANDROID_LOG_DEBUG, "OPENCV_HELPER", "resized:" + thumbImageBytes.size());

    jbyteArray result = (*env).NewByteArray(thumbImageBytes.size());
    (*env).SetByteArrayRegion(result, 0, thumbImageBytes.size(), (jbyte *) bytes);
    delete []bytes;

    env->ReleaseStringUTFChars(source, sourcePath);
    return result;
};