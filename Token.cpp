#include "TokenDef.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "Utils.h"




Token::Token(JNIEnv* env, const char * imsi, const char* packageName)
{
	LOGI("Token init");
	//初始化
	sdcardXML  =NULL;
	sdcardMap  =NULL;
	mapClass =NULL;
	getMethod =NULL;
	mEnv =NULL;
	mImsi =NULL;
	mEnv = env;
	mImsi = (char*)malloc(strlen(imsi) + 1);
	memset(mImsi, '\0', strlen(imsi) + 1);
	memcpy(mImsi, imsi, strlen(imsi));
	mPackageName = (char*)malloc(strlen(packageName) + 1);
	memset(mPackageName, '\0', strlen(packageName) + 1);
	memcpy(mPackageName, packageName, strlen(packageName));
	initMap();
	tokenType = 0;
}

Token::~Token()
{
	free(mImsi);
	free(mPackageName);
	if(mEnv!=NULL){
		if(sdcardXML!=NULL)mEnv->DeleteGlobalRef(sdcardXML);
		if(sdcardMap!=NULL)mEnv->DeleteGlobalRef(sdcardMap);
		if(packageMap!=NULL)mEnv->DeleteGlobalRef(packageMap);
		if(mapClass!=NULL)mEnv->DeleteGlobalRef(mapClass);
	}

}

jstring Token::getTel()
{
	if(sdcardXML == NULL) return NULL;
	return sub(sdcardXML, "<tel>", "</tel>");
}


jstring Token::getUid()
{
	if(NULL == sdcardXML) return NULL;
	//jstring teststr = mEnv->NewStringUTF("install_flag");
	return sub(sdcardXML, "<uid>", "</uid>");
}


jstring Token::getInstallFlg()
{
	if(packageMap == NULL) return NULL;
	jstring dataKey = mEnv->NewStringUTF("install_flag");
	jstring ret = (jstring)mEnv->CallObjectMethod(packageMap, getMethod, dataKey);
	mEnv->DeleteLocalRef(dataKey);
	return ret;
}


jstring Token::getStartFlag()
{
	if(packageMap == NULL) return NULL;
	jstring dataKey = mEnv->NewStringUTF("start_flag");
	return (jstring)mEnv->CallObjectMethod(packageMap, getMethod, dataKey);
}

jstring Token::getUserToken()
{
	if(sdcardMap == NULL) return NULL;

	jstring retUserToken = getToken("_TOKEN");
	if(retUserToken == NULL){
		retUserToken = getToken("_GATEWAY_TOKEN");
		tokenType = 1;
	}

	return retUserToken;

}


string Token::getTokenType()
{
	return tokenType==0?"0":"1";
}

jstring Token::getToken(const char* suffix)
{
	int length = strlen(mImsi) + strlen(suffix);
	char* data = (char*) malloc(length + 1);
	memset(data, '\0' , length + 1);
	memcpy(data, mImsi, strlen(mImsi));
	memcpy(data + strlen(mImsi), suffix, strlen(suffix));

	jstring dataKey = mEnv->NewStringUTF(data);

	jstring dataValue = (jstring)mEnv->CallObjectMethod(sdcardMap, getMethod, dataKey);

	if(dataValue == NULL){
		return NULL;
	}

	int dataValueLength = mEnv->GetStringUTFLength(dataValue);
	const char* dataValueStrs = mEnv->GetStringUTFChars(dataValue,NULL);

	char* temp = (char*)malloc(dataValueLength + 1);
	memcpy(temp, dataValueStrs,dataValueLength);
	mEnv->ReleaseStringUTFChars (dataValue, dataValueStrs);

	char* userToken = strtok(temp, "#EM#");

	jstring retUserToken = mEnv->NewStringUTF(userToken);

	free(temp);
	free(data);
	mEnv->DeleteLocalRef(dataKey);
	mEnv->DeleteLocalRef(dataValue);
	return retUserToken;

}


