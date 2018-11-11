package com.katae.pad.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;

public class BadgeService extends Service {

    boolean mIsSupportedBade = true;
    int count = 0;

    public BadgeService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                count++;
                if(mIsSupportedBade) {
                    setBadgeNum(count);
                }
            }

        }).start();

        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int anHour = 10000;//60 * 60 * 1000; // 这是一小时的毫秒数

        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;

        Intent i = new Intent(this, BadgeServiceReceiver.class);

        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);

        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

        return super.onStartCommand(intent, flags, startId);
        //return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /** set badge number*/
    public void setBadgeNum(int num){
        try{
            Bundle bunlde =new Bundle();
            bunlde.putString("package", "com.katae.pad");
            bunlde.putString("class", "com.katae.pad.activity.LoginActivity");
            bunlde.putInt("badgenumber", num);
            this.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/ba dge/"), "change_badge", null, bunlde);
        } catch(Exception e) {
            mIsSupportedBade = false;
        }
    }
}
