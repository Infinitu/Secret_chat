package com.example.jaebong.secerettalk.event_handler;

import android.util.Log;

/**
 * Created by JaeBong on 15. 6. 1..
 */
public class SessionOkHandler implements EventHandler {
    @Override
    public String getHandler() {
        return "0x1002";
    }

    @Override
    public void handleEvent(String message) {
        Log.e("Session Success","Success");
    }
}
