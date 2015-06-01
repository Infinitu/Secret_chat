package com.example.jaebong.secerettalk.tcp;

import java.net.Socket;

/**
 * Created by JaeBong on 15. 5. 31..
 */
public interface TcpDispatcher {
    public void dispatch(Socket socket, HandleMap handlers);
}
