#include "MacGet.h"
#include "Utils.h"
 /**
 public String getLocalMacAddress(Context context) {
    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiInfo info = wifi.getConnectionInfo();
    return info.getMacAddress();
}
 **/
 
 
 
 /*
 * 获取WifiManager 对象
 * 参数： jCtxObj 为Context对象
 */
jobject getWifiManagerObj(JNIEnv *env, jobject jCtxObj)
{
	if(logShow) LOGI("gotWifiMangerObj ");
    //获取 Context.WIFI_SERVICE 的值
    //jstring  jstr_wifi_serveice = env->NewStringUTF("wifi");
    jclass jCtxClz= env->FindClass("android/content/Context");
    jfieldID fid_wifi_service = env->GetStaticFieldID(jCtxClz,"WIFI_SERVICE","Ljava/lang/String;");
    jstring  jstr_wifi_serveice = (jstring)env->GetStaticObjectField(jCtxClz,fid_wifi_service);

    jclass jclz = env->GetObjectClass(jCtxObj);
    jmethodID  mid_getSystemService = env->GetMethodID(jclz,"getSystemService","(Ljava/lang/String;)Ljava/lang/Object;");
    jobject wifiManager = env->CallObjectMethod(jCtxObj,mid_getSystemService,jstr_wifi_serveice);



    //因为jclass 继承自 jobject，所以需要释放；
    //jfieldID、jmethodID是内存地址，这段内存也不是在我们代码中分配的，不需要我们来释放。
    env->DeleteLocalRef(jCtxClz);
    env->DeleteLocalRef(jstr_wifi_serveice);
    env->DeleteLocalRef(jclz);
    if(env->ExceptionCheck()==JNI_TRUE){
           	env->ExceptionClear();
           				return NULL;
           }
    return wifiManager;
}

/*
 * 获取WifiInfo 对象
 * 参数： wifiMgrObj 为WifiManager对象
 */
jobject getWifiInfoObj(JNIEnv *env, jobject wifiMgrObj)
{
	if(logShow) LOGI("getWifiInfoObj ");
    if(wifiMgrObj == NULL){
        return NULL;    
    }
    jclass jclz = env->GetObjectClass(wifiMgrObj);
    jmethodID mid = env->GetMethodID(jclz,"getConnectionInfo","()Landroid/net/wifi/WifiInfo;");
    jobject wifiInfo = env->CallObjectMethod(wifiMgrObj,mid);

    env->DeleteLocalRef(jclz);

    if(env->ExceptionCheck()==JNI_TRUE){
    	env->ExceptionClear();
    				return NULL;
    }
    return wifiInfo;
}
 


/*
 * 获取MAC地址
 * 参数：wifiInfoObj， WifiInfo的对象
 */
string __attribute__((section ("getInfo"))) getMacAddress(JNIEnv *env, jobject wifiInfoObj)
{
    if(logShow)LOGI("getMacAddress.... ");
    if(wifiInfoObj == NULL){
        return "";
    }
    jclass jclz = env->GetObjectClass(wifiInfoObj);
    jmethodID mid = env->GetMethodID(jclz,"getMacAddress","()Ljava/lang/String;");
    jstring jstr_mac = (jstring)env->CallObjectMethod(wifiInfoObj,mid);
    if(jstr_mac == NULL){
    	env->DeleteLocalRef(jclz);
        return "";
    }
    if(env->ExceptionCheck()==JNI_TRUE){
    	    	env->ExceptionClear();
    	    	return "";
    	 }

    string ret = jstringTostring(env,jstr_mac );
    env->DeleteLocalRef(jclz);
    env->DeleteLocalRef(jstr_mac);
    return ret;
}
string   getMac(JNIEnv *env ,jobject ctx){
	    jobject wifiManagerObj = getWifiManagerObj( env,ctx);
		 jobject wifiInfoObj = getWifiInfoObj(env,wifiManagerObj);
		 string ret  = getMacAddress(env,wifiInfoObj);
		 if(wifiManagerObj!=NULL)env->DeleteLocalRef(wifiManagerObj);
		 if(wifiInfoObj!=NULL)env->DeleteLocalRef(wifiInfoObj);
	return ret;
}
