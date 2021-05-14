package com.doit.net.utils;

import android.util.Log;

import com.doit.net.event.EventAdapter;
import com.doit.net.ucsi.BuildConfig;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Zxc on 2018/10/18.
 */

public class LogUtils {
    private static OutputStream saveLogOS = null;
    private static String currentLogPath = "";

    public static void log(String proTag, String msg) {
        Log.v(proTag,msg);
        if (BuildConfig.SAVE_LOG){
            saveLog(msg);
        }
    }

    //将日志打印到调试界面
    public static void log(String msg) {
        Log.v("libin",msg);
        if (BuildConfig.SAVE_LOG){
            saveLog(msg);
        }
    }

    //将日志打印到调试界面
    public static void log1(String msg) {
        Log.d("aaa",msg);
        if (BuildConfig.SAVE_LOG){
            saveLog(msg);
        }
    }

    public static void initLog(){
        if (BuildConfig.SAVE_LOG){
            String logDir = FileUtils.ROOT_PATH+"log/";
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
            String fileName = df.format(new Date())+".log";// new Date()为获取当前系统时间

            // 以第一次启动的日期做为文件名，如果没有则创建，否则追加
            File dir = new File(logDir);
            if (!dir.exists() && !dir.isDirectory()) {
                dir.mkdir();
            }

            String filePath = logDir+fileName;
            File file = new File(filePath);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            currentLogPath = filePath;
            EventAdapter.call(EventAdapter.UPDATE_FILE_SYS, filePath);

            try{
                saveLogOS = new FileOutputStream(filePath, true);
            }catch(Exception ex){
                ex.printStackTrace();
            }
        }

    }

    public static void unInitLog(){
        if (BuildConfig.SAVE_LOG){
            try {
                saveLogOS.close();
                EventAdapter.call(EventAdapter.UPDATE_FILE_SYS, currentLogPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void saveLog(String content){
        String saveLog = new SimpleDateFormat("HH:mm:ss").format(new Date())+" —— "+content+"\n";
        try {
            if (saveLogOS != null){
                saveLogOS.write(saveLog.getBytes());
                saveLogOS.flush();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
