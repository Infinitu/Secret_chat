package com.example.jaebong.secerettalk.tcp;

import android.content.Context;
import android.util.Log;

import java.net.Socket;

/**
* @brief ThreadPerDispatcher
* @details 한 연결(Socket)에 스레드를 하나씩 할당하여 Demultiplexing하도록 전달한다.
* @author flashscope
* @date 2014-05-11
* @version 0.0.1
*/
public class ThreadPerDispatcher implements TcpDispatcher{

    private Context context;

    public ThreadPerDispatcher(Context context){ this.context = context; }


	public void dispatch(Socket socket, HandleMap handleMap) {
        try {
            Runnable pingPong = new PingPongRequest(socket, handleMap,context);

            Thread thread = new Thread(pingPong);
            thread.start();

        } catch (Exception e) {
            Log.e("ThreadPerDispatcher", "error : "+e);
        }

	}
}
