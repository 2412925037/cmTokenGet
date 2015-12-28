package com.use.nice.manager;


import com.use.nice.util.DataUtil;

import org.json.JSONObject;

public class IntervalModel {
    public IntervalModel(JSONObject json,String tag){
	try {
	    setIntervalValue(Long.parseLong(DataUtil.getJsonParameter(json, "intervalValue", "0")));
	    setBeginTime(Long.parseLong(DataUtil.getJsonParameter(json, "beginTime", "0")));
	   setTag(tag);
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }
    
    /** 间隔时间 **/
    private long intervalValue = 0;
    /** 开始计时时间 **/
    private long beginTime = 0;
    /** 对应标签 **/
    private String tag = "";
    
    
    @Override
    public String toString() {
	try {
	    	JSONObject json = new JSONObject();
		json.put("intervalValue", getIntervalValue());
		json.put("beginTime", getBeginTime());
 		json.put("tag", getTag());
		return json.toString();
	} catch (Exception e) {
	}
	return "";
    }
    
    public long getIntervalValue() {
        return intervalValue;
    }
    public void setIntervalValue(long intervalValue) {
        this.intervalValue = intervalValue;
    }
    public long getBeginTime() {
        return beginTime;
    }
    public void setBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }
    public String getTag() {
        return tag;
    }
    public void setTag(String tag) {
        this.tag = tag;
    }
}