#include <jni.h>
#include <string>
#include <android/log.h>
#include "time.h"
#include <vector>
#include <algorithm>
#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <assert.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <android/native_window_jni.h>
#include <pthread.h>
#include <unistd.h>

pthread_t test_call_read_depth_frame_thread;
pthread_t test_call_read_rgb_frame_thread;

#include <DeviceInterface.h>
//#include "utilbase.h"

class _NullString{
public:
    _NullString(){}
    operator const char*() const {
        return nullptr;
    }
};

static const _NullString ANY_DEVICE;

VideoFrame *g_pYUVFrameData = new VideoFrame();
VideoFrame *g_pDepthFrameData = new VideoFrame();
VideoStream * streamDepth = nullptr;
VideoStream * streamColor = nullptr;
int g_count = 1;


void * test_call_read_depth_frame_function(void * vptr_args)
{
    if (g_count > 0) {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar first");
        g_count = 0;
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar not first return");
        return nullptr;
    }
    
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar test_call_read_depth_frame_function");
//    Device * device = (Device*)vptr_args;
//    VideoFrame *pFrame = (VideoFrame*)vptr_args;
    
    Device device;
    
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar device.isValid result = %d", device.isValid());
    
//    if (!device.isValid()) {
//
//    } else {
//        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar device.isValid false. return!");
//        return nullptr;
//    }
    
//    FILE* pf = fopen("/storage/emulated/0/huaweiarDepth.yuv", "wt+");
    
    int result = 0;
    int count = 0;
    do {
        result = device.open(ANY_DEVICE);
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar device.open result = %d", result);
        if (0 == result) {
            __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar device.open succeed");
            break;
        }
        count++;
        usleep(500*1000);
    } while (count<=20);
    
    if (nullptr == streamDepth) {
        streamDepth = new VideoStream();
    }
    
    result = streamDepth->create(device, SENSOR_DEPTH);
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar create result = %d", result);

    if (0 == result) {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar create succeed.");
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar create failed. return!!!");
        return nullptr;
    }
    
    int mode = streamDepth->getVideoMode();
    streamDepth->setVideoMode(mode);
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar mode = %d", mode);
    
    const CameraIntrInfo * depthInfo = device.getSensorInfo(SENSOR_DEPTH);
    
    
//    FILE* pf = fopen("/storage/emulated/0/huaweiarDepth.yuv", "w");
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar before start");
    int r = streamDepth->start();
    if (r != 0) {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar start failed. return");
    }
    
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar after start");
    VideoFrame pDepthFrame;
//    const CameraIntrInfo * depthInfo = device.getSensorInfo(SENSOR_DEPTH);
    int irWidth = 400;//depthInfo->width;
    int irHeight = 640;//depthInfo->height;
    
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar depth w=%d,h=%d", irWidth, irHeight);
    
    pDepthFrame.data = malloc(irWidth*irHeight* sizeof(float));

//    for(int i = 0; i < 1; ++i){
        int i = 0;
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar before readFrame %d", i);
        r = streamDepth->readFrame(&pDepthFrame);
        if (r != 0) {
            __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar readFrame %d failed", i);
        }
//    }
    
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar pDepthFrame[0]=%f",
                        ((float *)pDepthFrame.data)[0]);
//    free(pDepthFrame.data);
    
//    g_pDepthFrameData->data = malloc(irWidth*irHeight);
//    streamDepth->readFrame(g_pDepthFrameData);
//    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar g_pDepthFrameData->data[0]=%u",
//                        ((unsigned char *)g_pDepthFrameData->data)[0]);
    
    FILE* pf = fopen("/storage/emulated/0/huaweiarDepth.yuv", "w");
    int lenPerFrame = irWidth*irHeight* sizeof(float);
    int Frame = 1;
    int len = lenPerFrame*Frame;
    if (pf) {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang open depth success\n");
        fwrite (pDepthFrame.data , sizeof(char), len , pf );
        fclose (pf);
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang open depth failed\n");
    }
    
    free(pDepthFrame.data);
//    device.close();
//    streamDepth->stop();
//    streamDepth->destroy();
//    delete streamDepth;
    pthread_exit(nullptr);
    
}
;
int g_yuvCount = 1;

