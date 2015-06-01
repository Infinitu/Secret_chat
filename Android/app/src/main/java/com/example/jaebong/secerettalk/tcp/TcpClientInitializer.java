package com.example.jaebong.secerettalk.tcp;

import android.content.Context;
import android.util.Log;

import com.example.jaebong.secerettalk.event_handler.EventHandler;
import com.example.jaebong.secerettalk.xmlLoader.ClientListData;
import com.example.jaebong.secerettalk.xmlLoader.Handle;
import com.example.jaebong.secerettalk.xmlLoader.HandlerData;
import com.example.jaebong.secerettalk.xmlLoader.HandlerListData;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TcpClientInitializer {

    public static Context context;
    private Socket socket;
    public TcpClientInitializer(Context context, Socket socket) {
        this.context = context;
        this.socket = socket;

    }
    public void startInitialize(){
        String clientName = "reactor";

        TcpReactor reactor = new TcpReactor(socket, context);

        ArrayList<Handle> handlers = getHandlerList(clientName);

        for(Handle handler : handlers) {
            try {
                reactor.registerHandler(handler.getHeader(), (EventHandler)Class.forName( handler.getClassName() ).newInstance());
            } catch (InstantiationException | IllegalAccessException
                    | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        reactor.startConnection();

    }

    private static ArrayList<Handle> getHandlerList(String clientName) {
        ArrayList<Handle> handlers = new ArrayList<Handle>();

        try {

            Serializer serializer = new Persister();
            InputStream source = context.getAssets().open("handler_list.xml");


            ClientListData clientList = serializer.read(ClientListData.class, source);

            for (HandlerListData handlerListData : clientList.getClient()) {

                if (clientName.equals(handlerListData.getName())) {
                    List<HandlerData> handlerList = handlerListData.getHandler();
                    for (HandlerData handler : handlerList) {
                        handlers.add(new Handle(handler.getHeader(),handler.getHandler()));
                        Log.w("Insert Handler Success",handler.getHandler());
                    }
                }
            }

        } catch (Exception e) {
            Log.e("TcpClientInitializer","error : "+e);
        }


        return handlers;

    }
}