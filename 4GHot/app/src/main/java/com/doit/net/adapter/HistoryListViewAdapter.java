package com.doit.net.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.doit.net.bean.DBBlackInfo;
import com.doit.net.event.AddToLocalBlackListener;
import com.doit.net.event.AddToLocationListener;
import com.doit.net.push.RequestUtils;
import com.doit.net.utils.CacheManager;
import com.doit.net.bean.DBUeidInfo;
import com.doit.net.utils.NetWorkUtils;
import com.doit.net.utils.SPUtils;
import com.doit.net.utils.ToastUtils;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.utils.VersionManage;
import com.doit.net.bean.WhiteListInfo;
import com.doit.net.view.AddBlacklistDialog;
import com.doit.net.view.AddWhitelistDialog;
import com.doit.net.view.ModifyBlackListDialog;
import com.doit.net.view.ModifyWhitelistDialog;
import com.doit.net.R;
import com.doit.net.utils.DateUtils;

import org.xutils.DbManager;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

public class HistoryListViewAdapter extends BaseSwipeAdapter {

    private DbManager dbManager;
    private Context mContext;
    private static List<DBUeidInfo> ueidList = new ArrayList<>();

    public HistoryListViewAdapter(Context mContext) {
        this.mContext = mContext;
        dbManager = UCSIDBManager.getDbManager();
    }

    public void refreshData(){
        notifyDataSetChanged();
    }

    public void setUeidList(List<DBUeidInfo> ueidList) {
        HistoryListViewAdapter.ueidList = ueidList;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.doit_layout_ueid_list_item, null);
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        TextView index = convertView.findViewById(R.id.position);
        index.setText((position + 1) + ".");

        TextView text_data = convertView.findViewById(R.id.tvUeidItemText);
        SwipeLayout swipeLayout = convertView.findViewById(R.id.layout_user_info);
        DBUeidInfo resp = ueidList.get(position);
        String content = "IMSI:"+resp.getImsi()+"\n";
        if (!TextUtils.isEmpty(resp.getMsisdn())){
            content += "手机号:"+resp.getMsisdn()+"\n";
        }
        content += mContext.getString(R.string.lab_rpt_time)+ DateUtils.convert2String(resp.getCreateDate(), DateUtils.LOCAL_DATE);
        text_data.setText(content);
        text_data.setTag(position);


        if(VersionManage.isArmyVer()){
            convertView.findViewById(R.id.iv_translate).setVisibility(View.GONE);
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
        }else {
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

        //if(BuildConfig.LOC_MODEL){
        if(CacheManager.getLocMode()){
            convertView.findViewById(R.id.add_to_localtion).setOnClickListener(new AddToLocationListener(mContext,resp.getImsi()));
        }else{
            convertView.findViewById(R.id.add_to_localtion).setVisibility(View.GONE);
        }

    }


    @Override
    public int getCount() {
        return ueidList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        private TextView itemText;
    }

    public interface onItemLongClickListener {
        void onItemLongClick(MotionEvent motionEvent, int position);
    }

}
