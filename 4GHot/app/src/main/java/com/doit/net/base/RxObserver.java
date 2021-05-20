package com.doit.net.base;


import android.text.TextUtils;

import com.doit.net.push.RequestUtils;
import com.doit.net.utils.LogUtils;
import com.doit.net.utils.SPUtils;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Author：Libin on 2019/05/31 09:34
 * Email：1993911441@qq.com
 * Describe：回调封装
 */
public class RxObserver<T extends BaseBean> implements Observer<T> {

    private Callback<T> mCallback;

    public RxObserver(Callback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {

    }

    @Override
    public void onNext(T t) {
        if (t.getCode() == 0) {
            mCallback.onSuccess(t);
        }else if (t.getCode() == 401 || t.getCode() ==403){  //token失效
            String username = SPUtils.getString(SPUtils.USERNAME,"");
            String password = SPUtils.getString(SPUtils.PASSWORD,"");
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)){
                RequestUtils.login(username,password);
            }
        }else {
            mCallback.onFail(t.getMsg());
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        LogUtils.log(e.toString());
        mCallback.onFail("网络异常，请检查网络是否正常连接");
    }

    @Override
    public void onComplete() {

    }

}
