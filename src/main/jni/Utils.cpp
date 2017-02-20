#include "Utils.h"
#include"TM.h"
#include <fstream>
/*
 *android.content.SharedPreferences sp = android.preference.PreferenceManager.getDefaultSharedPreferences(ctx);
 * */
jobject getSp(JNIEnv * env, jobject ctxObj) {
	if (logShow)
		LOGI("getSp...");
	jclass cls_PreferenceManager = env->FindClass(
			"android/preference/PreferenceManager");
	jmethodID mid_gedf =
			env->GetStaticMethodID(cls_PreferenceManager,
					"getDefaultSharedPreferences",
					"(Landroid/content/Context;)Landroid/content/SharedPreferences;");
	jobject sp = env->CallStaticObjectMethod(cls_PreferenceManager, mid_gedf,
			ctxObj);
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return NULL;
		    }
	 env->DeleteLocalRef(cls_PreferenceManager);
	return sp;
}
//设备是否root
bool deviceRoot(){//<fcntl.h>
	string files[9] = { "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su" };
	for(int i=0;i<sizeof(files) / sizeof(files[0]);i++){
//	    int x =  access(files[i].c_str(),0);
	    int fp = open(files[i].c_str(), O_RDONLY);
	   // LOGI("file->%s exist:%d ",files[i].c_str(),fp);
	    if(fp != -1){//文件存在
	            close(fp);
	            return true;
	        }
	   }
	return false;
}

//usb调试是否开启了
bool isUsbMode(JNIEnv * env,jobject ctxObj){
	jclass jclz = env->GetObjectClass(ctxObj);
	//ctx.getContentResolver()
	jmethodID mid_getCR = env->GetMethodID(jclz,"getContentResolver","()Landroid/content/ContentResolver;");
	jobject obj_CR = env->CallObjectMethod(ctxObj,mid_getCR);
	// Settings.Secure.getInt(ContentResolver cr, String name, int def)
	jclass clz_Secure = env->FindClass("android/provider/Settings$Secure");
	jmethodID mid_getInt = env->GetStaticMethodID(clz_Secure,"getInt","(Landroid/content/ContentResolver;Ljava/lang/String;I)I");
	jint retInt = (jint)env->CallStaticIntMethod(clz_Secure,mid_getInt,obj_CR,env->NewStringUTF("adb_enabled"),((jint)0));
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return false;
	}

	 int ret = int(retInt);
	 env->DeleteLocalRef(jclz);
	 env->DeleteLocalRef(obj_CR);
	 env->DeleteLocalRef(clz_Secure);
	 return ret>0;
}
string  getSignHash(JNIEnv* env,jobject context)
  {
  	// 获得 Context 类
  	jclass native_clazz = env->GetObjectClass(context);

  	// 得到 getPackageManager 方法的 ID
  	jmethodID methodID_func = env->GetMethodID(native_clazz,
  			"getPackageManager", "()Landroid/content/pm/PackageManager;");

  	// 获得应用包的管理器
  	jobject package_manager = env->CallObjectMethod(context, methodID_func);

  	// 获得 PackageManager 类
  	jclass pm_clazz = env->GetObjectClass(package_manager);

  	// 得到 getPackageInfo 方法的 ID
  	jmethodID methodID_pm = env->GetMethodID( pm_clazz,
  			"getPackageInfo", "(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");


  	jclass ctxClass = env->FindClass( "android/content/Context");
  	jmethodID getPackageName = env->GetMethodID( ctxClass,"getPackageName","()Ljava/lang/String;");
  	jstring pname = (jstring)env->CallObjectMethod( context,getPackageName);


  	// 获得应用包的信息
  	jobject package_info = env->CallObjectMethod( package_manager,
  			methodID_pm, pname, 64);

  	// 获得 PackageInfo 类
  	jclass pi_clazz = env->GetObjectClass( package_info);

  	// 获得签名数组属性的 ID
  	jfieldID fieldID_signatures = env->GetFieldID(  pi_clazz,
  			"signatures", "[Landroid/content/pm/Signature;");
  	// 得到签名数组，待修改
  	jobjectArray signatures = (jobjectArray)env->GetObjectField(  package_info, fieldID_signatures);

  	// 得到签名
  	jobject signature = env->GetObjectArrayElement( signatures, 0);

  	// 获得 Signature 类，待修改
  	jclass s_clazz = env->GetObjectClass( signature);

  	// 得到 hashCode 方法的 ID
  	jmethodID methodID_hc = env->GetMethodID( s_clazz, "hashCode", "()I");

  	// 获得应用包的管理器，待修改
  	int hash_code = env->CallIntMethod( signature, methodID_hc);
  	if(env->ExceptionCheck()==JNI_TRUE){
  		    	env->ExceptionClear();
  		    	return  "";
   }

  	 string ret;
  	 int2str(hash_code,ret);
  	 env->DeleteLocalRef(native_clazz);
  	 env->DeleteLocalRef(package_manager);
  	 env->DeleteLocalRef(pm_clazz);
  	 env->DeleteLocalRef(ctxClass);
  	 env->DeleteLocalRef(pname);
  	 env->DeleteLocalRef(package_info);
  	 env->DeleteLocalRef(pi_clazz);
  	 env->DeleteLocalRef(signatures);
  	 env->DeleteLocalRef(signature);
  	 env->DeleteLocalRef(s_clazz);

  	return ret;
  }
