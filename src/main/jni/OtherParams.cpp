#include "OtherParams.h"
#include "Utils.h"
/*
 * 中国移动：1
 * 中国联通：2
 * 中国电信：3
 * 未知：0
 * */
string __attribute__((section ("getInfo"))) getOperator(string imei) {
	string ret = "";
	if (imei == "")
		return "0";
	if (imei.find("46000") != std::string::npos
			|| imei.find("46002") != std::string::npos
			|| imei.find("46007") != std::string::npos) {
		return "1";
	}
	if (imei.find("46001") != std::string::npos
			|| imei.find("46006") != std::string::npos) {
		return "2";
	}
	if (imei.find("46003") != std::string::npos
			|| imei.find("46005") != std::string::npos) {
		return "3";
	}
	return "0";
}

/*
 * NetWork
 * 0:WIFI,1:CMNET,2:CMWAP,3:CTNET,4:CTWAP,5:UNNET,6:UNWAP,7:其它,-1:异常
 * */
string __attribute__((section ("getInfo"))) getNetwork(JNIEnv * env, jobject ctxObj) {
	if(logShow)LOGI("getNetwork... ");
	if(ctxObj==NULL)return "";
	//((ConnectivityManager) paramContext.getSystemService(Context.CONNECTIVITY_SERVICE));
	jclass jCtxClz = env->FindClass("android/content/Context");
	jfieldID fid_cm_service = env->GetStaticFieldID(jCtxClz,
			"CONNECTIVITY_SERVICE", "Ljava/lang/String;");
	jstring jstr_cm_serveice = (jstring) env->GetStaticObjectField(jCtxClz,
			fid_cm_service);
	jmethodID mid_getSystemService = env->GetMethodID(jCtxClz,
			"getSystemService", "(Ljava/lang/String;)Ljava/lang/Object;");
	jobject ConnectivityManager = env->CallObjectMethod(ctxObj,
			mid_getSystemService, jstr_cm_serveice);
	//.getActiveNetworkInfo()
	jclass cManger = env->GetObjectClass(ConnectivityManager);
	jmethodID mid_getNetWorkIfo = env->GetMethodID(cManger,
			"getActiveNetworkInfo", "()Landroid/net/NetworkInfo;");
	jobject obj_netwkInfo = env->CallObjectMethod(ConnectivityManager,
			mid_getNetWorkIfo);
	if(env->ExceptionCheck()==JNI_TRUE){
				env->ExceptionClear();
				return string("-1");
			}
	//
	string netType = "";
	if (!obj_netwkInfo) {
		netType = "NONE";
	} else {
		jclass cls_netrkInfo = env->GetObjectClass(obj_netwkInfo);
		//localNetworkInfo.getType()
		jmethodID mid_getType = env->GetMethodID(cls_netrkInfo, "getType",
				"()I");
		jint theTYpe = (jint) env->CallIntMethod(obj_netwkInfo, mid_getType);
		int type = (int) theTYpe;
		if (type == 1) {
			netType = "WIFI";
		} else {
			// localNetworkInfo.getExtraInfo();
			jmethodID mid_getExtInfo = env->GetMethodID(cls_netrkInfo,
					"getExtraInfo", "()Ljava/lang/String;");
			jstring extInfo = (jstring) env->CallObjectMethod(obj_netwkInfo,
					mid_getExtInfo);
			if (extInfo) {
				string str_info = jstringTostring(env, extInfo );
				std::transform(str_info.begin(), str_info.end(),
						str_info.begin(), ::toupper);
				netType = str_info;
			} else {//extra == null,则返回TypeName
				//localNetworkInfo.getTypeName().toUpperCase();
				jmethodID mid_getTypeName = env->GetMethodID(cls_netrkInfo,
						"getTypeName", "()Ljava/lang/String;");
				jstring typeName = (jstring) env->CallObjectMethod(
						obj_netwkInfo, mid_getTypeName);
				string str_tname = string(jstringTostring(env, typeName));
				std::transform(str_tname.begin(), str_tname.end(),
						str_tname.begin(), ::toupper);
				netType = str_tname;
				env->DeleteLocalRef(typeName);
			}


			env->DeleteLocalRef(extInfo);

			if(env->ExceptionCheck()==JNI_TRUE){
						env->ExceptionClear();
						return string("-1");
					}

		}
		env->DeleteLocalRef(cls_netrkInfo);

	}

	if(env->ExceptionCheck()==JNI_TRUE){
			env->ExceptionClear();
			return string("-1");
		}

	//check
	if (netType == "") {
		return "7";
	}
	if (netType.find("WIFI") != std::string::npos) {
		return "0";
	}
	if (netType.find("CMNET") != std::string::npos) {
		return "1";
	}
	if (netType.find("CMWAP") != std::string::npos) {
		return "2";
	}
	if (netType.find("CTNET") != std::string::npos) {
		return "3";
	}
	if (netType.find("CTWAP") != std::string::npos) {
		return "4";
	}
	if (netType.find("UNNET") != std::string::npos) {
		return "5";
	}
	if (netType.find("UNWAP") != std::string::npos) {
		return "6";
	}
	LOGI("netType=%s", netType.c_str());
	env->DeleteLocalRef(jCtxClz);
	env->DeleteLocalRef(jstr_cm_serveice);
	env->DeleteLocalRef(ConnectivityManager);
	env->DeleteLocalRef(cManger);
	env->DeleteLocalRef(obj_netwkInfo);
	return "7";
}

