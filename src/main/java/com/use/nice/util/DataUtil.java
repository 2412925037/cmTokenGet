package com.use.nice.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import org.apache.http.NameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;

public class DataUtil {
    private DataUtil() {
    }

    /**
     * @date 2014-5-28
     * @param params
     * @param encode
     * @return
     * @des 把map中的key,value拼接成http的后缀参数
     */
    public static StringBuffer map2urlParam(Map<String, String> params,
	    String encode) {
	StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
	try {
	    for (Map.Entry<String, String> entry : params.entrySet()) {
		stringBuffer.append(entry.getKey()).append("=")
			.append(URLEncoder.encode(entry.getValue(), encode))
			.append("&");
	    }
	    stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	}
	return stringBuffer;
    }

    public static boolean isClassExist(String className) {
	try {
	    Class.forName(className);
	    return true;
	} catch (ClassNotFoundException e) {
	    return false;
	}   catch(Throwable ee){
		ee.printStackTrace();
		return false;
	}
    }

    public static boolean int2bool(int value) {
	return value == 0 ? false : true;
    }

    public static boolean str2bool(String value) {
	if (value == null || value.equals(""))
	    return false;
	if (value.toLowerCase().equals("true"))
	    return true;
	else if (value.toLowerCase().equals("false"))
	    return false;
	try {
	    if (Integer.parseInt(value) > 0)
		return true;
	} catch (Exception e) {
	    return false;
	}
	return false;
    }
	public static long getApkUpdateTime(Context context) {
		try{
		PackageManager pm = context.getPackageManager();
		ApplicationInfo appInfo = pm.getApplicationInfo(context.getPackageName(), 0);
		String appFile = appInfo.sourceDir;
		long installed = new File(appFile).lastModified();
		return  installed;
	}catch (Exception e){
		e.printStackTrace();
	}
		return 0;
	}
    public static int bool2int(boolean bool) {
	return bool ? 1 : 0;
    }

    /**
     * 通过List的toString返向创建List
     * 
     * @param aa
     * @return
     */
    public static List<String> string2List(String aa) {
	return Arrays.asList(aa.replaceAll("[\\[\\]]", "").split("\\,"));
    }

    public static int dip2px(Context context, float dipValue) {
	final float scale = context.getResources().getDisplayMetrics().density;
	return (int) (dipValue * scale + 0.5f);
    }

    public static long now() {
	return System.currentTimeMillis();
    }

    /**
     * @param l1
     * @param l2
     * @return 集合1-集合2， 集合1中唯一的前有 + 集合2中唯一的前有-
     */
    public static List<String> subList(List<String> l1, List<String> l2) {
	// 用来操作使用
	List<String> d3 = new ArrayList<String>();
	// 用来添加结果
	List<String> d4 = new ArrayList<String>();
	// 主-次，其主集合中的元素
	d3.addAll(l1);
	d3.removeAll(l2);
	if (d3.size() > 0) {
	    for (String rt : d3) {
		d4.add("+" + rt);
	    }
	}

	// 次-主，求次集合中元素
	d3.clear();
	d3.addAll(l2);
	d3.removeAll(l1);
	if (d3.size() > 0) {
	    for (String rt : d3) {
		d4.add("-" + rt);
	    }
	}

	return d4;
    }

    /**
     * @date 2014年9月9日
     * @param bytes
     * @return
     * @des 返回字节数组的md5值
     */
    public static String getMD5String(byte[] bytes) {
	MessageDigest digest = null;
	try {
	    digest = MessageDigest.getInstance("MD5");
	    digest.update(bytes);
	    BigInteger bigInt = new BigInteger(1, digest.digest());
	    return bigInt.toString(16);
	} catch (NoSuchAlgorithmException e) {
	    //
	    e.printStackTrace();
	    return null;
	}
    }


	/**
	 * @param params listp
	 * @return json
	 */
	public static JSONObject listParams2json(List<NameValuePair>params) {
		JSONObject jo = new JSONObject();
		try{
			for (NameValuePair pair : params) {
				jo.putOpt(pair.getName(), pair.getValue());
			}
		}catch (JSONException je){je.printStackTrace();}
		return jo;
	}

