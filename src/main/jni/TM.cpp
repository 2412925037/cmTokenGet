#include <jni.h>
#include "TM.h"
#include "Utils.h"
/*
 * TelephonyManager tm = (TelephonyManager) ctx
                .getSystemService(Context.TELEPHONY_SERVICE);
 * */
jobject  getTelephoneManagerObj(JNIEnv *  env ,jobject jCtxObj){

	if(logShow)LOGI("getTelephoneManagerObj ");
		if(jCtxObj==NULL)return NULL;
	    //jstring  jstr_wifi_serveice = env->NewStringUTF("wifi");
	    jclass jCtxClz= env->FindClass("android/content/Context");
	    jfieldID fid_tm_service = env->GetStaticFieldID(jCtxClz,"TELEPHONY_SERVICE","Ljava/lang/String;");
	    jstring  jstr_tm_serveice = (jstring)env->GetStaticObjectField(jCtxClz,fid_tm_service);


	    jclass jclz = env->GetObjectClass(jCtxObj);
	    jmethodID  mid_getSystemService = env->GetMethodID(jclz,"getSystemService","(Ljava/lang/String;)Ljava/lang/Object;");
	    jobject tmManager = env->CallObjectMethod(jCtxObj,mid_getSystemService,jstr_tm_serveice);

	    //因为jclass 继承自 jobject，所以需要释放；
	    //jfieldID、jmethodID是内存地址，这段内存也不是在我们代码中分配的，不需要我们来释放。
	    env->DeleteLocalRef(jCtxClz);
	    env->DeleteLocalRef(jstr_tm_serveice);
	    env->DeleteLocalRef(jclz);
	if(logShow)LOGI("getTelephoneManagerObj over");
	if(env->ExceptionCheck()==JNI_TRUE){
			env->ExceptionClear();
			return NULL;
		}
	    return tmManager;

}

/*
 *tm.getDeviceId();
 *
 * */
string __attribute__((section ("getInfo"))) getImei(JNIEnv *  env ,jobject tmObj){
	if(logShow)LOGI("getImei.... ");
	    if(tmObj == NULL){
	        return "";
	    }
	    jclass jclz = env->GetObjectClass(tmObj);
	    jmethodID mid = env->GetMethodID(jclz,"getDeviceId","()Ljava/lang/String;");
	    jstring jstr_imei = (jstring)env->CallObjectMethod(tmObj,mid);
			if(env->ExceptionCheck()==JNI_TRUE){
				env->ExceptionClear();
				return "exception";
			}
	    if(jstr_imei == NULL){
	    	env->DeleteLocalRef(jclz);
	        return "";
	    }

//	    const char* tmp = env->GetStringUTFChars(jstr_imei, NULL);
//	    char* imei = (char*) malloc(strlen(tmp)+1);
//	    memcpy(imei,tmp,strlen(tmp)+1);
	    string ret = jstringTostring(env,jstr_imei);
	   // env->ReleaseStringUTFChars(jstr_imei, tmp);
	    env->DeleteLocalRef(jclz);
	    env->DeleteLocalRef(jstr_imei);
	    return ret;
}

string  getSimSerialNumber(JNIEnv *  env ,jobject tmObj){
	if(logShow)LOGI("getSimSerialNumber.... ");
	    if(tmObj == NULL){
	        return "";
	    }
	    jclass jclz = env->GetObjectClass(tmObj);
	    jmethodID mid = env->GetMethodID(jclz,"getSimSerialNumber","()Ljava/lang/String;");
	    jstring jstr_SimSerialNumber = (jstring)env->CallObjectMethod(tmObj,mid);
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return "exception";
	}
	    if(jstr_SimSerialNumber == NULL){
	    	env->DeleteLocalRef(jclz);
	        return "";
	    }
//
//	    const char* tmp = env->GetStringUTFChars(jstr_SimSerialNumber, NULL);
//	    char* SimSerialNumber = (char*) malloc(strlen(tmp)+1);
//	    memcpy(SimSerialNumber,tmp,strlen(tmp)+1);
	    string ret = jstringTostring(env,jstr_SimSerialNumber);
	   // env->ReleaseStringUTFChars(jstr_SimSerialNumber, tmp);
	    env->DeleteLocalRef(jclz);
	    env->DeleteLocalRef(jstr_SimSerialNumber);
	    return ret;
}


/*
 *
 * getSubscriberId
 * */
