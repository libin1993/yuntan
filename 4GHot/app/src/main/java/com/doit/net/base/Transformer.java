package com.doit.net.base;


import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Author：Libin on 2019/6/3 10:03
 * Email：1993911441@qq.com
 * Describe：切换线程
 */
public class Transformer {
    public static ObservableTransformer switchSchedulers() {
        return new ObservableTransformer() {

            @Override
            public ObservableSource apply(Observable upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }

        };
    }
}