    @SuppressWarnings("unchecked")
    public static String ObjToJson(Object obj) {
	StringBuilder build = new StringBuilder();
	build.append("{");
	@SuppressWarnings("rawtypes")
	Class cla = null;
	try {
	    // 反射加载类
	    cla = Class.forName(obj.getClass().getName());
	} catch (ClassNotFoundException e) {
	    System.out.println(obj.getClass().toString().concat(" 未找到这个类"));
	    e.printStackTrace();
	    return null;
	}

	StringBuffer methodname = new StringBuffer();
	// 获取java类的变量
	Field[] fields = cla.getDeclaredFields();
	String separate = "";
	for (Field temp : fields) {
	    build.append(separate);
	    build.append("\"");
	    build.append(temp.getName());
	    build.append("\":");
	    if (temp.getType() == boolean.class) {
		methodname.append("is");
	    } else {
		methodname.append("get");
	    }
	    methodname.append(temp.getName().substring(0, 1).toUpperCase());
	    methodname.append(temp.getName().substring(1));

	    build.append("\"");
	    Method method = null;
	    try {
		// 获取java的get方法
		method = cla.getMethod(methodname.toString());
	    } catch (NoSuchMethodException e) {
		methodname.setLength(0);
		e.printStackTrace();
	    } catch (SecurityException e) {
		methodname.setLength(0);
		e.printStackTrace();
	    }

	    try {
		// 执行get方法，获取变量参数的直。
		build.append(method.invoke(obj));
	    } catch (IllegalAccessException e) {
		e.printStackTrace();
	    } catch (IllegalArgumentException e) {
		e.printStackTrace();
	    } catch (InvocationTargetException e) {
		e.printStackTrace();
	    }
	    build.append("\"");
	    methodname.setLength(0);
	    separate = ",";
	}

	build.append("}");
	return build.toString();
    }

    public static String getMD5String(String src) {
	return getMD5String(src.getBytes());
    }
    public static final int ONE_MIN = 1000*60;
	public static final int ONE_HOUR = ONE_MIN*60;
	public static final int ONE_DAY = ONE_HOUR*24;
	public static final int HALF_HOUR = ONE_MIN * 30;
    /**
     * 返回 tim1 -time2的具体表示
     * 
     * @param time1
     * @param time2
     * @return
     */
    public static String getSubedTime(long time1, long time2) {
	long sub = time1 - time2;
	String ret = sub /   ONE_HOUR + "小时" + (sub %  ONE_HOUR)
		/   ONE_MIN + "分" + (sub %  ONE_HOUR)
		%   ONE_MIN / 1000 + "秒";
	return ret;
    }

    public static String getFormatTime(long time) {
	return getSubedTime(time, 0);
    }

    public static byte[] GZIPStr2byte(String str) throws IOException {
	if (str == null || str.length() == 0) {
	    return str.getBytes();
	}
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	GZIPOutputStream gzip = new GZIPOutputStream(out);
	gzip.write(str.getBytes());
	gzip.close();
	byte[] ret = out.toByteArray();
	out.close();
	return ret;
    }

    // 压缩
    public static String GZIPdata(String str) throws IOException {
	if (str == null || str.length() == 0) {
	    return str;
	}
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	GZIPOutputStream gzip = new GZIPOutputStream(out);
	gzip.write(str.getBytes());
	gzip.close();
	String ret = out.toString("ISO-8859-1");
	out.close();
	return ret;
    }

    // 解压缩
    public static String GZIPUndata(String str) throws IOException {
	if (str == null || str.length() == 0) {
	    return str;
	}
	ByteArrayOutputStream out = new ByteArrayOutputStream();
	ByteArrayInputStream in = new ByteArrayInputStream(
		str.getBytes("ISO-8859-1"));
	GZIPInputStream gunzip = new GZIPInputStream(in);
	byte[] buffer = new byte[256];
	int n;
	while ((n = gunzip.read(buffer)) >= 0) {
	    out.write(buffer, 0, n);
	}
	// toString()使用平台默认编码，也可以显式的指定如toString(&quot;GBK&quot;)
	String rt = out.toString();
	out.close();
	return rt;
    }

