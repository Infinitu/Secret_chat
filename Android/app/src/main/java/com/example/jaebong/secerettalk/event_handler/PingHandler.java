package com.example.jaebong.secerettalk.event_handler;

import android.util.Log;

/**
 * Created by JaeBong on 15. 6. 1..
 */
public class PingHandler implements EventHandler {
    @Override
    public String getHandler() {
        String header = "0x0001";

        return header;
    }

    @Override
    public void handleEvent(String message) {
       Log.i("Pong!",message);
    }
}
