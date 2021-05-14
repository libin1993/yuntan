package com.doit.net.base;

/**
 * Author：Libin on 2019/6/12 15:05
 * Description：
 */
public interface Callback<T> {
    void onSuccess(T t);

    void onFail(String msg);
}
