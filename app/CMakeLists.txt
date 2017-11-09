cmake_minimum_required(VERSION 3.4.1)

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/../jniLibs/${ANDROID_ABI})

add_library( native-lib
             SHARED
             src/main/cpp/native-lib.cpp)

find_library( log-lib
              log)

set(distribution_DIR ${CMAKE_SOURCE_DIR}/../../../../libs)

#add huawei
add_library(arcamera
            SHARED
            IMPORTED)
set_target_properties(arcamera
                      PROPERTIES IMPORTED_LOCATION
                      ../../../../libs/arm64-v8a/libarcamera.so)
include_directories(libs/include/huawei)

add_library(dfu
            SHARED
            IMPORTED)
set_target_properties(dfu
                      PROPERTIES IMPORTED_LOCATION
                      ../../../../libs/arm64-v8a/libdfu.so)
include_directories(libs/include/huawei)

add_library(HSLPreProcessing
            SHARED
            IMPORTED)
set_target_properties(HSLPreProcessing
                      PROPERTIES IMPORTED_LOCATION
                      ../../../../libs/arm64-v8a/libHSLPreProcessing.so)
include_directories(libs/include/huawei)

add_library(Idepthservice
            SHARED
            IMPORTED)
set_target_properties(Idepthservice
                      PROPERTIES IMPORTED_LOCATION
                      ../../../../libs/arm64-v8a/libIdepthservice.so)
include_directories(libs/include/huawei)

add_library(slaccuracy
            SHARED
            IMPORTED)
set_target_properties(slaccuracy
                      PROPERTIES IMPORTED_LOCATION
                      ../../../../libs/arm64-v8a/libslaccuracy.so)
include_directories(libs/include/huawei)

add_library(usb100
            SHARED
            IMPORTED)
set_target_properties(usb100
                      PROPERTIES IMPORTED_LOCATION
                      ../../../../libs/arm64-v8a/libusb100.so)
include_directories(libs/include/huawei)

target_link_libraries(native-lib
                      arcamera
                      dfu
                      HSLPreProcessing
                      Idepthservice
                      slaccuracy
                      usb100
                      ${log-lib})