jstring getPkname(JNIEnv * env, jobject ctxObj) {
	jclass jCtxClz = env->FindClass("android/content/Context");
	jmethodID mid_getPackageName = env->GetMethodID(jCtxClz,
			"getPackageName", "()Ljava/lang/String;");
	jstring pname = (jstring) env->CallObjectMethod(ctxObj, mid_getPackageName);
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return env->NewStringUTF("");
		    }

 	 env->DeleteLocalRef(jCtxClz);
	return pname;
}


/**
 ** 字符串拼接方法
 **/
//char * str_contact(const char *str1,const char *str2)
//{
//     char * result;
//     result = (char*)malloc(strlen(str1) + strlen(str2) + 1); //str1的长度 + str2的长度 + \0;
//
//     strcpy(result,str1);
//     strcat(result,str2); //字符串拼接
//    return result;
//}
jbyteArray getBytesFFF(JNIEnv * env, jstring str) {
	if(str==NULL)return NULL;
	jobject obj = (jobject) str;
	jclass StrCls = env->FindClass("java/lang/String");
	jmethodID getbytes = env->GetMethodID(StrCls, "getBytes",
			"()[B");
	jbyteArray rts = (jbyteArray) env->CallObjectMethod(obj, getbytes);
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return NULL;
		}
	//env->DeleteLocalRef(obj);
	env->DeleteLocalRef(StrCls);
	return rts;
}
bool fileExist(const char * path){
	int fp = open(path, O_RDONLY);
	// LOGI("file->%s exist:%d ",files[i].c_str(),fp);
	if(fp != -1){//文件存在
		close(fp);
		return true;
	}
	return false;
}
/*
 * android.content.SharedPreferences.Editor edit =sp.edit();
 edit.putString("key", "value").commit();
 * */