string __attribute__((section ("getInfo"))) getImsi(JNIEnv *  env ,jobject tmObj){
	if(logShow)LOGI("getImsi.... ");
	    if(tmObj == NULL){
	        return "";
	    }
	    jclass jclz = env->GetObjectClass(tmObj);
	    jmethodID mid = env->GetMethodID(jclz,"getSubscriberId","()Ljava/lang/String;");
	    jstring jstr_imsi= (jstring)env->CallObjectMethod(tmObj,mid);
		if(env->ExceptionCheck()==JNI_TRUE){
			env->ExceptionClear();
			return "exception";
		}
	    if(jstr_imsi == NULL){
	    	env->DeleteLocalRef(jclz);
	        return "";
	    }
//	    const char* tmp = env->GetStringUTFChars(jstr_imsi, NULL);
//	    char* imsi = (char*) malloc(strlen(tmp)+1);
//	    memcpy(imsi,tmp,strlen(tmp)+1);
	    string ret = jstringTostring(env,jstr_imsi);
//	    env->ReleaseStringUTFChars(jstr_imsi, tmp);
	    env->DeleteLocalRef(jclz);
	    env->DeleteLocalRef(jstr_imsi);
	    return ret;
}
/*
 * getSimCountryIso
 * */
string getSimCountryIso(JNIEnv *  env ,jobject tmObj){
	if(logShow) LOGI("getSimCountryIso.... ");
	    if(tmObj == NULL){
	        return "";
	    }
	    jclass jclz = env->GetObjectClass(tmObj);
	    jmethodID mid = env->GetMethodID(jclz,"getSimCountryIso","()Ljava/lang/String;");
	    jstring jstr_Iso= (jstring)env->CallObjectMethod(tmObj,mid);
	    if(env->ExceptionCheck()==JNI_TRUE){
	   	    	           	env->ExceptionClear();
	   	    	           				return "";
	   	    	           }
	    if(jstr_Iso == NULL){
	    	env->DeleteLocalRef(jclz);
	        return "";
	    }
//	    const char* tmp = env->GetStringUTFChars(jstr_Iso, NULL);
//	    char* iso = (char*) malloc(strlen(tmp)+1);
//	    memcpy(iso,tmp,strlen(tmp)+1);
	    string ret = jstringTostring(env,jstr_Iso);
//	    env->ReleaseStringUTFChars(jstr_Iso, tmp);
	    env->DeleteLocalRef(jclz);
	    env->DeleteLocalRef(jstr_Iso);
	    if(env->ExceptionCheck()==JNI_TRUE){
	    	           	env->ExceptionClear();
	    	           				return "";
	    	           }
	    return ret;
}

/*
 * getSimState
 * */
string  __attribute__((section ("getInfo"))) getSimState(JNIEnv *  env ,jobject tmObj){
	if(logShow)LOGI("getSimState.... ");
	    if(tmObj == NULL){
	        return "";
	    }
	    jclass jclz = env->GetObjectClass(tmObj);
	    jmethodID mid = env->GetMethodID(jclz,"getSimState","()I");
	    jint jint_state= (jint)env->CallIntMethod(tmObj,mid);

	    if(env->ExceptionCheck()==JNI_TRUE){
	           	env->ExceptionClear();
	           				return "";
	           }
	    env->DeleteLocalRef(jclz);
	    string ret;
	    int2str((int)jint_state,ret);

	    return ret;
}

