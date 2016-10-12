package com.example.back.clientradar;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by back on 2016-10-12.
 */

public class LocationService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
