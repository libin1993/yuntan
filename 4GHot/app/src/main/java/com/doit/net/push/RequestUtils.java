package com.doit.net.push;

import android.text.TextUtils;
import android.widget.Toast;

import com.doit.net.base.Api;
import com.doit.net.base.BaseBean;
import com.doit.net.base.Callback;
import com.doit.net.base.RxObserver;
import com.doit.net.base.Transformer;
import com.doit.net.bean.UserBean;
import com.doit.net.utils.GsonUtils;
import com.doit.net.utils.LogUtils;
import com.doit.net.utils.NetWorkUtils;
import com.doit.net.utils.RetrofitUtils;
import com.doit.net.utils.SPUtils;
import com.doit.net.utils.ToastUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Author：Libin on 2021/5/19 10:44
 * Email：1993911441@qq.com
 * Describe：
 */
public class RequestUtils {
    public static void login(String username,String password){
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
    public static void bindDevice(){

        String deviceNo = SPUtils.getString(SPUtils.DEVICE_NO,"");
        if(TextUtils.isEmpty(deviceNo)){
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

                    }

                    @Override
                    public void onFail(String msg) {
                        LogUtils.log("绑定设备失败："+msg);
                    }
                }));
    }


    /**
     * 解绑设备
     */
    public static void unbind(){

        RetrofitUtils.getInstance()
                .getService()
                .unbind()
                .compose(Transformer.switchSchedulers())
                .subscribe(new RxObserver(new Callback<BaseBean>() {

                    @Override
                    public void onSuccess(BaseBean baseBean) {
                        LogUtils.log("解绑成功："+baseBean.toString());
                    }

                    @Override
                    public void onFail(String msg) {
                        LogUtils.log("解绑失败："+msg);
                    }
                }));
    }


    /**
     * 上报imsi
     */
    public static void uploadIMSI(String imsi){
        Map<String,Object> map = new HashMap<>();
        map.put("deviceType","0");
        map.put("latitude", 28.229429);
        map.put("longitude", 113.13568);
        map.put("location", "");
        map.put("source", "电围设备");
        map.put("type", "imsi");
        map.put("time", String.valueOf(System.currentTimeMillis()/10000));
        map.put("deviceId", SPUtils.getString(SPUtils.DEVICE_NO,""));
        map.put("value", imsi);
        RequestBody body= RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), GsonUtils.jsonString(map));

        Retrofit retrofit = new Retrofit.Builder().baseUrl(Api.UPLOAD_HOST).build();

        Api api = retrofit.create(Api.class);
        Call<ResponseBody> call = api.contentListener(body);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                LogUtils.log("imsi上传成功："+response.code());
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtils.log("imsi上传失败："+t.getMessage());
            }
        });

    }
}
