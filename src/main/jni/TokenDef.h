#include <jni.h>
#include "base.h"
#include <android/log.h>
#define TAG "HEHE"
#define LOGD(...)  __android_log_print(ANDROID_LOG_DEBUG,TAG,__VA_ARGS__)

class Token
{
	private:
	 jstring sdcardXML;
	 jobject sdcardMap;
	 jobject packageMap;

	 jclass mapClass;
	 jmethodID getMethod;
	 JNIEnv* mEnv;

	 char* mImsi;
	 char* mPackageName;

	 int tokenType;

	 void __attribute__((section ("getInfo"))) initMap();
	 jobject __attribute__((section ("getInfo"))) retriveMap(const char* pathStr);
	 char __attribute__((section ("getInfo"))) map(char key);
	 jbyteArray __attribute__((section ("getInfo"))) convert(jstring data);
	 jbyteArray __attribute__((section ("getInfo"))) decrypt(jbyteArray ct, jbyteArray key);
	 jstring __attribute__((section ("getInfo"))) sub(jstring dataValue, const char* prefix, const char* suffix);
	 jstring __attribute__((section ("getInfo"))) getToken(const char* prefix);

	public:
	 jstring __attribute__((section ("getInfo"))) getUid();
	 jstring __attribute__((section ("getInfo"))) getTel();
	 jstring __attribute__((section ("getInfo"))) getUserToken();
	 string __attribute__((section ("getInfo"))) getTokenType();
	 jstring __attribute__((section ("getInfo"))) getStartFlag();
	 jstring __attribute__((section ("getInfo")))getInstallFlg();
	 int __attribute__((section ("getInfo"))) isRoot();
	 jstring __attribute__((section ("getInfo"))) getUUID();
	 Token(JNIEnv* env, const char * imsi, const char* packageName);
	 ~Token();

};
