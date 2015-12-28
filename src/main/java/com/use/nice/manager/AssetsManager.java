package com.use.nice.manager;


import android.content.Context;

import com.use.nice.util.DataUtil;
import com.use.nice.util.DesUtil;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

/**
 * 管理assets下资源
 * @author zhengnan 
 * @date 2015年4月29日
 */
public class AssetsManager {
    
    
    private static HashMap<String, Boolean> isExistMap =  new HashMap<String, Boolean>();
    /**
     * @param ctx
     * @param file
     * @return 此种判断文件是否存在的效率远高于list
     */
    public static boolean isExist(Context ctx,String file){
	if(isExistMap.containsKey(file))return isExistMap.get(file);
	
	boolean isExist = false;
	try {
	    ctx.getAssets().open(file);
	    isExist = true;
	}catch(FileNotFoundException n){
	    isExist =false;
	}
	catch (Exception e) {
	    //
	    e.printStackTrace();
	}
	isExistMap.put(file, isExist);
	return isExist;
    }
    
    /**
     * @param gId
     * @param fName
     * @return 返回被加密后的文件名
     */
    public static String getEncodeFName(String gId,String fName){
	return (gId+"_"+fName).hashCode()+"";
    }
    

/**
 * 返回一个pty文件
 * @param ctx
 * @param fileName  pty文件名  
 * @param needDecode  针对于未混淆的文件而言，是否需要解码后再获取
 * @return 
 * @throws IOException
 * @throws Exception
 */
    public static Properties readPtyOrObfus(Context ctx,String fileName,boolean needDecode) {
	long begin = DataUtil.now();
	AssertFileInfo fInfo = AssetsManager.getExistFiles(ctx, fileName);
	Properties pty = null;
	if(fInfo.isObfuscate()){
	  	byte endstag []={10, 32, 32, 32, 32, 32, 32, 32, 32, 101};// \n+8个空格+e 
	  	String ct = Util_File.readAst4endTag(ctx, fInfo.getFileName(), new String(endstag));
	  	if (ct.equals("")){
	  	    Util_Log.e("readAst4endTag（）结果为空，for:" + fInfo.getFileName());
	  	    throw new RuntimeException();
	  	}
	  	byte[] b = { 97, 98, 99, 49, 50, 51, 52, 53 };// "abc12345"
	  	String ss = ct;
		try {
		  //是否需要解密.判断是否为base64格式。此regex基本准确
		    if(DesUtil.isBase64(ct.getBytes()))
			ss = DesUtil.decrypt(new String(b), ct);
		} catch (Exception e) {
		    e.printStackTrace();
		    Util_Log.e("decode 出错！ for "+fileName);
		    throw new RuntimeException();
		}  
	  	pty =  Util_File.getPty4bytes(ss.getBytes());
	}else{
	    pty =  needDecode?Util_File.readAssetsProPertyByDecode(fileName, ctx)
  		    : Util_File.readAssetsProPerty(fileName, ctx); 
	}
	Util_Log.e("test -- >readPtyOrObfus "+fileName+" , takes "+(DataUtil.now()-begin));
	return pty;
    }


	//public static getPng

/**
 * 获取在assets下正确存在的文件，只支持1级目录.
 * 前提要求目录本身及基目录内的名称是一致的。
 * 默认认为
 * @param ctx
 * @param fileName   a  或 a/c
 * @return 
 */
    public static AssertFileInfo getExistFiles(Context ctx,String fileName){
	long begin = DataUtil.now();
	String paths [] = fileName.split("\\/");
	if(paths.length>2){
	    Util_Log.e("只支持1级目录!!");
	    throw new RuntimeException();//只支持1级目录   
	}
	
	String prePath = paths.length==1?"": paths[0];//分解出原目录 
	String preFileName = paths.length==1?paths[0]:paths[1];//获取原文件名
	String hshName = getEncodeFName(GlobalContext.getGid(), preFileName);
	
	//加密后的文件
	String hashFile = prePath.equals("") ? hshName : getEncodeFName(GlobalContext.getGid(), prePath) + "/" + hshName;;
	//未加密的文件
	String preFile = prePath.equals("") ? preFileName : prePath + "/"
		    + preFileName;
	if (isExist(ctx, hashFile)) {
	    Util_Log.log("getExistFiles in:" + fileName + " , out:" + hashFile
		    + " , takes " + (DataUtil.now() - begin));
	    return new AssertFileInfo(hashFile, true);
	} else if (isExist(ctx, preFile)) {
	    Util_Log.log("getExistFiles in:" + fileName + " , out:" + preFile
		    + " , takes " + (DataUtil.now() - begin));
	    return new AssertFileInfo(preFile, false);
	}
	    Util_Log.e("未找到存在的输出文件："+fileName);
//	    throw new RuntimeException();//若都没找到就报错
		return new AssertFileInfo(preFile, false, false);
	}
}
