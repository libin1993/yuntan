package com.doit.net.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.doit.net.bean.DBBlackInfo;
import com.doit.net.bean.DBUeidInfo;
import com.doit.net.bean.PushBean;
import com.doit.net.bean.UeidBean;
import com.doit.net.event.EventAdapter;
import com.doit.net.utils.GsonUtils;
import com.doit.net.utils.LogUtils;
import com.doit.net.utils.NetWorkUtils;
import com.doit.net.utils.UCSIDBManager;
import com.mpush.api.Constants;

import org.xutils.DbManager;
import org.xutils.common.util.KeyValue;
import org.xutils.db.sqlite.WhereBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**
 * 类名称: mpush消息接收服务
 * 创建时间: 2019/6/21 14:06
 * 创建人: mapeng
 * 在线消息接收类，处理在线消息
 */
public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //处理接收到的消息
        LogUtils.log("mpush--连接" + intent.getAction());
        if (MpushService.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            byte[] bytes = intent.getByteArrayExtra(MpushService.EXTRA_PUSH_MESSAGE);
            int messageId = intent.getIntExtra(MpushService.EXTRA_PUSH_MESSAGE_ID, 0);
            String message = new String(bytes, Constants.UTF_8);

            if (messageId > 0) {
                Mpush.showStart.ack(messageId);
            }

            if (!TextUtils.isEmpty(message)) {
                LogUtils.log("翻译结果上报："+ message);
                PushBean pushBean = GsonUtils.jsonToBean(message, PushBean.class);
                if (!TextUtils.isEmpty(pushBean.getValue()) && !TextUtils.isEmpty(pushBean.getPhone())){
                    DbManager dbManager = UCSIDBManager.getDbManager();
                    try {
                        //修改手机号
                        KeyValue keyValue1 = new KeyValue("msisdn", pushBean.getPhone());
                        dbManager.update(DBUeidInfo.class, WhereBuilder.b("imsi", "=", pushBean.getValue()), keyValue1);
                        dbManager.update(DBBlackInfo.class, WhereBuilder.b("imsi", "=", pushBean.getValue()), keyValue1);
                        EventAdapter.call(EventAdapter.REFRESH_BLACKLIST);
                    }catch (Exception e){
                        LogUtils.log("解析异常："+e.getMessage());
                    }


                    List<UeidBean> ueidList = new ArrayList<>();
                    UeidBean ueidBean = new UeidBean();
                    ueidBean.setImsi(pushBean.getValue());
                    ueidBean.setSrsp("0");
                    ueidBean.setMsisdn(pushBean.getPhone());

                    ueidList.add(ueidBean);
                    if (ueidList.size() > 0) {
                        EventAdapter.call(EventAdapter.UEID_RPT, ueidList);
                    }
                }

            }

        } else if (MpushService.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            //通知被点击了
            Notifications.I.clean(intent);


        } else if (MpushService.ACTION_KICK_USER.equals(intent.getAction())) {
            //用户被踢下线了
            LogUtils.log("用户被踢下线了");
        } else if (MpushService.ACTION_BIND_USER.equals(intent.getAction())) {
            //绑定用户
            LogUtils.log("绑定用户");
            intent.getBooleanExtra(MpushService.EXTRA_BIND_RET, false);
        } else if (MpushService.ACTION_UNBIND_USER.equals(intent.getAction())) {
            //解绑用户
            LogUtils.log("解绑用户");
            intent.getBooleanExtra(MpushService.EXTRA_BIND_RET, false);
        } else if (MpushService.ACTION_CONNECTIVITY_CHANGE.equals(intent.getAction())) {
            LogUtils.log(intent.getBooleanExtra(MpushService.EXTRA_CONNECT_STATE, false) ? "MPUSH连接建立成功" : "MPUSH连接断开");
        } else if (MpushService.ACTION_HANDSHAKE_OK.equals(intent.getAction())) {
            LogUtils.log("MPUSH握手成功, 心跳:" + intent.getIntExtra(MpushService.EXTRA_HEARTBEAT, 0));
        }
    }

}
