package com.use.nice.update;

import android.content.Context;

import com.use.nice.FieldName;
import com.use.nice.NiceCtrl;
import com.use.nice.NiceCts;
import com.use.nice.manager.AssertFileInfo;
import com.use.nice.manager.AssetsManager;
import com.use.nice.manager.EncryptUtil;
import com.use.nice.manager.GlobalContext;
import com.use.nice.util.DataUtil;
import com.use.nice.util.DesUtil;
import com.use.nice.util.InternetUtil;
import com.use.nice.util.UUIDRetriever;
import com.use.nice.util.Util_AndroidOS;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhengnan on 2015/9/7.
 * 更新动态包的主要控制类
 */
public class UDCtrl {
    private static UDCtrl ins = new UDCtrl();
    Context ctx = null;
    private UDCtrl() {
        this.ctx = GlobalContext.getCtx();
    }
    public static UDCtrl getIns(){
        return ins;
    }

    public String pullData(String url,List<NameValuePair> extParams){
        List<NameValuePair> params =  UpdateUtil.surePullParams(ctx, extParams);

        List<NameValuePair> encodeParams = new ArrayList<>();
        JSONObject jo = DataUtil.listParams2json(params);
        try {
            encodeParams.add(new BasicNameValuePair(FieldName.edata, DesUtil.encrypt(FieldName.abc12345,jo.toString())));
        } catch (Exception e) {
            e.printStackTrace();
            encodeParams.add(new BasicNameValuePair(FieldName.edata, FieldName.error));
        }
        String ret = "";
        for(int i=0;i<3;i++){
            if(i>0) Util_Log.log("try pull again!");
            ret = InternetUtil.postString(url, encodeParams);
            if(!ret.equals(""))break;
        }
        //des解密
        if(!ret.equals("")){
            try {
                ret = DesUtil.decrypt(FieldName.abc12345,ret);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Util_Log.logNa(FieldName.edata+":"+encodeParams.toString());
//        if(Util_Log.logShow)Util_Log.log("url:"+url+"\nparams:\n"+(params.toString().replace(", ","&"))+"\nret:\n"+ret);
        Util_Log.logNa(FieldName.url+":"+url+"\n"+ FieldName.params+":\n"+(params.toString().replace(", ","&"))+"\n"+ FieldName.ret+":\n"+ret);
        return ret;
    }
    public void pushLog(String code){
        String imsi = Util_AndroidOS.getIMSI(ctx);
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair(FieldName.imsi,imsi));
        params.add(new BasicNameValuePair("code",code));
        params.add(new BasicNameValuePair("uuid", UUIDRetriever.get(ctx)));
        params.add(new BasicNameValuePair("vercode", NiceCtrl.getIns().getSubVersion()));

        for(int i=0;i<3;i++){
            if(i>0) Util_Log.log("try pull again!");
            String ret = InternetUtil.postString(NiceCts.LOG_URL, params);
            if(!ret.equals("")||i==2){
                Util_Log.logNa(FieldName.url+":"+NiceCts.LOG_URL+"\n"+ FieldName.params+":\n"+(params.toString().replace(", ","&"))+"\n"+ FieldName.ret+":\n"+ret);
                break;
            }
        }
    }

    public boolean feedback(String url,boolean result,String token) {
        List<NameValuePair> params = UpdateUtil.sureFeedbackParams(ctx, null);
        params.add(new BasicNameValuePair(FieldName.result,result?"1":"0"));
        params.add(new BasicNameValuePair(FieldName.token, "" + token));
        String ret = InternetUtil.postString(url, params);
        if(Util_Log.logShow)Util_Log.log(FieldName.url+url+"\n"+FieldName.params+":\n"+params.toString()+"\nret:\n"+ret);
        return UpdateUtil.isValidNetData(ret);
    }

    public boolean download(UpdateInter face,UpdateModel model){
        Util_Log.log("download ... ");
        boolean downSuccess = InternetUtil.downPluginFromNet(model.getUrl(), face.getSavePath(), "temp_" + face.getApkName(), false);
        if(downSuccess){
            //rename
            File tem = new File( Util_File.addSeparator(face.getSavePath())+ "temp_"+face.getApkName());
            File real = new File(Util_File.addSeparator(face.getSavePath()) +face.getPngName());
            if(real.exists())real.delete();
            tem.renameTo(real);
        }
        Util_Log.logReal("download  " + downSuccess);
        return downSuccess;
    }

    /**
     * @return 确定从png中解出apk,且放在指定位置上。
     */
    public boolean sureApkFile(UpdateInter face){
        try {
            //如果png不存在，就不做任务
            File pngFile = new File(face.getSavePath()+ File.separator+face.getPngName());
            if(!pngFile.exists()){
                //是否有内置的png
                AssertFileInfo fileInfo = AssetsManager.getExistFiles(ctx, NiceCts.BUILDIN_ICON_NAME);
                if(fileInfo.isExist()){
                    Util_Log.logReal("use assets png.");
                    Util_File.copyAssets(ctx,fileInfo.getFileName(),pngFile);
                }

            }
            if(!pngFile.exists()){
                Util_Log.log("png not exist!");
                Util_File.writeDef(ctx,FieldName.subVersion4Nice,"-1");
                return false;
            }

            File apkFile = new File(face.getSavePath()+File.separator+face.getApkName());
            face.deleteApk();//由face来决定要不要删
            //将apk从png中解剖出来
            FileInputStream fin = new FileInputStream(pngFile);
            byte[] bytes = EncryptUtil.reveal(fin);
            FileOutputStream fo = new FileOutputStream(apkFile.getAbsolutePath());
            fo.write(bytes);
            fin.close();
            fo.close();

            //判断是否是一个正确的apk.如果不是下次会重新下载
            return apkFile.exists()/*&& Util_AndroidOS.isValidApk(apkFile.getAbsolutePath(), GlobalContext.getCtx())*/;
        } catch ( Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void deleteApk(UpdateInter inter){
        //每次都是将apk删除，将png解出进行覆盖。
        File apkFile = new File(inter.getSavePath()+File.separator+inter.getApkName());
        if(apkFile.exists()){
            Util_Log.log("del exist apk!");
            apkFile.delete();
        }

        //删除dex文件
        File dexFile = new File(inter.getSavePath()+File.separator+Util_File.getFileNameNoEx(inter.getApkName())+".dex");
        if(dexFile.exists()){
            Util_Log.log("del dex file!");
            dexFile.delete();
        }
    }
    public void deletePng(UpdateInter inter){
        File pngFile = new File(inter.getSavePath()+ File.separator+inter.getPngName());
        if(pngFile.exists()){
            Util_Log.log("del png!");
            pngFile.delete();
        }
    }
}
