package com.example.jaebong.secerettalk.tcp;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.jaebong.secerettalk.SecretTalkCotract;
import com.example.jaebong.secerettalk.user_profile.UserProfileDao;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;

/**
 * Created by JaeBong on 15. 6. 1..
 */
public class SendSessionLoginRequest {
    private Socket socket;
    private final Context context;
    private UserProfileDao dao;
    private ReadAndHandleData reader;

    public SendSessionLoginRequest(final Context context, final Socket socket){
        this.context = context;
        this.dao = new UserProfileDao(context);
        this.socket = socket;

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OutputStream os = socket.getOutputStream();
                    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

                    String request = "1.0.0|" + tm.getDeviceId() + "|" + dao.getAccessToken() + "|" + Build.VERSION.RELEASE + "|" + SecretTalkCotract.APP_VERSION + "|" + Build.MODEL;
                    Log.e("SendSessionLoginRequest", "request : " + request);

                    byte[] header = {0x10, 0x01};
                    byte[] length = intToByteArray(request.length());
                    byte[] requestByte = request.getBytes(Charset.forName("UTF-8"));

                    byte[] sendMessage = new byte[header.length + length.length + requestByte.length];
                    System.arraycopy(header, 0, sendMessage, 0, header.length);
                    System.arraycopy(length, 0, sendMessage, header.length, length.length);
                    System.arraycopy(requestByte, 0, sendMessage, header.length + length.length, requestByte.length);

                    Log.e("SendSessionLoginRequest", "requestByte success" + sendMessage);
                    os.write(sendMessage);
                    Log.e("Send Success","s");
                }catch(Exception e) {
                    Log.e("OuptSteram Error", "Error : " + e);
                }

                try{
                    InputStream is = socket.getInputStream();

                    byte[] response = new byte[10];

                    if(is != null) {
                        is.read(response);
                    }else{
                        Log.e("InputStream Error","No inputStream");
                    }

                    if(response != null) {
                        reader.readData(response, 0);
                    }else{
                        Log.e("response error", "response == null");
                    }

                }catch (Exception e)
                {
                    Log.e("SendSessionLoginRequest","Error : " + e);
                }
            }
        });

        thread.start();


    }

    public static final byte[] intToByteArray(int value) {
        return new byte[] {
                (byte)(value >>> 24),
                (byte)(value >>> 16),
                (byte)(value >>> 8),
                (byte)value};
    }
}
