package com.example.jaebong.secerettalk.event_handler;

import java.io.InputStream;

/**
 * Created by JaeBong on 15. 5. 27..
 */
public interface EventHandler {

    public String getHandler();

    public void handleEvent(String message);
}