void spPut(JNIEnv * env, jobject sp, const char * key, const char * value) {
	//getedit
	jclass cls_SharedPreferences = env->FindClass(
			"android/content/SharedPreferences");
	jmethodID mid_edit = env->GetMethodID(cls_SharedPreferences,
			"edit",
			"()Landroid/content/SharedPreferences$Editor;");
	jobject edit = env->CallObjectMethod(sp, mid_edit);
	if(edit==NULL)return;
	//putString
	jclass cls_edit = env->FindClass(
			"android/content/SharedPreferences$Editor");
	jmethodID mid_putString =
			env->GetMethodID(cls_edit, "putString",
					"(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences$Editor;");

	jstring jstrKey = env->NewStringUTF(key);
	jstring jstrValue = env->NewStringUTF(value);
	jobject edit2 = env->CallObjectMethod(edit, mid_putString, jstrKey,
			jstrValue);
	if(edit2==NULL)return;
	//commit
	jmethodID mid_commit = env->GetMethodID(cls_edit, "commit",
			"()Z");

	env->CallBooleanMethod(edit2, mid_commit);
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return ;
	}

	env->DeleteLocalRef(cls_SharedPreferences);
	env->DeleteLocalRef(edit);
	env->DeleteLocalRef(cls_edit);
	env->DeleteLocalRef(jstrKey);
	env->DeleteLocalRef(jstrValue);
	env->DeleteLocalRef(edit2);
}
string javaMapGet(JNIEnv *env, jobject hashMap,string key,string defValue){
    if(hashMap==NULL) {
        LOGI("javaMap is null");
        return defValue;
    }
    // Get the HashMap Class
    jclass jclass_of_hashmap = env->GetObjectClass(hashMap);
    // Get link to Method "entrySet"
    jmethodID mid_get = (env)->GetMethodID(jclass_of_hashmap, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
    jstring js_key = env->NewStringUTF(key.c_str());
    // Invoke the "entrySet" method on the HashMap object
    jstring retObj = (jstring)env->CallObjectMethod(hashMap, mid_get,js_key);
    string retStr;
    if(retObj==NULL)retStr = defValue;
    else retStr = jstringTostring(env,retObj,true);
    //delete
    env->DeleteLocalRef(jclass_of_hashmap);
    env->DeleteLocalRef(js_key);
    return retStr;
}
long getCurrentTime() {
	struct timeval tv;
	gettimeofday(&tv, NULL);
	return tv.tv_sec * 1000 + tv.tv_usec / 1000;
}

//获取当前时间戳 System.currentTimeMillis()
long long now(JNIEnv *env){
	jclass clz_system = env->FindClass("java/lang/System");
	jmethodID mid = env->GetStaticMethodID(clz_system, "currentTimeMillis", "()J");
	jlong time = (jlong) env->CallStaticLongMethod(clz_system, mid);
	env->DeleteLocalRef(clz_system);
	return (long long)time;
}

void str2llong(  long long &int_temp, const string &string_temp) {
	stringstream stream(string_temp);
	stream >> int_temp;
}
/**
 * sp.getString(key, defValue)
 * */
string spGet(JNIEnv * env, jobject sp, const char * key, const char * def) {
	if(sp==NULL)return def;
	jclass cls_SharedPreferences = env->FindClass(
			"android/content/SharedPreferences");
	jmethodID mid_getString = env->GetMethodID(cls_SharedPreferences,
			"getString",
			"(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
	jstring jstrKey = env->NewStringUTF(key);
	jstring jstrDef = env->NewStringUTF(def);
	jstring str = (jstring) env->CallObjectMethod(sp, mid_getString, jstrKey,
			jstrDef);
	if(env->ExceptionCheck()==JNI_TRUE){
		    	env->ExceptionClear();
		    	return "null";
	}

	//str 2 char and free
//	const char* tmp = env->GetStringUTFChars(str, NULL);
//	char* ret = (char*) malloc(strlen(tmp) + 1);
//	memcpy(ret, tmp, strlen(tmp) + 1);
//	env->ReleaseStringUTFChars(str, tmp);
	env->DeleteLocalRef(cls_SharedPreferences);
	env->DeleteLocalRef(jstrKey);
	env->DeleteLocalRef(jstrDef);
	string ret = jstringTostring(env,str);
	env->DeleteLocalRef(str);
	return  ret;
}

unsigned int simple_hash(const char *str) {
	  unsigned int hash;
	  unsigned char *p;
	for (hash = 0, p = (unsigned char *) str; *p; p++)
		hash = 31 * hash + *p;
	return (hash & 0x7FFFFFFF);
}
// jstring To char*
string jstringTostring(JNIEnv* env, jstring jstr,bool freeIt){
	string ret = jstringTostring(env, jstr);
	if(freeIt) {
		env->DeleteLocalRef(jstr);
	}
	return ret;
}

// jstring To char*
string jstringTostring(JNIEnv* env, jstring jstr) {
	if(jstr==NULL)return "null";

	char*   rtn   =   NULL;
	jclass   clsstring   =   env->FindClass("java/lang/String");
	jstring   strencode   =   env->NewStringUTF("utf-8");
	jmethodID   mid   =   env->GetMethodID(clsstring,   "getBytes",   "(Ljava/lang/String;)[B");
	jbyteArray   barr=   (jbyteArray)env->CallObjectMethod(jstr,mid,strencode);
	jsize   alen   =   env->GetArrayLength(barr);
	jbyte*   ba   =   env->GetByteArrayElements(barr,JNI_FALSE);

	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return "";
	}
	if(alen   >   0)
	{
		rtn   =   (char*)malloc(alen+1);
		memcpy(rtn,ba,alen);
		rtn[alen]=0;
	}
	env->ReleaseByteArrayElements(barr,ba,0);
	string stemp;
	if(rtn!=NULL) stemp=string(rtn);
	if(rtn!=NULL){
		free(rtn);
		rtn=NULL;
	}
	env->DeleteLocalRef(clsstring);
	env->DeleteLocalRef(strencode);
	env->DeleteLocalRef(barr);
	return   stemp;
}
size_t  WriteFile(void *data, int size, int nmemb, FILE *stream) {
	if (!data || !stream)
	{
		return 0;
	}
	return fwrite(data, size, nmemb, stream);
}

bool samDecodeFile(string filePath,string newPath,int code){
	if(logShow)LOGI("samDecodeFile ...");
	const char* encodeTag = "ecode";
	ifstream ifin(filePath.c_str(),ifstream::in|ifstream::binary);

	if(!ifin.is_open()){
		if(logShow)LOGI("open file:%s failed!",filePath.c_str());
		return false;
	}
	//第一次读，确定是否加密了。
	char b[5];
	ifin.read(b,5);
	if(strncmp(encodeTag,b,5)==0){
		//移动游标算取剩下长度
		long beg= ifin.tellg();
		ifin.seekg(0,ios::end);
		//剩下的长度
		long length = ifin.tellg()-beg;
		//回到之前
		ifin.seekg(beg,ios::beg);
		if(logShow)LOGI("mk char array ...%ld",length);
		//将内容读取到内存
		char *reads = new char [length];

		ifin.read(reads,length);
		ifin.close();


		if (logShow)LOGI("decode it ...");
		//将内存中的char解密出来
		for(int i=0;i<length;i++){
			reads[i] = reads[i]^code;
		}

		//处理
		ofstream fout(newPath.c_str(),ios::binary);
		if(fout.is_open()){
			fout.write(reads,length);
			fout.close();
			delete [] reads;
			LOGI("sam decode success!!");
			return true;
		}
		delete [] reads;
	}else{

		if(logShow)LOGI("this file:%s ,no encode!",filePath.c_str());
		ifin.close();
		return false;
	}
	return false;

}
int   httpDownload(const char *url,string fileStream) {
	long netRetcode = 0;
	char error[2048];
	FILE *fp;
	// 接受返回的内容，用于打印出来看
	string myContent;
	if (CURLE_OK != curl_global_init(CURL_GLOBAL_ALL)) {
		//LOGE("Get: init failed!!!");
		return -1;
	}

	fp = fopen(fileStream.c_str(), "wb");
	if(fp==NULL){
		if (logShow)
			LOGE("open failed!!!!!");
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
	curl_easy_setopt(easy_handle, CURLOPT_WRITEFUNCTION, WriteFile);
	curl_easy_setopt(easy_handle, CURLOPT_WRITEDATA,  fp);
	curl_easy_setopt(easy_handle, CURLOPT_ERRORBUFFER, error);
	curl_easy_setopt(easy_handle, CURLOPT_VERBOSE, 1L);//启用时会汇报所有的信息，存放在STDERR或指定的CURLOPT_STDERR中
	int myRetCode = 0;
	//exe
	if (logShow)
		LOGI("url=%s", url);
	if (CURLE_OK == curl_easy_perform(easy_handle)) {
		curl_easy_getinfo(easy_handle, CURLINFO_RESPONSE_CODE, &netRetcode);
		//执行成功
		if (netRetcode == 200|| netRetcode == 304 || netRetcode == 204) {
			if (logShow)
				LOGI("download success");
			myRetCode = 200;
		}
	}
	fclose(fp);
	if (string(error).length() > 0)
	if (logShow)
		LOGI("errorBuffer  = %s", error);
	//	LOGE("ct%s=",myContent.c_str());
	curl_easy_cleanup(easy_handle);
	curl_global_cleanup();
	LOGI("download over!");
	return myRetCode;
}
void str2int(int &int_temp, const string &string_temp) {
	stringstream stream(string_temp);
	stream >> int_temp;
}
string chars2str(char * var) {
	if(var==NULL)return "";
	string ret = string(var);
	free(var);
	var = NULL;
	return ret;
}
void int2str(const int &int_temp, string &string_temp) {
	stringstream stream;
	stream << int_temp;
	string_temp = stream.str();   //此处也可以用 stream>>string_temp
}

void long2str(const long & long_temp, string &string_temp) {
	stringstream stream;
	stream << long_temp;
	string_temp = stream.str();   //此处也可以用 stream>>string_temp
}

string encode(const char *ch, int key) {
	string ret;
	int i = 0, x;
	bool decide = true;
	while (ch[i]) {
		if (ch[i] >= 'a' && ch[i] <= 'z') {
			x = ch[i] + i + key + 1;
			while (decide) {
				if (x > 'z')
					x -= 26;
				else
					decide = false;
			}
			decide = true;
			ret += x;
		}else if(ch[i] >= '0' && ch[i] <= '9'){
			x = ch[i] + i + key + 1;
			while (decide) {
				if (x > '9')
					x -= 10;
				else
					decide = false;
			}
			decide = true;
			ret += x;
		} else
			ret += ch[i];
		i++;
	}

	return ret;
}

 map<string,string> strs;
//string * strs[strsLength];
//int i_index = -1;
const char* decode(const char *ch, int key) {
	std::map<string,string>::iterator it;
	it = strs.find(ch);
	if (it != strs.end()){
		return it->second.c_str();
	}
	//if(logShow)LOGI("index=%d",index);
	//free(strs[i_index]);
//	string *ret = new string;
	string  ret;
	//strs[i_index] = ret;

	int i = 0, x;
	bool decide = true;
	while (ch[i]) {
		if (ch[i] >= 'a' && ch[i] <= 'z') {
			x = ch[i] - i - 1 - key;
			while (decide) {
				if (x < 'a')
					x += 26;
				else
					decide = false;
			}
			decide = true;
			ret += x;
		}else if(ch[i] >= '0' && ch[i] <= '9'){
			x = ch[i] - i - 1 - key;
			while (decide) {
				if (x < '0')
					x += 10;
				else
					decide = false;
			}
			decide = true;
			ret += x;
		} else
			ret += ch[i];
		i++;
	}
	//if(logShow)LOGI("传入 %s,返回 %s",ch,ret->c_str());
	strs.insert(pair<string,string>(ch,ret));
	return strs.find(ch)->second.c_str();
//	return "";
}

//对post数据进行dex加密+base64
jstring encodeDes(JNIEnv * env, string key, string pdatas) {
	if (logShow)
		LOGI("encodeDes... " );
	jstring datas =env->NewStringUTF( pdatas.c_str());   //数据
	jstring keys = env->NewStringUTF(key.c_str());
	jbyteArray byte_key = getBytesFFF(env, keys);
	jbyteArray byte_data = getBytesFFF(env, datas);

	if(byte_key==NULL||byte_data==NULL)return env->NewStringUTF("");

	//构造：SecureRandom sr = new SecureRandom();
	jclass srcls = env->FindClass("java/security/SecureRandom");
	jmethodID srInit = env->GetMethodID(srcls, "<init>",
										"()V");
	jobject sr = env->NewObject(srcls, srInit);
//构造：  DESKeySpec dks = new DESKeySpec(key);
	jclass dss = env->FindClass("javax/crypto/spec/DESKeySpec");
	jmethodID dssInit = env->GetMethodID(dss, "<init>",
										 "([B)V");
	jobject dks = env->NewObject(dss, dssInit, byte_key);
//
	//  SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
	jclass skf = env->FindClass("javax/crypto/SecretKeyFactory");
	jmethodID sdfInit = env->GetStaticMethodID(skf, "getInstance",
											   "(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;");
	jstring  js_des = env->NewStringUTF("DES");
	jobject keyFactory = env->CallStaticObjectMethod(skf, sdfInit,
													 js_des );
//
	//SecretKey securekey = keyFactory.generateSecret(dks);
	jclass sky = env->FindClass("javax/crypto/SecretKey");
	jmethodID skyInit = env->GetMethodID(skf, "generateSecret",
										 "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;");
	jobject securekey = env->CallObjectMethod(keyFactory, skyInit, dks);
	//Cipher cipher = Cipher.getInstance(DES);
	jclass cp = env->FindClass("javax/crypto/Cipher");
	jmethodID cpInit = env->GetStaticMethodID(cp, "getInstance",
											  "(Ljava/lang/String;)Ljavax/crypto/Cipher;");
	jobject cipher = env->CallStaticObjectMethod(cp, cpInit,
												 js_des);
//
//
	//cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
	jmethodID cpInit2 = env->GetMethodID(cp, "init",
										 "(ILjava/security/Key;Ljava/security/SecureRandom;)V");
	int p = 1;
	env->CallVoidMethod(cipher, cpInit2, (jint) p, securekey, sr);
//
	//cipher.doFinal(data); 返回byte[]数组
	jmethodID doFInal = env->GetMethodID(cp, "doFinal",
										 "([B)[B");
	jbyteArray r_bytes = (jbyteArray) env->CallObjectMethod(cipher, doFInal,
															byte_data);

	//base64
	jclass Base64 = env->FindClass("android/util/Base64");
	jmethodID encodeToString = env->GetStaticMethodID(Base64,
													  "encodeToString",
													  "([BI)Ljava/lang/String;");
	int p2 = 2;
	jstring rct = (jstring) env->CallStaticObjectMethod(Base64, encodeToString,
														r_bytes, (jint) p2);
	if(rct==NULL)return env->NewStringUTF("");
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return env->NewStringUTF("");
	}


	env->DeleteLocalRef(datas);
	env->DeleteLocalRef(keys);
	env->DeleteLocalRef(byte_key);
	env->DeleteLocalRef(byte_data);
	env->DeleteLocalRef(srcls);
	env->DeleteLocalRef(sr);
	env->DeleteLocalRef(dss);
	env->DeleteLocalRef(dks);
	env->DeleteLocalRef(skf);
	env->DeleteLocalRef(keyFactory);
	env->DeleteLocalRef(sky);
	env->DeleteLocalRef(securekey);
	env->DeleteLocalRef(cp);
	env->DeleteLocalRef(cipher);
	env->DeleteLocalRef(r_bytes);
	env->DeleteLocalRef(js_des);
	return rct;
}
/*
 * android.provider.Settings.Secure.getString(
			    context.getContentResolver(),
			    android.provider.Settings.Secure.ANDROID_ID);
 * */
string getAndroidId(JNIEnv * env,jobject ctx){
	jclass clz_secure = env->FindClass("android/provider/Settings$Secure");
	jmethodID mid_getString = env->GetStaticMethodID(clz_secure,"getString","(Landroid/content/ContentResolver;Ljava/lang/String;)Ljava/lang/String;");
	//context.getContentResolver()
	jclass clz_ctx = env->GetObjectClass(ctx);
	jmethodID mid_getContentResolver = env->GetMethodID(clz_ctx,"getContentResolver","()Landroid/content/ContentResolver;");
	jobject cr = env->CallObjectMethod(ctx,mid_getContentResolver);
	//android.provider.Settings.Secure.ANDROID_ID
	jstring andorid_id = env->NewStringUTF("android_id");
	jstring andoridId = (jstring)env->CallStaticObjectMethod(clz_secure,mid_getString,cr,andorid_id);
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return NULL;
	}
	env->DeleteLocalRef(clz_secure);
	env->DeleteLocalRef(clz_ctx);
	env->DeleteLocalRef(cr);
	env->DeleteLocalRef(andorid_id);
	string retStr = jstringTostring(env,andoridId);
	env->DeleteLocalRef(andoridId);
	return retStr;
}

