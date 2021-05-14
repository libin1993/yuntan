package com.doit.net.utils;

import android.text.TextUtils;

import com.doit.net.bean.UserInfo;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Zxc on 2018/11/28.
 */

public class AccountManage {
    private static final String SUPER_ACCOUNT = "sanhui";
    private static final String SUPER_PASSWORD = "synway88861158";
    private static String adminAccount;
    private static String adminPassword;
    private static final String ADMIN_REMARK = "administrator88861158";  //存储在账号文件用于鉴定管理员账户
    private static String currentLoginAccount = "";

    public static String LOCAL_FTP_ACCOUNT_PATH = FileUtils.ROOT_PATH + "FtpAccount/";
    public static String ACCOUNT_FILE_NAME = "account";

    private static DbManager dbManager = UCSIDBManager.getDbManager();

    private static boolean hasGetAccountFromDev = false;

    private static int currentPermissionLevel = 1;

    public static String getAdminRemark() {
        return ADMIN_REMARK;
    }

    public static int getCurrentPerLevel() {
        return currentPermissionLevel;
    }

    public static void setCurrentPerLevel(int currentPermissionLevel) {
        AccountManage.currentPermissionLevel = currentPermissionLevel;
    }

    //等级越高，可见的就越多
    public static final int PERMISSION_LEVEL1 = 1;
    public static final int PERMISSION_LEVEL2 = 2;
    public static final int PERMISSION_LEVEL3 = 3;

    public static String getAdminAcount() {
        return adminAccount;
    }

    public static String getAdminPassword() {
        return adminPassword;
    }


    public static String getCurrentLoginAccount() {
        return currentLoginAccount;
    }

    public static void setCurrentLoginAccount(String currentLoginAccount) {
        AccountManage.currentLoginAccount = currentLoginAccount;
    }

    public static void saveAccoutToPref(String account, String password) {
        adminAccount = account;
        adminPassword = password;
        SPUtils.setString("admin_account", account);
        SPUtils.setString("admin_password", password);
    }

    public static void getAdminAccoutFromPref() {
        adminAccount = SPUtils.getString("admin_account", "");
        adminPassword = SPUtils.getString("admin_password", "");

        if ("".equals(adminAccount) || "".equals(adminPassword)) {
            adminAccount = "admin";
            adminPassword = "admin";
            SPUtils.setString("admin_account", "admin");
            SPUtils.setString("admin_password", "admin");
        }
    }

    public static void updateAdminAccoutToPref(String account, String password) {
        SPUtils.setString("admin_account", account);
        SPUtils.setString("admin_password", password);
    }

    public static void clearCurrentAccountDB() {
        try {
            dbManager.delete(UserInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkAccount(String inputAccount, String inputPassword) {
        if (inputAccount.equals(SUPER_ACCOUNT) && inputPassword.equals(SUPER_PASSWORD)) {
            setCurrentPerLevel(PERMISSION_LEVEL3);
            return true;
        } else if (inputAccount.equals(adminAccount) && inputPassword.equals(adminPassword)) {
            setCurrentPerLevel(PERMISSION_LEVEL2);
            return true;
        } else {
            try {
                UserInfo userInfo = dbManager.selector(UserInfo.class)
                        .where("account", "=", inputAccount).findFirst();

                if (userInfo != null && userInfo.getAccount().equals(inputAccount) && userInfo.getPassword().equals(inputPassword)) {
                    setCurrentPerLevel(PERMISSION_LEVEL1);
                    return true;
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        return false;
    }

    public static String getSuperAccount() {
        return SUPER_ACCOUNT;
    }


    /**************** 以下为账户文件上下载相关方法 ****************/
    public static boolean UpdateAccountToDevice() {
        List<UserInfo> listUserInfo = null;
        boolean result = true;

        try {
            listUserInfo = dbManager.selector(UserInfo.class).findAll();
        } catch (DbException e) {
            result = false;
            e.printStackTrace();
        }

        File namelistFile = new File(LOCAL_FTP_ACCOUNT_PATH + ACCOUNT_FILE_NAME);
        if (namelistFile.exists()) {
            namelistFile.delete();
        }

        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(LOCAL_FTP_ACCOUNT_PATH + ACCOUNT_FILE_NAME, true), StandardCharsets.UTF_8));
            if (listUserInfo != null) {
                for (UserInfo info : listUserInfo) {
                    LogUtils.log("上传账户："+info.toString());
                    bufferedWriter.write(info.getAccount() + ","+info.getPassword() + ","+info.getRemake()+"#");
                }

                new Thread() {
                    public void run() {
                        try {
                            FTPManager.getInstance().connect();
                            FTPManager.getInstance().uploadFile(false,LOCAL_FTP_ACCOUNT_PATH, ACCOUNT_FILE_NAME);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (bufferedWriter != null) {
                try {
                    bufferedWriter.close();
                } catch (IOException e) {
                }
            }
        }




        return result;
    }



    public static boolean UpdateAccountFromFileToDB() {
        boolean result = true;

        try {
            dbManager.delete(UserInfo.class);
        } catch (DbException e) {
            e.printStackTrace();
        }

        File file = new File(LOCAL_FTP_ACCOUNT_PATH + ACCOUNT_FILE_NAME);
        if (!file.exists()) {
            return false;
        }


        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(file));
            StringBuilder sb = new StringBuilder();
            String s="";
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s);
            }

            String content = sb.toString();
            LogUtils.log("设备下载的账号："+content);
            if (TextUtils.isEmpty(content)){
                return false;
            }

            String[] split = content.split("#");
            for (String s1 : split) {
                if (!TextUtils.isEmpty(s1)){
                    String[] split1 = s1.split(",");
                    if (split1.length >= 2){
                        UserInfo info = new UserInfo();
                        info.setAccount(split1[0]);
                        info.setPassword(split1[1]);
                        if (split1.length == 3) {
                            info.setRemake(split1[2]);
                        } else {
                            info.setRemake("");
                        }
                        dbManager.save(info);
                    }
                }
            }

        } catch (IOException e) {
            result = false;
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                }
            }
        }

        return result;
    }

    public static void checkAccountDir() {
        File dir = new File(LOCAL_FTP_ACCOUNT_PATH);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdir();
        }
    }

    public static void deleteAccountFile() {
        File file = new File(LOCAL_FTP_ACCOUNT_PATH);
        if (!file.exists()) {//判断是否待删除目录是否存在
            return;
        }

        String[] content = file.list();
        for (String name : content) {
            File temp = new File(LOCAL_FTP_ACCOUNT_PATH, name);
            temp.delete();
        }
    }

}
