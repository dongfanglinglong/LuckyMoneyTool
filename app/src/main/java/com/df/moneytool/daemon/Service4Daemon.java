package com.df.moneytool.daemon;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class Service4Daemon extends Service {

    public static final String PROCESS = "com.df.moneytool:daemon";

    public Service4Daemon() {}

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_NOT_STICKY;
    }
}
