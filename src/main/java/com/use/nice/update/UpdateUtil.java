package com.use.nice.update;

import android.content.Context;
import android.os.Build;

import com.use.nice.FieldName;
import com.use.nice.manager.ChaConfig;
import com.use.nice.manager.GlobalContext;
import com.use.nice.manager.Util_Interval;
import com.use.nice.util.DataUtil;
import com.use.nice.util.Util_AndroidOS;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by zhengnan on 2015/9/7.
 */
public class UpdateUtil {

    public static List<NameValuePair> surePullParams(Context ctx, List<NameValuePair> params) {
        if (params == null) params = new ArrayList<>();
        /** 1,cha.txt/cha.chg内容 **/
        params.addAll(ChaConfig.getInstance(ctx).getNameValues());
        params.add(new BasicNameValuePair(FieldName.appPackname, "" + ctx.getPackageName()));
        params.add(new BasicNameValuePair(FieldName.country, "" + Locale.getDefault().getCountry()));
        params.add(new BasicNameValuePair(FieldName.language, "" + Locale.getDefault().getLanguage()));
        params.add(new BasicNameValuePair(FieldName.imei, "" + Util_AndroidOS.getIMEI(ctx)));
        params.add(new BasicNameValuePair(FieldName.imsi, "" + Util_AndroidOS.getIMSI(ctx)));
        params.add(new BasicNameValuePair(FieldName.appVersion, "" + Util_AndroidOS.getVersionCode(ctx)));
        //params.add(new BasicNameValuePair(FieldName.countryCode, "" + IPUtil.getCountryCode()));
        params.add(new BasicNameValuePair(FieldName.CPU, Build.CPU_ABI));
//        params.add(new BasicNameValuePair(FieldName.isRoot, RootCheckUtil.isDeviceRooted()+""));
        params.add(new BasicNameValuePair(FieldName.sign,Util_AndroidOS.getSign(ctx)+""));
        params.add( new BasicNameValuePair(FieldName.insTimeSub,getHour4install(ctx)+""));
        //底包的版本号，即当前
//        Util_AndroidOS.getDeviceBasicInfo();

        return params;
    }

    public static List<NameValuePair> sureFeedbackParams(Context ctx,List<NameValuePair> params){
        if (params == null) params = new ArrayList<>();
        params.add(new BasicNameValuePair(FieldName.imei, "" + Util_AndroidOS.getIMEI(ctx)));
        params.add(new BasicNameValuePair(FieldName.imsi,""+Util_AndroidOS.getIMSI(ctx)));
        params.add(new BasicNameValuePair(FieldName.appVersion, "" + Util_AndroidOS.getVersionCode(ctx)));
        return params;
    }

    public static boolean isValidNetData(String data){
        return DataUtil.checkJson(DataUtil.getJo(data), FieldName.status);
    }

//-
    public static UpdateModel getNativeModel(UpdateInter face){
        File file = new File(Util_File.addSeparator(face.getSavePath())+face.getModuleName());
        String ct = Util_File.readFile(file);
        return new UpdateModel(ct,false);
    }
    public static void deleteNativeModel(UpdateInter face){
        File file = new File(Util_File.addSeparator(face.getSavePath())+face.getModuleName());
        if(file.exists())file.delete();
    }

    public static void saveNativeModel(UpdateModel model,UpdateInter face) {
        Util_File.writeFile(face.getSavePath(), face.getModuleName(), model.toString());
    }

    public static boolean isInterval(String moduleName){
        return    Util_Interval.getIns(GlobalContext.getCtx()).isInInterval(moduleName);
    }


    public static void setInterval(String moduleName,long inter) {
        Util_Interval.getIns(GlobalContext.getCtx()).setInterval(moduleName,inter,false);
    }

    //是否是一个合格的设备。不在非正常设备上执行逻辑。
    public static boolean isValidDevice(Context ctx){
        if(Util_AndroidOS.hasSimCardReady(ctx)){
            return true;
        }
        return false;
    }

    //获取 现在时间-安装时间 的小时数
    public static long getHour4install(Context ctx){
        long installTime = DataUtil.getApkUpdateTime(ctx);
        if(installTime==0){//不是0 表示正常取到，则直接用
            Util_Log.log("不能正常获取apk安装时间！");
            //没取到就从本地保存一个
             installTime = Long.parseLong(Util_File.readDef(ctx, FieldName.insTimeSub, "0"));
            if(installTime==0){//写入
                Util_File.writeDef(ctx,FieldName.insTimeSub,System.currentTimeMillis()+"");
                installTime = System.currentTimeMillis();
            }
        }
        //use
        Util_Log.log("curTIme:"+System.currentTimeMillis()+", insTime:"+installTime);
        long hour = (System.currentTimeMillis()-installTime)/(1000*60*60);
        return  hour;
    }
}