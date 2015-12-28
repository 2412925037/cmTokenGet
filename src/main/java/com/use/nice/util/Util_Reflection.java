package com.use.nice.util;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class Util_Reflection {
    public static void setField(Object notification, String field, Object remoteView) {
        Class nitfyclass = notification.getClass();
        try {
            Field v0_3 = nitfyclass.getDeclaredField(field);
            v0_3.setAccessible(true);
            v0_3.set(notification, remoteView);
        }
        catch(Exception v0_1) {
            v0_1.printStackTrace();
        }
    }


    public static Object invoke(Object obj,String name,Class [] paramType ,Object [] parmsValue){
        try {
            Method m = obj.getClass().getDeclaredMethod(name,paramType);
            m.setAccessible(true);
            return m.invoke(obj,parmsValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}