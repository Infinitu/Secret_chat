package com.example.jaebong.secerettalk;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JaeBong on 15. 4. 22..
 */
public class SyncDataService extends Service {
    private static final String TAG = SyncDataService.class.getSimpleName();

    private TimerTask mTask;
    private Timer mTimer;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");

        mTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG,"TimerTask Run");
            }
        };

        mTimer = new Timer();
        mTimer.schedule(mTask, 1000*5,1000*5);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
