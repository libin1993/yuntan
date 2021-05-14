package com.doit.net.application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.doit.net.utils.SPUtils;
import com.doit.net.utils.CipherUtils;
import com.doit.net.utils.MyExceptionHandler;

import org.xutils.x;

/**
 * 全局应用程序类：用于保存和调用全局应用配置及访问网络数据
 * 
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
public class MyApplication extends Application {
    

    public static Context mContext;
    
    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;
        // 注册App异常崩溃处理器
        x.Ext.init(this);

        SPUtils.init(this);
        CipherUtils.init("");

        //异常捕获
        Thread.setDefaultUncaughtExceptionHandler(new MyExceptionHandler());
    }

    /**
     * @param base 分包
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
