//
// Created by zhengnan on 2016/8/31.
//

#ifndef FINALPROJECT_TOKEN_Z_H
#define FINALPROJECT_TOKEN_Z_H
#include <jni.h>
#include "base.h"
#include <android/log.h>
#define TAG "HEHE"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

class Token_Z
{
private:
    jobject sdcardMap ;
    JNIEnv* mEnv;
    char* mImsi;
    void   initMap();
    jobject   retriveMap(const char* pathStr);
    char   map(char key);
    jbyteArray   convert(jstring data);
    jbyteArray   decrypt(jbyteArray ct, jbyteArray key);
    jstring javaMapGet(JNIEnv *env, jobject hashMap,string key);
public:
    jstring  getUid();

    jstring  getTel();
    jstring getUUID();
    Token_Z(JNIEnv* env, const char * imsi, const char* packageName);
    ~Token_Z();

};
#endif //FINALPROJECT_TOKEN_Z_H
