package com.use.nice.util;

import android.content.Context;

import com.use.nice.NiceCts;
import com.use.nice.NiceConnectProxy;

import java.io.File;

import dalvik.system.DexClassLoader;

/**
 * Created by zhengnan on 2015/9/17.
 */
public class LoadUtil {
    //一个classLoader实例
    private static DexClassLoader clCache = null;
    /**
     * @param ctx
     * @param className
     * @param newClsLoader  是用新的classLoader还是用缓存的。
     * @return
     */
    public static Class loadClass( Context ctx,String className,boolean newClsLoader){
        File file = new File(Util_File.addSeparator(NiceCts.SAVE_PATH_NAME)+ NiceCts.APK_NAME);
        //	String assetsApkName = "lib1.apk";
//		final File dexInternalStoragePath = new File(ctx.getDir("dex", Context.MODE_PRIVATE),
//				assetsApkName);

        //copy apk
//			if(!dexInternalStoragePath.exists()){
//				boolean copy = Util_File.copyAssets(ctx, assetsApkName, dexInternalStoragePath);
//				Util_Log.log("copy apk > "+copy);
//			}

        // Internal storage where the DexClassLoader writes the optimized dex file to.

        // Initialize the class loader with the secondary dex file.
        if(newClsLoader||clCache==null)//用新的clsLoader

            clCache = new DexClassLoader(file.getAbsolutePath(),
                    file.getParent(),
                    null,
                    ctx. getClassLoader());
        try {
            Class libProviderClazz = null;
            // Load the library class from the class loader.
            libProviderClazz =
                    clCache.loadClass(className);
            return  libProviderClazz;
        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    private static NiceConnectProxy proxy = null;
    public static NiceConnectProxy getSubNiceObj(Context ctx, boolean newClsLoader){
        try {

            if(proxy!=null&&!newClsLoader)return proxy;
            Object ins = loadClass(ctx, NiceCts.subConnectClass,false).newInstance();
            return  proxy =  new NiceConnectProxy(ins);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
