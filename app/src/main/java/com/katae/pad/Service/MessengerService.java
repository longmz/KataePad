package com.katae.pad.Service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

public class MessengerService extends Service {

    private Messenger activityMessenger;

    private MessengerHandler messengerHandler;

    private int count = 0;
    private static volatile boolean isRunning;

    public MessengerService() {
        messengerHandler = new MessengerHandler();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //只在service创建的时候调用一次，可以在此进行一些一次性的初始化操作
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //当其他组件调用startService()方法时，此方法将会被调用
        //在这里进行这个service主要的操作
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Messenger(messengerHandler).getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        isRunning = false;
        return super.onUnbind(intent);
    }

    private class MessengerHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.replyTo != null) {
                activityMessenger = msg.replyTo;
                notifyActivity();
            }
            super.handleMessage(msg);
        }
    }

    private void notifyActivity(){
        isRunning = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(isRunning) {
                    try {
                        Thread.sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    count++;
                    Message message = Message.obtain();
                    message.arg1 = count;
                    try {
                        activityMessenger.send(message);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
}
