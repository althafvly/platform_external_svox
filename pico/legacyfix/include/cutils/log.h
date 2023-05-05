#pragma once
#include <assert.h>
#include <nativehelper/JNIHelp.h>
#include <android/log.h>
/*
 * This file uses ", ## __VA_ARGS__" zero-argument token pasting to
 * work around issues with debug-only syntax errors in assertions
 * that are missing format strings.  See commit
 * 19299904343daf191267564fe32e6cd5c165cd42
 */
#if defined(__clang__)
#pragma clang diagnostic push
#pragma clang diagnostic ignored "-Wgnu-zero-variadic-macro-arguments"
#endif

/*
 * Use __VA_ARGS__ if running a static analyzer,
 * to avoid warnings of unused variables in __VA_ARGS__.
 * Use constexpr function in C++ mode, so these macros can be used
 * in other constexpr functions without warning.
 */
#ifdef __clang_analyzer__
#ifdef __cplusplus
extern "C++" {
template <typename... Ts>
constexpr int __fake_use_va_args(Ts...) {
  return 0;
}
}
#else
extern int __fake_use_va_args(int, ...);
#endif /* __cplusplus */
#define __FAKE_USE_VA_ARGS(...) ((void)__fake_use_va_args(0, ##__VA_ARGS__))
#else
#define __FAKE_USE_VA_ARGS(...) ((void)(0))
#endif /* __clang_analyzer__ */

#ifndef __predict_false
#define __predict_false(exp) __builtin_expect((exp) != 0, 0)
#endif

#define ALOG __android_log_print
#define LOG_EX(env, priority, tag, ...) \
    jniLogException(env, priority, tag, ##__VA_ARGS__)
#ifndef ALOGV
#define __ALOGV(...) ((void)ALOG(ANDROID_LOG_VERBOSE, LOG_TAG, __VA_ARGS__))
#if LOG_NDEBUG
#define ALOGV(...)                   \
  do {                               \
    __FAKE_USE_VA_ARGS(__VA_ARGS__); \
    if (false) {                     \
      __ALOGV(__VA_ARGS__);          \
    }                                \
  } while (false)
#else
#define ALOGV(...) __ALOGV(__VA_ARGS__)
#endif
#endif
#ifndef ALOGE
#define ALOGE(...) ((void)ALOG(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__))
#endif
#ifndef ALOGI
#define ALOGI(...) ((void)ALOG(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__))
#endif
#ifndef ALOGE_IF
#define ALOGE_IF(cond, ...)                                                             \
  ((__predict_false(cond))                                                              \
       ? (__FAKE_USE_VA_ARGS(__VA_ARGS__), (void)ALOG(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)) \
       : ((void)0))
#define LOGE_EX(env, ...) LOG_EX(env, ANDROID_LOG_ERROR, LOG_TAG, ##__VA_ARGS__)
#endif

