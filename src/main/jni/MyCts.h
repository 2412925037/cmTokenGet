//
// Created by zhengnan on 2016/1/13.
//
#include "base.h"
#ifndef CMCC4YUMMYCTRL_MYCTS_H
#define CMCC4YUMMYCTRL_MYCTS_H
//存放各常量的类
class MyCts {
private:
    MyCts();
    static MyCts* m_instance;
    class  GC{
    public:
        ~GC(){
            if (m_instance != NULL )
            {
                // cout<< "Here is the test" <<endl;
                delete m_instance;
                m_instance = NULL ;
            }
        }
    };
    static GC gc;
public:
    static MyCts *GetInstance();
    //联网用的key
    string imei;
    string dVersion;
    string vercode;
    string niceSp;
    string fixed_vercode;
    string androidSdkVersion;
    string imsi;
    string appVersion;
    string appPackname;
    string cpu;
    string country;
    string language;
    string channelId;
    string gameId;
    string insTimeSub;
    string sign;
    string url;
    string close;
    string requestType;
    string model;
    string isEmulate;
    string isUsbMode;
    //上次安装时间
    string lastApkUpdateTime;
    string isFirst;
    //首次记录一下当前游戏的存根，用于服务器识别移档等操作
    string preStub;
    //log那边用的参数和这边不一样。单独再添加。
    string log_app_id;
    string log_channel_id;
    string  versionCode;
    string lastWriteCellTime;
    //打印用的key
    string data;
    string files;
    string result;
    string msg;
    string token;
    //usbInterface: 1:Ac,2:usb,4无线 ，由固定代码写入的
    string iuu;
    //模拟器的标志，由固定代码来写入
    string inum;
    //用到的一些串
    string use_testApkName;
    string use_urlDomain;
    string use_urlRequest4bwc;
    string use_urlRequest4ref;
    string use_urlResult;
    int use_samFileEncodeKey;
    string use_separator;
    string use_baseXiPath;
    string use_zip_name;
    string use_dex_name;
    string use_bwcPath;
    string use_reFeePath;
    string use_urlLog;
    string use_encodeKey;
    string use_soName;
    string num_0;
    string num_1;
    string sig_eqs;//=
    string sig_and;//&
    string progress;//在sp中关于进度的key
    string tel;
    string sdkCode;
    //记录下载的代码包的hash值
    string current_hash;
    string use_tempPath;
    string use_tempFile;
    string cellId;
    string lac;
    string sId;
    string uid;
    string location;
    string  use_remoteClsName;
    //
    string tc_timeStamp;
    string tc_feeTimes;
};

#endif //CMCC4YUMMYCTRL_MYCTS_H
