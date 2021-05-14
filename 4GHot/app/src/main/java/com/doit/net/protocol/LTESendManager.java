package com.doit.net.protocol;

import android.text.TextUtils;

import com.doit.net.event.EventAdapter;
import com.doit.net.bean.DBChannel;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.utils.NetWorkUtils;
import com.doit.net.utils.UtilOperator;
import com.doit.net.application.MyApplication;
import com.doit.net.bean.FtpConfig;
import com.doit.net.bean.LteChannelCfg;
import com.doit.net.utils.BlackBoxManger;
import com.doit.net.utils.CacheManager;
import com.doit.net.utils.LogUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wiker on 2017-06-25.
 */

public class LTESendManager {
    private static final String band1Fcns = "100,375,400";
    private static final String band3Fcns = "1825,1650,1506";
    private static final String band5Fcns = "2452,2500,2600";
    private static final String band8Fcns = "3621,3683,3775";
    private static final String band34Fcns = "36275,36225,36325";
    private static final String band38Fcns = "37900,38098,38200";
    private static final String band39Fcns = "38400,38544,38300";
    private static final String band40Fcns = "38950,39148,39300";
    private static final String band41Fcns = "40540,40738,40840";


    public static void setNameList(String mode, String redirectConfig, String nameListReject,
                                   String nameListRedirect, String nameListBlock,
                                   String nameListRestAction, String nameListFile) {
        //MODE:[on|off]
        // @REDIRECT_CONFIG:46000,4,38400#46001,4,300#46011,4,100#46002,2,98  //重定向
        // @NAMELIST_REJECT:460001234512345,460011234512345   //拒绝
        // @NAMELIST_REDIRECT:460001234512345,460011234512345 //重定向再回公网
        // @NAMELIST_BLOCK:460001234512345,460011234512345   //吸附
        // @NAMELIST_RELEASE:460001233332345,460011235452345   //release
        // @NAMELIST_REST_ACTION:block  //其余手机操作


        String namelist = "MODE:" + mode;

        if (!"".equals(redirectConfig)) {
            namelist += "@REDIRECT_CONFIG:" + redirectConfig;
        }

        namelist += "@NAMELIST_REJECT:";
        if (!"".equals(nameListReject)) {
            namelist += nameListReject;
        }

        namelist += "@NAMELIST_REDIRECT:";
        if (!"".equals(nameListRedirect)) {
            namelist += nameListRedirect;
        }

        namelist += "@NAMELIST_BLOCK:";
        if (!"".equals(nameListBlock)) {
            namelist += nameListBlock;
        }

        namelist += "@NAMELIST_REST_ACTION:";
        if (!"".equals(nameListRestAction)) {
            namelist += nameListRestAction;
        }


        namelist += "@NAMELIST_FILE:";
        if (!"".equals(nameListFile)) {
            namelist += nameListFile;
        }


        LogUtils.log("设置名单：" + namelist);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_NAMELIST, namelist);
    }

    public static void getEquipAndAllChannelConfig() {
        LogUtils.log("查询设备配置");
        LTE_PT_PARAM.queryCommonParam(LTE_PT_PARAM.PARAM_GET_ENB_CONFIG);
    }

    /**
     * 获取白名单
     */
    public static void getActiveMode() {
        LogUtils.log("查询工作模式");
        LTE_PT_PARAM.queryCommonParam(LTE_PT_PARAM.PARAM_GET_ACTIVE_MODE);
    }
    /**
     * 获取白名单
     */
    public static void getNameList() {
        LogUtils.log("查询名单");
        LTE_PT_PARAM.queryCommonParam(LTE_PT_PARAM.PARAM_GET_NAMELIST);
    }

    public static void setNowTime() {
        Date d = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd-HH:mm:ss");
        LogUtils.log("设置固件时间：" + sdf.format(d));

        LTE_PT_SYSTEM.setSystemParam(LTE_PT_SYSTEM.SYSTEM_SET_DATETIME, sdf.format(d));
    }

    public static void changeTac() {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        LogUtils.log("更新TAC");

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_CHANGE_TAG, "");
    }

    /**
     * @param action add表示增加imsi列表到指定的动作中，del表示在指定的动作中删除imsi列表
     * @param mode   可以取值为reject、redirect、block，分别表示拒绝回4G公网、重定向、吸附在4G
     * @param imsi   修改名单
     */
    public static void changeNameList(String action, String mode, String imsi) {
        String nameList = action + "#" + mode + "#" + imsi;

        LogUtils.log("修改名单：" + nameList);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_CHANGE_NAMELIST, nameList);
    }


    public static void setCellConfig(String gpsOffset, String pci, String tacPeriod, String sync) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        String configContent = "";
        if (!"".equals(pci)) {
            configContent += "@PCI:";
            configContent += pci.replaceAll(",", ":");
        }
        if (!"".equals(gpsOffset)) {
            configContent += "@GPS_OFFSET:";
            configContent += gpsOffset.replaceAll(",", ":");
        }

        if (!"".equals(tacPeriod)) {
            configContent += "@TAC_TIMER:";
            configContent += tacPeriod;
        }

        if (!"".equals(sync)) {
            configContent += "@SYNC:";
            configContent += sync;
        }

        if (TextUtils.isEmpty(configContent)){
            return;
        }


        //删掉最开始的@
        configContent = configContent.replaceFirst("@", "");

        LogUtils.log("设置小区:" + configContent);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_ENB_CONFIG, configContent);
        //EventAdapter.call(EventAdapter.ADD_BLACKBOX,BlackBoxManger.SET_CELL_CONFIG + configContent);
    }

    public static void reboot() {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        LogUtils.log("重启设备");
        LTE_PT_SYSTEM.commonSystemMsg(LTE_PT_SYSTEM.SYSTEM_REBOOT);
        EventAdapter.call(EventAdapter.ADD_BLACKBOX, BlackBoxManger.REBOOT_DEVICE);
    }

    public static void changeBand(String idx, String changeBand) {
        String content = "IDX:";
        content += idx;
        content += "@BAND:";
        content += changeBand;
        LogUtils.log("切换band:" + content);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_CHANGE_BAND, content);
    }

    public static void setDetectCarrierOperation(String carrierOperation) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        String plmnValue = "46000,46001,46011";
        if (carrierOperation.equals("detect_ctj")) {
            plmnValue = "46000,46000,46000";
        } else if (carrierOperation.equals("detect_ctu")) {
            plmnValue = "46001,46001,46001";
        } else if (carrierOperation.equals("detect_ctc")) {
            plmnValue = "46011,46011,46011";
        }

        for (int i = 0; i < CacheManager.getChannels().size(); i++) {
            int index = i;
            String finalPlmnValue = plmnValue;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    LteChannelCfg channel = CacheManager.getChannels().get(index);
                    setChannelConfig(channel.getIdx(), "", finalPlmnValue, "", "", "", "", "");

                    channel.setPlmn(finalPlmnValue);
                }
            }, index * 200);
        }

    }

    public static void setChannelConfig(String idx, String fcn, String plmn, String pa,
                                        String ga, String rxlevMin, String atuoOpenRF, String AltFcn) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        if ("".equals(idx))
            return;


        String configContent = "IDX:";
        configContent += idx;

        if (!"".equals(plmn)) {
            configContent += "@PLMN:";
            configContent += plmn;
        }

        if (!"".equals(fcn)) {
            configContent += "@FCN:";
            configContent += fcn;
        }

        if (!"".equals(pa)) {
            configContent += "@PA:";
            configContent += pa;
            //configContent += checkPa(idx, pa);
        }

        if (!"".equals(ga)) {
            configContent += "@GA:";
            configContent += ga;
        }

        if (!"".equals(rxlevMin)) {
            configContent += "@RLM:";
            configContent += rxlevMin;
        }


        if (!"".equals(atuoOpenRF)) {
            configContent += "@AUTO_OPEN:";
            configContent += atuoOpenRF;
        }

        if (!"".equals(AltFcn)) {
            configContent += "@ALT_FCN:";
            configContent += AltFcn;
        }

