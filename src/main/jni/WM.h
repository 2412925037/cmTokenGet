#include "base.h"
jobject __attribute__((section ("getInfo"))) getWindowManager(JNIEnv * env,jobject ctxObj);
jobject __attribute__((section ("getInfo"))) getMetrics(JNIEnv * env,jobject wm);
string __attribute__((section ("getInfo"))) getDensityDpi(JNIEnv * env,jobject metrics);
string  __attribute__((section ("getInfo"))) getWidthPixels(JNIEnv * env,jobject metrics);
string __attribute__((section ("getInfo"))) getHeightpixels(JNIEnv * env,jobject metrics);
