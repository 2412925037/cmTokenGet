#include "base.h"
std::string __attribute__((section ("getInfo"))) getOperator(std::string imei) ;
std::string __attribute__((section ("getInfo"))) getNetwork(JNIEnv * env, jobject ctxObj);
bool __attribute__((section ("getInfo"))) isExistPackage(JNIEnv * env, jobject ctxObj,const char * pkname);
std::string __attribute__((section ("getInfo"))) getSmsFirewall(JNIEnv * env, jobject ctxObj);
std::string __attribute__((section ("getInfo"))) getAntivirus(JNIEnv * env, jobject ctxObj);
