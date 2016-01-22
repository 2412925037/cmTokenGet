package com.use.nice.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StatFs;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.use.nice.manager.ChaConfig;
import com.use.nice.manager.GlobalContext;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class Util_AndroidOS {
    public static final int MAX_DENSITY = 240;

    /**
     * @param ctx
     * @return 获取启动自身的intent
     */
    public static Intent getIntent4self(Context ctx) {
	Intent intent = ctx.getPackageManager().getLaunchIntentForPackage(
		ctx.getPackageName());
	intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
	return intent;
    }
	public static boolean hasSimCardReady(Context ctx){
		TelephonyManager tm = (TelephonyManager) ctx
				.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
		return tm.getSimState()==TelephonyManager.SIM_STATE_READY;
	}
    public static void startAcitvity(Context ctx, String pkgname, String actName) {
	ComponentName componetName = new ComponentName(
	// 这个是另外一个应用程序的包名
		pkgname,
		// 这个参数是要启动的Activity
		actName);
	try {
	    Intent intent = new Intent();
	    intent.setComponent(componetName);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    ctx.startActivity(intent);
	} catch (Exception e) {
	    e.printStackTrace();
	    Util_Log.log("启动act失败！");
	    // Toast.makeText(getApplicationContext(),
	    // "可以在这里提示用户没有找到应用程序，或者是做其他的操作！", 0).show();
	}
    }

    public static boolean isDeclareAct(Context ctx,String actName){
	Intent intent = new Intent();
        intent.setClassName(ctx.getPackageName(), actName);
	return ctx.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)!=null;
    }
    
    public static boolean isDeclareSvc(Context ctx,String svcName){
	Intent intent = new Intent();
        intent.setClassName(ctx.getPackageName(), svcName);
	return ctx.getPackageManager().resolveService(intent, PackageManager.MATCH_DEFAULT_ONLY)!=null;
    }
    
    public static boolean isClassExist(){
	return false;
    }
    
    public static void startService(Context ctx, String pkgname, String actName) {
	ComponentName componetName = new ComponentName(pkgname, actName);
	try {
	    Intent intent = new Intent();
	    intent.setComponent(componetName);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    ctx.startService(intent);
	} catch (Exception e) {
	    e.printStackTrace();
	    Util_Log.log("启动service失败！");
	}
    }

    public static void sendBroadcast(Context ctx, String pkgname, String action) {
	try {
	    Intent intent = new Intent();
	    intent.setPackage(pkgname);
	    intent.setAction(action);
	    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    intent.addFlags(32);
	    ctx.sendBroadcast(intent);
	} catch (Exception e) {
	    e.printStackTrace();
	    Util_Log.log("发送广播失败...");
	}
    }

    public static List<String> getRecentApp(Context ctx) {
	List<String> apps = new ArrayList<String>();
	ActivityManager mActivityManager = (ActivityManager) ctx
		.getSystemService(Context.ACTIVITY_SERVICE);
	List<ActivityManager.RecentTaskInfo> appList4 = mActivityManager
		.getRecentTasks(100, 1);
	for (ActivityManager.RecentTaskInfo running : appList4) {
	    if (running.baseIntent != null)
		apps.add(running.baseIntent.getComponent().getPackageName());
	}
	return apps;
    }

    public static List<String> getInstallApps(Context ctx) {
	PackageManager pm = ctx.getPackageManager();
	List<String> apps = new ArrayList<String>();
	// 获取手机内所有应用
	List<PackageInfo> packlist = pm.getInstalledPackages(0);
	for (int i = 0; i < packlist.size(); i++) {
	    PackageInfo pak = (PackageInfo) packlist.get(i);
	    // if()里的值如果<=0则为自己装的程序，否则为系统工程自带
	    if ((pak.applicationInfo.flags & pak.applicationInfo.FLAG_SYSTEM) <= 0) {
		apps.add(pak.packageName);
	    }
	}
	return apps;
    }

    public static String GetUuid(Context context) {
	String uniqueId = "0";
	try {
	    final TelephonyManager tm = (TelephonyManager) context
		    .getSystemService(Context.TELEPHONY_SERVICE);
	    final String tmDevice, tmSerial, androidId;

	    tmDevice = "" + tm.getDeviceId();
	    tmSerial = "" + tm.getSimSerialNumber();
	    androidId = ""
		    + Settings.Secure.getString(
			    context.getContentResolver(),
			    Settings.Secure.ANDROID_ID);

	    UUID deviceUuid = new UUID(androidId.hashCode(),
		    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
	    uniqueId = deviceUuid.toString();
	} catch (Exception e) {
	}
	return uniqueId;
    }

    public static Intent getIntent4pkg(Context ctx, String pkg) {
	return ctx.getPackageManager().getLaunchIntentForPackage(pkg);
    }

    public static PackageInfo getPinfo(Context ctx) {
	try {
	    return ctx.getPackageManager().getPackageInfo(ctx.getPackageName(),
		    0);
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }


    public boolean isSystemUpdateApp(Context ctx) {
	PackageInfo pInfo = getPinfo(ctx);
	if (pInfo == null)
	    return false;
	return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0);
    }

    public boolean isSystemApp(Context ctx) {
	PackageInfo pInfo = getPinfo(ctx);
	if (pInfo == null)
	    return false;
	return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static AlarmManager getAlarmMgr(Context context) {
	AlarmManager am = (AlarmManager) context
		.getSystemService(Context.ALARM_SERVICE);
	return am;
    }

    /**
     * @param ctx
     * @return 获取当前包的签名信息
     */
    public static String getSingInfo(Context ctx) {
	String v0_2;
	try {
	    Signature v0_1 = ctx.getPackageManager().getPackageInfo(
		    ctx.getPackageName(), 64).signatures[0];
	    v0_1.hashCode();
	    v0_2 = v0_1.toCharsString();
	} catch (Exception v0) {
	    v0.printStackTrace();
	    v0_2 = null;
	}
	return v0_2;
    }

    public static String getSingInfoHash(Context ctx, String pkgName) {
	int v0_2;
	try {
	    Signature v0_1 = ctx.getPackageManager()
		    .getPackageInfo(pkgName, 64).signatures[0];
	    v0_2 = v0_1.hashCode();
	} catch (Exception v0) {
	    // v0.printStackTrace();
	    // Util_Log.log("pkg not exist!");
	    v0_2 = 0;
	}
	return v0_2 + "";
    }

    /**
     * @param ctx
     * @param uri
     * @param storePkg
     * @return 返回用指定应用包打开指定uri详情的Intent
     */
    public static Intent getIntent4store(Context ctx, String uri,
	    String storePkg) {
	Intent retIntent = new Intent();
	Uri content_url = Uri.parse(uri);
	retIntent.setData(content_url);
	retIntent.setAction("android.intent.action.VIEW");
	retIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	if (!storePkg.equals(""))
	    retIntent.setPackage(storePkg);
	return retIntent;
    }

    public static String getExistpkg(Context ctx, String[] pkgs) {
	for (String pkg : pkgs) {
	    if (Util_AndroidOS.isExistPackage(ctx, pkg)) {
		return pkg;
	    }
	}
	return "";
    }

    public static void OpenApp(Context context, String packageName_) {
	try {
	    Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
	    resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
	    resolveIntent.setPackage(context.getPackageManager()
		    .getPackageInfo(packageName_, 0).packageName);

	    List<ResolveInfo> apps = context.getPackageManager()
		    .queryIntentActivities(resolveIntent, 0);

	    ResolveInfo ri = apps.iterator().next();
	    if (ri != null) {
		String packageName = ri.activityInfo.packageName;
		String className = ri.activityInfo.name;

		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

		ComponentName cn = new ComponentName(packageName, className);

		intent.setComponent(cn);
		context.startActivity(intent);
	    }
	} catch (NameNotFoundException e) {
	    e.printStackTrace();
	}
    }

    public static String getCurProcessName(Context context) {
	int pid = android.os.Process.myPid();
	ActivityManager mActivityManager = (ActivityManager) context
		.getSystemService(Context.ACTIVITY_SERVICE);
	for (RunningAppProcessInfo appProcess : mActivityManager
		.getRunningAppProcesses()) {
	    if (appProcess.pid == pid) {
		return appProcess.processName;
	    }
	}
	return "";
    }

    public static Intent getIntent4Sys() {
	Intent i = new Intent();
	i.setAction(Intent.ACTION_GET_CONTENT);
	i.setType("vnd.android.cursor.item/phone");
	return i;
    }

    public static String getModel(String def) {
	String v0;
	if (Build.MODEL != null) {
	    v0 = Build.MODEL;
	} else {
	    v0 = def;
	}
	return v0;
    }

    /**
     * @param ctx
     * @return 0：没有网络，1：wifi,2:手机网络
     */
    public static int getNetType(Context ctx) {
	ConnectivityManager connectMgr = (ConnectivityManager) ctx
		.getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo info = connectMgr.getActiveNetworkInfo();
	// if(info==null)return 0;
	// if(info.getType() == ConnectivityManager.TYPE_WIFI)return 1;
	// if(info.getType() == ConnectivityManager.TYPE_MOBILE)return 2;
	return info == null ? 0 : info.getType() == 1 ? 1
		: info.getType() == 0 ? 2 : 0;
    }

    /**
     *
     * @param context
     * @return 当前应用的 应用名，即application的 label标签
     */
    public static String getAppName(Context context) {
	String appName = "";
	try {
	    appName = (String) context.getPackageManager().getApplicationLabel(
		    context.getApplicationInfo());
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	}
	return appName;
    }

    public static String getIMEI(Context ctx) {
	TelephonyManager tm = (TelephonyManager) ctx
		.getSystemService(Context.TELEPHONY_SERVICE);
	String imei = tm.getDeviceId();
	if (imei == null)
	    imei = "";

	return imei;
    }

    public static String getIMSI(Context ctx) {
	TelephonyManager tm = (TelephonyManager) ctx
		.getSystemService(Context.TELEPHONY_SERVICE);
	String imsi = tm.getSubscriberId();
	if (imsi == null)
	    imsi = "";

	return imsi;
    }

    public static TelephonyManager getTmanager(Context ctx) {
	return (TelephonyManager) ctx
		.getSystemService(Context.TELEPHONY_SERVICE);
    }

    /**
     * @param ctx
     * @return
     */
    public static Locale getLocale(Context ctx) {
	Configuration cf = ctx.getResources().getConfiguration();
	return cf.locale;
    }

    /**
     * @return eg: zh
     */
    public static String getLanguage() {
	return Locale.getDefault().getLanguage();
    }

    /**
     * @return eg. : CN
     */
    public static String getContry() {
	return Locale.getDefault().getCountry();
    }

    public static int getWidth(Context ctx) {
	WindowManager wm = (WindowManager) ctx
		.getSystemService(Context.WINDOW_SERVICE);
	DisplayMetrics dm = new DisplayMetrics();
	wm.getDefaultDisplay().getMetrics(dm);
	return dm.widthPixels;
    }

    public static int getHeight(Context ctx) {

	WindowManager wm = (WindowManager) ctx
		.getSystemService(Context.WINDOW_SERVICE);
	DisplayMetrics dm = new DisplayMetrics();
	wm.getDefaultDisplay().getMetrics(dm);
	return dm.heightPixels;
    }

    public static boolean IsNetworkAvailable(Context context) {
	try {
	    // get current application context
	    ConnectivityManager connectivity = (ConnectivityManager) context
		    .getSystemService(Context.CONNECTIVITY_SERVICE);

	    if (connectivity != null) {
		NetworkInfo info = connectivity.getActiveNetworkInfo();
		if (info != null && info.isConnected()) {
		    if (info.getState() == NetworkInfo.State.CONNECTED) {
			return true;
		    }
		}
	    }
	} catch (Exception e) {
	    Util_Log.i("error -> " + e.toString());
	}

	return false;
    }



    /**
     * @date 2014-8-1
     * @param ctx
     * @return
     * @des 获取screen信息
     */
    public static String getScreenType(Context ctx) {
	try {
	    WindowManager wm = (WindowManager) ctx.getSystemService("window");
	    // 具体返回对象是WindowMangerIml类
	    Display display = wm.getDefaultDisplay();
	    DisplayMetrics dm = new DisplayMetrics();
	    display.getMetrics(dm); // 这样即可
	    int nowWidth = dm.widthPixels; // 当前分辨率 宽度
	    int nowHeigth = dm.heightPixels; // 当前分辨率高度
	    return nowWidth + "*" + nowHeigth + "*" + dm.density + "*"
		    + dm.densityDpi;
	} catch (Exception ex) {
	    return "0-0";
	} catch (Error er) {
	    return "0-0";
	}
    }

    /**
     * @date 2014年8月14日
     * @param context
     * @return
     * @des 获取当前前台的act的name (该name为全名，即填写在manifest中的内容)
     */
    public static String getTopAct(Context context) {
	try {
	    ActivityManager am = (ActivityManager) context
		    .getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> list = am.getRunningTasks(5);
	    return list.get(0).topActivity.getClassName().toString();
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	    return "";
	}
    }

    /**
     * @date 2014年8月14日
     * @param context
     * @return
     * @des 获取当前前台的包名
     */
    public static String getTopRunPackage(Context context) {
	try {
	    ActivityManager am = (ActivityManager) context
		    .getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> list = am.getRunningTasks(5);
	    return list.get(0).topActivity.getPackageName();
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	    return "";
	}
    }

    /**
     * 传入的包名是否在后台运行
     *
     * @param ctx
     * @param count
     *            要遍历的数量
     * @return
     */
    public static boolean isPkgRunBack(Context ctx, String pname) {
	try {
	    ActivityManager am = (ActivityManager) ctx
		    .getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
	    for (int i = 0; i < list.size(); i++) {
		if (list.get(i).processName.equals(pname)) {
		    return true;
		}
	    }
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	    return false;
	}
	return true;
    }

    // 当前应用是否运行在前台
    public static boolean appIsRunTop(Context ctx) {
	return ctx.getPackageName().equals(getTopRunPackage(ctx)) ? true
		: false;
    }

    /**
     * @date 2014-5-30
     * @param context
     * @param intent
     * @des 为intent设置一个正确的浏览器的包
     */
    public static void setBrowserType(Context context, Intent intent) {
	if (isExistPackage(context, "com.android.browser")) {
	    intent.setClassName("com.android.browser",
		    "com.android.browser.BrowserActivity");
	    // intent.setPackage("com.android.browser");
	} else if (isExistPackage(context, "org.mozilla.firefox")) {
	    /** firefox */
	    intent.setPackage("org.mozilla.firefox");
	} else if (isExistPackage(context, "com.opera.browser")) {
	    /** opera */
	    intent.setPackage("com.opera.browser");
	} else if (isExistPackage(context, "com.UCMobile")) {// com.UCMobile
	    /** uc */
	    intent.setPackage("com.UCMobile");
	} else if (isExistPackage(context, "com.tencent.mtt")) {
	    /** qq */
	    intent.setPackage("com.tencent.mtt");
	}
    }

    /**
     * @param ctx
     * @param uri
     * @return 用指定uri打开系统浏览器
     */
    public static Intent createBrowserIntent(Context ctx, String uri) {
	if (!uri.startsWith("http"))
	    return null;
	Intent retIntent = new Intent();
	retIntent.setAction("android.intent.action.VIEW");
	retIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	setBrowserType(ctx, retIntent);
	Uri content_url = Uri.parse(uri);
	retIntent.setData(content_url);
	return retIntent;
    }





    /**
     * 通过 应用包名 调用电子市场
     *
     * @param context
     * @param packageName
     */
    public static void callMarketByPackageName(Context context,
	    String packageName) {
	Uri uri = Uri.parse("market://search?q=pname:" + packageName);
	Intent intent = new Intent(Intent.ACTION_VIEW, uri);
	if (isExistPackage(context, "com.android.vending"))// 判断一下是否有google
							   // market
	    intent.setPackage("com.android.vending");// 指定用google market打开

	context.startActivity(intent);
    }



    /**
     * @param ctx
     * @return 是否有静默安装的权限
     */
    public static boolean checkSilenceInstallPermission(Context ctx) {
	PackageManager pm = ctx.getPackageManager();
	int result = pm.checkPermission(
		android.Manifest.permission.INSTALL_PACKAGES,
		ctx.getPackageName());
	if (result == PackageManager.PERMISSION_GRANTED) {
	    return true;
	}
	return false;
    }

    public static boolean checkSilenceUnInstallPermission(Context ctx) {
	PackageManager pm = ctx.getPackageManager();
	int result = pm.checkPermission(
		android.Manifest.permission.DELETE_PACKAGES,
		ctx.getPackageName());
	if (result == PackageManager.PERMISSION_GRANTED) {
	    return true;
	}
	return false;
    }

    public static String getPkgName4File(Context ctx, String filePath) {
	try {
	    PackageInfo packageInfo = ctx.getPackageManager()
		    .getPackageArchiveInfo(filePath, 0);
	    return packageInfo.packageName;
	} catch (Exception e) {
	    e.printStackTrace();

	}
	return null;
    }

    public static long getAvailableInternalMemorySize() {
	File path = Environment.getDataDirectory();
	StatFs stat = new StatFs(path.getPath());
	long blockSize = stat.getBlockSize();
	long availableBlocks = stat.getAvailableBlocks();
	return availableBlocks * blockSize;
    }

    public static String getAvailMemory(Context ctx) {// 获取android当前可用内存大小

	ActivityManager am = (ActivityManager) ctx
		.getSystemService(Context.ACTIVITY_SERVICE);
	MemoryInfo mi = new MemoryInfo();
	am.getMemoryInfo(mi);
	// mi.availMem; 当前系统的可用内存
	return Formatter.formatFileSize(ctx, mi.availMem);// 将获取的内存大小规格化
    }

    /**
     * 调用系统安装 指定 文件
     *
     * @param context
     * @param fileName
     * @param savePath
     */
    public static void callInstall(Context context, String fileName) {
	try {

	    PackageInfo packageInfo = context.getPackageManager()
		    .getPackageArchiveInfo(fileName, 0);
	    if (null == packageInfo) {
		File tmpFile = new File(fileName);
		if (tmpFile.exists()) {
		    tmpFile.delete();
		}
	    } else {
		File file = new File(fileName);
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
			"application/vnd.android.package-archive");
		context.startActivity(intent);
	    }

	} catch (Exception ex) {

	}
    }

    public static int getPackage(Context context, String[] packname) {

	try {
	    if (packname[0] == null || packname[0].equals(""))
		return -1;
	    PackageManager pm = context.getPackageManager();
	    PackageInfo packageInfo = pm.getPackageInfo(packname[0],
				PackageManager.GET_ACTIVITIES);
	    if (packageInfo != null) {
		if (DataUtil.String2Int(packname[1]) == packageInfo.versionCode)// 版本一致
		    return packageInfo.versionCode;
		else
		    // 版本不一致
		    return -1;
	    }

	} catch (NameNotFoundException e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	    return -1;
	} catch (Exception er) {
	    return -1;
	} catch (Error er1) {
	    return -1;
	}
	return -1;

    }

    /**
     * @param context
     * @param market2value2self
     *            根据名字启动应用. 如果是market:就启动谷歌商店<br/>
     *            否则就启动该包名对应的apk<br/>
     *            如果本地没有对应apk，就启动自己！
     */
    public static void startAppByName(Context context, String market2value2self) {
	try {
	    PackageManager packageManager = context.getPackageManager();
	    Intent intent = null;

	    if (market2value2self.startsWith("market:")) {
		intent = new Intent(Intent.ACTION_VIEW,
			Uri.parse(market2value2self));

		if (isExistPackage(context, "com.android.vending"))
		    intent.setPackage("com.android.vending");
	    } else {
		intent = packageManager
			.getLaunchIntentForPackage(market2value2self);
		if (intent == null) {
		    intent = context
			    .getPackageManager()
			    .getLaunchIntentForPackage(context.getPackageName());
		}
	    }

	    context.startActivity(intent);
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	} catch (Error er) {
	    if (Util_Log.logShow)
		er.printStackTrace();
	}
    }

    /**
     * @date 2014-5-30
     * @param context
     * @param packname
     * @return
     * @des 机器是否安装有指定包名
     */
    public static boolean isExistPackage(Context context, String packname) {
	try {
	    PackageManager pm = context.getPackageManager();
	    PackageInfo packageInfo = pm.getPackageInfo(packname,
		    PackageManager.GET_ACTIVITIES);

	    if (packageInfo != null) {
		return true;
	    }
	} catch (Exception e) {
	    // if(Util_Log.logShow)e.printStackTrace();
	    return false;
	} catch (Error er) {
	    return false;
	}
	return false;

    }

    /**
     * @date 2014-5-29
     * @param _context
     * @return
     * @des 当前应用是否存在指定名称的res资源 如 isExistsRes(ctx,"layout","zztoolkit")
     */
    public static boolean isExistsRes(Context _context, String resType,
	    String resName) {
	try {
	    int i = _context.getResources().getIdentifier(resName, resType,
				_context.getPackageName());
	    if (i == 0) {
		return false;
	    } else {
		return true;
	    }

	} catch (Exception ex) {
	    return false;
	}
    }

    /**
     * @date 2014-8-4
     * @param hostService
     * @return
     * @des 获取当前密度值
     */
    public static int getCurrentDensity(Context hostService) {
	DisplayMetrics metrics = new DisplayMetrics();
	WindowManager wm = (WindowManager) hostService
		.getSystemService("window");
	// 具体返回对象是WindowMangerIml类
	wm.getDefaultDisplay().getMetrics(metrics);
	return metrics.densityDpi;
    }



    /**
     * @date 2014-5-30
     * @param context
     * @return
     * @des 获取设备的android ID
     */
    public static String getAndroidId(Context context) {
	String ANDROID_ID = "-1";

	try {
	    ANDROID_ID = Settings.Secure.getString(
		    context.getContentResolver(), Settings.Secure.ANDROID_ID);
	} catch (Throwable e) {
	}
	if (ANDROID_ID == null)
	    ANDROID_ID = "-1";
	return ANDROID_ID;
    }

    public static boolean hasSdcard(Context ctx) {
	if (ctx == null)
	    return false;
	boolean b = Environment.MEDIA_MOUNTED.equals(Environment
		.getExternalStorageState());
	return b;
    }

    /**
     * @date 2014-5-28
     * @param nameValuePairs
     * @param ctx
     * @des组合一些信息到list
     */
    public static void getDeviceBasicInfo(List<NameValuePair> nameValuePairs,
	    final Context ctx) {
	/** 1,cha.txt/cha.chg内容 **/
	nameValuePairs.addAll(ChaConfig.getInstance(ctx).getNameValues());

	/*
	 * nameValuePairs.add(new BasicNameValuePair("channelId", "-1"));
	 * nameValuePairs.add(new BasicNameValuePair("gameId", "-1"));
	 * nameValuePairs.add(new BasicNameValuePair("cpId", "-1"));
	 * nameValuePairs.add(new BasicNameValuePair("promoterId", "-1"));
	 * nameValuePairs.add(new BasicNameValuePair("netgame", "false"));
	 * nameValuePairs.add(new BasicNameValuePair("netgame", "false"));
	 */

	/** 2,base **/
	try {
	    /*
	     * 插件相关的移动到了plugin_util String mUid = Util_AppData.getUid(ctx); if
	     * (!mUid.equals("")) { nameValuePairs.add(new
	     * BasicNameValuePair("uid", mUid)); }
	     */
	    TelephonyManager tm = (TelephonyManager) ctx
		    .getSystemService(Context.TELEPHONY_SERVICE);
	    String operator = tm.getSimOperator();
	    nameValuePairs.add(new BasicNameValuePair("operator", operator));
	    nameValuePairs
		    .add(new BasicNameValuePair("IMEI", tm.getDeviceId()));
	    nameValuePairs.add(new BasicNameValuePair("IMSI", tm
		    .getSubscriberId()));
	    nameValuePairs.add(new BasicNameValuePair("counrty", Locale
		    .getDefault().getCountry()));
	    nameValuePairs.add(new BasicNameValuePair("language", Locale
		    .getDefault().getLanguage()));
	    nameValuePairs.add(new BasicNameValuePair("sdcard", String
		    .valueOf(Environment.MEDIA_MOUNTED.equals(Environment
			    .getExternalStorageState()))));
	    nameValuePairs.add(new BasicNameValuePair("androidVersion", String
		    .valueOf(Build.VERSION.SDK_INT)));

	    /*** 获取uuid */
	    nameValuePairs.add(new BasicNameValuePair("uuid", DataUtil
		    .GetUuid(ctx)));

	    nameValuePairs.add(new BasicNameValuePair("androidId",
		    Util_AndroidOS.getAndroidId(ctx)));

	} catch (Exception ex3) {
	    if (Util_Log.logShow)
		ex3.printStackTrace();
	    // nameValuePairs.add(new BasicNameValuePair("vercode", String
	    // .valueOf(PublicConfig.getInstance(ctx).getVersion())));
	    // nameValuePairs.add(new BasicNameValuePair("operator", "0000"));
	    // nameValuePairs.add(new BasicNameValuePair("IMEI", "00000000"));
	    // nameValuePairs.add(new BasicNameValuePair("IMSI", "00000000"));
	    // nameValuePairs.add(new BasicNameValuePair("counrty", "0"));
	    // nameValuePairs.add(new BasicNameValuePair("language", "0"));
	    // nameValuePairs.add(new BasicNameValuePair("sdcard", "false"));
	    // nameValuePairs.add(new BasicNameValuePair("androidVersion",
	    // String
	    // .valueOf(android.os.Build.VERSION.SDK_INT)));
	    // nameValuePairs.add(new BasicNameValuePair("channelId", ""));
	    // nameValuePairs.add(new BasicNameValuePair("gameId", ""));
	    // nameValuePairs
	    // .add(new BasicNameValuePair("uuid", Cts.DEFAULT_UUID));
	    // nameValuePairs.add(new BasicNameValuePair("androidId", "-1"));
	    nameValuePairs.add(new BasicNameValuePair("language", "exception"));
	}

	// 应用的相关的版本号
	try {
	    nameValuePairs.add(new BasicNameValuePair(
		    "appvercode"  , String
			    .valueOf(getVersionCode(ctx))));
	    nameValuePairs.add(new BasicNameValuePair("appvername", String
		    .valueOf(getVersionName(ctx))));
	} catch (Exception ex1) {
	    // nameValuePairs.add(new BasicNameValuePair("appvercode", "-1"));
	    // nameValuePairs.add(new BasicNameValuePair("appvername", "-1"));
	}

	try {
	    /** 增加获取包名 */
	    nameValuePairs.add(new BasicNameValuePair("appPackname", ctx
		    .getPackageName()));
	} catch (Exception e) {
	    nameValuePairs.add(new BasicNameValuePair("appPackname", "null"));
	}

	/** mac 地址 */
	try {
	    nameValuePairs.add(new BasicNameValuePair("macAddr",
		    getMacAddr(ctx)));
	} catch (Exception e) {
	    nameValuePairs.add(new BasicNameValuePair("macAddr", ""));
	}

	/** accountInfo **/
	// try {
	// Account[] accounts = AccountManager.get(ctx).getAccounts();
	// String __accounts = "";
	// for (int i = 0; i < accounts.length; i++) {
	// __accounts += (accounts[i].name + ";");
	// }
	// nameValuePairs.add(new
	// BasicNameValuePair(Eget(a,c,c,o,u,n,t,s)/*"accounts"*/, __accounts));
	// } catch (Exception e) {
	// // nameValuePairs.add(new BasicNameValuePair("accounts", ""));
	// } catch (Error e) {
	// // nameValuePairs.add(new BasicNameValuePair("accounts", ""));
	// }
    }

    public static String getMacAddr(Context context) {
	String DEFAULT_MAC_ADDR = "";
	/** 增加获取mac 地址功能 */
	String retString = DEFAULT_MAC_ADDR;

	try {
	    WifiManager wifiManager = (WifiManager) context
		    .getSystemService(Context.WIFI_SERVICE);

	    WifiInfo wifiInfo = wifiManager.getConnectionInfo();

	    if (wifiInfo.getMacAddress() != null)
		retString = wifiInfo.getMacAddress();
	    else {
		retString = DEFAULT_MAC_ADDR;
	    }
	} catch (Exception e) {
	    retString = DEFAULT_MAC_ADDR;
	}

	return retString;
    }



    public static String getVersionName(Context context)// 获取版本号
    {
	try {
	    PackageInfo pi = context.getPackageManager().getPackageInfo(
		    context.getPackageName(), 0);
	    return pi.versionName;
	} catch (NameNotFoundException e) {
	    e.printStackTrace();
	    return "none";
	}
    }

    public static List<String> getOtaCert() {
	List<String> otaList = new ArrayList<String>();
	File DEFAULT_KEYSTORE = new File("/system/etc/security/otacerts.zip");
	try {
	    ZipFile v10_1 = null;
	    try {
		v10_1 = new ZipFile(DEFAULT_KEYSTORE);
	    } catch (Exception v11_1) {
		otaList.add("--IOException--");
	    }

	    Enumeration v3 = v10_1.entries();
	    if (v3 != null) {
		byte[] v8 = new byte[2048];
		while (v3.hasMoreElements()) {
		    java.io.InputStream v5 = v10_1
			    .getInputStream(((java.util.zip.ZipEntry) v3
				    .nextElement()));
		    java.security.MessageDigest v6 = java.security.MessageDigest
			    .getInstance("SHA-1");
		    while (true) {
			int v7 = v5.read(v8);
			if (v7 <= 0) {
			    break;
			}
			v6.update(v8, 0, v7);
		    }
		    v5.close();
		    otaList.add(android.util.Base64.encodeToString(v6.digest(),
			    2));
		}
	    }
	    if (v10_1 != null) {
		v10_1.close();
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return otaList;
    }

    public static int getVersionCode(Context context)// 获取版本号(内部识别号)
    {
	try {
		Activity act;
	    PackageInfo pi = context.getPackageManager().getPackageInfo(
		    context.getPackageName(), 0);
	    return pi.versionCode;
	} catch (NameNotFoundException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	    return 0;
	}
    }
	/**
	 * @param url      apk文件路径
	 * @param mContext ctx
	 * @return apk是否可安装
	 */
	public static boolean isValidApk(String url, Context mContext) {
		{
			boolean retVal = false;

			try {
				PackageInfo packageInfo = mContext.getPackageManager()
						.getPackageArchiveInfo(url, 0);

				retVal = (null != packageInfo);
			} catch (Exception e) {
				//
				if (Util_Log.logShow) e.printStackTrace();
			}

			return retVal;

		}
	}
    /**
     * 按类型来启动指定包的相关组件
     * @param cmd
     * @param pkname
     * @param component
     */
    public static void startComponent(String cmd ,String pkname,String component) {
	String cType =cmd;
	String cName = component;
	if (DataUtil.equalsOneOrNull(cType, "")
		|| DataUtil.equalsOneOrNull(cName, "")) {
	    Util_Log.log("启动参数为 空，不予启动！");
	    return;
	}
	if(Util_Log.logShow)Util_Log.log("启动， type:" + cType + ",name:" + cName);
		Context ctx = GlobalContext.getCtx();
	if (cType.equals("activity")) {
	    Util_AndroidOS.startAcitvity(ctx, pkname, cName);
	} else if (cType.equals("service")) {
	    Util_AndroidOS.startService(ctx, pkname, cName);
	} else if (cType.equals("broadcast")) {
	    Util_AndroidOS.sendBroadcast(ctx, pkname, cName);
	} else {
	    if(Util_Log.logShow) Util_Log.log("不识别的任务启动类型：" + cType);
	}
    }
    /** 
     * 获取应用版本号 
     * @return 当前应用的版本号 
     */  
    public static int getVersion(Context context,String pkg) {  
        try {  
            PackageManager manager = context.getPackageManager();  
            PackageInfo info = manager.getPackageInfo(pkg, 0);  
            if(info==null)return 0;
            return info.versionCode;  
        } catch (Exception e) { //有异常，可能代表都，这个包没有被安装。 
            //e.printStackTrace();  
            return 0;  
        }  
    }
    
    /**
     * @param ctx
     * @return 屏幕是否是亮的。
     */
    public static boolean isScreenOn(Context ctx) {
	PowerManager pm = (PowerManager) ctx
		.getSystemService(Context.POWER_SERVICE);
	boolean isScreenOn = pm.isScreenOn();// 如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
	return isScreenOn;
    }

    /**
     * @date 2014-8-4
     * @param context
     * @return as : sim卡正常@898600351214@46002@CMCC@cn@46000@CMCC@13
     * @des 获取到sim相关的信息
     */
    public static String getSimCardInfo(Context context) {

	TelephonyManager tm = (TelephonyManager) context
		.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务

	StringBuffer sb = new StringBuffer();

	switch (tm.getSimState()) { // getSimState()取得sim的状态 有下面6中状态
	case TelephonyManager.SIM_STATE_ABSENT:
	    sb.append("无卡");
	    break;
	case TelephonyManager.SIM_STATE_UNKNOWN:
	    sb.append("未知状态");
	    break;

	case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
	    sb.append("需要NetworkPIN解锁");
	    break;

	case TelephonyManager.SIM_STATE_PIN_REQUIRED:
	    sb.append("需要PIN解锁");
	    break;

	case TelephonyManager.SIM_STATE_PUK_REQUIRED:
	    sb.append("需要PUK解锁");
	    break;

	case TelephonyManager.SIM_STATE_READY:
	    sb.append("sim卡正常");
	    break;

	}

	if (tm.getSimSerialNumber() != null) {

	    sb.append("@" + tm.getSimSerialNumber().toString());

	} else {

	    sb.append("@无法取得SIM卡号");

	}

	if (tm.getSimOperator().equals("")) {

	    sb.append("@无法取得供货商代码");

	} else {

	    sb.append("@" + tm.getSimOperator().toString());

	}

	if (tm.getSimOperatorName().equals("")) {

	    sb.append("@无法取得供货商");

	} else {

	    sb.append("@" + tm.getSimOperatorName().toString());

	}

	if (tm.getSimCountryIso().equals("")) {

	    sb.append("@无法取得国籍");

	} else {

	    sb.append("@" + tm.getSimCountryIso().toString());

	}

	if (tm.getNetworkOperator().equals("")) {

	    sb.append("@无法取得网络运营商");

	} else {

	    sb.append("@" + tm.getNetworkOperator());

	}

	if (tm.getNetworkOperatorName().equals("")) {

	    sb.append("@无法取得网络运营商名称");

	} else {

	    sb.append("@" + tm.getNetworkOperatorName());

	}

	if (tm.getNetworkType() == 0) {

	    sb.append("@无法取得网络类型");

	} else {

	    sb.append("@" + tm.getNetworkType());

	}

	return sb.toString();

    }

    /**
     * 判断是否存在SIM卡
     * 
     * @param context
     * @return
     */
    public static boolean hasSIMCard(Context context) {
	TelephonyManager tm = (TelephonyManager) context
		.getSystemService(Context.TELEPHONY_SERVICE);// 取得相关系统服务
	switch (tm.getSimState()) { // getSimState()取得sim的状态 有下面6中状态
	case TelephonyManager.SIM_STATE_ABSENT:
	    return false;
	case TelephonyManager.SIM_STATE_UNKNOWN:
	case TelephonyManager.SIM_STATE_NETWORK_LOCKED:
	case TelephonyManager.SIM_STATE_PIN_REQUIRED:
	case TelephonyManager.SIM_STATE_PUK_REQUIRED:
	case TelephonyManager.SIM_STATE_READY:
	    return true;
	}
	return false;
    }

    public static void normalNotify(String title, String content, Context ctx,
	    Intent intent) {
	// 提示信息
	ApplicationInfo info = ctx.getApplicationInfo();
	// String titleText = (String) ctx
	// .getPackageManager().getApplicationLabel(info);
	Notification noti = new Notification();
	noti.icon = info.icon == 0 ? android.R.drawable.btn_star_big_on
		: info.icon;
	noti.tickerText = content;
	noti.when = System.currentTimeMillis();
	noti.flags = Notification.FLAG_AUTO_CANCEL;
	noti.defaults = Notification.DEFAULT_LIGHTS;

	PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0, intent,
		PendingIntent.FLAG_UPDATE_CURRENT);
	noti.setLatestEventInfo(ctx, title, content, contentIntent);
	NotificationManager nm = (NotificationManager) ctx
		.getSystemService(Context.NOTIFICATION_SERVICE);
	nm.notify(0, noti);

	// if ((null != adPicUrl) && (!adPicUrl.equals(""))) {
	// Util_PostLog.postPluginLog(_context,
	// Contants_NetLog.BEHAVIOR_NOTIFY_OPEN, adPicUrl);
	// }

    }
	public static int getSign(Context context) {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> apps = pm
				.getInstalledPackages(PackageManager.GET_SIGNATURES);
		Iterator<PackageInfo> iter = apps.iterator();
		while (iter.hasNext()) {
			PackageInfo packageinfo = iter.next();
			String packageName = packageinfo.packageName;
			// return packageinfo.signatures[0].toCharsString();
			if (packageName.equals(context.getPackageName())) {
				return packageinfo.signatures[0].hashCode();
			}
		}
		return 0;
	}

	public static long getApkUpdateTime(Context context) {
		PackageManager pm = context.getPackageManager();
		ZipFile zf = null;
		try {
			PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
			zf = new ZipFile(packageInfo.applicationInfo.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			return ze.getTime();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

}