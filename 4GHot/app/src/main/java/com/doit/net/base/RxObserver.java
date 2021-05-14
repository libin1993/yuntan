package com.doit.net.base;


import android.content.Intent;


import com.doit.net.utils.LogUtils;

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
        }else {
            mCallback.onFail(t.getMsg());
        }
    }

    @Override
    public void onError(@NonNull Throwable e) {
        e.printStackTrace();
        LogUtils.log(e.toString());
        mCallback.onFail("网络异常,请联系客服");
    }

    @Override
    public void onComplete() {

    }

}
