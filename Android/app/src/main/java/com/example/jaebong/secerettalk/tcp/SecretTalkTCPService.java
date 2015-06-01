package com.example.jaebong.secerettalk.tcp;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.jaebong.secerettalk.SecretTalkCotract;

import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by JaeBong on 15. 4. 22..
 */
public class SecretTalkTCPService extends Service {
    private static final String TAG = SecretTalkTCPService.class.getSimpleName();

    private TimerTask mTask;
    private Timer mTimer;
    public Socket socket = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG,"onDestroy");
        try{
            socket.close();
        }
        catch(Exception e){
            Log.e("Error","error");
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG,"onStartCommand");

        mTask = new TimerTask() {
            @Override
            public void run() {
                if(socket == null){
                    try {
                        socket = new Socket(SecretTalkCotract.TCP_URL, SecretTalkCotract.TCP_PORT);
                        SendSessionLoginRequest sq = new SendSessionLoginRequest(getApplicationContext(), socket);
                    }catch(Exception e){
                        Log.e("sokectError","error"+ e);
                    }
                }
                TcpClientInitializer initializer = new TcpClientInitializer(getApplicationContext(),socket);
                initializer.startInitialize();
            }

        };
        mTimer = new Timer();
        mTimer.schedule(mTask,1000*30,1000*30);



        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
