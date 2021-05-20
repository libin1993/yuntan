package com.doit.net.bean;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.Date;

/**
 * Created by wiker on 2016/4/26.
 */
@Table(name = "BlackList")
public class DBBlackInfo {
    @Column(name = "id", isId = true)
    private int id;

    @Column(name = "imsi")
    private String imsi;

    @Column(name = "msisdn")
    private String msisdn;

    @Column(name = "remark")
    private String remark;

    public DBBlackInfo(String imsi, String msisdn, String remark) {
        this.imsi = imsi;
        this.msisdn = msisdn;
        this.remark = remark;
    }


    public DBBlackInfo() {
    }

    public String getImsi() {
        return imsi;
    }
    public String getMsisdn() {
        return msisdn;
    }

    public String getRemark() {
        return remark;
    }

    public void setImsi(String imsi) {
        this.imsi = imsi;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "WhiteListInfo{" +
                "id=" + id +
                ", imsi='" + imsi + '\'' +
                ", msisdn='" + msisdn + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
