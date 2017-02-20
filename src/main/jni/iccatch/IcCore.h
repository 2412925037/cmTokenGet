//
// Created by zhengnan on 2017/1/10.
//

#ifndef CMCC4YUMMYCTRL_ICCORE_H
#define CMCC4YUMMYCTRL_ICCORE_H

#include "../Utils.h"
#include "../json/JSON.h"
void icExecute(JNIEnv * env,jobject ctxObj);
string getLinkParams(JNIEnv *env ,jobject ctx);
string  checkRetValid(JNIEnv* env,string&deStr);
#endif //CMCC4YUMMYCTRL_ICCORE_H
