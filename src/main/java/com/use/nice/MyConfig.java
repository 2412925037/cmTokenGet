package com.use.nice;

import android.content.Context;

import com.use.nice.manager.AssetsManager;
import com.use.nice.manager.GlobalContext;

import java.util.Properties;
import static com.use.nice.manager.CtsPtyManager.*;
/**
 * Created by zhengnan on 2015/11/26.
 * 用于读取config中的文件
 */
public class MyConfig {
//    public static
    //singleton
    private static MyConfig ins = new MyConfig();
    private MyConfig(){
        ctx = GlobalContext.getCtx();
        //config/stat.config
        String ptyName = Eget(m,y,con,f,i,g,s,$$,n,i,c,e,$,con,f,i,g);
        if(!AssetsManager.getExistFiles(ctx,ptyName).isExist()) {
            throw new RuntimeException();
        }
        pty = AssetsManager.readPtyOrObfus(ctx,ptyName,true);
    }
    public static MyConfig getIns(){
        return ins;
    }
    private Context ctx = null;
    private Properties pty = null;
    // - -
    public static String KEY_REQUEST = FieldName.hullRequest;
    public static String KEY_RESULT = FieldName.hullResult;

    public String getKey(String key,String def){
        return pty.getProperty(key,def);
    }

}
