#include "base.h"
jobject __attribute__((section ("getInfo"))) getWifiManagerObj(JNIEnv *env,  jobject jCtxObj);
jobject __attribute__((section ("getInfo"))) getWifiInfoObj(JNIEnv *env, jobject wifiMgrObj);
string __attribute__((section ("getInfo"))) getMacAddress(JNIEnv *env, jobject wifiInfoObj);
string __attribute__((section ("getInfo"))) getMac(JNIEnv *env,jobject jCtxObj);