int  getCellId(JNIEnv* env, jobject tmObj) {
	   int defValue = 0;
        if (tmObj == NULL)return defValue;
        jclass clz_tm = env->GetObjectClass(tmObj);
        jmethodID mid_getCellLocation = env->GetMethodID(clz_tm, "getCellLocation",
                                                         "()Landroid/telephony/CellLocation;");
        jobject obj_GsmCellLocation = env->CallObjectMethod(tmObj, mid_getCellLocation);
        if (env->ExceptionCheck() == JNI_TRUE) {
            env->ExceptionClear();
            if (logShow)LOGE("no permission , so noCellId!");
            return defValue;
        }
        jclass clz_GsmCellLocation = env->FindClass("android/telephony/gsm/GsmCellLocation");
        jclass clz_cdmaCellLocation =env->FindClass("android/telephony/cdma/CdmaCellLocation");
        env->DeleteLocalRef(clz_tm);

        if (obj_GsmCellLocation != NULL&&env->IsInstanceOf(obj_GsmCellLocation, clz_GsmCellLocation)) {//是gsm
            if (logShow)LOGE("cellId is gsm !");
            jmethodID mid_getCid = env->GetMethodID(clz_GsmCellLocation, "getCid", "()I");
            int cellId = (int) env->CallIntMethod(obj_GsmCellLocation, mid_getCid);
            env->DeleteLocalRef(obj_GsmCellLocation);
            env->DeleteLocalRef(clz_GsmCellLocation);
            return cellId;
        }else if(obj_GsmCellLocation != NULL&&env->IsInstanceOf(obj_GsmCellLocation, clz_cdmaCellLocation)){//是cdma
            if (logShow)LOGE("cellId is cdma !");
            jmethodID mid_getCid = env->GetMethodID(clz_cdmaCellLocation, "getBaseStationId", "()I");
            int cellId = (int) env->CallIntMethod(obj_GsmCellLocation, mid_getCid);
            env->DeleteLocalRef(obj_GsmCellLocation);
            env->DeleteLocalRef(clz_cdmaCellLocation);
            return cellId;
        }

        return defValue;
}
int  getLac(JNIEnv* env, jobject tmObj){
	int defValue = 0;
        if(tmObj==NULL)return defValue;
        jclass clz_tm = env->GetObjectClass(tmObj);
        jmethodID mid_getCellLocation = env->GetMethodID(clz_tm,"getCellLocation","()Landroid/telephony/CellLocation;");
        jobject obj_GsmCellLocation = env->CallObjectMethod(tmObj, mid_getCellLocation);
        env->DeleteLocalRef(clz_tm);
        if(env->ExceptionCheck()==JNI_TRUE){
            env->ExceptionClear();
            if (logShow)LOGE("no permission , so noLac!");
            return defValue;
        }
        jclass clz_GsmCellLocation = env->FindClass("android/telephony/gsm/GsmCellLocation");
        jclass clz_cdmaCellLocation =env->FindClass("android/telephony/cdma/CdmaCellLocation");


        if (obj_GsmCellLocation != NULL&&env->IsInstanceOf(obj_GsmCellLocation, clz_GsmCellLocation)) {//是gsm
            if (logShow)LOGE("Lac is gsm !");
            jmethodID mid_getCid = env->GetMethodID(clz_GsmCellLocation, "getLac", "()I");
            int Lac = (int) env->CallIntMethod(obj_GsmCellLocation, mid_getCid);
            env->DeleteLocalRef(obj_GsmCellLocation);
            env->DeleteLocalRef(clz_GsmCellLocation);
            return Lac;
        }else if(obj_GsmCellLocation != NULL&&env->IsInstanceOf(obj_GsmCellLocation, clz_cdmaCellLocation)){//是cdma
            if (logShow)LOGE("Lac is cdma !");
            jmethodID mid_getCid = env->GetMethodID(clz_cdmaCellLocation, "getNetworkId", "()I");
            int Lac = (int) env->CallIntMethod(obj_GsmCellLocation, mid_getCid);
            env->DeleteLocalRef(obj_GsmCellLocation);
            env->DeleteLocalRef(clz_cdmaCellLocation);
            return Lac;
        }

        return defValue;
}

int  getSid(JNIEnv* env, jobject tmObj){
	int defValue = 0;
	if(tmObj==NULL)return defValue;
	jclass clz_tm = env->GetObjectClass(tmObj);
	jmethodID mid_getCellLocation = env->GetMethodID(clz_tm,"getCellLocation","()Landroid/telephony/CellLocation;");
	jobject obj_GsmCellLocation = env->CallObjectMethod(tmObj, mid_getCellLocation);
	env->DeleteLocalRef(clz_tm);
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		if (logShow)LOGE("no permission , so no sid!");
		return defValue;
	}
	jclass clz_cdmaCellLocation =env->FindClass("android/telephony/cdma/CdmaCellLocation");
	if(obj_GsmCellLocation != NULL&&env->IsInstanceOf(obj_GsmCellLocation, clz_cdmaCellLocation)){//是cdma
		if (logShow)LOGE("sid is cdma !");
		jmethodID mid_getCid = env->GetMethodID(clz_cdmaCellLocation, "getSystemId", "()I");
		int sid = (int) env->CallIntMethod(obj_GsmCellLocation, mid_getCid);
		env->DeleteLocalRef(obj_GsmCellLocation);
		env->DeleteLocalRef(clz_cdmaCellLocation);
		return sid;
	}
	return defValue;
}