# SVOX Pico TTS Engine
# This makefile builds both an activity and a shared library.

#disable build in PDK
ifneq ($(TARGET_BUILD_PDK),true)

LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_PACKAGE_NAME := PicoTts

LOCAL_SRC_FILES := \
    $(call all-java-files-under, src) \
    $(call all-java-files-under, compat)

LOCAL_JNI_SHARED_LIBRARIES := libjni_ttscompat libjni_ttspico
LOCAL_PROGUARD_FLAG_FILES := proguard.flags

LOCAL_STATIC_ANDROID_LIBRARIES := \
    androidx.appcompat_appcompat \
    androidx.preference_preference

LOCAL_SYSTEM_EXT_MODULE := true
LOCAL_SDK_VERSION := current

LOCAL_MULTILIB := both

include $(BUILD_PACKAGE)

include $(LOCAL_PATH)/compat/jni/Android.mk \
    $(LOCAL_PATH)/legacyfix/Android.mk \
    $(LOCAL_PATH)/lib/Android.mk \
    $(LOCAL_PATH)/tts/Android.mk

endif #TARGET_BUILD_PDK