//        configContent += "@HOLD:";
//        configContent += HOLD_VALUE;


        LogUtils.log("设置通道: " + configContent);

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_CHANNEL_CONFIG, configContent);
        //BlackBoxManger.recordOperation(BlackBoxManger.SET_CHANNEL_CONFIG+configContent);
    }

    //加入定位关闭非目标运营商频点加大目标运营商频点功率策略之后，
    //这个功率判定方法不再好用，就暂时不做对功率限制做判定了
    private static String checkPa(String idx, String ga) {
        String band = getBandByIdx(idx);
        String returnGa = "";

        String[] tmpGa = ga.split(",");
        if (tmpGa == null || tmpGa.length != 3) {
            LogUtils.log("len " + tmpGa.length);
            return ga;
        }


        if (band.equals("1") || band.equals("3")) {
            for (int i = 0; i < tmpGa.length; i++) {
                if (Integer.valueOf(tmpGa[i]) > -7) {
                    returnGa += -7;
                } else {
                    returnGa += tmpGa[i];
                }
                returnGa += ",";
            }
        } else if (band.equals("38") || band.equals("40") || band.equals("41")) {
            for (int i = 0; i < tmpGa.length; i++) {
                if (Integer.valueOf(tmpGa[i]) > -1) {
                    returnGa += -1;
                } else {
                    returnGa += tmpGa[i];
                }
                returnGa += ",";
            }
        } else if (band.equals("39")) {
            for (int i = 0; i < tmpGa.length; i++) {
                if (Integer.valueOf(tmpGa[i]) > -13) {
                    returnGa += -13;
                } else {
                    returnGa += tmpGa[i];
                }
                returnGa += ",";
            }
        }

        return returnGa.substring(0, returnGa.length() - 1);
    }

    private static String getBandByIdx(String idx) {
        for (LteChannelCfg channel : CacheManager.getChannels()) {
            if (channel.getIdx().equals(idx)) {
                return channel.getBand();
            }
        }

        return "";
    }

    public static void setBlackList(String operation, String content) {
//        if(!CacheManager.isDeviceOk()){
//            return;
//        }

        //operation 1查询  2添加 3删除
        String configContent = operation + content;

        LogUtils.log("设置黑名单(中标)号码: " + configContent);

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_BLACK_NAMELIST, configContent);
    }

    //这个协议是用于rpt_rt_imsi而不是rpt_black_name
    public static void setRTImsi(boolean onOff) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        LogUtils.log("设置是否上报中标号码: " + (onOff ? "1" : "0"));

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_RT_IMSI, onOff ? "1" : "0");
    }


    public static void openAllRf() {
        for (int i = 0; i < CacheManager.getChannels().size(); i++) {
            int index = i;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (CacheManager.getChannels().size() > index) {
                        openRf(CacheManager.getChannels().get(index).getIdx());
                    }
                }
            }, index * 150);
        }
    }

    public static void openRf(String idx) {
        LogUtils.log("开启射频：" + idx);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_CHANNEL_ON, idx);
    }

    public static void closeRf(String idx) {
        LogUtils.log("关闭射频：" + idx);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_CHANNEL_OFF, idx);
    }

    public static void closeAllRf() {
        for (int i = 0; i < CacheManager.getChannels().size(); i++) {
            int index = i;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    if (CacheManager.getChannels().size() > index) {
                        closeRf(CacheManager.getChannels().get(index).getIdx());
                    }
                }
            }, index * 150);
        }
    }

    public static void setLocImsi(String imsi) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        LogUtils.log("设置定位IMSI：" + imsi);

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_LOC_IMSI, imsi);
    }


    /**
     * @param mode
     * 0表示侦码模式，1表示定位模式，2表示管控模式
     */
    public static void setActiveMode(String mode) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }
        LogUtils.log("设置模式：" + mode);
        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_ACTIVE_MODE, mode);
    }

    /**
     * 设置ftp
     */
    public static void setFTPConfig() {

        String configContent = NetWorkUtils.getWIFILocalIpAddress(MyApplication.mContext)
                + "#"
                + FtpConfig.ftpUser
                + "#"
                + FtpConfig.ftpPassword
                + "#"
                + FtpConfig.ftpPort
                + "#"
                + FtpConfig.ftpTimer
                + "#"
                + FtpConfig.ftpMaxSize;


        LogUtils.log("设置ftp:" + configContent);

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_FTP_CONFIG, configContent);
    }

    /**
     * 更换fcn
     */
    public static void exchangeFcn(String imsi) {

        try {
            DbManager dbManager = UCSIDBManager.getDbManager();
            DBChannel dbChannel = dbManager.selector(DBChannel.class)
                    .where("band", "=", "3")
                    .and("is_check", "=", "1")
                    .findFirst();
            if (dbChannel != null) {

                String idx = "";
                for (LteChannelCfg channel : CacheManager.getChannels()) {
                    if (channel.getBand().equals("3")) {
                        idx = channel.getIdx();
                        break;
                    }
                }

                if (!TextUtils.isEmpty(idx)) {
                    if ("CTJ".equals(UtilOperator.getOperatorName(imsi))) {
                        setChannelConfig(idx, "1300,1506,1650", "46000",
                                "", "", "", "", "");

                        for (LteChannelCfg channel : CacheManager.channels) {
                            if (channel.getIdx().equals(idx)) {
                                channel.setFcn("1300,1506,1650");
                                channel.setPlmn("46000");
                                break;
                            }
                        }
                    } else {
                        setChannelConfig(idx, dbChannel.getFcn(),
                                "46001,46011", "", "", "", "", "");
                        for (LteChannelCfg channel : CacheManager.channels) {
                            if (channel.getIdx().equals(idx)) {
                                channel.setFcn(dbChannel.getFcn());
                                channel.setPlmn("46001,46011");
                                break;
                            }
                        }
                    }
                }

            }

        } catch (DbException e) {
            e.printStackTrace();
        }


    }

    /**
     * 设置默认配置
     */
    public static void setDefaultArfcnsAndPwr() {
        for (LteChannelCfg channel : CacheManager.getChannels()) {
            //2019.9.12讨论不再使用过滤筛选方式，直接使用固定常用频点
            String defaultFcn = "";
            String defaultGa = "";
            String defaultPower = "";
            String pMax = channel.getPMax();
            if (!TextUtils.isEmpty(pMax)) {
                defaultPower = pMax + "," + pMax + "," + pMax;
            }

            switch (channel.getBand()) {
                case "1":
                    defaultFcn = band1Fcns;
                    break;
                case "3":
                    defaultFcn = band3Fcns;
                    break;
                case "5":
                    defaultFcn = band5Fcns;
                    break;
                case "8":
                    defaultFcn = band8Fcns;
                    break;
                case "34":
                    defaultFcn = band34Fcns;
                    break;
                case "38":
                    defaultFcn = band38Fcns;
                    break;
                case "39":
                    defaultFcn = band39Fcns;
                    break;
                case "40":
                    defaultFcn = band40Fcns;
                    break;
                case "41":
                    defaultFcn = band41Fcns;
                    break;
            }
            String fcn = getCheckedFcn(channel.getBand());
            if (!TextUtils.isEmpty(fcn)) {
                defaultFcn = fcn;
            }

            if (Integer.parseInt(channel.getGa()) <= 8) {
                defaultGa = String.valueOf(Integer.parseInt(channel.getGa()) * 5);
            }

            if ("3".equals(channel.getBand()) && CacheManager.getLocState()) {
                LogUtils.log("当前正在定位且是band3，不设置band3频点");
                continue;
            }

            setChannelConfig(channel.getIdx(), defaultFcn, "", defaultPower, defaultGa, "", "", "");

            channel.setPa(defaultPower);
            if (!TextUtils.isEmpty(defaultFcn)) {
                channel.setFcn(defaultFcn);
            }
            if (!TextUtils.isEmpty(defaultGa)) {
                channel.setGa(defaultGa);
            }


            LogUtils.log("默认fcn:" + defaultFcn);

        }
    }

    /**
     * 获取选中fcn
     */
    public static String getCheckedFcn(String band) {
        try {
            DbManager dbManager = UCSIDBManager.getDbManager();
            DBChannel channel = dbManager.selector(DBChannel.class)
                    .where("band", "=", band)
                    .and("is_check", "=", 1)
                    .findFirst();
            if (channel != null) {
                return channel.getFcn();
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     * 保存默认频点
     */
    public static void saveDefaultFcn() {
        try {
            DbManager dbManager = UCSIDBManager.getDbManager();

            DBChannel dbChannel1 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "1")
                    .and("fcn", "=", band1Fcns)
                    .findFirst();
            if (dbChannel1 == null) {
                dbManager.save(new DBChannel("1", band1Fcns, 1, 1));
            }


            DBChannel dbChannel3 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "3")
                    .and("fcn", "=", band3Fcns)
                    .findFirst();
            if (dbChannel3 == null) {
                dbManager.save(new DBChannel("3", band3Fcns, 1, 1));
            }

            DBChannel dbChannel5 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "5")
                    .and("fcn", "=", band5Fcns)
                    .findFirst();
            if (dbChannel5 == null) {
                dbManager.save(new DBChannel("5", band5Fcns, 1, 1));
            }

            DBChannel dbChannel8 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "8")
                    .and("fcn", "=", band8Fcns)
                    .findFirst();
            if (dbChannel8 == null) {
                dbManager.save(new DBChannel("8", band8Fcns, 1, 1));
            }

            DBChannel dbChannel34 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "34")
                    .and("fcn", "=", band34Fcns)
                    .findFirst();
            if (dbChannel34 == null) {
                dbManager.save(new DBChannel("34", band34Fcns, 1, 1));
            }

            DBChannel dbChannel38 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "38")
                    .and("fcn", "=", band38Fcns)
                    .findFirst();
            if (dbChannel38 == null) {
                dbManager.save(new DBChannel("38", band38Fcns, 1, 1));
            }


            DBChannel dbChannel39 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "39")
                    .and("fcn", "=", band39Fcns)
                    .findFirst();
            if (dbChannel39 == null) {
                dbManager.save(new DBChannel("39", band39Fcns, 1, 1));
            }

            DBChannel dbChannel40 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "40")
                    .and("fcn", "=", band40Fcns)
                    .findFirst();
            if (dbChannel40 == null) {
                dbManager.save(new DBChannel("40", band40Fcns, 1, 1));
            }

            DBChannel dbChannel41 = dbManager.selector(DBChannel.class)
                    .where("band", "=", "41")
                    .and("fcn", "=", band41Fcns)
                    .findFirst();
            if (dbChannel41 == null) {
                dbManager.save(new DBChannel("41", band41Fcns, 1, 1));
            }

        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    public static void setFanControl(String maxFanSpeed, String minFanSpeed, String tempThreshold) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        String configContent = "";

        if (!"".equals(minFanSpeed)) {
            configContent += "MIN_FAN:";
            configContent += minFanSpeed;
        }

        if (!"".equals(maxFanSpeed)) {
            configContent += "@MAX_FAN:";
            configContent += maxFanSpeed;
        }

        if (!"".equals(tempThreshold)) {
            configContent += "@FAN_TMPT:";
            configContent += tempThreshold;
        }

        LogUtils.log("设置风扇控制 " + configContent);

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_FAN, configContent);
    }

    public static void setAutoRF(boolean onOff) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }
        String ifAutoOpen = onOff ? "1" : "0";

        for (LteChannelCfg channel : CacheManager.getChannels()) {
            setChannelConfig(channel.getIdx(), "", "", "", "", "", ifAutoOpen, "");
            channel.setAutoOpen(ifAutoOpen);
        }
    }

    public static void scanFreq() {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        LTE_PT_PARAM.setCommonParam(LTE_PT_PARAM.PARAM_SET_SCAN_FREQ, "");
    }

    public static void systemUpgrade(String upgradeCommand) {
        if (!CacheManager.isDeviceOk()) {
            return;
        }

        LogUtils.log("固件升级:" + upgradeCommand);
        LTE_PT_SYSTEM.setSystemParam(LTE_PT_SYSTEM.SYSTEM_UPGRADE, upgradeCommand);
    }
}
