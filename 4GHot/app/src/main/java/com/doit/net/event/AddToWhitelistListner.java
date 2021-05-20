package com.doit.net.event;

import android.content.Context;
import android.view.View;

import com.doit.net.utils.UCSIDBManager;
import com.doit.net.bean.WhiteListInfo;
import com.doit.net.utils.ToastUtils;
import com.doit.net.R;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zxc on 2019/7/1.
 */

public class AddToWhitelistListner implements View.OnClickListener {
    private Context mContext;
    private String imsi;
    private String msisdn="";
    private String remark="";

    public AddToWhitelistListner(Context mContext, String imsi, String msisdn, String remark) {
        this.mContext = mContext;
        this.remark = remark;
        this.msisdn = msisdn;
        this.imsi = imsi;

    }

    public AddToWhitelistListner(Context mContext, String imsi) {
        this.mContext = mContext;
        this.imsi = imsi;
    }

    @Override
    public void onClick(View v) {
        try {

            DbManager dbManager = UCSIDBManager.getDbManager();
            if (!"".equals(imsi)){
                long count = dbManager.selector(WhiteListInfo.class)
                        .where("imsi","=",imsi)
                        .count();
                if(count>0){

                    ToastUtils.showMessage( R.string.exist_whitelist);
                    return;
                }
            }else{
                long count = dbManager.selector(WhiteListInfo.class)
                        .where("msisdn","=",msisdn)
                        .count();
                if(count>0){
                    ToastUtils.showMessage( R.string.exist_whitelist);
                    return;
                }
            }

            WhiteListInfo info = new WhiteListInfo();
            info.setRemark(remark);
            info.setImsi(imsi);
            info.setMsisdn(msisdn);
            dbManager.save(info);

            ToastUtils.showMessage(R.string.add_success);
        } catch (DbException e) {
            e.printStackTrace();
            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(mContext.getString(R.string.add_whitelist_fail))
                    .show();
        }
    }
}
