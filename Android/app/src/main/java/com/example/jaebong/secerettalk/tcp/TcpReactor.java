package com.example.jaebong.secerettalk.tcp;

import android.content.Context;
import android.util.Log;

import com.example.jaebong.secerettalk.event_handler.EventHandler;

import java.net.Socket;

/**
 * Created by JaeBong on 15. 5. 31..
 */
public class TcpReactor {
    private Socket socket;
    private HandleMap handleMap;
    private Context context;


    public TcpReactor(Socket socket, Context context) {
        this.handleMap = new HandleMap();
        this.socket = socket;
        this.context = context;

    }

    public void startConnection(){
        TcpDispatcher dispatcher = new ThreadPerDispatcher(context);
        dispatcher.dispatch(socket,handleMap);
    }

    public void registerHandler(String header, EventHandler handler) {
        handleMap.put(header, handler);
        Log.e("Put " + header +" In Handler", handler.getHandler());
    }

    public void registerHandler(EventHandler handler) {
        handleMap.put(handler.getHandler(), handler);
    }

    public void removeHandler(EventHandler handler) {
        handleMap.remove(handler.getHandler());
    }

}