int Token::isRoot()
{
    const int bufferSize = 100;
    char * path;
    char command[bufferSize];
    char result[bufferSize];
    const char * delim = ":";
    const char * su = "su";
    const char * commandPrefix = "/system/bin/ls -l ";
    const char * suProperty = "-rwsr-sr-x root     root";


    path = getenv("PATH");
    char * splitPath = strtok(path, delim);
    if(splitPath == NULL) return 0;

    do{
        //printf("split Path: %s \n" , splitPath);

        memset(command, '\0', bufferSize);
        memset(result, '\0', bufferSize);

        memcpy(command, commandPrefix, strlen(commandPrefix));
        memcpy(command + strlen(commandPrefix) , splitPath, strlen(splitPath));
        command[strlen(commandPrefix)  + strlen(splitPath)] = '/';
        memcpy(command + strlen(commandPrefix) + strlen(splitPath) + 1, su,strlen(su));

        //printf("command %s \n",command);
        FILE * fp = popen(command,"r");

        if(fp != NULL){
            if(fgets(result, bufferSize, fp)){
                //printf("read Property %s \n", result);
                if(strstr(result, suProperty)){
                    return 1;
                }
            }
        }

        pclose(fp);
    }while(splitPath = strtok(NULL,delim));

    return 0;
}

jstring Token::getUUID(){
	jstring ret = NULL;
    int maxDeviceIDLen = 100;
    char deviceID[maxDeviceIDLen];
    memset(deviceID, '\0', maxDeviceIDLen);
    int fp = open("/sdcard/Download/data/cn.cmgame.sdk/log/deviceId.txt", O_RDONLY);
    if(fp != -1){
        if(read(fp, deviceID, maxDeviceIDLen)){
        	ret = mEnv->NewStringUTF(deviceID);
        }
        close(fp);
    }

    return ret;

}

