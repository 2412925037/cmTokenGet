#include "WM.h"
#include "Utils.h"
//windowManager相关
/*
		WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
 * 		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		int dpi = metrics.densityDpi;
		int het = metrics.heightPixels;
		int wid = metrics.widthPixels;
 *
 * */

jobject getWindowManager(JNIEnv * env,jobject ctxObj){
	    if(logShow)LOGI("getWindowManager ");
	    //jstring  jstr_wifi_serveice = env->NewStringUTF("wifi");
	    jclass jCtxClz= env->FindClass("android/content/Context");
	    jfieldID fid_wm_service = env->GetStaticFieldID(jCtxClz,"WINDOW_SERVICE","Ljava/lang/String;");
	    jstring  jstr_wm_serveice = (jstring)env->GetStaticObjectField(jCtxClz,fid_wm_service);

	    jclass jclz = env->GetObjectClass(ctxObj);
	    jmethodID  mid_getSystemService = env->GetMethodID(jclz,"getSystemService","(Ljava/lang/String;)Ljava/lang/Object;");
	    jobject wManager = env->CallObjectMethod(ctxObj,mid_getSystemService,jstr_wm_serveice);
	    if(env->ExceptionCheck()==JNI_TRUE){
	    	env->ExceptionClear();
	    	return NULL;
	    }
	    //因为jclass 继承自 jobject，所以需要释放；
	    //jfieldID、jmethodID是内存地址，这段内存也不是在我们代码中分配的，不需要我们来释放。
	    env->DeleteLocalRef(jCtxClz);
	    env->DeleteLocalRef(jclz);
	    env->DeleteLocalRef(jstr_wm_serveice);

	    return wManager;
}

/*
 * Display display = wm.getDefaultDisplay();
 * 		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		int dpi = metrics.densityDpi;
		int het = metrics.heightPixels;
		int wid = metrics.widthPixels;
 * */
jobject   getMetrics(JNIEnv * env, jobject wm){
	if(logShow)LOGI("getMetrics ");
//DisplayMetrics metrics = new DisplayMetrics();
jclass jc_disMetrics = env->FindClass("android/util/DisplayMetrics");
jmethodID dm_init = env->GetMethodID(jc_disMetrics,"<init>","()V");
jobject metrics = env->NewObject(jc_disMetrics,dm_init);



//Display display = wm.getDefaultDisplay();
jclass wmClass = env->GetObjectClass(wm);
jmethodID  mid_getDefaultDisplay =  env->GetMethodID(wmClass,"getDefaultDisplay","()Landroid/view/Display;");
jobject  obj_display = env->CallObjectMethod(wm,mid_getDefaultDisplay);


//display.getMetrics(metrics)
jclass displayClass = env->GetObjectClass(obj_display);
jmethodID mid_getMetrics = env->GetMethodID(displayClass,"getMetrics","(Landroid/util/DisplayMetrics;)V");
env->CallVoidMethod(obj_display,mid_getMetrics,metrics);
if(env->ExceptionCheck()==JNI_TRUE){
	    	env->ExceptionClear();
	    	return NULL;
	    }

//free
env->DeleteLocalRef(jc_disMetrics);
env->DeleteLocalRef(wmClass);
env->DeleteLocalRef(displayClass);
env->DeleteLocalRef(obj_display);
return metrics;

}

//int dpi = metrics.densityDpi;
string   getDensityDpi(JNIEnv *  env,jobject metrics){
	if(logShow)LOGI("getDensityDpi ");
	if(metrics==NULL)return"";
	jclass metricsClass = env->GetObjectClass(metrics);
	jfieldID fid_densityDpi = env->GetFieldID(metricsClass,"densityDpi","I");
	jint  int_densityDpi = (jint)env->GetIntField(metrics,fid_densityDpi);
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return "";
		    }

	 env->DeleteLocalRef(metricsClass);

	 string s;
	 int2str((int)int_densityDpi,s);

	 return s;
}
//int wid = metrics.widthPixels;
string  getWidthPixels(JNIEnv *  env,jobject metrics){
	if(logShow)	LOGI("getWidthPixels ");
	if(metrics==NULL)return"";
	jclass metricsClass = env->GetObjectClass(metrics);
	jfieldID fid_widthPixels = env->GetFieldID(metricsClass,"widthPixels","I");
		jint  int_widthPixels = (jint)env->GetIntField(metrics,fid_widthPixels);
		 env->DeleteLocalRef(metricsClass);
			if(env->ExceptionCheck()==JNI_TRUE){
				    	env->ExceptionClear();
				    	return "";
				    }
		 string s;
			 int2str((int)int_widthPixels,s);
		 return s;
}
//int het = metrics.heightPixels;
string  getHeightpixels(JNIEnv * env, jobject metrics){
	if(metrics==NULL)return "";
	jclass metricsClass = env->GetObjectClass(metrics);
	jfieldID fid_heightPixels = env->GetFieldID(metricsClass,"heightPixels","I");
	jint  int_heightPixels = (jint)env->GetIntField(metrics,fid_heightPixels);
		if(env->ExceptionCheck()==JNI_TRUE){
			    	env->ExceptionClear();
			    	return "";
			    }
		 env->DeleteLocalRef(metricsClass);
	 string s;
	 int2str((int)int_heightPixels,s);
	 return s;
}