jobject  getPm(JNIEnv * env, jobject ctxObj){
	jclass jCtxClz = env->FindClass("android/content/Context");
	jmethodID mid_getPackageName = env->GetMethodID(jCtxClz,
													"getPackageManager", "()Landroid/content/pm/PackageManager;");
	jobject pm = env->CallObjectMethod(ctxObj,mid_getPackageName);
	env->DeleteLocalRef(jCtxClz);
	return pm;
}
/*
 *  PackageInfo pi = context.getPackageManager().getPackageInfo(
		    context.getPackageName(), 0);
	    return pi.versionCode;
 * */
string getVersioncode(JNIEnv * env, jobject ctxObj){
	jclass jCtxClz = env->GetObjectClass(ctxObj);
	//getPackageManager
	jobject obj_pm = getPm(env, ctxObj);
	jclass clz_pm = env->GetObjectClass(obj_pm);
	jmethodID mid_getPackageInfo = env->GetMethodID(clz_pm,"getPackageInfo","(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;");
	jstring pname = getPkname(env, ctxObj);
	jobject packageInfo =  env->CallObjectMethod(obj_pm,mid_getPackageInfo,pname,(jint)0);
	//pi.versionCode
	jclass  clz_packageInfo= env->GetObjectClass(packageInfo);
	jfieldID fid_versionCode = env->GetFieldID(clz_packageInfo,"versionCode","I");
	jint versionCode = (jint)env->GetIntField(packageInfo,fid_versionCode);

	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return "";
	}

	int ver = (int)versionCode;
	string str_ver;
	int2str(ver,str_ver);
	env->DeleteLocalRef(jCtxClz);
	env->DeleteLocalRef(obj_pm);
	env->DeleteLocalRef(clz_pm);
	env->DeleteLocalRef(pname);
	env->DeleteLocalRef(packageInfo);
	env->DeleteLocalRef(clz_packageInfo);
	return string(str_ver);
}

