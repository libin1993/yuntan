package com.doit.net.utils;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.widget.LinearLayoutCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.doit.net.R;


/**
 * Author：Li Bin on 2019/7/15 11:47
 * Description：
 */
public class PopupWindowUtils {
    private static PopupWindowUtils mInstance;
    private PopupWindow mPopupWindow;

    private PopupWindowUtils() {
    }

    public static PopupWindowUtils getInstance() {
        if (mInstance == null) {
            synchronized (PopupWindowUtils.class) {
                if (mInstance == null) {
                    mInstance = new PopupWindowUtils();
                }
            }
        }

        return mInstance;

    }


    /**
     * 显示弹出信息框
     *
     * @param activity
     * @param message
     */
    public void showPopWindow(Activity activity, String message, String confirmMsg, final PopupOnClickListener onClickListener) {
        if (activity.isFinishing()) {
            return;
        }

        mPopupWindow = new PopupWindow();
        View mPopBackView = LayoutInflater.from(activity).inflate(R.layout.layout_popup, null);

        //设置Popup具体控件
        TextView tvCancel = mPopBackView.findViewById(R.id.tv_left_msg);
        TextView tvConfirm = mPopBackView.findViewById(R.id.tv_right_msg);
        TextView tvMsg = mPopBackView.findViewById(R.id.tv_popup_msg);

        tvMsg.setText(message);
        if (!TextUtils.isEmpty(confirmMsg)) {
            tvConfirm.setText(confirmMsg);
        }


        //设置Popup具体参数
        mPopupWindow.setContentView(mPopBackView);
        mPopupWindow.setClippingEnabled(false);
        mPopupWindow.setWidth(LinearLayoutCompat.LayoutParams.MATCH_PARENT);//设置宽
        mPopupWindow.setHeight(LinearLayoutCompat.LayoutParams.MATCH_PARENT);//设置高
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), (Bitmap) null));
        mPopupWindow.setFocusable(true);//点击空白，popup不自动消失
        mPopupWindow.setTouchable(true);//popup区域可触摸
        mPopupWindow.setOutsideTouchable(false);//非popup区域可触摸

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onCancel();
            }
        });

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onConfirm();
            }
        });

        if (mPopupWindow.isShowing()) {
            ///如果正在显示等待框，，，则结束掉等待框然后重新显示
            mPopupWindow.dismiss();
        }
        mPopupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }


    /**
     * 隐藏弹出信息框
     */
    public void dismiss() {
        if (mPopupWindow == null) {
            return;
        }

        if (mPopupWindow.isShowing()) {
            mPopupWindow.dismiss();
        }
    }


    public interface PopupOnClickListener {
        void onCancel();

        void onConfirm();
    }
}