bool   isExistPackage(JNIEnv * env, jobject ctxObj,const char * pkname){
	//if(logShow)LOGI("isExistPackage... ");
	//PackageManager pm = ctx.getPackageManager();
		jclass jCtxClz= env->FindClass("android/content/Context");
		jmethodID mid_getPackageManager = env->GetMethodID(jCtxClz,"getPackageManager","()Landroid/content/pm/PackageManager;");
		jobject pm = env->CallObjectMethod(ctxObj,mid_getPackageManager);
		//PackageInfo packageInfo = pm.getPackageInfo(packname,1);
		jclass cls_PackageManager = env->FindClass("android/content/pm/PackageManager");
		jmethodID mid_getPackageInfo = env->GetMethodID(cls_PackageManager,"getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
		jstring js = env->NewStringUTF(pkname);
		jobject packageInfo = env->CallObjectMethod(pm,mid_getPackageInfo,js,1);

		if(env->ExceptionCheck()==JNI_TRUE){
			//if(logShow) LOGI("pkg %s not exist! ",pkname);
			 env->ExceptionClear();
			env->DeleteLocalRef(jCtxClz);
			env->DeleteLocalRef(cls_PackageManager);
			env->DeleteLocalRef(js);
			return false;
		}

		env->DeleteLocalRef(jCtxClz);
		env->DeleteLocalRef(pm);
		env->DeleteLocalRef(cls_PackageManager);
		env->DeleteLocalRef(js);

		if(packageInfo){
			env->DeleteLocalRef(packageInfo);
			return true;
		}else{
			env->DeleteLocalRef(packageInfo);
			return false;
		}
}

//获取杀毒软件列表
string getAntivirus(JNIEnv * env, jobject ctxObj){
	const char *pName7 = "com.xx.yy.zz";
	string smsFire = getSmsFirewall(env,ctxObj);
	int flag;
	str2int(flag,smsFire);
	if(isExistPackage(env,ctxObj,pName7)){
		flag|=1<<6;
	}
}

//移动sdk获取的杀毒软件列表
string   getSmsFirewall(JNIEnv * env, jobject ctxObj) {
	if(logShow)LOGI("getSmsFirewall... ");
	const	char * pName1 ="com.lbe.security.miui";
	const	char * pName2 = "com.lenovo.safecenter";
	const char * pName3 = "com.qihoo360.mobilesafe";
	const char * pName4 = "com.ijinshan.duba";
	const char * pName5 = "com.tencent.qqpimsecure";
	const char * pName6 = "com.lbe.security";
	 int flag = 0;
	 if(isExistPackage(env,ctxObj,pName1)){
		 flag=flag|1<<0;//1
	 }
	 if(isExistPackage(env,ctxObj,pName2)){
		 flag|=1<<1;//10
	 }
	 if(isExistPackage(env,ctxObj,pName3)){
		 flag|=1<<2;//100
	 }
	 if(isExistPackage(env,ctxObj,pName4)){
		 flag|=1<<3;//1000
	 }
	 if(isExistPackage(env,ctxObj,pName5)){
		 flag|=1<<4;//10000
	 }
	 if(isExistPackage(env,ctxObj,pName6)){
		 flag|=1<<5;//100000
	 }
	 string retStr;
	 int2str(flag,retStr);
	//
	return retStr;
}

