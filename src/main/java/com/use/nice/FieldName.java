package com.use.nice;

import com.use.nice.manager.EE;

import static com.use.nice.manager.CtsPtyManager.C;
import static com.use.nice.manager.CtsPtyManager.Eget;
import static com.use.nice.manager.CtsPtyManager.I;
import static com.use.nice.manager.CtsPtyManager.L;
import static com.use.nice.manager.CtsPtyManager.N;
import static com.use.nice.manager.CtsPtyManager.O;
import static com.use.nice.manager.CtsPtyManager.P;
import static com.use.nice.manager.CtsPtyManager.R;
import static com.use.nice.manager.CtsPtyManager.V;
import static com.use.nice.manager.CtsPtyManager.a;
import static com.use.nice.manager.CtsPtyManager.ad;
import static com.use.nice.manager.CtsPtyManager.app;
import static com.use.nice.manager.CtsPtyManager.b;
import static com.use.nice.manager.CtsPtyManager.c;
import static com.use.nice.manager.CtsPtyManager.code;
import static com.use.nice.manager.CtsPtyManager.d;
import static com.use.nice.manager.CtsPtyManager.e;
import static com.use.nice.manager.CtsPtyManager.f;
import static com.use.nice.manager.CtsPtyManager.g;
import static com.use.nice.manager.CtsPtyManager.h;
import static com.use.nice.manager.CtsPtyManager.i;
import static com.use.nice.manager.CtsPtyManager.ion;
import static com.use.nice.manager.CtsPtyManager.is;
import static com.use.nice.manager.CtsPtyManager.k;
import static com.use.nice.manager.CtsPtyManager.l;
import static com.use.nice.manager.CtsPtyManager.m;
import static com.use.nice.manager.CtsPtyManager.n;
import static com.use.nice.manager.CtsPtyManager.nterval;
import static com.use.nice.manager.CtsPtyManager.o;
import static com.use.nice.manager.CtsPtyManager.p;
import static com.use.nice.manager.CtsPtyManager.q;
import static com.use.nice.manager.CtsPtyManager.r;
import static com.use.nice.manager.CtsPtyManager.s;
import static com.use.nice.manager.CtsPtyManager.t;
import static com.use.nice.manager.CtsPtyManager.u;
import static com.use.nice.manager.CtsPtyManager.v;
import static com.use.nice.manager.CtsPtyManager.w;
import static com.use.nice.manager.CtsPtyManager.x;
import static com.use.nice.manager.CtsPtyManager.*;
/**
 * Created by zhengnan on 2015/9/17.
 */
public class FieldName {

    public static final String udNice = Eget(u,d,N,i,c,e);//"udNice";
    public static final String subVersion4Nice = Eget(s,u,b,V,e,r,s,ion,"4",N,i,c,e);//"subVersion4Nice";
    public static final String onReceiver = Eget(o,n,R,e,c,e,i,v,e,r);//"onReceiver";
    public static final String onCreate = Eget(o,n,C,r,e,a,t,e);//"onCreate";
    public static final String vercode = Eget(v,e,r,code);//"vercode";
    public static final String dVersion = Eget(d,V,e,r,s,ion);//"dVersion";
    /**
     * 联网相关的参数
     **/
    public static final String nextInterval =Eget(n,e,x,t,I,nterval);// "nextInterval";
    public static final String status =Eget(s,t,a,t,u,s);// "status";
    public static final String token =Eget(t,o,k,e,n);// "token";
    public static final String url = Eget(EE.url);//"url";
    public static final String downLoadOk = Eget(d,o,w,n,L,o,ad,O,k);//"downLoadOk";
    public static final String feedbackOk = Eget(f,e,e,d,b,a,c,k,O,k);//"feedbackOk";
    public static final String isValid = Eget(is,V,a,l,i,d);//"isValid";
    public static final String appPackname = Eget(app,P,a,c,k,n,a,m,e);//"appPackname";
    public static final String country = Eget(c,o,u,n,t,r,y);//"country";
    public static final String language = Eget(l,a,n,g,u,a,g,e);//"language";
    public static final String countryCode = Eget(c,o,u,n,t,r,y,C,o,d,e);//"countryCode";
    public static final String close = Eget(c, l, o, s, e);
    public static final String data = Eget(EE.data);
    public static final String CPU = Eget(C,P,U);
    public static final String progress = Eget(p,r,o,g,r,e,s,s);
    public static final String bad_progress = Eget(b,a,d,_,p,r,o,g,r,e,s,s);
    //使用相关
    public static final String ret =Eget(r,e,t);// "ret";
    public static final String params=Eget(p,a,r,a,m,s);// "params";
    public static final String success = Eget(s, u, c, c, e, s, s);
    public static final String  soSuccess = Eget(s,o,S,u,c,c,e,s,s);
    public static final String failed = Eget(f, a, i, le, d);
    public static final String isRunning = Eget(is,R,u,n,n,ing);
    public static final String imei = Eget(i,m,e,i);
    public static final String imsi = Eget(i,m,s,i);
    public static final String appVersion = Eget(app, V, e, r, s, ion);
    public static final String result = Eget(r, e, s, u, l, t);
    public static final String isRoot = Eget(is, R, o, o, t);
    public static final String sign = Eget(s, i, g, n);
    public static final String insTimeSub = Eget(i, n, s, T, i, m, e,S,u,b);
    public static final String nice_forver = Eget(n,i,c,e,"_",f,o,r,v,e,r);
    public static final String abc12345 = Eget( a,b,c,"1","2","3","4","5");
    public static final String edata = Eget(e,data);
    public static final String error = Eget(err,o,r);
    //stat.config的key
    public static final String hullRequest= Eget(h,u,l,l,R,e,q,u,e,s,t);
    public static final String hullResult= Eget(h,u,l,l,R,e,s,u,l,t);
    public static final String hullLog = Eget(h, u, l, l, L, o, g);


}
