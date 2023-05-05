LOCAL_PATH:= $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE := libjni_ttscompat

LOCAL_SDK_VERSION := current
LOCAL_SYSTEM_EXT_MODULE := true

LOCAL_SRC_FILES:= \
	com_android_tts_compat_SynthProxy.cpp

LOCAL_C_INCLUDES += \
	external/svox/pico/legacyfix/include \
	$(JNI_H_INCLUDE)

LOCAL_SHARED_LIBRARIES := \
	liblog

LOCAL_STATIC_LIBRARIES := \
	libnativehelper_compat_libc++ \
    libcutils_legacyfix

LOCAL_CFLAGS := \
    -Wall -Werror \
    -Wno-unused-parameter

LOCAL_NDK_STL_VARIANT := c++_static

include $(BUILD_SHARED_LIBRARY)
