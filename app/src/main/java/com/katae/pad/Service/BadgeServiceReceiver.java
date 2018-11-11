package com.katae.pad.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BadgeServiceReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        //if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            /* 服务开机自启动 */
            //Intent service = new Intent(context, BadgeService.class);
            //service.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //注意，必须添加这个标记，否则启动会失败
            //context.startService(service);
        //}
        Intent i = new Intent(context, BadgeService.class);
        context.startService(i);
    }
}
