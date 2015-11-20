LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := gpuimage-library
LOCAL_SRC_FILES := yuv-decoder.c
LOCAL_CFLAGS += -O3
LOCAL_LDFLAGS := -Wl,--build-id
LOCAL_LDLIBS := -llog

include $(BUILD_SHARED_LIBRARY)