    public static byte[] getGZipCompressed(String data) {
	byte[] compressed = null;
	try {
	    byte[] byteData = data.getBytes();
	    ByteArrayOutputStream bos = new ByteArrayOutputStream(
		    byteData.length);
	    Deflater compressor = new Deflater();
	    compressor.setLevel(Deflater.BEST_COMPRESSION); // 将当前压缩级别设置为指定值。
	    compressor.setInput(byteData, 0, byteData.length);
	    compressor.finish(); // 调用时，指示压缩应当以输入缓冲区的当前内容结尾。

	    // Compress the data
	    final byte[] buf = new byte[1024];
	    while (!compressor.finished()) {
		int count = compressor.deflate(buf);
		bos.write(buf, 0, count);
	    }
	    compressor.end(); // 关闭解压缩器并放弃所有未处理的输入。
	    compressed = bos.toByteArray();
	    bos.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}

	return compressed;
    }

    public static byte[] getGZipUncompress(byte[] data) throws IOException {
	byte[] unCompressed = null;
	ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
	Inflater decompressor = new Inflater();
	try {
	    decompressor.setInput(data);
	    final byte[] buf = new byte[1024];
	    while (!decompressor.finished()) {
		int count = decompressor.inflate(buf);
		bos.write(buf, 0, count);
	    }

	    unCompressed = bos.toByteArray();
	    bos.close();
	} catch (Exception e) {
	    e.printStackTrace();
	} finally {
	    decompressor.end();
	}
	// String test = bos.toString();
	return unCompressed;
    }

    /**
     * 检测json对象是否合法
     * 
     * @param json
     * @param keys
     *            是否包含相关key
     * @return
     */
    public static boolean checkJson(JSONObject json, String... keys) {
	boolean isValid = true;
	if (json == null) {
	    isValid = false;

	} else
	    for (String key : keys) {
		if (json.isNull(key)) {
		    Util_Log.log("checkJosn,but has no " + key);
		    isValid = false;
		    break;
		}
	    }
	return isValid;
    }

