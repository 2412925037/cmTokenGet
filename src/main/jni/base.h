#include <sys/system_properties.h>
#include <android/log.h>
#include <jni.h>
#include <stdio.h>
#include <string.h>
#include <iostream>
#include <sstream>//for convert type
#include  <algorithm>
#include <cctype> // for toupper
#include <fcntl.h> //for check file exist
#include <map>
using namespace std;
#define   LOG_TAG    "J_Nice"
#define   LOGI(...)  __android_log_print(ANDROID_LOG_INFO,LOG_TAG,__VA_ARGS__)
#define   LOGE(...)  __android_log_print(ANDROID_LOG_ERROR,LOG_TAG,__VA_ARGS__)
#define  logShow false
