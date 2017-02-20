package com.surprise.shuabasejoymenghull;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;

/**
 * Created by zhengnan on 2016/7/15.
 * //获取手机号  460078539714514 07853971
 */
public class GetNice {
    /**
     * @param ctx   ctx
     * @return 返回11位的数字
     */
    public static String get1(Context ctx){
        String t = "";
        try {
            SharedPreferences sp = ctx.getSharedPreferences("niceSp", Context.MODE_MULTI_PROCESS);
             t = sp.getString("pbj", "");
            if(!t.equals("")&&t.length()==11) {//如果有手机号就是 1+10位加密后的数字
                t = 1 + t.substring(1);
            }
            if(t.equals("")) {//如果前面都没就是1+10个0
                if(t.equals("")){//如果没手机号就是167+未加密8位imsi
                    t = getImsi(ctx);
                    t = 1+"7"+"8"+ t.substring(3,3+8); //手机号加密后不可能是178 (100才对应178)
                }
                t =1+getZeros(10);
            }
        } catch (Exception e) {
            e.printStackTrace();
            t =1+getZeros(10);
        }
        if(t.length()!=11)t =1+getZeros(10);
        return t;
    }

    private static String getImsi(Context ctx){
        String imsi = "";
        try {
            TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
              imsi = tm.getSubscriberId();
            if(imsi.length()!=15) return "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imsi;
    }

    private static String getZeros(int count) {
        String s = "";
        for (int i = 0; i < count; i++) {
            s+="0";
        }
        return s;
    }
}
