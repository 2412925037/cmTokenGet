//
// Created by zhengnan on 2016/8/31.
//

#include "Token_Z.h"
#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include "Utils.h"




Token_Z::Token_Z(JNIEnv* env, const char * imsi, const char* packageName)
{
    LOGI("Token init");
    //初始化
    sdcardMap  =NULL;
    mEnv =NULL;
    mImsi =NULL;
    if(imsi==""){
        return;
    }
    mEnv = env;
    mImsi = (char*)malloc(strlen(imsi) + 1);
    memset(mImsi, '\0', strlen(imsi) + 1);
    memcpy(mImsi, imsi, strlen(imsi));
    initMap();
}

Token_Z::~Token_Z()
{LOGI("Token free!");
    free(mImsi);
    if(mEnv!=NULL){
        if(sdcardMap!=NULL)mEnv->DeleteGlobalRef(sdcardMap);
    }

}

jstring Token_Z::getTel()
{
    if(sdcardMap == NULL) return NULL;
    string myImsi(mImsi);
    return javaMapGet(mEnv,sdcardMap,myImsi+"_PHONE_NUMBER");
}

jstring Token_Z::getUid()
{
    if(sdcardMap == NULL) return NULL;
    string myImsi(mImsi);
    return javaMapGet(mEnv,sdcardMap,myImsi+"_IDENTITYID");
}

void Token_Z::initMap()
{
    sdcardMap = retriveMap("/sdcard/Download/data/cn.cmgame.sdk/sdk_prefs.txt");
    if(sdcardMap == NULL) return;
    sdcardMap = mEnv->NewGlobalRef(sdcardMap);
}

jstring Token_Z::javaMapGet(JNIEnv *env, jobject hashMap,string key){
    if(hashMap==NULL) {
        LOGI("javaMap is null");
        return NULL;
    }
    jstring  retJstr = NULL;
    // Get the HashMap Class
    jclass jclass_of_hashmap = env->GetObjectClass(hashMap);
    // Get link to Method "entrySet"
    jmethodID mid_get = (env)->GetMethodID(jclass_of_hashmap, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
    jstring js_key = env->NewStringUTF(key.c_str());
    // Invoke the "entrySet" method on the HashMap object
      retJstr = (jstring)env->CallObjectMethod(hashMap, mid_get,js_key);
    //delete
    env->DeleteLocalRef(jclass_of_hashmap);
    env->DeleteLocalRef(js_key);
    return retJstr;
}
jobject Token_Z::retriveMap(const char* pathStr)
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



char Token_Z::map(char key) {
    if (key < 65) {
        return key - 48;
    } else {
        return key - 65 + 10;
    }
}

jbyteArray Token_Z::convert(jstring data) {
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

jbyteArray Token_Z::decrypt(jbyteArray ct, jbyteArray key) {

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

jstring Token_Z::getUUID(){
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


