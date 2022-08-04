#svox_lang_pack := PicoLangInstallerDeuDeu

LOCAL_PATH:= $(call my-dir)
svox_lang_pack ?= picolanginstaller
include $(LOCAL_PATH)/pico/Android.mk $(LOCAL_PATH)/$(svox_lang_pack)/Android.mk
