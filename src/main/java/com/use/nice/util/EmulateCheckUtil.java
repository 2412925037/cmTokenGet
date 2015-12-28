package com.use.nice.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhengnan on 2015/12/7.
 * 用于严谨的判断是不是模拟器
 *
 *  外部直接调用isValidDevice即可。
 */
public class EmulateCheckUtil {
    public   interface ResultCallBack{
        void isEmulator();//是模拟器
        void isDevice();//是正常设备
        void notSure();//不确定
    }
    private static String validDevice = new String(new byte[]{118, 97, 108, 105, 100, 68, 101, 118, 105, 99, 101});
    //iuu：当前充电模式。
    private static String usb_interface = new String(new byte[]{105, 117, 117});
    private static boolean exeTag = false;
    private static ExecutorService service = Executors.newSingleThreadExecutor();

    /**
     * @param ctx ctx
     * @param cb 回调
     *           用于严谨的检测是不是模拟器
     */
    public static void isValidDevice(Context ctx,final ResultCallBack cb) {
        try{
            boolean isEmulate = isEmulate4simple(ctx) ;
            if(cb==null)return ;
            //简单检测不出结果，就用电池信息检测
            if(!isEmulate) {
                {//注册
                    if(!exeTag){
                        Util_File.writeDef(ctx,usb_interface,"0");
                        ctx.registerReceiver(new MyBatReceiver(cb),
                                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                    }

                    exeTag = true;
                }
            }else{
                cb.isEmulator();
            }
        }catch (Throwable e){
            cb.notSure();
        }
    }

    /**
     * @param ctx ctx
     * @return  简单的判断是不是模拟器
     */
    public static boolean isEmulate4simple(Context ctx){
        // 如果 运行的 是一个 模拟器
        TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = telephonyManager.getDeviceId();
        if (deviceId == null || deviceId.trim().length() == 0 || deviceId.matches("0+")) {
            return true;
        }
        if(Build.FINGERPRINT.startsWith("generic"))return true;
        String networkOperator = telephonyManager.getNetworkOperatorName();
        if(networkOperator!=null&&"Android".equals(networkOperator)) {
            return true;
        }
        return  false;
    }

    private static class MyBatReceiver extends BroadcastReceiver{
        ResultCallBack cb = null;
        public MyBatReceiver(ResultCallBack cb){
            this.cb = cb;
        }
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Intent.ACTION_BATTERY_CHANGED.equals(action)){
                int plugged = intent.getIntExtra("plugged", 0);
                int voltage = intent.getIntExtra("voltage", 0);
                int temperature = intent.getIntExtra("temperature", 0);
                Util_File.writeDef(context,usb_interface,plugged+"");
                if(Util_Log.logShow)Util_Log.log("plugged:"+plugged+",voltage:"+voltage+",temperature:"+temperature);
//                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
                if(plugged== BatteryManager.BATTERY_PLUGGED_AC&&voltage==0&&temperature==0){
//                    sp.edit().putString(validDevice,"false").commit();
                   cb.isEmulator();
                }else {
//                    sp.edit().putString(validDevice,"true").commit();
                    cb.isDevice();
                }
                context.unregisterReceiver(this);
                exeTag = false;
            }
        }
    }

}
