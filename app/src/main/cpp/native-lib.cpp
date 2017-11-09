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

pthread_t test_call_read_depth_frame_thread;
pthread_t test_call_read_rgb_frame_thread;

#include <DeviceInterface.h>
//#include "utilbase.h"

void * test_call_read_depth_frame_function(void * vptr_args)
{
    //Device * device = (Device*)vptr_args;
    //VideoFrame *pFrame = (VideoFrame*)vptr_args;
    
//    Device device;
//    device.open("123");
//    VideoStream * streamDepth = new VideoStream();
//
//    streamDepth->create(device, SENSOR_DEPTH);
//
//    streamDepth->start();
//
//    VideoFrame pDepthFrame;
//    const CameraIntrInfo * depthInfo = device.getSensorInfo(SENSOR_DEPTH);
//    int irWidth = depthInfo->width;
//    int irHeight = depthInfo->height;
//
//    pDepthFrame.data = malloc(irWidth*irHeight);
//
//    for(int i = 0; i < 200; ++i){
//        streamDepth->readFrame(&pDepthFrame);
//    }
//
//    free(pDepthFrame.data);
//
//    pthread_exit(NULL);
}

void * test_call_read_color_frame_function(void * vptr_args)
{
    //Device * device = (Device*)vptr_args;
    //VideoFrame *pFrame = (VideoFrame*)vptr_args;
    
//    Device device;
//    device.open("123");
//    VideoStream * streamColor = new VideoStream();
//
//    streamColor->create(device, SENSOR_COLOR);
//
//    streamColor->start();
//
//    VideoFrame pColorFrame;
//    const CameraIntrInfo * rgbInfo = device.getSensorInfo(SENSOR_COLOR);
//    int irWidth = rgbInfo->width;
//    int irHeight = rgbInfo->height;
//
//    pColorFrame.data = malloc(irWidth*irHeight*3/2);
//
//    for(int i = 0; i < 200; ++i){
//        streamColor->readFrame(&pColorFrame);
//    }
//
//    free(pColorFrame.data);
//
//    pthread_exit(NULL);
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
Java_appmagics_avatardemo_Tracker_openARCameraJNI(JNIEnv *env, jobject instance) {
    pthread_create(&test_call_read_rgb_frame_thread, nullptr,
                   test_call_read_color_frame_function, nullptr);
    pthread_create(&test_call_read_depth_frame_thread, nullptr,
                   test_call_read_depth_frame_function, nullptr);
}

extern "C"
float* getDataOut() {
    return nullptr;
}

extern "C"
unsigned char* getPreviewData() {
    return nullptr;
}