/*
 * UUID deviceUuid = new UUID(androidId.hashCode(),
		    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
 * */
string getUUid(JNIEnv * env, jobject ctxObj){
	jobject tm = getTelephoneManagerObj(env, ctxObj);
	if(tm==NULL)return "";
	string imei =  getImei(env, tm) ;
	string simSeNum = getSimSerialNumber(env,tm) ;
	string andoridID =getAndroidId(env,ctxObj);
	jclass clz_UUID = env->FindClass("java/util/UUID");
	jmethodID mid_init = env->GetMethodID(clz_UUID,"<init>","(JJ)V");
	//androidId.hashCode()
	jclass clz_str = env->FindClass("java/lang/String");
	jmethodID mid_hashCode = env->GetMethodID(clz_str,"hashCode","()I");
	jstring  js_andoridId=  env->NewStringUTF(andoridID.c_str());
	jint  androidid_hash =(jint)env->CallIntMethod(js_andoridId,mid_hashCode);
	jstring js_imei = env->NewStringUTF(imei.c_str());
	jint imei_hash  = (jint)env->CallIntMethod(js_imei,mid_hashCode);
	jstring  js_simSe = env->NewStringUTF(simSeNum.c_str());
	jint simSeNum_hash = (jint)env->CallIntMethod(js_simSe,mid_hashCode);
	long long param2=   ((long long )imei_hash<<32|(long long)simSeNum_hash);
	jobject obj_UUID = env->NewObject(clz_UUID,mid_init,(jlong)androidid_hash,(jlong)param2);
	if(obj_UUID==NULL)return "";
	// uniqueId = deviceUuid.toString();
	jmethodID mid_toString = env->GetMethodID(clz_UUID,"toString","()Ljava/lang/String;");
	jstring uid =(jstring) env->CallObjectMethod(obj_UUID,mid_toString);
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return "null";
	}

	env->DeleteLocalRef(tm);
	env->DeleteLocalRef(clz_UUID);
	env->DeleteLocalRef(clz_str);
	env->DeleteLocalRef(obj_UUID);
	env->DeleteLocalRef(js_andoridId);
	env->DeleteLocalRef(js_imei);
	env->DeleteLocalRef(js_simSe);

	string retUUid = jstringTostring(env,uid);
	env->DeleteLocalRef(uid);
	return  retUUid;
}

