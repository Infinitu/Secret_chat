package com.example.jaebong.secerettalk.event_handler;

import android.util.Log;

/**
 * Created by JaeBong on 15. 6. 1..
 */
public class PongHandler implements EventHandler {
    @Override
    public String getHandler() {
        String result = "0x0002";
        return result;
    }

    @Override
    public void handleEvent(String message) {
        Log.i("server alive","serverAlive");
    }
}
