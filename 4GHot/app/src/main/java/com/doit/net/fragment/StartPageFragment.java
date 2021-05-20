package com.doit.net.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.doit.net.base.BaseFragment;
import com.doit.net.event.EventAdapter;
import com.doit.net.protocol.LTESendManager;
import com.doit.net.utils.CacheManager;
import com.doit.net.view.MySweetAlertDialog;
import com.doit.net.R;

/**
 * Created by Zxc on 2019/12/17.
 */

public class StartPageFragment extends BaseFragment {


    private ImageButton ivPowerStart;
    public StartPageFragment(){
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.start_page_fragment, null);
        ivPowerStart = rootView.findViewById(R.id.ivPowerStart);
        initWidget();

        return rootView;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initWidget() {

        ivPowerStart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    ivPowerStart.setImageResource(R.drawable.start_button_press);
                }else if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                    //改为抬起时的图片
                    ivPowerStart.setImageResource(R.drawable.start_button);

                    if (CacheManager.isDeviceOk()){
                        CacheManager.setPressStartButtonFlag(true);
                        turnToDetectPage();
                        LTESendManager.openAllRf();
                    }else{
                        new MySweetAlertDialog(getContext(), MySweetAlertDialog.WARNING_TYPE)
                                .setTitleText("提示")
                                .setContentText("设备未连接或未初始化完成，是否进入工作页面？")
                                .setCancelText(getContext().getString(R.string.cancel))
                                .setConfirmText(getContext().getString(R.string.sure))
                                .showCancelButton(true)
                                .setConfirmClickListener(new MySweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(MySweetAlertDialog sweetAlertDialog) {
                                        CacheManager.setPressStartButtonFlag(true);
                                        turnToDetectPage();
                                        sweetAlertDialog.dismiss();
                                    }

                                })
                                .show();
                    }


                }
                return false;
            }
        });
    }

    private void turnToDetectPage() {
        EventAdapter.call(EventAdapter.POWER_START);
    }
}
