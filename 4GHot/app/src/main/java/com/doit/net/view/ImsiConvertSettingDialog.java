package com.doit.net.view;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.doit.net.utils.ImsiMsisdnConvert;
import com.doit.net.utils.SPUtils;
import com.doit.net.utils.ToastUtils;
import com.doit.net.R;
import org.xutils.x;

/**
 * Created by Zxc on 2019/7/1.
 */

public class ImsiConvertSettingDialog extends Dialog {
    private View mView;
    private BootstrapEditText etServerAddress;
    private BootstrapEditText etAccount;
    private BootstrapEditText etPassword;
    private CheckBox cbRememberAccout;
    private TextView etRestConvertTimes;
    private BootstrapButton btAuthenticate;
    private BootstrapButton btClose;

    private static final String PRF_KEY_USERNAME = "translate_username";
    private static final String PRF_KEY_PASSWORD = "translate_password";
    private static final String PRF_KEY_IS_REMEMBER = "rememeber_translate";

    Activity activity;

    public ImsiConvertSettingDialog(Activity activity) {
        super(activity, R.style.Theme_dialog);
        this.activity = activity;
        initView();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mView);
        x.view().inject(this,mView);
    }

    @Nullable
    @Override
    public ActionBar getActionBar() {
        return super.getActionBar();
    }

    private void initView(){
        LayoutInflater inflater= LayoutInflater.from(getContext());
        mView = inflater.inflate(R.layout.layout_imsi_convert_setting, null);
        setCancelable(false);

        cbRememberAccout = mView.findViewById(R.id.cbRememberAccout);
        etServerAddress = mView.findViewById(R.id.etServerAddress);
        //etServerAddress.setText("http://101.201.213.210:8081");
        etAccount = mView.findViewById(R.id.etAccount);
//        if (!ImsiMsisdnConvert.getCurrentUsername().equals("")){
//            etAccount.setText(ImsiMsisdnConvert.getCurrentUsername());
//        }

        etPassword = mView.findViewById(R.id.etPassword);
//        if (!ImsiMsisdnConvert.getCurrentpassword().equals("")){
//            etPassword.setText(ImsiMsisdnConvert.getCurrentpassword());
//        }

        if (SPUtils.getBoolean(PRF_KEY_IS_REMEMBER, false)) {
            etAccount.setText(SPUtils.getString(PRF_KEY_USERNAME, ""));
            etPassword.setText(SPUtils.getString(PRF_KEY_PASSWORD, ""));
            cbRememberAccout.setChecked(true);
        }

        etRestConvertTimes = mView.findViewById(R.id.etRestConvertTimes);
        if(ImsiMsisdnConvert.getRestConvertTimes() != -1){
            etRestConvertTimes.setText(""+ImsiMsisdnConvert.getRestConvertTimes());
        }


        btAuthenticate = mView.findViewById(R.id.btAuthenticate);
        btAuthenticate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String serverAddress = etServerAddress.getText().toString();
                final String username = etAccount.getText().toString();
                final String password = etPassword.getText().toString();

                if ("".equals(serverAddress)){
                    ToastUtils.showMessage( "请输入服务器地址！");
                    return;
                } else if (username.equals("")){
                    ToastUtils.showMessage("请输入账号！");
                    return;
                }else if ("".equals(password)){
                    ToastUtils.showMessage("请输入密码！");
                    return;
                }

                new Thread() {
                    @Override
                    public void run() {
                        boolean res = ImsiMsisdnConvert.authenticate(activity, serverAddress, username, password);
                        if (res){
                            if (cbRememberAccout.isChecked()) {
                                SPUtils.setBoolean(PRF_KEY_IS_REMEMBER, true);
                                SPUtils.setString(PRF_KEY_USERNAME, username);
                                SPUtils.setString(PRF_KEY_PASSWORD, password);
                            } else {
                                // editor.clear();
                                SPUtils.setBoolean(PRF_KEY_IS_REMEMBER, false);
                                SPUtils.setString(PRF_KEY_USERNAME, "");
                                SPUtils.setString(PRF_KEY_PASSWORD, "");
                            }
                        }
                    }
                }.start();
            }
        });

        btClose = mView.findViewById(R.id.btClose);
        btClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
