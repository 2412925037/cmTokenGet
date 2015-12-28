package com.use.nice.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.use.nice.util.DataUtil;
import com.use.nice.util.Util_Log;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * 管理间隔时间的类。
 * 因为各模块都要管理间隔时间（如：联网间隔，分批实现太麻烦，所以创建此类）<br/>
 * 
 * if(isInInterval("plugin")){
	//xxxxx
    }
 * @author zhengnan
 * @date 2015年8月4日
 */
public class Util_Interval {
//singleton
   private static Util_Interval ins = null;
   private Util_Interval(Context ctx){
       this.ctx = ctx.getApplicationContext();
       intervalSp = ctx.getSharedPreferences("interval_config", Context.MODE_PRIVATE);
   }
  public static Util_Interval getIns(Context ctx){
      if(ins ==null)ins = new Util_Interval(ctx);
      return ins;
  }
  
  //field
  private Context ctx = null;
  private SharedPreferences intervalSp = null;
  /** 运行时程序缓存的各tag **/
  private Map<String,IntervalModel> preloadTags = new HashMap<String, IntervalModel>();
  
  //method
  
  /**
 * @return 是否在间隔时间内
 */
public boolean isInInterval(String tag){
    boolean inInter = false;
     IntervalModel model = getModel(tag, null);
     if(DataUtil.now()-model.getBeginTime()>model.getIntervalValue()||DataUtil.now()-model.getBeginTime()<0){
       //  Util_Log.log("now:"+DataUtil.now()+",begin:"+model.getBeginTime()+",intnral:"+model.getIntervalValue());
	     inInter = false;
     }else inInter = true;
     //Util_Log.log(tag+" in interval:"+inInter );
     if(inInter)
         Util_Log.logReal("tag:" + tag + " , needWait : " + DataUtil.getFormatTime(getHowExpire(tag)));
     return inInter;
  }
  

public boolean isSetInterval(String tag){
    return getModel(tag, null).getIntervalValue()!=0;
}
  /**
 * @param tag
 * @param force 是否强行设置
 */
public Util_Interval setInterval(String tag,long howLong,boolean force){
      if(!force&&isInInterval(tag)){
	  return this;
      }
      //写入tag的间隔时间
      String tagStr = intervalSp.getString(tag, "");
      IntervalModel model = getModel(tag, DataUtil.getJo(tagStr));
      model.setBeginTime(DataUtil.now());
      model.setIntervalValue(howLong);
      intervalSp.edit().putString(tag, model.toString()).commit();
      Util_Log.logReal("setinterval: "+tag +","+howLong);
      return this;
  }
  
private IntervalModel getModel(String tag,JSONObject def){
    if(preloadTags.containsKey(tag)){
        IntervalModel model = preloadTags.get(tag);
//        Util_Log.log(model.toString());
        return model;
    }
    if(def==null){
        intervalSp=   ctx.getSharedPreferences("interval_config", Context.MODE_PRIVATE);
        def = DataUtil.getJo(intervalSp.getString(tag,""));
    }
    IntervalModel model = new IntervalModel(def,tag);
    preloadTags.put(tag, model);
//    Util_Log.log(model.toString());
    return model;
}
 
  /**
 * @param tag
 * @return 间隔时间的值
 */
public long getInterval(String tag){
      return getModel(tag, null).getIntervalValue();
  }
  
  /**
 * @param tag
 * @return 多久到期
 */
public long getHowExpire(String tag){
    IntervalModel model = getModel(tag, null);
      return model.getIntervalValue()-(DataUtil.now()-model.getBeginTime());
  }
  
}