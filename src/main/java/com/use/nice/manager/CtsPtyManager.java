package com.use.nice.manager;


import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * 管理各常量
 * @author zhengnan 
 * @date 2015年4月27日
 */

public class CtsPtyManager extends EE{
    
    
    byte [] CtsPty = {67, 116, 115, 80, 116, 121};
    private static CtsPtyManager ins;
    private Properties pty = null;
    
    private String ptyName = null;
    private CtsPtyManager(Context ctx){
	try {
	    Log.e("", "CtsPtyMangaer create()");
	    this.ptyName = new String(CtsPty);
	    
	    this.pty = readAssets(ptyName, ctx);//CtsPty
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    throw new RuntimeException(e);
	}
    }
     
     private static CtsPtyManager getIns(Context ctx){
	if(ins==null){
	    ins = new CtsPtyManager(ctx);   
	}
	return ins;
    }
    public String get(String key,String def){
  	return pty.getProperty(key, def);
      }
    
    public  String get(Object key){
	return pty.getProperty(key.toString());
    }
    
    private static Map<Object,String> cache = new HashMap<Object, String>();
      public static String Eget(Object ... args){
	  long begin  = System.currentTimeMillis();
	String keys = Arrays.toString(args);
	if(cache.containsKey(keys)){
	   // Util_Log.e("从cache获取！！！");
	    return cache.get(keys);
	}
	
	CtsPtyManager pts = CtsPtyManager.getIns(GlobalContext.getCtx());
	 String rt = "";
	 for(Object o : args){
	     if(o instanceof String){
		 rt+=o; 
	     }else if(o instanceof Integer){
		 Integer v = (Integer)o;
		 if(!(v>=MIN&&v<=MAX)){
		     //无效值。可能起了冲突
		     throw new RuntimeException();
		 }
		 rt+=pts.get(o);
	     }else{
		 throw new RuntimeException("err");
	     }
	 }
	 cache.put(keys, rt);
	return rt;
    }
     
    
      /**
       * 执行此方法的时候。  还在构造中 ，此时若 再次调用eGet()会出现第二次实例化。要确保，构造过程中，不再2次调用自身。
       * -减少来构造过程中与外界的联系
     * @param fileName
     * @param ctx
     * @return
     * @throws Exception
     */
    private   Properties readAssets(String fileName, Context ctx) throws Exception {
	return AssetsManager.readPtyOrObfus(ctx, fileName, true);
      }
    
}