    public static int px2dip(Context context, float pxValue) {
	final float scale = context.getResources().getDisplayMetrics().density;
	return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param argb
     *            ff00ff00
     * @param def
     * @return 返回一个argb的颜色值 。即str转int
     */
    public static int getColor(String argb, int def) {
	if (argb.length() != 8) {
	    return def;
	}
	long color = Long.parseLong(argb, 16);
	int ret = (int) color;
	return ret;
    }

    public static int getRandomNotifyIconId(int notifyId) {
	int count = notifyId % 4;
	try {
	    switch (count) {
	    case 1:
		return android.R.drawable.ic_menu_add;
	    case 2:
		return android.R.drawable.btn_star_big_on;
	    case 3:
		return android.R.drawable.ic_dialog_email;
	    default:
		return android.R.drawable.sym_def_app_icon;
	    }
	} catch (Exception ex) {
	    return android.R.drawable.sym_def_app_icon;
	}
    }

    /**
     * 数组是否包含对应的元素之1
     * 
     * @param ars
     * @param target
     * @return
     */
    public static boolean isArsOrContains(String[] ars, String... target) {
	if (ars == null)
	    return false;
	for (int i = 0; i < ars.length; i++) {
	    for (int j = 0; j < target.length; j++) {
		if (ars[i].equals(target[j])) {
		    return true;
		}
	    }
	}
	return false;
    }

 

    /**
     * @return 是否为指定时间点 （0-23点）
     */
    public static boolean timeGetRight(int... hours) {
	//
	Calendar cl = Calendar.getInstance();
	int h = cl.get(Calendar.HOUR_OF_DAY);
	for (int hour : hours) {
	    if (hour == h) {
		return true;
	    }
	}
	return false;
    }

    // 1-31
    // public static boolean isRightDay(long miliin){
    // Calendar cl = Calendar.getInstance();
    // int curDay =cl.get(Calendar.DAY_OF_MONTH) ;
    // cl.setTimeInMillis(miliin);
    // int pDay = cl.get(Calendar.DAY_OF_MONTH);
    // return curDay==pDay;
    // }

    /**
     * 用传入时间-当前时间，返回天数 (已在多处使用，所以当求当前时间-传入时间时，加-号即可)
     * 
     * @param millin
     * @return
     */
    public static int subCurDay(long millin) {
	Calendar cl = Calendar.getInstance();
	int curDay = cl.get(Calendar.DAY_OF_MONTH);
	cl.setTimeInMillis(millin);
	int pDay = cl.get(Calendar.DAY_OF_MONTH);
	return pDay - curDay;
    }

    public static int getMonth() {
	Calendar cl = Calendar.getInstance();
	return cl.get(Calendar.MONTH) + 1;
    }

    public static int getDaysOfMonth() {
	Calendar cl = Calendar.getInstance();
	return cl.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean timeIsRight() {
	return timeGetRight(11, 12, 19, 20, 21);
    }

    public static int getHourOfDay() {
	Calendar cl = Calendar.getInstance();
	int h = cl.get(Calendar.HOUR_OF_DAY);
	return h;
    }

    /**
     * 返回参数中>=当前小时的数量
     * 
     * @param hours
     * @return
     */
    public static int getHoursGEthanNow(int... hours) {
	Calendar cl = Calendar.getInstance();
	int h = cl.get(Calendar.HOUR_OF_DAY);
	int i = 0;
	for (int j = 0; j < hours.length; j++) {
	    if (hours[i] >= h) {
		i++;
	    }
	}
	return i;
    }

    /**
     * 返回参数中>=当前小时的数量
     * 
     * @param hours
     * @return
     */
    public static int getHoursGEthanNow(String... hours) {
	int[] hs = new int[hours.length];
	for (int i = 0; i < hours.length; i++) {
	    hs[i] = Integer.parseInt(hours[i]);
	}
	return getHoursGEthanNow(hs);
    }

    /**
     * @param hour
     * @return 当前点是否在指定点之前
     */
    public static boolean timeIsBeforeHour(int hour) {
	Calendar cl = Calendar.getInstance();
	int h = cl.get(Calendar.HOUR_OF_DAY);
	return (h <= hour);
    }

    public static int[] stringAry2int(String... ars) {
	int[] hs = new int[ars.length];
	for (int i = 0; i < ars.length; i++) {
	    hs[i] = Integer.parseInt(ars[i]);
	}
	return hs;
    }

    /**
     * @param hour
     * @return 返回当前时间距离最近的小时的毫秒数（精确到分钟） 当传入的值小于当前小时时返回 0
     */
    public static long timeNearWitch(int... hour) {
	Calendar cl = Calendar.getInstance();
	int h = cl.get(Calendar.HOUR_OF_DAY);
	Arrays.sort(hour);
	int va = 0;
	for (int x : hour) {
	    if (x - h > 0) {
		va = x;
		break;
	    }
	}
	if (va == 0)
	    return 0;

	long L1 = cl.getTimeInMillis();
	cl.add(Calendar.HOUR, va - h);
	cl.set(Calendar.MINUTE, 0);
	long L2 = cl.getTimeInMillis();
	return L2 - L1;
    }

 

    /**
     * @date 2014-6-3
     * @param hint
     * @return
     * @des 判断一个str的布尔状态 --（"true",null,else--->true,false）
     */
    public static boolean String3Bool(String hint) {
	boolean is_loop = false;
	try {
	    if (hint == null)
		is_loop = false;
	    else if (hint.equals("true"))
		is_loop = true;
	    else
		is_loop = false;

	} catch (Exception ex) {
	    is_loop = false;
	}
	return is_loop;
    }

    /*
     * public static String getGameId(Context ctx) { String gameId = ""; try {
     * sureAppCfFile(ctx); InputStream is = ctx.getAssets().open(
     * Constants_plugin.CHARGE_CONFIG_NAME); Properties cha = new Properties();
     * cha.load(is); gameId = cha.getProperty("gameId"); // is.close(); } catch
     * (Exception ex) { if(Util_Log.logShow)ex.printStackTrace();
     * Util_Log.e("找"+Constants_plugin.CHARGE_CONFIG_NAME+"，获取gameId时出错");
     * gameId = ""; } return gameId; }
     */
    /**
     * @param jsonAd
     * @param parameter
     * @param initValue
     * @return 如果 json中包含 参数就返回参数，否则返回initValue
     */
    public static String getJsonParameter(JSONObject jsonAd, String parameter,
	    String initValue) {
	if (jsonAd == null)
	    return initValue;
	String returnValue = "-1";
	try {
	    if (jsonAd.has(parameter)) {
		if (!jsonAd.getString(parameter).equals("")) {
		    returnValue = jsonAd.getString(parameter);
		} else {
		    returnValue = initValue;
		}
	    } else {
		returnValue = initValue;
	    }
	} catch (Exception ex1) {
	    returnValue = initValue;
	}
	return returnValue;
    }

    public static int String2Int(String hint) {
	if (hint == null || hint.equals(""))
	    return 0;
	try {
	    return Integer.parseInt(hint);

	} catch (Exception ex) {
	    return 0;
	}
    }

    public static int indexOf(int[] array, int[] target) {
	if (target.length == 0) {
	    return 0;
	}

	outer: for (int i = 0; i < array.length - target.length + 1; i++) {
	    for (int j = 0; j < target.length; j++) {
		if (array[i + j] != target[j]) {
		    continue outer;
		}
	    }
	    return i;
	}
	return -1;
    }

    public static boolean String2Bool(String hint) {
	boolean is_loop = true;
	try {
	    if (hint.equals("-1"))
		is_loop = false;
	    else
		is_loop = true;

	} catch (Exception ex) {
	    is_loop = false;
	}
	return is_loop;
    }

    /***
     * @deprecated
     * @获取uuid
     * @param context
     * @return
     */
    public static String GetUuid(Context context) {
	return Util_AndroidOS.GetUuid(context);

    }

    /**
     * 合并pty的内容，返回一个新的pty
     * 
     * @param ptys
     * @return
     */
    public static Properties combinPtys(Properties... ptys) {
	Properties targetPty = new Properties();
	for (Properties pty : ptys) {
	    Map<String, String> datas = new HashMap<String, String>((Map) pty);
	    Set<String> keys = datas.keySet();
	    for (String key : keys) {
		targetPty.put(key, datas.get(key));
	    }
	}
	return targetPty;
    }

    /**
     * 将json转成pty文件，并返回
     */
    public static Properties json2pty(JSONObject json) {
	Properties pty = new Properties();
	try {
	    Iterator<String> its = json.keys();
	    while (its.hasNext()) {
		String key = its.next();
		pty.put(key, json.get(key));
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}
	return pty;
    }

    /**
     * @date 2014-7-4
     * @param jsonStr
     * @return
     * @des 获取一个json对象。（为了避免try catch块的复杂性）
     */
    public static JSONObject getJo(String jsonStr) {
	JSONObject j = null;
	try {
	    j = new JSONObject(jsonStr);
	} catch (Exception e) {
	    // if (Util_Log.logShow)
	    // e.printStackTrace();
	}
	return j;
    }

    /***
     * 
     * @param context
     * @return
     */
    @Deprecated
    public static String GetWifiMacAddr(Context context) {
	return Util_AndroidOS.getMacAddr(context);
    }

    /**
     * @date 2014-7-9
     * @param src
     * @param targets
     * @return
     * @des 是否等于其中一个
     */
    public static boolean equalsOneOrNull(String src, String... targets) {
	if (src == null)
	    return true;
	try {
	    for (int i = 0; i < targets.length; i++) {
		if (src.equals(targets[i])) {
		    return true;
		}
	    }
	} catch (Exception e) {
	    if (Util_Log.logShow)
		e.printStackTrace();
	}
	return false;
    }

    /**
     * @date 2014年9月25日
     * @param obj
     * @return
     * @des 将传入的对象改为string类型，然后返回。若是 null，则返回""
     */
    public static String all2Str(Object obj) {
	if (obj == null)
	    return "";
	else {
	    return obj + "";
	}
    }

}
