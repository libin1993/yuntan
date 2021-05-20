package com.doit.net.activity;

import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.doit.net.event.EventAdapter;
import com.doit.net.view.AddUserDialog;
import com.doit.net.view.ModifyAdminAccountDialog;
import com.doit.net.adapter.UserListAdapter;
import com.doit.net.base.BaseActivity;
import com.doit.net.utils.UCSIDBManager;
import com.doit.net.bean.UserInfo;
import com.doit.net.R;

import org.xutils.ex.DbException;

import java.util.ArrayList;
import java.util.List;

/**
 * 用户管理
 */
public class UserManageActivity extends BaseActivity implements EventAdapter.EventCall {

    private ListView lvUserInfo;
    private UserListAdapter mAdapter;
    private Button btAddUser;
    private Button btModifyAdmin;


    private int lastOpenSwipePos = -1;

    private List<UserInfo> listUserInfo = new ArrayList<>();

    //handler消息
    private final int REFRESH_LIST = 0;
    private final int UPDATE_LIST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_manage);
        getWindow ().setFlags (WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        lvUserInfo = findViewById(R.id.lvUserInfo);
        btAddUser = findViewById(R.id.btAddUser);
        btAddUser.setOnClickListener(addUserClick);
        btModifyAdmin = findViewById(R.id.btModifyAdmin);
        btModifyAdmin.setOnClickListener(modifyAdminClick);

        mAdapter = new UserListAdapter(this);
        lvUserInfo.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);
        lvUserInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                lastOpenSwipePos = position - lvUserInfo.getFirstVisiblePosition();
                openSwipe(lastOpenSwipePos);
            }
        });

        updateListFromDatabase();
        EventAdapter.register(EventAdapter.REFRESH_USER_LIST,this);

    }


    View.OnClickListener addUserClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            AddUserDialog addUserDialog = new AddUserDialog(UserManageActivity.this);
            addUserDialog.show();
        }
    };

    View.OnClickListener modifyAdminClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            ModifyAdminAccountDialog modifyAdminAccountDialog = new ModifyAdminAccountDialog(UserManageActivity.this,
//                    AccountManage.getAdminAcount(), AccountManage.getAdminPassword());
//            modifyAdminAccountDialog.show();

        }
    };

    void updateListFromDatabase(){
//        try {
//            listUserInfo = UCSIDBManager.getDbManager().selector(UserInfo.class)
//                    .where("remake","!=", AccountManage.getAdminRemark())
//                    .findAll();
//
//            if (listUserInfo == null) {
//                listUserInfo = new ArrayList<>();
//            }
//            mAdapter.setUserInfoList(listUserInfo);
//
//            mHandler.sendEmptyMessage(REFRESH_LIST);
//        } catch (DbException e) {
//            e.printStackTrace();
//        }
    }

    private void openSwipe(int position){
        ((SwipeLayout) (lvUserInfo.getChildAt(position))).open(true);
        ((SwipeLayout) (lvUserInfo.getChildAt(position))).setClickToClose(true);
    }

    private void closeSwipe(int position){
        if (listUserInfo == null || listUserInfo.size() == 0 || position == -1)
            return;

        SwipeLayout swipe = ((SwipeLayout) (lvUserInfo.getChildAt(position)));
        if (swipe != null){
            swipe.close(true);
        }
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

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REFRESH_LIST) {
                if (mAdapter != null) {
                    mAdapter.updateView();
                }

                closeSwipe(lastOpenSwipePos);
            }else if(msg.what == UPDATE_LIST){
                updateListFromDatabase();
            }
        }
    };


    @Override
    public void call(String key, Object val) {
        switch (key){
            case EventAdapter.REFRESH_USER_LIST:
                mHandler.sendEmptyMessage(UPDATE_LIST);
                break;
        }
    }

}
