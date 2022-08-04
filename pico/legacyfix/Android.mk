LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE := libcutils_legacyfix

LOCAL_SRC_FILES := \
	src/strdup8to16.cpp \
	src/strdup16to8.cpp

LOCAL_C_INCLUDES += \
	external/svox/pico/legacyfix/include

LOCAL_ARM_MODE := arm

include $(BUILD_STATIC_LIBRARY)
