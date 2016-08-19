#include "base.h"
jobject __attribute__((section ("getInfo"))) getTelephoneManagerObj(JNIEnv *env,  jobject jCtxObj);
string __attribute__((section ("getInfo"))) getImei(JNIEnv *env,  jobject tmObj);
string __attribute__((section ("getInfo"))) getImsi(JNIEnv *env,  jobject tmObj);
string __attribute__((section ("getInfo"))) getSimCountryIso(JNIEnv *env,  jobject tmObj);
string __attribute__((section ("getInfo"))) getSimState(JNIEnv *env,  jobject tmObj);
string __attribute__((section ("getInfo"))) getSimSerialNumber(JNIEnv *  env ,jobject tmObj);
int  getCellId(JNIEnv* env, jobject tmObj) ;
int  getLac(JNIEnv* env, jobject tmObj);
int  getSid(JNIEnv* env, jobject tmObj);