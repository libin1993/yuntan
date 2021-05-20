package com.doit.net.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.doit.net.application.MyApplication;
import com.doit.net.base.BaseBean;
import com.doit.net.base.Callback;
import com.doit.net.base.RxObserver;
import com.doit.net.base.Transformer;
import com.doit.net.bean.UserBean;
import com.doit.net.push.RequestUtils;
import com.doit.net.utils.FileUtils;
import com.doit.net.base.BaseActivity;
import com.doit.net.utils.BlackBoxManger;
import com.doit.net.utils.CacheManager;
import com.doit.net.utils.GsonUtils;
import com.doit.net.utils.NetWorkUtils;
import com.doit.net.utils.PopupWindowUtils;
import com.doit.net.utils.RetrofitUtils;
import com.doit.net.utils.SPUtils;
import com.doit.net.utils.ToastUtils;
import com.doit.net.utils.LogUtils;
import com.doit.net.R;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity {

    private CheckBox ckRememberPass;
    private EditText etUserName;
    private EditText etPassword;
    private Button btLogin;
    ImageView ivUserNameClear;
    ImageView ivdPasswordClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);


        initView();
        checkLocalDir();
        initLog();
    }

    private void checkLocalDir() {
        File dir = new File(FileUtils.ROOT_PATH);
        if (!dir.exists() && !dir.isDirectory()) {
            dir.mkdir();
        }

        BlackBoxManger.checkBlackBoxFile();

    }

    private void initLog() {
        LogUtils.initLog(); //必须在UPDATE_FILE_SYS事件注册后，否则电脑端无法显示
    }


    /**
     * 是否开启搜寻功能
     */
    private void checkLocMode() {
        boolean ifOpenLocMode = SPUtils.getBoolean(SPUtils.LOC_PREF_KEY, true);

        CacheManager.setLocMode(ifOpenLocMode);
        SPUtils.setBoolean(SPUtils.LOC_PREF_KEY, ifOpenLocMode);
    }

    private void initView() {
        etUserName = findViewById(R.id.etImsi);
        etPassword = findViewById(R.id.etPassword);
        ivUserNameClear = findViewById(R.id.ivUserNameClear);
        ivdPasswordClear = findViewById(R.id.ivdPasswordClear);
        ckRememberPass = findViewById(R.id.ckRememberPass);
        btLogin = findViewById(R.id.btLogin);

        CacheManager.DEVICE_IP = SPUtils.getString(SPUtils.DEVICE_IP,CacheManager.DEFAULT_DEVICE_IP);

        checkLocMode();

        boolean isRemember = SPUtils.getBoolean(SPUtils.REMEMBER_PASSWORD, false);
        if (isRemember) {
            String userName = SPUtils.getString(SPUtils.USERNAME, "");
            String Password = SPUtils.getString(SPUtils.PASSWORD, "");
            etUserName.setText(userName);
            etPassword.setText(Password);
            ckRememberPass.setChecked(true);
        }


        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SPUtils.setBoolean(SPUtils.REMEMBER_PASSWORD,ckRememberPass.isChecked());
                if(!NetWorkUtils.getNetworkState()){
                    new AlertDialog.Builder(LoginActivity.this,AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                            .setTitle("网络不可用")
                            .setMessage("请先连接设备WIFI，否则将无法使用")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                                }
                            })
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false)
                            .show();
                }else {
                    String username = etUserName.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();

                    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                        ToastUtils.showMessage("密码或账号为空，请重新输入");
                        return;
                    }

                    login(username,password);
                }
            }
        });

        addClearListener(etUserName, ivUserNameClear);
        addClearListener(etPassword, ivdPasswordClear);
    }


    /**
     * @param username
     * @param password
     *
     * 登录
     */
    public void login(String username,String password){
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        map.put("pwd", password);

        RequestBody body= RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), GsonUtils.jsonString(map));
        RetrofitUtils.getInstance()
                .getService()
                .login(body)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver(new Callback<UserBean>() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        String token = userBean.getData().getToken();
                        String userId = userBean.getData().getUser().getId();
                        SPUtils.setString(SPUtils.TOKEN,token);
                        SPUtils.setString(SPUtils.USER_ID,userId);
                        SPUtils.setString(SPUtils.USERNAME,username);
                        SPUtils.setString(SPUtils.PASSWORD,password);
                        LogUtils.log("登录token："+token);
                        LogUtils.log("登录userId："+userId);

                        bindDevice();

                    }

                    @Override
                    public void onFail(String msg) {
                        ToastUtils.showMessage(msg);
                    }
                }));
    }


    /**
     * 绑定设备
     */
    public void bindDevice(){
        String deviceNo = SPUtils.getString(SPUtils.DEVICE_NO,"");
        if(TextUtils.isEmpty(deviceNo)){
            startActivity(new Intent(LoginActivity.this,MainActivity.class));
            finish();
            return ;
        }
        Map<String,Object> map = new HashMap<>();
        map.put("deviceNo",deviceNo);

        RequestBody body= RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), GsonUtils.jsonString(map));
        RetrofitUtils.getInstance()
                .getService()
                .bind(body)
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver(new Callback<BaseBean>() {
                    @Override
                    public void onSuccess(BaseBean baseBean) {
                        LogUtils.log("绑定设备成功："+baseBean.toString());
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }

                    @Override
                    public void onFail(String msg) {
                        LogUtils.log("绑定设备失败："+msg);
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                }));
    }



    private void addClearListener(final EditText et, final ImageView iv) {
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //如果有输入内容长度大于0那么显示clear按钮
                if (editable.length() > 0) {
                    iv.setVisibility(View.VISIBLE);
                } else {
                    iv.setVisibility(View.INVISIBLE);
                }
            }
        });

        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setText("");
            }
        });
    }
}
