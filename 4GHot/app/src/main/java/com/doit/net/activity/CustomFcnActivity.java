package com.doit.net.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.doit.net.utils.CacheManager;
import com.doit.net.bean.DBChannel;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.utils.LogUtils;
import com.doit.net.view.MySweetAlertDialog;
import com.doit.net.utils.ToastUtils;
import com.doit.net.view.AddFcnDialog;
import com.doit.net.base.BaseActivity;
import com.doit.net.bean.LteChannelCfg;
import com.doit.net.bean.SectionBean;
import com.doit.net.R;

import org.xutils.DbManager;
import org.xutils.db.sqlite.WhereBuilder;
import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author：Libin on 2020/5/22 13:33
 * Email：1993911441@qq.com
 * Describe：自定义频点
 */
public class CustomFcnActivity extends BaseActivity {
    @BindView(R.id.rv_custom_fcn)
    RecyclerView rvCustomFcn;


    private BaseSectionQuickAdapter<SectionBean, BaseViewHolder> adapter;
    private List<SectionBean> dataList = new ArrayList<>();

    private DbManager dbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_fcn);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);
        dbManager = UCSIDBManager.getDbManager();
        initView();
        initData();
    }

    private void initView() {
        rvCustomFcn.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BaseSectionQuickAdapter<SectionBean, BaseViewHolder>(R.layout.layout_fcn_item,
                R.layout.layout_fcn_header, dataList) {

            @Override
            protected void convert(BaseViewHolder helper, SectionBean item) {
                DBChannel channel = item.t;
                helper.setText(R.id.tv_fcn, "频点: " + channel.getFcn());
                ImageView ivCheck = helper.getView(R.id.iv_select_fcn);
                TextView tvCheck = helper.getView(R.id.tv_select_fcn);
                LinearLayout llEdit = helper.getView(R.id.ll_edit_fcn);
                LinearLayout llDelete = helper.getView(R.id.ll_delete_fcn);
                if (channel.isCheck() == 1) {
                    ivCheck.setImageResource(R.mipmap.ic_fcn_checked);
                    tvCheck.setVisibility(View.VISIBLE);
                } else {
                    ivCheck.setImageResource(R.mipmap.ic_fcn_normal);
                    tvCheck.setVisibility(View.INVISIBLE);
                }

                if (channel.isDefault() == 1) {
                    llEdit.setVisibility(View.GONE);
                    llDelete.setVisibility(View.GONE);
                } else {
                    llEdit.setVisibility(View.VISIBLE);
                    llDelete.setVisibility(View.VISIBLE);
                }

                helper.addOnClickListener(R.id.ll_select_fcn);
                helper.addOnClickListener(R.id.ll_edit_fcn);
                helper.addOnClickListener(R.id.ll_delete_fcn);
            }

            @Override
            protected void convertHead(BaseViewHolder helper, SectionBean item) {
                helper.setText(R.id.tv_band,  "频段: " + item.header);
                helper.addOnClickListener(R.id.iv_add_fcn);
            }


        };

        rvCustomFcn.setAdapter(adapter);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                switch (view.getId()) {
                    case R.id.iv_add_fcn:
                        SectionBean sectionBean = dataList.get(position);

                        AddFcnDialog addFcnDialog = new AddFcnDialog(CustomFcnActivity.this,
                                "添加频点", sectionBean.header, "", new AddFcnDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm(String value) {

                                addFcn(position, sectionBean.header,value);
                            }
                        });
                        addFcnDialog.show();
                        break;
                    case R.id.ll_select_fcn:
                        SectionBean section = dataList.get(position);
                        if (section.t.isCheck() == 1) {
                            return;
                        }
                        checkFcn(section.t.getBand(), section.t.getFcn());
                        break;
                    case R.id.ll_edit_fcn:
                        AddFcnDialog editFcnDialog = new AddFcnDialog(CustomFcnActivity.this,
                                "编辑频点", dataList.get(position).t.getBand(),
                                dataList.get(position).t.getFcn(), new AddFcnDialog.OnConfirmListener() {
                            @Override
                            public void onConfirm(String value) {
                                SectionBean sectionBean = dataList.get(position);
                                update(position, sectionBean.t.getId(), sectionBean.t.getBand(), value);
                            }
                        });
                        editFcnDialog.show();
                        break;
                    case R.id.ll_delete_fcn:
                        SectionBean sectionDelete = dataList.get(position);
                        deleteDcn(position, sectionDelete.t.getBand(), sectionDelete.t.getFcn(), sectionDelete.t.isCheck());
                        break;
                }
            }
        });
    }

    /**
     * 设置默认fcn
     */
    private void checkFcn(String band, String fcn) {
        for (int i = 0; i < dataList.size(); i++) {
            SectionBean sectionBean = dataList.get(i);
            if (!sectionBean.isHeader && band.equals(sectionBean.t.getBand())) {
                if (fcn.equals(sectionBean.t.getFcn())) {
                    dataList.get(i).t.setCheck(1);
                } else {
                    dataList.get(i).t.setCheck(0);
                }
            }
        }

        adapter.notifyDataSetChanged();

        try {
            List<DBChannel> dbChannel = dbManager.selector(DBChannel.class)
                    .where("band", "=", band)
                    .findAll();
            for (DBChannel channel : dbChannel) {
                if (fcn.equals(channel.getFcn())) {
                    channel.setCheck(1);
                } else {
                    channel.setCheck(0);
                }
                dbManager.update(dbChannel);
            }

            ToastUtils.showMessage("设置成功，下次启动APP生效");
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param position
     * @param id
     * @param band
     * @param fcn      编辑fcn
     */
    private void update(int position, int id, String band, String fcn) {
        for (int i = 0; i < dataList.size(); i++) {
            SectionBean sectionBean = dataList.get(i);
            if (!sectionBean.isHeader && band.equals(sectionBean.t.getBand()) && fcn.equals(sectionBean.t.getFcn())) {
                ToastUtils.showMessage("已存在相同频点");
                return;
            }
        }

        dataList.get(position).t.setFcn(fcn);
        adapter.notifyItemChanged(position);

        try {

            DBChannel dbChannel = dbManager.selector(DBChannel.class)
                    .where("id", "=", id)
                    .findFirst();
            if (dbChannel != null) {
                dbChannel.setFcn(fcn);
                dbManager.update(dbChannel);
                ToastUtils.showMessage("修改成功，下次启动APP生效");
            }

        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除fcn
     */
    private void deleteDcn(int position, String band, String fcn, int isCheck) {
        //删除已选中的，设置默认fcn为选中状态

        new MySweetAlertDialog(CustomFcnActivity.this, MySweetAlertDialog.WARNING_TYPE)
                .setTitleText("删除频点")
                .setContentText("确定要删除该频点吗？")
                .setCancelText(getString(R.string.cancel))
                .setConfirmText(getString(R.string.sure))
                .showCancelButton(true)
                .setConfirmClickListener(new MySweetAlertDialog.OnSweetClickListener() {

                    @Override
                    public void onClick(MySweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();

                        try {
                            WhereBuilder whereBuilder = WhereBuilder.b();
                            whereBuilder.and("band", "=", band)
                                    .and("fcn", "=", fcn);
                            dbManager.delete(DBChannel.class, whereBuilder);
                        } catch (DbException e) {
                            e.printStackTrace();
                            LogUtils.log("删除频点失败：" + e.getMessage());
                        }

                        if (isCheck == 1) {
                            for (int i = 0; i < dataList.size(); i++) {
                                SectionBean sectionBean = dataList.get(i);
                                if (!sectionBean.isHeader && band.equals(sectionBean.t.getBand())) {
                                    if (sectionBean.t.isDefault() == 1) {
                                        dataList.get(i).t.setCheck(1);
                                        try {
                                            DBChannel dbChannel = dbManager.selector(DBChannel.class)
                                                    .where("id", "=", dataList.get(i).t.getId())
                                                    .findFirst();
                                            if (dbChannel != null) {
                                                dbChannel.setCheck(1);
                                                dbManager.update(dbChannel);
                                            }

                                        } catch (DbException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                        dataList.remove(position);
                        adapter.notifyDataSetChanged();

                    }
                })
                .show();

    }

    /**
     * @param band
     * @param fcn  新增fcn
     */
    private void addFcn(int position, String band, String fcn) {
        int index = position; //插入的位置
        for (int i = position; i < dataList.size(); i++) {
            SectionBean sectionBean = dataList.get(i);
            if (!sectionBean.isHeader && band.equals(sectionBean.t.getBand()) && fcn.equals(sectionBean.t.getFcn())) {
                ToastUtils.showMessage("已存在相同频点");
                return;
            }

            if (sectionBean.isHeader) {
                if (band.equals(sectionBean.header)) {
                    index++;
                }
            } else {
                if (band.equals(sectionBean.t.getBand())) {
                    index++;
                }
            }

        }

        DBChannel dbChannel = new DBChannel(band, fcn, 0, 0);
        SectionBean section = new SectionBean(dbChannel);
        if (index == dataList.size()) {
            dataList.add(section);
        } else {
            dataList.add(index, section);
        }

        adapter.notifyDataSetChanged();

        try {
            dbManager.save(dbChannel);
        } catch (DbException e) {
            e.printStackTrace();
        }

    }

    private void initData() {
        for (LteChannelCfg channel : CacheManager.channels) {
            dataList.add(new SectionBean(true, channel.getBand()));
            try {
                List<DBChannel> channelList = dbManager.selector(DBChannel.class)
                        .where("band", "=", channel.getBand())
                        .findAll();
                for (DBChannel dbChannel : channelList) {
                    dataList.add(new SectionBean(dbChannel));
                }
            } catch (DbException e) {
                e.printStackTrace();
            }
        }

        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
