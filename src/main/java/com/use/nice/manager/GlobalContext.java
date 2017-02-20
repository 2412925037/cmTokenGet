package com.use.nice.manager;


import android.content.Context;
import android.util.Log;

import com.surprise.shuabasejoymenghull.BuildConfig;
import com.use.nice.util.Util_File;
import com.use.nice.util.Util_Log;

import java.util.Properties;


/**
 * 有些初始化需要是全局的。
 * ---在多个入口来调用即可
 * @author zhengnan 
 * @date 2015年4月29日
 */
public class GlobalContext {
    public static final boolean isTest = BuildConfig.DEBUG;
    private static String gameId = "0"; 
    private static String channelId = null;
    
    
    private static Context ctx = null;
    private static void setCtx(Context ctx){
	if(GlobalContext.ctx == null)GlobalContext.ctx = ctx;
    }
    public static Context getCtx(){
	return GlobalContext.ctx;
    }
    public static String getGid(){
    	if(gameId.equals("0")){
    		Log.e("error", "error gID!!!!");
    	}
	return gameId;
    }
    public static String getCid(){
	return channelId;
    }
   
    public static void init(Context ctx){//要求每个入口需要调用此初始化
	setCtx(ctx.getApplicationContext());
	gameId = ChaConfig.getInstance(ctx).getGameId();
	channelId = ChaConfig.getInstance(ctx).getChannelId();
//	Util_Log.e("test -- ChaConfig get>"+ (DataUtil.now()-Cts.begin));
	Util_Log.e("\ncId: " + channelId
                    + "\nGid:" + gameId
    );
        String str_channelId = new String(new byte[]{99, 104, 97, 110, 110, 101, 108, 73, 100});
        String str_gameId = new String(new byte[]{103, 97, 109, 101, 73, 100});
        Util_File.writeDef(ctx,str_channelId,channelId);
        Util_File.writeDef(ctx,str_gameId,gameId);

        //写入打包时间
        if(gameId!=null&&!gameId.equals("0")){
            try {
                Properties pty = Util_File.readAssetsProPerty("payment_res/apk_info.txt", ctx);
                String pakTime = "pakTime";
                String theValue = pty.getProperty("time", "");
                Util_File.writeDef(ctx,pakTime,theValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}