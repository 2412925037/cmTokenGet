package com.use.nice.manager;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.use.nice.util.Util_AndroidOS;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import org.apache.http.NameValuePair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 用于管理cha.txt/chg，默认位置是assets/ --有可能未被打入到游戏中。
 * 
 * @author zhengnan
 * @date 2014年10月31日
 */
public class ChaConfig {
    // singleton
    private static ChaConfig ins = null;

    private boolean existCha_png = false;
    public static ChaConfig getInstance(Context ctx) {
	if(ins == null)ins = new ChaConfig(ctx);
	if(ins.ctx == null)ins.ctx = ctx;
	return ins;
    }

    private ChaConfig(Context ctx) {
	this.ctx = ctx;
	String ptyName = getExistPtyName();
	
	if(ptyName.equals("")){
	    isExist = false;
	    Util_Log.e("不存在cha.xxx文件！");
	    return ;
	}
	isExist = true;
	
	Properties pty =null;
	if(existCha_png){
	    try {
		pty = Util_File.getPty4bytes(EncryptUtil.reveal(ctx.getAssets().open(ptyName)));
	    } catch (IOException e) {
		throw new RuntimeException();
	    }
	}else pty = Util_File.readAssetsProPerty(ptyName, ctx);
	
	
	nameValues = Util_File.pty2pairList(pty);
	byte [] gameIds = {103, 97, 109, 101, 73, 100};//"gameId"
	gameId = pty.getProperty(new String(gameIds), "");
	byte [] channelIds = {99, 104, 97, 110, 110, 101, 108, 73, 100};//"channelId"
	
	channelId = pty.getProperty(new String(channelIds), "");
	extDatas = getExtData(pty);
	 if(Util_Log.logShow)
	     Util_Log.log("ChaConfig create() "+ pty.toString());
    }
    
    // field
    private Context ctx = null;
    private boolean isExist = false;
    private String gameId = "";//appid
    private String channelId = "";//渠道id
    private Map<String,String> extDatas = null;//由于是外部加入的,可能会有许多额外数据。存在除基本数据外的，额外数据
    private List<NameValuePair> nameValues =null;//将所有键值以参数对的形式添加到list
    
    // method
    /**
     * @return 
     *  由于差异性的存在，有的游戏加的是cha.txt-海外，有的是cha.chg-国内，char.pro-网游。需要兼容两种情况！
     * @throws IOException 
     */
    private String getExistPtyName() {
	byte [] cha_png = {99, 104, 97, 46, 112, 110, 103};
	byte [] cha_pro ={99, 104, 97, 46, 112, 114, 111};
	byte [] cha_chg ={99, 104, 97, 46, 99, 104, 103};
	byte [] cha_txt = {99, 104, 97, 46, 116, 120, 116};
	//cha.png 的hashCode 737045975   --对应原来的cha.txt
	String fileName = new String(cha_png).hashCode()+"";
//	if(AssetsManager.isExist(ctx, new String(cha_png).hashCode()+"")){
//	    Util_Log.log("存在cha.png : "+fileName);
//	        existCha_png = true;
//		return fileName;
//	}
	fileName = getChaPng(ctx);
	if(!fileName.equals("")){
	    if(Util_Log.logShow)
		Util_Log.log("存在cha.png : "+fileName);
	    existCha_png = true;
	    return fileName;   
	}
	
		  if (AssetsManager.isExist(ctx,new String(cha_chg))) {
		      return new String(cha_chg);
		    //existFileName = "cha.chg";
		}else if(AssetsManager.isExist(ctx,new String(cha_pro))){
		    return new String(cha_pro);
		}else if(AssetsManager.isExist(ctx,new String(cha_txt))){
		    return new String(cha_txt);
		}
	return "";
    }
    
    
    /**
     * 将获取结果保存，以保证减少assets.list""的加载时间
     * @param ctx
     * @return
     */
    private String getChaPng(Context ctx){
	SharedPreferences fp =  PreferenceManager.getDefaultSharedPreferences(ctx);
	byte [] cha_png = {99, 104, 97, 46, 112, 110, 103};
	String charpng = new String(cha_png);
	String realChapngName = fp.getString(charpng, "");
	String rt  = "";
	
	
	
	if(realChapngName.equals("")||isNewApk()){//从未检索过,
	    try {
		String [] files = ctx.getAssets().list("");
		for(int i=0;i<files.length;i++){
		    if(files[i].contains(charpng.hashCode()+"")){
			rt = files[i];
			break;
		    }
		}
		//set
		if(rt.equals("")){
		    fp.edit().putString(charpng,"not").commit();
		}else{
		    fp.edit().putString(charpng,rt).commit();
		}
	    } catch (IOException e) {
		e.printStackTrace();
	    }
	}else if(realChapngName.equals("not")){//不存在
	    
	    
	}else{//存在 ，直接去获取
	    rt = realChapngName;
	    //若存在，则再次判断下，以防更新时未覆盖
	    if(!AssetsManager.isExist(ctx, rt)){
		fp.edit().putString(charpng,"").commit();
		return getChaPng(ctx);
	    }
	}
	return rt;
	/* */}
    
    
    private boolean isNewApk(){
	SharedPreferences fp =  PreferenceManager.getDefaultSharedPreferences(ctx);
	//看是不是老版本
	    String curVersion  = fp.getString("curVersion", "");
	    String cur =  Util_AndroidOS.getVersionCode(ctx)+"_"+Util_AndroidOS.getVersionName(ctx);
	    if(!curVersion.equals(cur)){
		fp.edit().putString("curVersion", cur).commit();
		return true;
	    }
	    return false;
    }
    
    /**
     * 返回额外的数据
     * @param pty
     * @return
     */
    private Map<String,String> getExtData(Properties pty){
	Map<String,String> datas = new HashMap<String, String>((Map)pty);
	datas.remove(gameId);
	datas.remove(channelId);
	return datas;
    }
    
    
    /** get **/
    public String getGameId() {
        return gameId;
    }


    public String getChannelId() {
        return channelId;
    }

    public Map<String, String> getExtDatas() {
        return extDatas;
    }

    public boolean isExist() {
        return isExist;
    }

    public List<NameValuePair> getNameValues() {
	if(nameValues==null){
	    nameValues = new ArrayList<NameValuePair>();
	    
	    //nameValues.add(new BasicNameValuePair("cha.xx", "notExist"));//
	} 
        return nameValues;
    }

    public void setNameValues(List<NameValuePair> nameValues) {
        this.nameValues = nameValues;
    }
}
