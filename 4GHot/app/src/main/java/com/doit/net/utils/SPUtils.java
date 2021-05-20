package com.doit.net.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wiker on 2016/4/28.
 */
public class SPUtils {
    public static final String DEVICE_IP = "DEVICE_IP"; //设备IP
    public static final String TOKEN = "TOKEN"; //登录Token
    public static final String USERNAME = "USERNAME"; //用户名
    public static final String PASSWORD = "PASSWORD"; //密码
    public static final String REMEMBER_PASSWORD = "REMEMBER_PASSWORD"; //记住密码
    public static final String USER_ID = "USER_ID"; //用户ID
    public static final String DEVICE_NO = "DEVICE_NO"; //设备编号
    public static final String LOC_PREF_KEY = "LOC_PREF_KEY"; //是否显示定位
    public static final String SET_STATIC_IP = "STATIC_IP";  //静态IP
    private static SharedPreferences settings;
    private static SharedPreferences.Editor editor;
    public static void init(Context context){
        settings = context.getSharedPreferences("config", Context.MODE_PRIVATE);
        editor = settings.edit();
    }

    public static void setString(String key,String val){
        editor.putString(key,val);
        editor.commit();
    }

    public static String getString(String key,String defVal){
        return settings.getString(key,defVal);
    }

    public static void setInt(String key,int val){
        editor.putInt(key,val);
        editor.commit();
    }

    public static int getInt(String key, int defVal){
        return settings.getInt(key,defVal);
    }

    public static void setBoolean(String key, boolean val){
        editor.putBoolean(key,val);
        editor.commit();
    }

    public static boolean getBoolean(String key, boolean defVal){
        return settings.getBoolean(key,defVal);
    }

    public static Long getLong(String key,Long defVal){
        return settings.getLong(key,defVal);
    }

    public static void setLong(String key, long val){
        editor.putLong(key,val);
        editor.commit();
    }

}