void * test_call_read_color_frame_function(void * vptr_args)
{
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar test_call_read_color_frame_function");
    //Device * device = (Device*)vptr_args;
    //VideoFrame *pFrame = (VideoFrame*)vptr_args;
//    return nullptr;
    Device device;
    
    if (g_yuvCount > 0) {
        g_yuvCount--;
    } else {
        return nullptr;
    }
    
    int result = 0;
    int count = 0;
    do {
        result = device.open(ANY_DEVICE);
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar device.open result = %d", result);
        if (0 == result) {
            __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar device.open succeed");
            break;
        }
        count++;
        usleep(500*1000);
    } while (count<=20);
    
    if (nullptr == streamColor) {
        streamColor = new VideoStream();
    }

    result = streamColor->create(device, SENSOR_COLOR);
    if (result != 0) {
        return nullptr;
    }
    
    int mode = streamColor->getVideoMode();
    streamColor->setVideoMode(mode);
    
    
    result = streamColor->start();
    if (result != 0) {
        return nullptr;
    }
    
    
    
    VideoFrame pColorFrame;
    const CameraIntrInfo * rgbInfo = device.getSensorInfo(SENSOR_COLOR);
    int irWidth = 800;//rgbInfo->width;
    int irHeight = 1280;//rgbInfo->height;
    
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar yuv w=%d,h=%d", irWidth, irHeight);
    
    
    pColorFrame.data = malloc(irWidth*irHeight*3/2);

//    for(int i = 0; i < 200; ++i){
    result = streamColor->readFrame(&pColorFrame);
    if (result != 0) {
        return nullptr;
    }
//    }


    
//    g_pYUVFrameData->data = malloc(irWidth*irHeight*3/2);
//    streamColor->readFrame(g_pYUVFrameData);
//    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar g_pYUVFrameData->data[0]=%u",
//                        ((unsigned char *)g_pYUVFrameData->data)[0]);
    
    FILE* pf = fopen("/storage/emulated/0/huaweiarYUV.yuv", "w");
    int lenPerFrame = irWidth*irHeight*3/2;
    int Frame = 1;
    int len = lenPerFrame*Frame;
//    unsigned char *buf = new unsigned char[len];
    if (pf) {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang open yuv success\n");
        fwrite (pColorFrame.data , sizeof(char), len , pf );
        fclose(pf);
    } else {
        __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang open yuv failed\n");
    }
    
    free(pColorFrame.data);
//    device.close();
//    streamColor->stop();
//    streamColor->destroy();
//    delete streamColor;
    
    pthread_exit(nullptr);
}

extern int clock_gettime(int, struct timespec *);

struct timespec now;

#define  LOGE(x...)  __android_log_print(ANDROID_LOG_ERROR,"shiyang jni",x)

std::string jstringTostring(JNIEnv *env, jstring jstr) {
    const char *c_str = NULL;
    c_str = env->GetStringUTFChars(jstr, NULL);
    std::string stemp(c_str);
    env->ReleaseStringUTFChars(jstr, c_str);
    return stemp;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_appmagics_avatardemo_MainActivity_stringFromJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

extern "C"
JNIEXPORT void JNICALL
Java_appmagics_avatardemo_MainActivity_initJNI(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
}

extern "C"
JNIEXPORT void JNICALL
Java_appmagics_avatardemo_Tracker_testJNI(
        JNIEnv *env, jobject instance, jobject context, jint type,
        jint imgw, jint imgh, jint formate, jstring respath_, jint img_angle) {
}

extern "C"
JNIEXPORT void JNICALL
Java_appmagics_avatardemo_Tracker_detectJNI(
        JNIEnv *env, jobject instance, jobject context, jint type,
        jint imgw, jint imgh, jint formate, jstring respath_, jint img_angle, jobject data,
        jfloatArray dataout) {

}

extern "C"
JNIEXPORT void JNICALL
Java_appmagics_avatardemo_ARCamera_openARCameraJNI(JNIEnv *env, jobject instance) {
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar Java_appmagics_avatardemo_Tracker_openARCameraJNI");
    int r0 = pthread_create(&test_call_read_rgb_frame_thread, nullptr,
                            test_call_read_color_frame_function, nullptr);
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar rgb pthreadcreate = %d", r0);
    
    int r1 = pthread_create(&test_call_read_depth_frame_thread, nullptr,
                            test_call_read_depth_frame_function, nullptr);
    __android_log_print(ANDROID_LOG_ERROR, "AR", "shiyang ar depth pthreadcreate = %d", r1);
}


extern "C"
JNIEXPORT void JNICALL
Java_appmagics_avatardemo_ARCamera_closeARCameraJNI(JNIEnv *env, jobject instance) {
    if (streamDepth) {
        streamDepth->stop();
        streamDepth->destroy();
        delete streamDepth;
    }
    
    if (streamColor) {
        streamColor->stop();
        streamColor->destroy();
        delete streamColor;
    }
}

extern "C"
float* getDataOut() {
    return nullptr;
}

extern "C"
unsigned char* getPreviewData() {
    return nullptr;
}

