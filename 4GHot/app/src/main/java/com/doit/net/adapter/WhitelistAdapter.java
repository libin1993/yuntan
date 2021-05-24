package com.doit.net.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.doit.net.event.EventAdapter;
import com.doit.net.utils.BlackBoxManger;
import com.doit.net.utils.CacheManager;
import com.doit.net.bean.WhiteListInfo;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.utils.LogUtils;
import com.doit.net.view.MySweetAlertDialog;
import com.doit.net.view.ModifyWhitelistDialog;
import com.doit.net.R;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Zxc on 2019/5/30.
 */

public class WhitelistAdapter extends BaseSwipeAdapter {
    private Context mContext;

    private static List<WhiteListInfo> listWhitelistInfo = new ArrayList<>();

    public WhitelistAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setUserInfoList(List<WhiteListInfo> listWhitelist) {
        listWhitelistInfo = listWhitelist;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.sl_white_list;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.layout_whitelist_item, null);
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView tvIndex = convertView.findViewById(R.id.tvIndex);
        TextView tvWhitelistInfo = convertView.findViewById(R.id.tvWhitelistInfo);
        SwipeLayout swipeLayout = convertView.findViewById(R.id.sl_white_list);
        final WhiteListInfo whitelistInfo = listWhitelistInfo.get(position);
        tvIndex.setText(" " + (position + 1) + ".");
        tvWhitelistInfo.setText("IMSI：" + whitelistInfo.getImsi() + "\n手机号：" + whitelistInfo.getMsisdn() +
                "\n备注：" + whitelistInfo.getRemark());
        tvWhitelistInfo.setTag(position);

        convertView.findViewById(R.id.ivDelete).setOnClickListener(new WhitelistAdapter.DeleteWhitelistListener(position));
        convertView.findViewById(R.id.ivModify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyWhitelistDialog modifyUserInfoDialog = new ModifyWhitelistDialog(mContext, whitelistInfo.getImsi(), whitelistInfo.getMsisdn(), whitelistInfo.getRemark());
                modifyUserInfoDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        EventAdapter.call(EventAdapter.REFRESH_WHITELIST);

                        if (swipeLayout != null) {
                            swipeLayout.close();
                        }
                    }
                });
                modifyUserInfoDialog.show();
            }
        });

    }

    @Override
    public int getCount() {
        return listWhitelistInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateView() {
        notifyDataSetChanged();
    }

    class DeleteWhitelistListener implements View.OnClickListener {
        private int position;

        public DeleteWhitelistListener(int position) {
            this.position = position;
        }

        @Override
        public void onClick(View v) {
            new MySweetAlertDialog(mContext, MySweetAlertDialog.WARNING_TYPE)
                    .setTitleText("删除名单")
                    .setContentText("确定要删除名单吗吗？")
                    .setCancelText(mContext.getString(R.string.cancel))
                    .setConfirmText(mContext.getString(R.string.sure))
                    .showCancelButton(true)
                    .setConfirmClickListener(new MySweetAlertDialog.OnSweetClickListener() {

                        @Override
                        public void onClick(MySweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismiss();

                            WhiteListInfo resp = listWhitelistInfo.get(position);
                            try {
                                UCSIDBManager.getDbManager().delete(resp);
                                if (!"".equals(resp.getImsi())) {
                                    CacheManager.updateWhitelistToDev(mContext);
                                }

                                EventAdapter.call(EventAdapter.REFRESH_WHITELIST);

                                LogUtils.log("删除白名单："+resp.toString());
                                EventAdapter.call(EventAdapter.ADD_BLACKBOX, BlackBoxManger.DELETE_WHITE_LIST
                                        + resp.getImsi() + "+" + resp.getMsisdn()+"+"+resp.getRemark());
                            } catch (DbException e) {
                                new SweetAlertDialog(mContext, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText(mContext.getString(R.string.del_white_list_fail))
                                        .show();
                            }

                        }
                    })
                    .show();
        }
    }
}