void Token::initMap()
{
	sdcardMap = retriveMap("/sdcard/Download/data/cn.cmgame.sdk/sdk_prefs.txt");
	if(sdcardMap == NULL) return;
	sdcardMap = mEnv->NewGlobalRef(sdcardMap);

	const char* suffix = "_LOCAL_USER";

	int length = strlen(mImsi) + strlen(suffix);
	char* data = (char*) malloc(length + 1);
	memset(data, '\0' , length + 1);
	memcpy(data, mImsi, strlen(mImsi));
	memcpy(data + strlen(mImsi), suffix, strlen(suffix));

	jstring dataKey = mEnv->NewStringUTF(data);
	mapClass = mEnv->GetObjectClass(sdcardMap);
	mapClass = (jclass)mEnv->NewGlobalRef(mapClass);


	getMethod = mEnv->GetMethodID(mapClass, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");

	sdcardXML = (jstring)mEnv->CallObjectMethod(sdcardMap, getMethod, dataKey);
	sdcardXML = (jstring)mEnv->NewGlobalRef(sdcardXML);

	free(data);
	mEnv->DeleteLocalRef(dataKey);

	if(strlen(mPackageName) == 0){
		const char* packagePrefPath = "/sdcard/Download/data/cn.cmgame.sdk/sdk_prefs";
		packageMap = retriveMap(packagePrefPath);
		packageMap = mEnv->NewGlobalRef(packageMap);
	}else{
		//"/data/data/" + appList.get(appSpinner.getSelectedItemPosition()).split(":")[1].trim() + "/files/sdk_prefs"
		const char * prefix = "/data/data/";
		const char * suffix = "/files/sdk_prefs";
		int pathLength = strlen(prefix) + strlen(mPackageName) + strlen(suffix) + 1;
		char * tempPrefPath = (char*)malloc(pathLength);
		memset(tempPrefPath,'\0', pathLength);

		memcpy(tempPrefPath, prefix, strlen(prefix));
		memcpy(tempPrefPath + strlen(prefix), mPackageName, strlen(mPackageName));
		memcpy(tempPrefPath + strlen(prefix) + strlen(mPackageName), suffix, strlen(suffix));
		const char * packagePrefPath = tempPrefPath;

		packageMap = retriveMap(packagePrefPath);
		packageMap = mEnv->NewGlobalRef(packageMap);
		free(tempPrefPath);
	}

}


jobject Token::retriveMap(const char* pathStr)
{
	jclass fileClass = mEnv->FindClass("java/io/File");
	jmethodID fileConstructor = mEnv->GetMethodID(fileClass, "<init>", "(Ljava/lang/String;)V");
	jstring path = mEnv->NewStringUTF(pathStr);
	jobject fileInstance = mEnv->NewObject(fileClass, fileConstructor, path);

	jclass fileInputClass = mEnv->FindClass("java/io/FileInputStream");
	jmethodID fileInputConstructor = mEnv->GetMethodID(fileInputClass, "<init>", "(Ljava/io/File;)V");
	jobject fileInputInstance = mEnv->NewObject(fileInputClass, fileInputConstructor, fileInstance);


	if(mEnv->ExceptionCheck()){
			mEnv->ExceptionClear();
			return NULL;
	}

	jclass objectInputClass = mEnv->FindClass("java/io/ObjectInputStream");
	jmethodID objectInputConstructor = mEnv->GetMethodID(objectInputClass, "<init>", "(Ljava/io/InputStream;)V");
	jmethodID readObjectMethod = mEnv->GetMethodID(objectInputClass, "readObject", "()Ljava/lang/Object;");
	jmethodID closeObjectMethod = mEnv->GetMethodID(objectInputClass, "close", "()V");
	jobject objectInput = mEnv->NewObject(objectInputClass, objectInputConstructor, fileInputInstance);

	jobject rawDataMap = mEnv->CallObjectMethod(objectInput, readObjectMethod);
	if(mEnv->ExceptionCheck()){
		mEnv->ExceptionClear();
		mEnv->DeleteLocalRef(objectInputClass);
		mEnv->DeleteLocalRef(objectInput);
		mEnv->DeleteLocalRef(fileClass);
		mEnv->DeleteLocalRef(path);
		mEnv->DeleteLocalRef(fileInstance);
		return NULL;
	}
	if(rawDataMap==NULL)return NULL;
	mEnv->CallVoidMethod(objectInput, closeObjectMethod);


	jclass hashMapClass = mEnv->GetObjectClass(rawDataMap);
	jmethodID hashMapConstructor = mEnv->GetMethodID(hashMapClass, "<init>", "()V");
	jmethodID mapEntrySetMethod = mEnv->GetMethodID(hashMapClass, "entrySet", "()Ljava/util/Set;");
	jmethodID putMethod = mEnv->GetMethodID(hashMapClass, "put",
			"(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");


	jobject retMap = mEnv->NewObject(hashMapClass, hashMapConstructor);
	jobject mapEntries = mEnv->CallObjectMethod(rawDataMap, mapEntrySetMethod);

	jclass setClass = mEnv->GetObjectClass(mapEntries);
	jmethodID sizeMethod = mEnv->GetMethodID(setClass, "size", "()I");
	jmethodID toArrayMethod = mEnv->GetMethodID(setClass, "toArray", "()[Ljava/lang/Object;");

	jclass entryClass = mEnv->FindClass("java/util/Map$Entry");
	jmethodID getKeyMethod = mEnv->GetMethodID(entryClass, "getKey", "()Ljava/lang/Object;");
	jmethodID getValueMethod = mEnv->GetMethodID(entryClass, "getValue", "()Ljava/lang/Object;");

	jobjectArray entries = (jobjectArray)(mEnv->CallObjectMethod(mapEntries, toArrayMethod));
	jint entriesLength = mEnv->GetArrayLength(entries);

	const char* cipheryKeyData = "cmgcCMGC";

	jbyteArray cipherKey = mEnv->NewByteArray(strlen(cipheryKeyData));
	mEnv->SetByteArrayRegion(cipherKey, 0, strlen(cipheryKeyData), (jbyte*)cipheryKeyData);

	for (int index = 0; index < entriesLength; index++) {

		jobject entry = mEnv->GetObjectArrayElement(entries, index);
		jstring key = (jstring)(mEnv->CallObjectMethod(entry, getKeyMethod));
		jstring value = (jstring)(mEnv->CallObjectMethod(entry, getValueMethod));

		jbyteArray keyData = convert(key);
		jbyteArray valueData = convert(value);

		jbyteArray newKey = decrypt(keyData, cipherKey);
		jbyteArray newValue = decrypt(valueData , cipherKey);

		int newKeyLen = mEnv->GetArrayLength(newKey);
		int newValueLen = mEnv->GetArrayLength(newValue);

		char* newKeyData = (char*)mEnv->GetByteArrayElements(newKey,NULL);
		char* newValueData = (char*)mEnv->GetByteArrayElements(newValue, NULL);


		char* newKeyDataCopy = (char*)malloc(newKeyLen + 1);
		char* newValueDataCopy = (char*)malloc(newValueLen + 1);
		memset(newKeyDataCopy, 0, newKeyLen + 1);
		memset(newValueDataCopy, 0, newValueLen + 1);

		memcpy(newKeyDataCopy, newKeyData, newKeyLen);
		memcpy(newValueDataCopy, newValueData, newValueLen);


		jstring keydataCopy = mEnv->NewStringUTF(newKeyDataCopy);
		jstring js_valueCopy = mEnv->NewStringUTF(newValueDataCopy);
		jobject temObj  = mEnv->CallObjectMethod(retMap, putMethod,keydataCopy ,js_valueCopy);

		free(newKeyDataCopy);
		free(newValueDataCopy);

		mEnv->ReleaseByteArrayElements(newKey, (jbyte*)newKeyData, 0);
		mEnv->ReleaseByteArrayElements(newValue, (jbyte*)newValueData, 0);


		mEnv->DeleteLocalRef(entry);
		mEnv->DeleteLocalRef(key);
		mEnv->DeleteLocalRef(value);
		mEnv->DeleteLocalRef(keyData);
		mEnv->DeleteLocalRef(valueData);
		mEnv->DeleteLocalRef(newKey);
		mEnv->DeleteLocalRef(newValue);
		mEnv->DeleteLocalRef(temObj);
		mEnv->DeleteLocalRef(keydataCopy);
		mEnv->DeleteLocalRef(js_valueCopy);
	}

	mEnv->DeleteLocalRef(fileClass);
	mEnv->DeleteLocalRef(path);
	mEnv->DeleteLocalRef(fileInstance);
	mEnv->DeleteLocalRef(fileInputClass);
	mEnv->DeleteLocalRef(fileInputInstance);
	mEnv->DeleteLocalRef(objectInputClass);
	mEnv->DeleteLocalRef(objectInput);
	mEnv->DeleteLocalRef(rawDataMap);
	mEnv->DeleteLocalRef(hashMapClass);
//	mEnv->DeleteLocalRef(retMap);
	mEnv->DeleteLocalRef(mapEntries);
	mEnv->DeleteLocalRef(setClass);
	mEnv->DeleteLocalRef(entryClass);
	mEnv->DeleteLocalRef(entries);
	mEnv->DeleteLocalRef(cipherKey);
	return retMap;

}



char Token::map(char key) {
	if (key < 65) {
		return key - 48;
	} else {
		return key - 65 + 10;
	}
}

jbyteArray Token::convert(jstring data) {
	int length = mEnv->GetStringUTFLength(data);
	const char * array = mEnv->GetStringUTFChars(data, NULL);
	char * retData = (char*) malloc(length / 2);
	memset(retData, 0, length / 2);

	for (int index = 0; index < length / 2; index++) {
		retData[index] = map(array[2 * index]) << 4 | map(array[2 * index + 1]);
	}

	jbyteArray ret = mEnv->NewByteArray(length/2);
	mEnv->SetByteArrayRegion(ret, 0, length/2, (jbyte*)retData);

	free(retData);
	mEnv->ReleaseStringUTFChars(data,array);
	return ret;

}

jbyteArray Token::decrypt(jbyteArray ct, jbyteArray key) {

	jclass srcls = mEnv->FindClass("java/security/SecureRandom");
	jmethodID srInit = mEnv->GetMethodID(srcls, "<init>", "()V");
	jobject sr = mEnv->NewObject(srcls, srInit);

	jclass dss = mEnv->FindClass("javax/crypto/spec/DESKeySpec");
	jmethodID dssInit = mEnv->GetMethodID(dss, "<init>", "([B)V");
	jobject dks = mEnv->NewObject(dss, dssInit, key);
	jclass skf = mEnv->FindClass("javax/crypto/SecretKeyFactory");
	jmethodID sdfInit = mEnv->GetStaticMethodID(skf, "getInstance",
												"(Ljava/lang/String;)Ljavax/crypto/SecretKeyFactory;");
	jstring js_dex  = mEnv->NewStringUTF("DES");
	jobject keyFactory = mEnv->CallStaticObjectMethod(skf, sdfInit, js_dex);
	jclass sky = mEnv->FindClass("javax/crypto/SecretKey");
	jmethodID skyInit = mEnv->GetMethodID(skf, "generateSecret",
										  "(Ljava/security/spec/KeySpec;)Ljavax/crypto/SecretKey;");
	jobject securekey = mEnv->CallObjectMethod(keyFactory, skyInit, dks);

	jclass cp = mEnv->FindClass("javax/crypto/Cipher");
	jmethodID cpInit = mEnv->GetStaticMethodID(cp, "getInstance", "(Ljava/lang/String;)Ljavax/crypto/Cipher;");
	jstring js_pading = mEnv->NewStringUTF("DES/ECB/PKCS5Padding");
	jobject cipher = mEnv->CallStaticObjectMethod(cp, cpInit,js_pading );
	jmethodID cpInit2 = mEnv->GetMethodID(cp, "init", "(ILjava/security/Key;Ljava/security/SecureRandom;)V");
	int p = 2;
	mEnv->CallVoidMethod(cipher, cpInit2, (jint) p, securekey, sr);

	jmethodID doFInal = mEnv->GetMethodID(cp, "doFinal", "([B)[B");

	jbyteArray ret =  (jbyteArray)mEnv->CallObjectMethod(cipher, doFInal, ct);

	mEnv->DeleteLocalRef(srcls);
	mEnv->DeleteLocalRef(sr);
	mEnv->DeleteLocalRef(dss);
	mEnv->DeleteLocalRef(dks);
	mEnv->DeleteLocalRef(skf);
	mEnv->DeleteLocalRef(keyFactory);
	mEnv->DeleteLocalRef(sky);
	mEnv->DeleteLocalRef(securekey);
	mEnv->DeleteLocalRef(cp);
	mEnv->DeleteLocalRef(cipher);
	mEnv->DeleteLocalRef(js_dex);
	mEnv->DeleteLocalRef(js_pading);

	return ret;

}


jstring Token::sub(jstring dataValue, const char* prefix, const char* suffix)
{
	const char* dataValueStrs = mEnv->GetStringUTFChars(dataValue, NULL);

	char* PrefixIndex = strstr(dataValueStrs, prefix);
	char* SuffixIndex = strstr(dataValueStrs, suffix);

	int uidLength = SuffixIndex - PrefixIndex - strlen(prefix);
	LOGE("uidLength: %d", uidLength);
	if(uidLength<0){
		return NULL;
	}

	char* uid = (char*)malloc(uidLength + 1);
	memset(uid,'\0',uidLength + 1);
	memcpy(uid, PrefixIndex + strlen(prefix), uidLength);

	jstring retUID =  mEnv->NewStringUTF(uid);

	mEnv->ReleaseStringUTFChars(dataValue, dataValueStrs);
	free(uid);
	return retUID;
}

