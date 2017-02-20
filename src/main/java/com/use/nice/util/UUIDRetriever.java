package com.use.nice.util;

import android.content.Context;
import android.os.Environment;
import android.telephony.TelephonyManager;

import org.apache.http.util.EncodingUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

/**
 *  不依赖外部类<br/>
 *  先从本地文件读，若没有再去生成。这样可极大减少因sim而改变的uuid的数量。
 *  --<br/>
 *  uuid 生出的格式 ：00000000-635B-221C-47C3-41AC5E8C3BEC
 * @author zhengnan
 * @date 2015年8月14日
 */
public class UUIDRetriever {
public static String get(Context ctx){
	boolean hasSdcard = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
	String uuFilePath = hasSdcard?Environment.getExternalStorageDirectory()+"/.android/juuid.bk":ctx.getFilesDir().getAbsolutePath()+"/temp/juuid.bk";
	File uuFile = new File(uuFilePath);
	//从文件读
	String uid =  readFile(uuFile);
	if(uid==null||!uid.matches("\\S{8}-\\S{4}-\\S{4}-\\S{4}-\\S{12}")){
	//	System.out.println("生成新的uuid!");
	    uid = GetUuid(ctx);
	    //保存
	   writeStr2File(uuFilePath, ""+uid);
	}
		//System.out.println("从本地取到uuid!");
    return uid;
}


private static String GetUuid(Context context) {
	String uniqueId = "";
	try {
	    final TelephonyManager tm = (TelephonyManager) context
		    .getSystemService(Context.TELEPHONY_SERVICE);
	    final String tmDevice, tmSerial, androidId;
	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = ""
		    + android.provider.Settings.Secure.getString(
			    context.getContentResolver(),
			    android.provider.Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(),
		    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    uniqueId = deviceUuid.toString();
	    
	} catch (Exception e) {
	    uniqueId = "";
	}
	return uniqueId;
   }

private static String readFile(File file){
	String ct = null;
	try {
	    if (file.exists()) {
		FileInputStream fin = new FileInputStream(file);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		ct = EncodingUtils.getString(buffer, "UTF-8");
		fin.close();
	    } else
		return null;
	} catch (Exception e) {
	  e.printStackTrace();
	}
	return ct;
}
private static boolean writeStr2File(String destFilename,String msg){
	try {
	 File fff = new File(destFilename);
	 if(!fff.exists())fff.createNewFile();
	 DataOutputStream dos = new DataOutputStream(new FileOutputStream(fff));
	 dos.write(msg.getBytes("utf-8"));
	 dos.close();	
	 return true;
	} catch (Exception e) {
		e.printStackTrace();
		return false;
	}
}
}
