/*
 * (C) Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     ohun@live.cn (夜色)
 */


package com.doit.net.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.SystemClock;

import com.mpush.api.Constants;


/**
 * Created by yxx on 2016/2/14.
 *
 * @author ohun@live.cn
 */
public final class MpushReceiver extends BroadcastReceiver {
    public static final String ACTION_HEALTH_CHECK = "com.mpush.HEALTH_CHECK";
    public static final String ACTION_NOTIFY_CANCEL = "com.mpush.NOTIFY_CANCEL";
    public static int delay = Constants.DEF_HEARTBEAT;
    public static State STATE = State.UNKNOWN;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        //处理心跳
        if (ACTION_HEALTH_CHECK.equals(action)) {
            if (Mpush.showStart.hasStarted()) {
                if (Mpush.showStart.client.isRunning()) {
                    if (Mpush.showStart.client.healthCheck()) {
                        startAlarm(context, delay);
                    }
                }
            }
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            //处理网络变化
            if (hasNetwork(context)) {
                if (STATE != State.CONNECTED) {
                    STATE = State.CONNECTED;
                    if (Mpush.showStart.hasStarted()) {
                        Mpush.showStart.onNetStateChange(true);
                    } else {
                        Mpush.showStart.checkInit(context).startPush();
                    }
                }
            } else {
                if (STATE != State.DISCONNECTED) {
                    STATE = State.DISCONNECTED;
                    Mpush.showStart.onNetStateChange(false);
                }
            }
        } else if (ACTION_NOTIFY_CANCEL.equals(action)) {
            //处理通知取消
        }
    }

    static void startAlarm(Context context, int delay) {
        Intent it = new Intent(MpushReceiver.ACTION_HEALTH_CHECK);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, it, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + delay, pi);
        MpushReceiver.delay = delay;
    }

    static void cancelAlarm(Context context) {
        Intent it = new Intent(MpushReceiver.ACTION_HEALTH_CHECK);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, it, 0);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        assert am != null;
        am.cancel(pi);
    }

    public static boolean hasNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo info = cm.getActiveNetworkInfo();
        return (info != null && info.isConnected());
    }
}
