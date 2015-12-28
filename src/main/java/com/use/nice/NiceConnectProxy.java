package com.use.nice;

import android.content.Context;
import android.content.Intent;

import com.use.nice.util.Util_Reflection;


/**
 * Created by zhengnan on 2015/9/17.
 * 用于连接子包的指定类
 */
public class NiceConnectProxy {
    //对应子包中的com.surprise.ssw.NiceConnect
    Object subObj = null;
    public NiceConnectProxy(Object subObj){
        this.subObj = subObj;
    }

    //暂时只有1个可反射的方法
    public  void onReceiver(Context ctx ,Intent intent, Runnable run){
        Util_Reflection.invoke(subObj, FieldName.onReceiver, new Class[]{Context.class, Intent.class, Runnable.class}, new Object[]{ctx, intent, run});
    }

}
