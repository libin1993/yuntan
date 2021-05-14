package com.doit.net.event;

import android.content.Context;
import android.view.View;

import com.doit.net.bean.DBBlackInfo;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.ucsi.R;
import com.doit.net.utils.ToastUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by wiker on 2016/4/27.
 */
public class AddToLocalBlackListener implements View.OnClickListener {
    private Context mContext;
    private String imsi;
    private String name="";
    private String remark="";

    public AddToLocalBlackListener(Context mContext, String name, String imsi, String remark) {
        this.mContext = mContext;
        this.imsi = imsi;
        this.name = name;
        this.remark = remark;
    }

    public AddToLocalBlackListener(Context mContext, String imsi) {
        this.mContext = mContext;
        this.imsi = imsi;
    }

    @Override
    public void onClick(View v) {
        try {
            DbManager dbManager = UCSIDBManager.getDbManager();
            long count = dbManager.selector(DBBlackInfo.class)
                    .where("imsi","=",imsi).count();
            if(count>0){
                ToastUtils.showMessage(R.string.tip_17);
                return;
            }
            DBBlackInfo info = new DBBlackInfo();
            info.setCreateDate(new Date());
            info.setRemark(remark);
            info.setImsi(imsi);
            info.setName(name);
            dbManager.save(info);

//            if (CacheManager.isDeviceOk() && !CacheManager.getLocState()){
//                ProtocolManager.setBlackList("2", "#"+imsi);
//            }

            ToastUtils.showMessage(R.string.add_success);
        } catch (DbException e) {
            new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText(mContext.getString(R.string.add_black_fail))
                    .show();
        }
    }
}
