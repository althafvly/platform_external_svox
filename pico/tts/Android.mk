LOCAL_PATH := $(call my-dir)

svox_tts_warn_flags := \
    -Wall -Werror \
    -Wno-sign-compare \
    -Wno-unused-const-variable \
    -Wno-unused-parameter \

# Build static library containing all PICO code
# excluding the compatibility code. This is identical
# to the rule below / except that it builds a shared
# library.
include $(CLEAR_VARS)

LOCAL_MODULE := libttspico_engine

LOCAL_CFLAGS := $(svox_tts_warn_flags)

LOCAL_SRC_FILES := \
	com_svox_picottsengine.cpp \
	svox_ssml_parser.cpp

LOCAL_C_INCLUDES += \
	external/svox/pico/lib \
	external/svox/pico/compat/include \
	external/svox/pico/legacyfix/include

LOCAL_STATIC_LIBRARIES := libsvoxpico libcutils_legacyfix

LOCAL_SHARED_LIBRARIES := \
	libcutils \
	libexpat \
	libutils \
	liblog

LOCAL_ARM_MODE := arm

include $(BUILD_STATIC_LIBRARY)


# Build Pico Shared Library. This rule is used by the
# compatibility code, which opens this shared library
# using dlsym. This is essentially the same as the rule
# above, except that it packages things a shared library.
include $(CLEAR_VARS)

LOCAL_MODULE := libjni_ttspico

LOCAL_SYSTEM_EXT_MODULE := true

LOCAL_SRC_FILES := \
	com_svox_picottsengine.cpp \
	svox_ssml_parser.cpp

LOCAL_CFLAGS := $(svox_tts_warn_flags)

LOCAL_C_INCLUDES += \
	external/svox/pico/lib \
	external/svox/pico/compat/include \
	external/svox/pico/legacyfix/include

LOCAL_STATIC_LIBRARIES := libsvoxpico libcutils_legacyfix
LOCAL_SHARED_LIBRARIES := libcutils libexpat libutils liblog

include $(BUILD_SHARED_LIBRARY)

svox_tts_warn_flags :=
