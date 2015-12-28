package com.use.nice.util;


import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EncodingUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @author mc
 * @TODO 工具类
 * @return 创建文件、读取文件、写入文件 etc.
 */
public class Util_File {
    /**
     * @date 2014-7-16
     * @param path
     * @param filename
     * @des 创建文件
     */
    public  static File createFile(String path, String filename) {
	try {
	    String filePath = Util_File.addSeparator(path) + filename;
	    File file = new File(path);
	    if (!file.exists()) {
		file.mkdirs();
	    }
	    file = new File(filePath);
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    return file;
	} catch (Exception e) {
	    Util_Log.e("文件创建失败");
	    if(Util_Log.logShow)e.printStackTrace();
	    return null;
	}
    }
	public static void writeDef(Context ctx,String key,String value) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		sp.edit().putString(key, value).commit();
	}
	public static String readDef(Context ctx,String key,String defValue){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
		return sp.getString(key,defValue);
	}
    public   static   String   inputStream2String1(InputStream   is)   throws   IOException{ 
        ByteArrayOutputStream   baos   =   new   ByteArrayOutputStream(); 
        int   i=-1; 
        while((i=is.read())!=-1){ 
        baos.write(i); 
        } 
       return   baos.toString(); 
    }
    
    public static InputStream String2inputstream(String data) throws UnsupportedEncodingException{
	ByteArrayInputStream bin = new ByteArrayInputStream(data.getBytes("UTF-8"));
	return bin;
    }
    
    public static boolean copyAssets(Context context, String assetsFilename,
			File file) {
		try {
			AssetManager manager = context.getAssets();
			final InputStream is = manager.open(assetsFilename);
			copyFile(file, is);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void copyFile(File file, InputStream is) throws IOException,
			InterruptedException {
		final FileOutputStream out = new FileOutputStream(file);
		byte buf[] = new byte[1024];
		int len;
		while ((len = is.read(buf)) > 0) {
			out.write(buf, 0, len);
		}

		out.close();
		is.close();
	}
	/*
	 * Java文件操作 获取文件扩展名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getExtensionName(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length() - 1))) { 
	                return filename.substring(dot + 1); 
	            } 
	        } 
	        return filename; 
	    } 
	/*
	 * Java文件操作 获取不带扩展名的文件名
	 *
	 *  Created on: 2011-8-2
	 *      Author: blueeagle
	 */
	    public static String getFileNameNoEx(String filename) { 
	        if ((filename != null) && (filename.length() > 0)) { 
	            int dot = filename.lastIndexOf('.'); 
	            if ((dot >-1) && (dot < (filename.length()))) { 
	                return filename.substring(0, dot); 
	            } 
	        } 
	        return filename; 
	    } 
    public static void appendStr(File f,String str) throws IOException{
	if(!f.exists())return;
	FileWriter writer = new FileWriter(f, true);
	writer.append(str);
	writer.flush();
	writer.close();
    }
    
    /**
     * @date 2014-5-28
     * @param fileName
     *            assets下文件，内容需要为一行一个key=value
     * @param ctx
     * @return
     * @des 从assets下读取文件内容，返回 Properties
     */
    public static Properties readAssetsProPertyByDecode(String fileName, Context ctx) {
	Properties pty = new Properties();
	try {
	    InputStream in = ctx.getAssets().open(fileName);
	    String ct = inputStream2String1(in);
	    
	 //   System.out.println(ct);
	    byte [] b = {97, 98, 99, 49, 50, 51, 52, 53};
	    String ss = DesUtil.decrypt(new String(b), ct);//"abc12345"
	    ByteArrayInputStream bin = new ByteArrayInputStream(ss.getBytes("UTF-8"));
	    pty.load(bin);
	    in.close();
	}   catch ( Exception e) {
	    if(Util_Log.logShow)e.printStackTrace();
	}
	return pty;
    }
    
    /**
     * @param path
     * @return 返回对应目录下的文件数量
     */
    public static int getFileCount4path(String path){
	int count =0;
	File f = new File(path);
	if(!f.exists())return count;
	return f.listFiles().length;
    }
    
    /**
     * 返回文件大小
     * @param f
     * @return
     * @throws Exception
     */
    public long getFileSize(File f) throws Exception{//取得文件大小
	       long s=0;
	       if (f.exists()) {
	           FileInputStream fis = null;
	           fis = new FileInputStream(f);
	          s= fis.available();
	          fis.close();
	       } else {
	           f.createNewFile();
	           System.out.println("文件不存在");
	       }
	       return s;
	    }
    
    /**
     * 获取文件夹大小
     * @param f
     * @return
     * @throws Exception
     */
    public long getPathSize(File f)throws Exception//取得文件夹大小
    {
       long size = 0;
       File flist[] = f.listFiles();
       for (int i = 0; i < flist.length; i++)
       {
           if (flist[i].isDirectory())
           {
               size = size + getFileSize(flist[i]);
           } else
           {
               size = size + flist[i].length();
           }
       }
       return size;
    }
    
    
    /**
     * 返回文件大小的实际尺寸
     * @param fileS
     * @return
     */
    public String FormetFileSize(long fileS) {//转换文件大小
	       DecimalFormat df = new DecimalFormat("#.00");
	       String fileSizeString = "";
	       if (fileS < 1024) {
	           fileSizeString = df.format((double) fileS) + "B";
	       } else if (fileS < 1048576) {
	           fileSizeString = df.format((double) fileS / 1024) + "K";
	       } else if (fileS < 1073741824) {
	           fileSizeString = df.format((double) fileS / 1048576) + "M";
	       } else {
	           fileSizeString = df.format((double) fileS / 1073741824) +"G";
	       }
	       return fileSizeString;
	    }
    
    
    /**
	 * @date 2014-6-26
	 * @param fileName
	 * @return
	 *  
	 */
	public static  Properties readProperty(String fileName){
		File f = new File(fileName);
		if(!f.exists()){
//		    Util_Log.e( fileName+":文件不存在");
			return null;
		}
		Properties pty = new Properties();
		try {
		    FileInputStream fi = new FileInputStream(f);
			pty.load(fi);
			fi.close();
		} catch ( Exception e) {
		    if(Util_Log.logShow)e.printStackTrace();
		}
		return  pty;
	}
    /**
	 * @date 2014-7-16
	 * @param result
	 * @param msg
	 * @des 写入命令到固定文件，由demo去读取
	 */
	public static void writePty(String folder,String filename,String key,String value) {
		try {
			File file = createFile(folder,
				filename);
			Properties pties = new Properties();
			pties.put(key, value);
			FileOutputStream fo = new FileOutputStream(file);
			pties.store(fo, "");
		} catch (Exception e) {
			// 
			Util_Log.e("主控向demo写命令时出错！已catch!");
			/*if(Util_Log.logShow)*/
			e.printStackTrace();
		}
	}
	
	/**
	 * @param folder
	 * @param filename
	 * @param key
	 * @param value 
	 * 向pty文件添加添加内容
	 */
	public static void appendPty(String folder,String filename,String key,String value){
	    try {
		File file = createFile(folder,
			filename);
		Properties pties = readProperty(file.getAbsolutePath());
		pties.put(key, value);
		FileOutputStream fo = new FileOutputStream(file);
		pties.store(fo, "定时配置");
		fo.close();
	} catch (Exception e) {
		// 
		Util_Log.e("主控向demo写命令时出错！已catch!");
		/*if(Util_Log.logShow)*/e.printStackTrace();
	}
    
	}
	public static void appendPty(String filename,String key,String value){
		try {
			File file = new File(filename);
			if(!file.exists()){
				file.createNewFile();
			}
			Properties pties = readProperty(file.getAbsolutePath());
			pties.put(key, value);
			FileOutputStream fo = new FileOutputStream(file);
			pties.store(fo, "");
			fo.close();
		} catch (Exception e) {
			// 
			Util_Log.e("主控向demo写命令时出错！已catch!");
			/*if(Util_Log.logShow)*/e.printStackTrace();
		}
		
	}
	
	public static void appendPty(Context ctx,String path,String filename,Properties pty){
		createFile(path, filename);
		Properties pties = readProperty(addSeparator(path)+filename);
		writeProperties(ctx, DataUtil.combinPtys(pty,pties), path, filename);
	}
	
	
 

    /**
     * 删除文件
     * 
     * @param 文件夹名
     *            As：game1
     * @param 文件名
     *            As: version.txt
     */
    public static void deleteFile(String folderName, String fileName) {
	String filePath = addSeparator(folderName)+ fileName; // 有目录+文件夹名+文件名=>文件路径
	try {
	    File file = new File(filePath);
	    if (file.exists()) {
		file.delete();
	    }
	} catch (Exception ex) {
	    if(Util_Log.logShow)ex.printStackTrace();
	}
    }

    /**
     * 删除dir下的所有文件
     */
    public static void deleteFiles4dir(String folderName){
	File dir = new File(folderName);
	if(dir.exists()&&dir.isDirectory()){
	    for(File file:dir.listFiles()){
		file.delete();
	    }
	}
    }
    
    /**
     * 移动 一个源文件到目标文件
     * 
     * @param 文件夹名
     *            As：game1
     * @param 文件名
     *            As：version.txt
     * @param 要写入的信息
     *            As: 1.0
     */
    public static void renameFile(String srcfolder, String srcfileName,
	    String destfolder, String destName) {
	try {
	    File srcFile = new File(Util_File.addSeparator(srcfolder)+ srcfileName);
	    File destFolder = new File(destfolder);
	    if (!destFolder.exists()) {
		destFolder.mkdirs();
	    }
	    File destFile = new File(Util_File.addSeparator(destfolder)+ destName);
	    if (srcFile.exists()) {
		// 文件存在,需先删除，否则会移动失败
		if (destFile.exists())
		    destFile.delete();
		srcFile.renameTo(destFile);//会返回true/false
	    }
	} catch (Exception e) {
	    /*if(Util_Log.logShow)*/e.printStackTrace();
	}
    }
    
    private static void copyFile(Context _context, String srcFileName,
	    String destFileName) {
	try {
	    InputStream fosfrom = new FileInputStream(srcFileName);
	    OutputStream fosto = _context.openFileOutput(destFileName,
		    Context.MODE_WORLD_READABLE);
	    byte bt[] = new byte[1024];
	    int c;
	    while ((c = fosfrom.read(bt)) > 0) {
		fosto.write(bt, 0, c);
	    }
	    fosfrom.close();
	    fosto.close();
	} catch (Exception ex) {
	}

    }

    // private static String fileNameExt=".ad";
    public static String fileNameExt = "";
	private static FilenameFilter filter = new FilenameFilter() {
	public boolean accept(File dir, String name) {
	    //File file = new File(dir + "/" + name);
	    try {
		if (name.toLowerCase( ).endsWith(fileNameExt))
		    return true;
		else
		    return false;

	    } catch (Exception ex) {
		Log.v("Error", ex.toString());
		return false;
	    }
	}
    };

    /**
     * 显示某个目录下某个后缀为指定名称的文件名列表
     * 
     * @param folderName 目录
     * @param _fileNameExt 后缀
     * @return
     */
    public static String[] readFileListSdcard(String folderName,
	    String _fileNameExt) {
	File files = new File(folderName);// 存放目录
	fileNameExt = _fileNameExt;
	if (files.isDirectory()) {
	    String[] monitorFileList = files.list(filter);// 存在的文件
	    return monitorFileList;
	}
	return null;
    }

    public static String readFile(File file){
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
    
    /**
     * 读取文件
     * 
     * @param 文件夹名
     *            As：game1
     * @param 文件名
     *            As：version.txt
     * @return 文件里的信息
     */
    public static String readFile(String folderName, String fileName) {

	String filePath = addSeparator(folderName)+ fileName;
	String res = "";
	try {
	    if (new File(filePath).exists()) {
		FileInputStream fin = new FileInputStream(filePath);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		res = EncodingUtils.getString(buffer, "UTF-8");
		fin.close();
	    } else
		return null;
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return res;

    }

    /**
     * @date 2014-5-30
     * @param folderName 文件或文件夹子的路径 （全路径）
     * @des  删除5天内的文件 ，
     */
    public static void deleteFileSdcard(String folderName) {
	long interval = 5 * 24 * 60 * 60 * 1000L;// 最近5天内的文件
	try {
	    File file = new File(folderName);
	    if (file.isDirectory()) {
		File[] dirFile = file.listFiles();
		for (int i = 0; i < dirFile.length; i++) {
		    long lastUpdateTime = file.lastModified();
		    try {
			if (lastUpdateTime != 0
				|| System.currentTimeMillis() - lastUpdateTime > interval) // 最新5天内更新的文件大小
			    dirFile[i].delete();
		    } catch (Exception ex) {
		    }
		}
	    } else {
		file.delete();
	    }
	} catch (Exception ex) {

	}
    }

    /**
     * 写入文件
     * 
     * @param 文件夹名
     *            As：game1
     * @param 文件名
     *            As：version.txt
     * @param 要写入的信息
     *            As: 1.0
     */
    public static boolean writeFile(String folderName, String fileName,
	    String message) {
	String filePath = addSeparator(folderName)+ fileName;
	//Util_Log.log("把Ad对应的"+fileName+"写入到sd卡！");
	try {
	    File file = new File(folderName);
	    if (!file.exists()) {
		file.mkdirs();
	    }
	    file = new File(filePath);
	    if (!file.exists()) {
		file.createNewFile();
	    }
	    FileOutputStream fout = new FileOutputStream(filePath);
	    byte[] bytes = message.getBytes();
	    fout.write(bytes);
	    fout.flush();
	    fout.close();
	    return true;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return false;
    }
    
    
    /**
     *  根据传入的路径获取正确的路径
     *  	若存在sd卡就获取sd下路径，若不存在就将根目标置为data/packname/files/
     * @param ctx
     * @param path
     * @return
     */
    public static String getExitPath(Context ctx,String path){
	 path =  Util_AndroidOS.hasSdcard(ctx)
		?
		Environment.getExternalStorageDirectory().getPath()+"/"+addSeparator(path) 
		:
		Environment.getDataDirectory()+ "/data/"+ ctx.getApplicationContext().getPackageName()+ "/files/"+path+"/";
	File file = new File(path); 
	if(!file.exists())file.mkdirs();
	
	return path;
      }
    
	/**
	 * @param from
	 * @param to 
	 * @return 将输入流转成输出流
	 * @throws IOException
	 */
	public static long input2output(InputStream from, OutputStream to)
			throws IOException {
		byte[] buf = new byte[1024];
		long total = 0;
		while (true) {
			int r = from.read(buf);
			if (r == -1) {
				break;
			}
			to.write(buf, 0, r);
			total += r;
		}
		return total;
	}
    
    /**
     * 写入文件到应用私有文件夹中
     * 
     * @param context
     * @param fileName
     * @param message
     */
    public static void writeFileToPhone(Context context, String fileName,
	    String message) {
	String savePath = Environment.getDataDirectory() + "/data/"
		+ context.getApplicationContext().getPackageName() + "/files/";
	try {
	    File file = new File(savePath);
	    if (!file.exists()) {
		file.mkdirs();
	    }
	    file = new File(savePath + fileName);
	    if (!file.exists()) {
		file.createNewFile();
	    } else {
		file.delete();
		file.createNewFile();
	    }
	    FileOutputStream fout = new FileOutputStream(file);
	    byte[] bytes = message.getBytes();
	    fout.write(bytes);
	    fout.flush();
	    fout.close();
	} catch (Exception ex) {
	     ex.printStackTrace();
	}

    }
    
    /**
     * @date 2014-8-1
     * @param context
     * @param perfFile
     * @des 清空 shares文件
     */
    public static void clearShares(Context context, String perfFile) {
	try {
	    SharedPreferences prefs = context.getSharedPreferences(perfFile,
		    Service.MODE_WORLD_WRITEABLE);
	    prefs.edit().clear().commit();
	} catch (Exception e) {
	     e.printStackTrace();
	}
    }
    
    /**
     * 从应用私有文件夹中读取文件
     * 
     * @param context
     * @param fileName
     * @return
     */
    public static String readFileFromPhone(Context context, String fileName) {
	String path = Environment.getDataDirectory() + "/data/"
		+ context.getApplicationContext().getPackageName() + "/files/"
		+ fileName;
	String res = "";
	FileInputStream fin = null;
	try {
	    if (new File(path).exists()) {
		fin = new FileInputStream(path);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		res = EncodingUtils.getString(buffer, "UTF-8");
		fin.close();
	    } else
		return null;
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    try {
		if (fin != null) {
		    fin.close();
		}
	    } catch (Exception ex) {
	    }
	}
	return res;
    }

    /**
     * 
     * 获得属性文件
     * 
     * @author
     * @param context
     * @param saveDir
     * @param fileName
     * @param hasSdCard
     * @return
     */
    public static Properties readProperties(Context context, String saveDir,
	    String fileName, boolean hasSdCard) {
	FileInputStream is = null;
	Properties properties = new Properties();
	try {
	    String path = getConfigSaveDir(context, saveDir, hasSdCard);
	    File file = new File(path);
	    if (!file.exists()) {
		file.mkdirs();
	    }
	    file = new File(path + fileName);
	    if (file.exists()) {
		is = new FileInputStream(file);
		properties.load(is);
	    }
	    return properties;
	} catch (Exception ex) {
	     ex.printStackTrace();
	    properties = null;
	    return properties;
	} finally {
	    try {
		if (is != null) {
		    is.close();
		}
	    } catch (Exception ex) {
	    }
	}
    }

    /**
     * 保存属性文件
     * 
     * @author yanrui
     * @param properties
     *            属性文件
     * @param context
     * @param saveDir
     *            路径
     * @param fileName
     *            文件名
     * @param hasSdCard
     *            是否有sd卡
     * @param isCover
     * @return
     */
    public static boolean writeProperties(Context context,
	    Properties properties, String saveDir, String fileName,
	    boolean hasSdCard, boolean isCover) {
	FileOutputStream outputStream = null;
	try {
	    String path = getConfigSaveDir(context, saveDir, hasSdCard);
	    File file = new File(addSeparator(path) + fileName);
	    if (!file.exists() || isCover) {
		file.delete();
		file.createNewFile();
	    }
	    outputStream = new FileOutputStream(file);
	    properties.store(outputStream, "UTF-8");
	    outputStream.close();
	    return true;
	} catch (Exception ex) {
	     ex.printStackTrace();
	    return false;
	} finally {
	    try {
		if (outputStream != null) {
		    outputStream.close();
		}
	    } catch (Exception ex) {
	    }
	}
    }
    
    /**
     * 向文件写入一个propties。
     * 	不是添加，而是重新写
     * @param ctx
     * @param pty
     * @param path
     * @param filename
     * @return
     */
    public static boolean writeProperties(Context ctx,Properties pty,String path,String filename){
	if(pty==null)return false;
	FileOutputStream outputStream = null;
	try {
	File apath = new File(path);
	if(!apath.exists())apath.mkdirs();
	 File fi = new File(Util_File.addSeparator(path)+filename);
	 outputStream = new FileOutputStream(fi);
	 pty.store(outputStream, "UTF-8");
	 outputStream.close();
	} catch (Exception e) {
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    public static void DeleteProprity(Context context, String saveDir,
	    String fileName, boolean hasSd) {
	try {
	    String path = getConfigSaveDir(context, saveDir, hasSd);
	    File file = new File(addSeparator(path) + fileName);
	    if (file.exists()) {
		file.delete();
	    }
	} catch (Exception e) {
	    // 
	}
    }

    /**
     * 保存属性文件到data和T卡
     * 
     * @author
     * @param properties 
     * @param context
     * @param saveDir 
     * @param fileName
     * @param hasSdCard
     * @param isCover
     * @return
     */
    public static boolean writePropertiesToSDcardAndPhone(Context context,
	    Properties properties, String saveDir, String fileName,
	    boolean hasSdCard, boolean isCover) {
	FileOutputStream outputStream = null;
	try {
	    String path = getConfigSaveDir(context, saveDir, hasSdCard);
	    File file = new File(addSeparator(path) + fileName);
	    if (!file.exists() || isCover) {
		file.createNewFile();
	    }
	    FileOutputStream fo = new FileOutputStream(file);
	    properties.store(fo, "UTF-8");
	    path = getConfigSaveDir(context, saveDir, !hasSdCard);
	    file = new File(addSeparator(path) + fileName);
	    if (!file.exists() || isCover) {
		file.createNewFile();
	    }
	    outputStream = new FileOutputStream(file);
	    properties.store(outputStream, "UTF-8");
	    
	    
	    fo.close();
	    return true;
	} catch (Exception ex) {
	    ex.printStackTrace();
	} finally {
	    try {
		if (outputStream != null) {
		    outputStream.close();
		}
	    } catch (Exception ex) {
	    }
	}
	return false;
    }

    /**
     * 获得文件路径
     * 
     * @author
     * @param context
     * @param hasSdCard
     *            是否有sdcard
     * @return
     */
    private static String getConfigSaveDir(Context context, String saveDir,
	    boolean hasSdCard) {
	try {
	    if (saveDir == null) {
		saveDir = "";
	    }
	    saveDir.trim();
	    if (!saveDir.equals("")
		    && saveDir.lastIndexOf("/") != saveDir.length() - 1) {
		saveDir += "/";
	    }
	    String path = hasSdCard == true ? saveDir : Environment
		    .getDataDirectory()
		    + "/data/"
		    + context.getApplicationContext().getPackageName()
		    + "/files/";
	    // 如果路径不存在创建路径
	    File file = new File(path);
	    if (!file.exists()) {
		file.mkdirs();
	    }
	    return path;
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }
    
    /**
     * 为目录添加/，当有时则不添加。确保尾部有个FIle.separator
     * @param path
     * @return
     */
    public  static String addSeparator(String path){
	if (path == null) {
	    path = "";
	    }
	path.trim();
	    if (!path.equals("")
		    && path.lastIndexOf("/") != path.length() - 1) {
		path += "/";
	    }
	return path;
    }
 
    /**
     * @date 2014-6-5
     * @param files
     * @return
     * @des 返回对应文件是否存在
     */
    public static boolean isExistAllFile(String ...files){
	boolean isExist = false;
	for(String file:files){
	    File f  = new File(file);
	    if(f.exists()){
		isExist = true;
	    }else{
		isExist = false;
		return false;
	    }
	}
	return isExist;
    }
    
  /**
   * 依次创建目录 
 * @param dirs
 */
public static  void makeDirs(String ... dirs){
	    for(String path:dirs){
		File f = new File(path);
		if(!f.exists()){
		    f.mkdirs();
		    Util_Log.log("create dirs : "+path);
		}
	    }
	   
    }
    

    /**
     * @date 2014-5-30
     * @param saveDir
     * @param fileName
     * @return
     * @des 从指定 文件读取一个bitmap出来 
     */
    public static Bitmap readBmpFromFile(String saveDir, String fileName) {
	Bitmap btp = null;
	// 或者使用Bitmap btp = BitmapFactory.decodeFile(saveDir+fileName);
	try {
	    File file = new File(addSeparator(saveDir) + fileName);
	    if (!file.exists()) {
		return null;
	    } else {
		if (file.length() == 0) {
		    file.delete();
		    return null;
		}
	    }
	    // BitmapFactory.Options options = null;
	    // if (options == null) options = new BitmapFactory.Options();
	    // options.inJustDecodeBounds = true;

	    btp = BitmapFactory.decodeFile(addSeparator(saveDir) + fileName);
	    // FileInputStream fs = new FileInputStream(file);
	    // BufferedInputStream bs = new BufferedInputStream(fs);
	    // btp = BitmapFactory.decodeStream(bs);
	    // if (options.mCancel || options.outWidth == -1
	    // || options.outHeight == -1) {
	    // //表示图片已损毁
	    // file.delete();
	    // return null;
	    // }
	    return btp;
	} catch (Exception ex) {
	    try {
		File file = new File(addSeparator(saveDir) + fileName);
		file.delete();
		return null;
	    } catch (Exception ex1) {
		return null;
	    }
	}
    }
    
    /**
     * 读取ast文件下的指定文件中的 0-指定的endtag的位置的文本并返回串
     * @param ctx
     * @param filename
     * @param endtag
     * @return
     */
    public static String readAst4endTag(Context ctx,String filename,String endtag){
	try {
	    InputStream in = ctx.getAssets().open(filename);
	    String ct = Util_File.inputStream2String1(in);
	//    byte endstag []={101, 110, 100, 61, 101, 110, 100};//"end=end" 
	    int end = ct.indexOf(endtag);//读取end=end前的str进行解密
	    if(end!=-1){
		ct = ct.substring(0, end);
	    }
	 //   System.out.println(ct);
	    in.close();
	    return  ct;
	}   catch ( Exception e) {
	     e.printStackTrace();
	}
	return "";
    }
    
    
    
    // 写入properties信息
    /**
     * @date 2014-5-30
     * @param folder 目录  F:/z/
     * @param filePath 文件 xxx.txt
     * @param parameterNames  key
     * @param parameterValues value
     * @param update 如果为true,表示向原来文件追加或修改内空。反之，则重置内容
     * @des 向指定文件写入 指定 的k,v   (k需要是唯一的，若非，则后都会覆盖前者)
     */
    public static void CreateOrUpdateProperties(String folder, String filePath,
	    String[] parameterNames, String[] parameterValues,boolean update) {
	Properties prop = new Properties();

	try {
	    File file = new File(folder);
	    if (!file.exists())
		file.mkdirs();
	    
	    for (int i = 0; i < parameterNames.length; i++)
		prop.setProperty(parameterNames[i], parameterValues[i]);
	    FileOutputStream fos = new FileOutputStream(Util_File.addSeparator(folder)+ filePath,
		    false);
	    //将prop存入到文件，
	    prop.store(fos, "");
	    fos.flush();
	    fos.close();
	} catch (IOException e) {
	//    System.err.println("Visit " + filePath
	//	    + " for updating   value error");
	    e.printStackTrace();
	}
    }

 
    
    /**
     * @date 2014-5-30
     * @param filePath
     *            文件
     * @param key
     * @return
     * @des 返回配置文件中所有的传入的key对应的value
     */
    public static String[] readPropsFile(String filePath, String[] key) {
	Properties props = new Properties();
	String[] returnValue = new String[key.length];
	try {
	    InputStream in = new BufferedInputStream(new FileInputStream(
		    filePath));
	    props.load(in);
	    in.close();
	    for (int i = 0; i < key.length; i++)
		returnValue[i] = props.getProperty(key[i]);
	    return returnValue;
	} catch (Exception e) {
	    if(Util_Log.logShow)e.printStackTrace();
	    return null;
	}
    }


    /**
     * 将pty文件转成联网参数集
     * @param pty
     * @return
     */
    public static List<NameValuePair> pty2pairList(Properties pty){
	List<NameValuePair> datas = new ArrayList<NameValuePair>();
	Iterator its = pty.entrySet().iterator();
	while(its.hasNext()){
	    Map.Entry<String, String> ent= (Map.Entry<String, String>)its.next();
	    datas.add(new BasicNameValuePair(ent.getKey(), ent.getValue()));
	}
	return datas;
    }
    

    /**
     * @date 2014-5-28
     * @param fileName
     *            assets下文件，内容需要为一行一个key=value
     * @param ctx
     * @return
     * @des 从assets下读取文件内容，返回 Properties
     */
    public static Properties readAssetsProPerty(String fileName, Context ctx) {
	Properties pty = new Properties();
	try {
	    InputStream in = ctx.getAssets().open(fileName);
	    pty.load(in);
	    in.close();
	} catch (FileNotFoundException e) {
	    if(Util_Log.logShow)e.printStackTrace();
	} catch (IOException e) {
	    if(Util_Log.logShow)e.printStackTrace();
	}
	return pty;
    }
    
    
    /**
     * 通过bytes造一个pty
     * @param bs
     * @return
     */
    public static Properties getPty4bytes(byte []bs){
	Properties pty = new Properties();
	try {
	  InputStream in =   new ByteArrayInputStream(bs);
	   pty.load(in);
	    in.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return pty;
    }
}