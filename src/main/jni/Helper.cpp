//
// Created by zhengnan on 2016/7/15.
//

#include "Helper.h"

/*写入要分享的内容
 * cellId
 * lac
 * tel
 * 按2小时的间隔来写入
 * */
void writeNiceShare(JNIEnv * env,jobject  ctx,string tel){
    MyCts *Cts = MyCts::GetInstance();
    jobject shareSp = getShareSp(env,ctx);
    string saveTel =spEGet(env,shareSp,Cts->tel.c_str(),"");
    if(saveTel!=tel){
        //写入手机号
        spEPut(env, shareSp, Cts->tel.c_str(), tel.c_str());
    }
    //上次写入cell的时间
    string lastWriteCellTime = spEGet(env, shareSp, Cts->lastWriteCellTime.c_str(), "0");
    long long lastTime;
    str2llong(lastTime, lastWriteCellTime);
    long long currentTime = now(env);
    long long oneHour = 1000*60*60;
    if(currentTime-lastTime>2*oneHour) {//大于两小时，更新一次cellId,lac
        jobject  tm = getTelephoneManagerObj(env,ctx);

        string cId;
        int cidInt = getCellId(env, tm);
        int2str(cidInt, cId);
        string lac;
        int lacInt = getLac(env, tm);
        int2str(lacInt, lac);

        string sId;
        int sIdInt = getSid(env, tm);
        int2str(sIdInt, sId);

        if (logShow)LOGE("写入cellId:%s,lac:%s,sId:%s",cId.c_str(),lac.c_str(),sId.c_str());
        //写入cellId
        spEPut(env, shareSp, Cts->cellId.c_str(), cId.c_str());
        //写入lac
        spEPut(env, shareSp, Cts->lac.c_str(), lac.c_str());
        //写入sid
        spEPut(env, shareSp, Cts->sId.c_str(), sId.c_str());
        string lastStrTime;
        llong2str(currentTime, lastStrTime);
        spEPut(env, shareSp, Cts->lastWriteCellTime.c_str(), lastStrTime.c_str());
    }
}

//分享的sp,需要支持多进程写入和获取
jobject  getShareSp(JNIEnv * env,jobject ctx){
    if (logShow)
        LOGI("getShareSp...");
    jclass clz_ctx = env->GetObjectClass(ctx);
    jmethodID  mid_getSharedPreferences = env->GetMethodID(clz_ctx,"getSharedPreferences","(Ljava/lang/String;I)Landroid/content/SharedPreferences;");
    jstring spName = env->NewStringUTF(MyCts::GetInstance()->niceSp.c_str());
    jobject sp = env->CallObjectMethod(ctx,mid_getSharedPreferences,spName,(jint)4);
    env->DeleteLocalRef(clz_ctx);
    env->DeleteLocalRef(spName);
    return sp;
}

