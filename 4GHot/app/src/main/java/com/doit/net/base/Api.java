package com.doit.net.base;




import com.doit.net.bean.UserBean;

import java.util.ArrayList;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;


/**
 * Author：Libin on 2021/05/14 13:32
 * Email：1993911441@qq.com
 * Describe：接口
 */
public interface Api {
    String HOST = "http://183.214.181.133:3005/";         //云探
    String PUSH_HOST = "http://183.214.181.133:3003";         //推送
    String UPLOAD_HOST = "http://183.214.181.133:9093/";         //上传IMSI

    //登录
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api-admin/login")
    Observable<UserBean> login(@Body RequestBody body);

    //绑定设备
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api-biz/userDeviceBind/bind")
    Observable<BaseBean> bind(@Body RequestBody body);

    //解绑设备
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("api-biz/userDeviceBind/unbind")
    Observable<BaseBean> unbind();

    //imsi上传
    @Headers({"Content-Type: application/json","Accept: application/json"})
    @POST("contentListener")
    Call<ResponseBody> contentListener(@Body RequestBody body);


}

