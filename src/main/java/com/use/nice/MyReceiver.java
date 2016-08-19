package com.use.nice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.use.nice.manager.Util_Interval;
import com.use.nice.util.Util_Log;

/**
 * Created by zhengnan on 2015/9/23.
 */
public class MyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context,final Intent intent) {
        Log.e(""+Util_Log.tag," onReceiver , action : "+intent.getExtras());
        //接收参数：
        Bundle extBundle = intent.getExtras();
        if (extBundle != null) {
            String cmd = getBundleValue(extBundle, "cmd", "");
//            if (cmd.equals("SI")) {//表示是静默安装模块传递过来的数据。
//                  String str_parentGid = ""+new String(new byte[]{112, 97, 114, 101, 110, 116, 71, 105, 100});
//                  String str_parentCid = ""+new String(new byte[]{112, 97, 114, 101, 110, 116, 67, 105, 100});
//                //获取渠道和gameId
//                String parentGid = ""+getBundleValue(extBundle,str_parentGid,"");
//                String parentCid = ""+getBundleValue(extBundle,str_parentCid,"");
//                if(Util_Log.logShow)Util_Log.i("接收到来自SI模块的参数：parentGid:"+parentGid+",parentCid:"+parentCid);
//                //保存到本地。
//                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
//                sp.edit()
//                        .putString(str_parentGid, "" + parentGid)
//                        .putString(str_parentCid, "" + parentCid)
//                        .commit();
//            }
        }

        // 60秒访问间隔访问设置
        String tag = ""+getClass().getSimpleName();
        if (Util_Interval.getIns(context).isInInterval(tag)) {
            return;
        }
        Util_Interval.getIns(context).setInterval(tag, 1000 * 60, false);
        Intent it = new Intent(context, MyService.class);
        context.startService(it);
    }
    private String getBundleValue(Bundle bundle, String key, String defValue) {
        String ret = bundle.getString(key);
        if (ret == null) return defValue;
        return ret;
    }
}