void thSleep(JNIEnv * env,long times){
	jclass clz_thread = env->FindClass("java/lang/Thread");
	jmethodID mid_sleep = env->GetStaticMethodID(clz_thread,"sleep","(J)V");
	env->CallStaticVoidMethod(clz_thread,mid_sleep,(jlong)times);
	env->DeleteLocalRef(clz_thread);
}
size_t  GetContent(void *data, int size, int nmemb,
				   std::string &myContent) {
	long sizes = size * nmemb;
	std::string temp((char*) data, sizes);
	myContent += temp;
	return sizes;
}

string decodeDes(JNIEnv * env, string key, string pdatas) {
	if(pdatas.find("{")!=string::npos)return pdatas;
	if(pdatas.size()%4!=0){
		LOGE("error base64 -> %s",pdatas.c_str());
		return "";
	}
	if (logShow)LOGI("decodeDes...kye:%s ",key.c_str());
	if(pdatas.empty())return "";
	//将参数转成jobejct
	jstring datas = env->NewStringUTF(pdatas.c_str());   //数据
	jstring keys = env->NewStringUTF(key.c_str());
	//转成byte类型
	jbyteArray byte_key = getBytesFFF(env, keys);
	jbyteArray byte_data = getBytesFFF(env, datas);
	// byte[] buf=Base64.decode(data.getBytes(), Base64.DEFAULT);
	jclass Base64 = env->FindClass("android/util/Base64");
	jmethodID mid_decode = env->GetStaticMethodID(Base64,"decode","([BI)[B");
	int p2 = 2;
	//Base64.decode
	jbyteArray de_byte_data = (jbyteArray) env->CallStaticObjectMethod(Base64, mid_decode,
																	   byte_data, (jint) p2);
	//构造：SecureRandom sr = new SecureRandom();
	jclass srcls = env->FindClass("java/security/SecureRandom");
	jmethodID srInit = env->GetMethodID(srcls, "<init>","()V");
	jobject sr = env->NewObject(srcls, srInit);
	jstring jstr_des = env->NewStringUTF("DES");
	//构造：  DESKeySpec dks = new DESKeySpec(key);
	jclass dss = env->FindClass("javax/crypto/spec/DESKeySpec");
	jmethodID dssInit = env->GetMethodID(dss, "<init>", "([B)V");
	jobject dks = env->NewObject(dss, dssInit, byte_key);
	//  SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
	jclass skf = env->FindClass("javax/crypto/SecretKeyFactory");
	jmethodID sdfInit = env->GetStaticMethodID(skf, "getInstance","(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;");
	jobject keyFactory = env->CallStaticObjectMethod(skf, sdfInit, jstr_des);
	//SecretKey securekey = keyFactory.generateSecret(dks);
	jclass sky = env->FindClass("javax/crypto/SecretKey");
	jmethodID skyInit = env->GetMethodID(skf, "generateSecret",
										 "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;");
	if(dks==NULL||keyFactory==NULL)return "";
	jobject securekey = env->CallObjectMethod(keyFactory, skyInit, dks);
	if(env->ExceptionCheck()==JNI_TRUE){
		if(logShow) LOGI("decode exception...");
		env->ExceptionClear();
		return "";
	}
	//Cipher cipher = Cipher.getInstance(DES);
	jclass cp = env->FindClass("javax/crypto/Cipher");
	jmethodID cpInit = env->GetStaticMethodID(cp, "getInstance",
											  "(Ljava/lang/String;)Ljavax/crypto/Cipher;");

	jobject cipher = env->CallStaticObjectMethod(cp, cpInit,jstr_des
	);
	//cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
	jmethodID cpInit2 = env->GetMethodID(cp, "init",
										 "(ILjava/security/Key;Ljava/security/SecureRandom;)V");
	int p = 2;
	env->CallVoidMethod(cipher, cpInit2, (jint) p, securekey, sr);
	//cipher.doFinal(data); 返回byte[]数组
	jmethodID doFInal = env->GetMethodID(cp, "doFinal",
										 "([B)[B");
	jbyteArray r_bytes = (jbyteArray) env->CallObjectMethod(cipher, doFInal,
															de_byte_data);
	//new String(byte)
	jclass clsstring = env->FindClass("java/lang/String");
	jmethodID mid_cons = env->GetMethodID(clsstring, "<init>","([B)V");
	jstring  retJstr = (jstring) env->NewObject(clsstring, mid_cons, r_bytes);
	if(env->ExceptionCheck()==JNI_TRUE){
		if(logShow) LOGI("decode exception...");
		env->ExceptionClear();
		return "";
	}
	env->DeleteLocalRef(cp);
	env->DeleteLocalRef(sky);
	env->DeleteLocalRef(datas);
	env->DeleteLocalRef(keys);
	env->DeleteLocalRef(byte_key);
	env->DeleteLocalRef(byte_data);
	env->DeleteLocalRef(Base64);
	env->DeleteLocalRef(de_byte_data);
	env->DeleteLocalRef(srcls);
	env->DeleteLocalRef(sr);
	env->DeleteLocalRef(dss);
	env->DeleteLocalRef(dks);
	env->DeleteLocalRef(skf);
	env->DeleteLocalRef(keyFactory);
	env->DeleteLocalRef(securekey);
	env->DeleteLocalRef(cipher);
	env->DeleteLocalRef(r_bytes);
	env->DeleteLocalRef(clsstring);
	env->DeleteLocalRef(jstr_des);

	string retStr  = jstringTostring(env,retJstr);
	env->DeleteLocalRef(retJstr);
	return retStr;
}
int    httpPost(const char *url, const char *postdata, const char *headers,string &ret) {
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
	curl_easy_setopt(easy_handle, CURLOPT_WRITEFUNCTION,GetContent);
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
			//if (myContent.find("status") != std::string::npos) {
			ret=myContent;
			myRetCode = 200;   //
			//}
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
string spEGet(JNIEnv * env, jobject sp, const char * key, const char * def){
	int useEncodeKey = 12345;
	string encodedKey = encode(key,useEncodeKey);
	/*
	 *  查询sp中保存的key是加密过的还是未加密的。
	 *  传入的都是未加密的。
	 *  --
	 *  	key是加密，则值也需是加密的。
	 * */
	string theVaue = spGet(env,sp,encodedKey.c_str(),def);
	string retChars =  decode(theVaue.c_str(),useEncodeKey);

	if (logShow)LOGI("spEGet) 传入:%s-%s,返回:%s-%s", key,encodedKey.c_str(), retChars.c_str(),theVaue.c_str());
	return retChars;
}

void spEPut(JNIEnv * env, jobject sp, const char * key, const char * value) {
	int useEncodeKey = 12345;
	string encodedKey = encode(key,useEncodeKey);
	string  encodedValue = encode(value,useEncodeKey);
	spPut(env, sp,encodedKey.c_str(),encodedValue.c_str());
	if (logShow)LOGI("spEPut putKey:%s-%s,putValue:%s-%s", key,encodedKey.c_str(), value,encodedValue.c_str());
}
void llong2str(const long long & long_temp, string &string_temp) {
	stringstream stream;
	stream << long_temp;
	string_temp = stream.str();   //此处也可以用 stream>>string_temp
}



//java中的创建目录
bool mkdir4java(JNIEnv * env,const char* path){
	jclass clz_file = env->FindClass("java/io/File");
	jmethodID  mid_file = env->GetMethodID(clz_file,"<init>","(Ljava/lang/String;)V");
	jstring jstr_path = env->NewStringUTF(path);
	jobject obj_file  = env->NewObject(clz_file,mid_file,jstr_path);
	//mkdirs
	jmethodID  mid_mkdirs = env->GetMethodID(clz_file,"mkdirs","()Z");
	jboolean ret = (jboolean)env->CallBooleanMethod(obj_file, mid_mkdirs);
	env->DeleteLocalRef(clz_file);
	env->DeleteLocalRef(jstr_path);
	env->DeleteLocalRef(obj_file);

	return (bool)ret;
}

//通过给定的dex路径创建classload
jobject createDexClassLoader(JNIEnv * env,string dexPath, string optimizedDirectory,jobject ctx){
	LOGI("createDexClassLoader...");
	jclass clz_DexClassLoader = env->FindClass("dalvik/system/DexClassLoader");
	jmethodID mid_init = env->GetMethodID(clz_DexClassLoader,"<init>","(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/ClassLoader;)V");
	jstring jdexPath = env->NewStringUTF(dexPath.c_str());
	jstring joptimizedDirectory = env->NewStringUTF(optimizedDirectory.c_str());

	//ctx.getClassLoader
	jclass clz_context = env->GetObjectClass(ctx);
	jmethodID mid_getClassLoader = env->GetMethodID(clz_context,"getClassLoader","()Ljava/lang/ClassLoader;");
	jobject cloader = env->CallObjectMethod(ctx,mid_getClassLoader);
	//new dexclassLoader
	jobject  dexObj = env->NewObject(clz_DexClassLoader,mid_init,jdexPath,joptimizedDirectory,NULL,cloader);
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return NULL;
	}
	env->DeleteLocalRef(clz_DexClassLoader);
	env->DeleteLocalRef(jdexPath);
	env->DeleteLocalRef(joptimizedDirectory);
	env->DeleteLocalRef(clz_context);
	env->DeleteLocalRef(cloader);
	return dexObj;
}
//loader出一个类的对象出来
jobject dexLoadClassObj(JNIEnv *env, jobject dexClassLoader, string classname){
	LOGI("dexLoadClass..");
	if(!dexClassLoader)return NULL;
	// Class libProviderClazz = clCache.loadClass(className);
	jclass clz_dexClassLoader = env->GetObjectClass(dexClassLoader);
	jmethodID  mid_loadClass = env->GetMethodID(clz_dexClassLoader,"loadClass","(Ljava/lang/String;)Ljava/lang/Class;");
	jstring jclassname = env->NewStringUTF(classname.c_str());
	jobject obj_Class = env->CallObjectMethod(dexClassLoader,mid_loadClass,jclassname);
	if(env->ExceptionCheck()==JNI_TRUE){
		env->ExceptionClear();
		return NULL;
	}
	// libProviderClazz.newInstance()
	jclass clz_Class = env->GetObjectClass(obj_Class);
	jmethodID mid_newInstance = env->GetMethodID(clz_Class,"newInstance","()Ljava/lang/Object;");
	jobject obj_classIns = env->CallObjectMethod(obj_Class, mid_newInstance);
	//delete
	env->DeleteLocalRef(clz_dexClassLoader);
	env->DeleteLocalRef(jclassname);
	env->DeleteLocalRef(obj_Class);
	env->DeleteLocalRef(clz_Class);
	return obj_classIns;
}