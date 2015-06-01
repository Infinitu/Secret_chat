package com.example.jaebong.secerettalk.tcp;


import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.jaebong.secerettalk.SecretTalkCotract;
import com.example.jaebong.secerettalk.user_profile.UserProfileDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

public class PingPongRequest implements Runnable{

   public static final int BUFFER_SIZE = 128;

    private Socket socket;
    private Context context;
    private ReadAndHandleData reader;
    private HandleMap handleMap;



    public PingPongRequest(){}

    public PingPongRequest(Socket socket, HandleMap handleMap, Context context){
        this.socket = socket;
        this.context = context;
        this.handleMap = handleMap;

    }

    private void sendPing(){
        try {
            OutputStream out = socket.getOutputStream();
            byte[] ping = {0x00, 0x01, 0x00, 0x00, 0x00, 0x00};
            out.write(ping);
        }catch(Exception e){
            Log.e("TcpDemultiplexer","SendPing Error :" + e);
        }
    }

    public void run(){
        try {
            sendPing();

            reader = new ReadAndHandleData(handleMap,context);

            byte[] buffer = new byte[BUFFER_SIZE];
            InputStream in = socket.getInputStream();
            in.read(buffer);

            reader.readData(buffer, 0);

        } catch (IOException e) {
            Log.e("TCPDemultiplexr",""+e);
        } catch (Exception e) {
            Log.e("TCPDemultiplexer",""+e);
        }
    }



}
