package com.doit.net.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.doit.net.push.RequestUtils;
import com.doit.net.utils.NetWorkUtils;
import com.doit.net.utils.SPUtils;
import com.doit.net.utils.ToastUtils;
import com.doit.net.view.AddBlacklistDialog;
import com.doit.net.view.AddWhitelistDialog;
import com.doit.net.view.ModifyBlackListDialog;
import com.doit.net.view.ModifyWhitelistDialog;
import com.doit.net.application.MyApplication;
import com.doit.net.bean.UeidBean;
import com.doit.net.event.AddToLocalBlackListener;
import com.doit.net.event.AddToLocationListener;
import com.doit.net.utils.CacheManager;
import com.doit.net.bean.DBBlackInfo;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.utils.VersionManage;
import com.doit.net.bean.WhiteListInfo;
import com.doit.net.utils.LogUtils;
import com.doit.net.utils.UtilOperator;
import com.doit.net.R;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

public class UeidListViewAdapter extends BaseSwipeAdapter {

    private Context mContext;

    public UeidListViewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    private DbManager dbManager;


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe2;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.doit_layout_ueid_list_item, null);
        dbManager = UCSIDBManager.getDbManager();

        return v;
    }

    @Override
    public synchronized void fillValues(int position, View convertView) {
        LinearLayout layoutItemText = convertView.findViewById(R.id.layoutItemText);
        if (position % 2 == 0) {
            layoutItemText.setBackgroundColor(mContext.getResources().getColor(R.color.deepgrey2));
        } else {
            layoutItemText.setBackgroundColor(mContext.getResources().getColor(R.color.black));
        }

        TextView index = convertView.findViewById(R.id.position);
        index.setText((position + 1) + ".");

        TextView tvContent = convertView.findViewById(R.id.tvUeidItemText);
        UeidBean resp = CacheManager.realtimeUeidList.get(position);

        SwipeLayout swipeLayout = convertView.findViewById(R.id.swipe2);

       if (VersionManage.isArmyVer()) {
           convertView.findViewById(R.id.add_to_black).setVisibility(View.GONE);
            convertView.findViewById(R.id.add_to_black).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {
                        WhiteListInfo info = dbManager.selector(WhiteListInfo.class).where("imsi", "=", resp.getImsi()).findFirst();
                        if (info != null) {
                            ModifyWhitelistDialog modifyWhitelistDialog = new ModifyWhitelistDialog(mContext,
                                    resp.getImsi(), info.getMsisdn(), info.getRemark(),false);
                            modifyWhitelistDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    notifyDataSetChanged();

                                    if (swipeLayout !=null){
                                        swipeLayout.close();
                                    }
                                }
                            });
                            modifyWhitelistDialog.show();
                        }else {
                            AddWhitelistDialog addWhitelistDialog = new AddWhitelistDialog(mContext, resp.getImsi());
                            addWhitelistDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialog) {
                                    notifyDataSetChanged();

                                    if (swipeLayout !=null){
                                        swipeLayout.close();
                                    }
                                }
                            });
                            addWhitelistDialog.show();
                        }
                    } catch (DbException e) {
                        e.printStackTrace();
                    }
                }
            });
        } else {
           convertView.findViewById(R.id.add_to_black).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   try {
                       DBBlackInfo info = dbManager.selector(DBBlackInfo.class).where("imsi", "=", resp.getImsi()).findFirst();
                       if (info != null) {

                           ModifyBlackListDialog modifyBlackListDialog = new ModifyBlackListDialog(mContext,
                                   resp.getImsi(), info.getMsisdn(), info.getRemark(),false);
                           modifyBlackListDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                               @Override
                               public void onDismiss(DialogInterface dialog) {
                                   notifyDataSetChanged();
                                   if (swipeLayout !=null){
                                       swipeLayout.close();
                                   }
                               }
                           });
                           modifyBlackListDialog.show();
                       }else {
                           AddBlacklistDialog addBlacklistDialog = new AddBlacklistDialog(mContext, resp.getImsi(),resp.getMsisdn());
                           addBlacklistDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                               @Override
                               public void onDismiss(DialogInterface dialog) {
                                   notifyDataSetChanged();
                                   if (swipeLayout !=null){
                                       swipeLayout.close();
                                   }
                               }
                           });
                           addBlacklistDialog.show();
                       }
                   } catch (DbException e) {
                       e.printStackTrace();
                   }
               }
           });

           convertView.findViewById(R.id.iv_translate).setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   if (!NetWorkUtils.getNetworkState()){
                       ToastUtils.showMessage("请先连接设备WIFI，否则将无法使用");
                       return;
                   }

                   if (TextUtils.isEmpty(SPUtils.getString(SPUtils.DEVICE_NO,""))){
                       ToastUtils.showMessage("请先绑定设备");
                       return;
                   }

                   RequestUtils.uploadIMSI(resp.getImsi());

                   ToastUtils.showMessage("正在翻译...");

                   if (swipeLayout !=null){
                       swipeLayout.close();
                   }
               }
           });
        }

        if (CacheManager.getLocMode()) {
            convertView.findViewById(R.id.add_to_localtion).setOnClickListener(new AddToLocationListener( mContext, resp.getImsi()));
        } else {
            convertView.findViewById(R.id.add_to_localtion).setVisibility(View.GONE);
        }


        checkBlackWhiteList(resp, tvContent);

    }

    private void checkBlackWhiteList(UeidBean resp, TextView tvContent) {

        String content = "IMSI：" + resp.getImsi() + "          " + "制式: " + UtilOperator.getOperatorNameCH(resp.getImsi()) + "\n";

        //如果是管控模式，其次检查白名单
        if (VersionManage.isArmyVer()) {

            try {
                if (!"".equals(resp.getImsi())) {

                    content += resp.getRptTime() + "       " + "次数：" + resp.getRptTimes() + "       "
                            + mContext.getString(R.string.ueid_last_intensity) + resp.getSrsp();
                    WhiteListInfo info = dbManager.selector(WhiteListInfo.class).where("imsi", "=", resp.getImsi()).findFirst();
                    if (info != null) {
                        String msisdn = info.getMsisdn();
                        String remark = info.getRemark();
                        if (!TextUtils.isEmpty(msisdn)) {
                            content += "\n" + "手机号：" + msisdn + "           ";
                        }

                        if (!TextUtils.isEmpty(remark)) {
                            if (!TextUtils.isEmpty(msisdn)) {
                                content += remark;
                            } else {
                                content += "\n" + remark;
                            }
                        }

                        tvContent.setTextColor(MyApplication.mContext.getResources().getColor(R.color.forestgreen));

                    } else {
                        tvContent.setTextColor(MyApplication.mContext.getResources().getColor(R.color.white));
                    }
                    tvContent.setText(content);
                }

            } catch (DbException e) {
                LogUtils.log("查询白名单异常" + e.getMessage());
            }
        }else {
            DBBlackInfo dbBlackInfo = null;
            try {
                dbBlackInfo = dbManager.selector(DBBlackInfo.class).where("imsi", "=", resp.getImsi()).findFirst();
            } catch (DbException e) {
                LogUtils.log("查询黑名单异常" + e.getMessage());
            }

            String msisdn = resp.getMsisdn();
            if (!TextUtils.isEmpty(msisdn)){
                content += "手机号：" + msisdn;
            }

            content += "\n"+mContext.getString(R.string.ueid_last_rpt_time) + resp.getRptTime();
            if (dbBlackInfo != null) {
                String remark = dbBlackInfo.getRemark();

                if (!TextUtils.isEmpty(remark)) {
                    content += "       "+remark;
                }

                tvContent.setTextColor(MyApplication.mContext.getResources().getColor(R.color.red));
            } else {
                tvContent.setTextColor(MyApplication.mContext.getResources().getColor(R.color.white));
            }
            tvContent.setText(content);
        }


    }

    @Override
    public int getCount() {
        return CacheManager.realtimeUeidList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
