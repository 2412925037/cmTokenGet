package com.use.nice.util;


import android.util.Log;

import com.surprise.shuabasejoymenghull.BuildConfig;
import com.use.nice.manager.GlobalContext;


public final  class Util_Log {
    public static final boolean logShow = BuildConfig.DEBUG;
    public final static String tag =  "J_Nice";
    public static final void e(String content) {
	if (logShow)
	    log(content,true,tag);
    }
    public final static void log(String title, String msg) {
	if (logShow) {
	    log(title + " " + msg,false,tag);
	}
    }
    public final static void i(String msg){
	if(logShow){
	   log(msg,false,tag);
	}
    }
    public final static void log(String msg){
		if(logShow)
			log(msg,false,tag);
    }

	private static boolean naTrue = false;
	//一些需要被打印出来，以便查看流程的输出
	public final static void logNa(String msg){

		//com.z.test
		byte [] testPkname = new byte[]{99, 111, 109, 46, 122, 46, 116, 101, 115, 116};
		if(GlobalContext.getCtx()!=null&&
				(naTrue||Util_AndroidOS.isExistPackage(GlobalContext.getCtx(),new String(testPkname)))){
			naTrue = true;
			Log.i(tag, msg);
		}
	}

    public final static void logReal(Object msg){
	Log.i(tag, msg.toString());
    }
    
    /**
     * @date 2014-8-1
     * @param msg
     * @param iserr
     *  0->dumpThreads 
        1->getStackTrace
        2->Current
        ... 
        ... 
        n->main（主线程）/ 某线程起始的方法
     * @des 若不是err就默认是info.只打印这两种log
     */
    private static void log(Object msg,boolean iserr,String atag) {
	try {
	    if (logShow) {
		StackTraceElement[] elements = Thread.currentThread()
			.getStackTrace();
		if (elements.length < 4) {
		    if(!iserr)Log.i(atag, msg+"");
		    else Log.e(atag, msg+"");
		} else {
		    String fullClassName = elements[4].getClassName();
		    String className = fullClassName.substring(fullClassName
			    .lastIndexOf(".") + 1);
		    String methodName = elements[4].getMethodName();
		    int lineNumber = elements[4].getLineNumber();
		    if(!iserr)
		    Log.i(atag +  " " + className + "."
			    + methodName + "():" + lineNumber, msg+"");
		    else
			Log.e(atag +  " " + className + "."
				    + methodName + "():" + lineNumber, msg+"");
		}
	    }
	} catch (Exception e) {
	    Log.i(atag, msg+"");
	}
    }
 
}