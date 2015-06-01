package com.example.jaebong.secerettalk.tcp;

import android.content.Context;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by JaeBong on 15. 6. 1..
 */
public class ReadAndHandleData {

    private HandleMap handleMap;
    private Context context;

    private final int HEADER_SIZE = 2;
    private final int LENGTH_SIZE = 4;

    interface STATUS{
        int NOT_RECIEVED = 0;
        int RECIEVED = 1;

    }

    int statusOfHeader = STATUS.NOT_RECIEVED;     int nowLenOfHeader = 0;
    int statusOfLength = STATUS.NOT_RECIEVED;     int nowLenOfLength = 0;
    int statusOfMessage = STATUS.NOT_RECIEVED;    int nowLenOfMessage = 0;

    byte[] headerBuffer = new byte[HEADER_SIZE];
    byte[] lengthBuffer = new byte[LENGTH_SIZE];
    byte[] realMessage;
    int messageLength;

    public ReadAndHandleData(HandleMap handleMap,Context context){
        this.handleMap = handleMap;
        this.context = context;
    }

    public void readData(byte[] msg, int indexOfMessage) throws Exception {


        //헤더가 다 밭아졌는지 판별
        if (statusOfHeader == STATUS.NOT_RECIEVED) {
            try {
                //크다면 header를 읽어와 저장
                for (; nowLenOfHeader < HEADER_SIZE; nowLenOfHeader++) {
                    headerBuffer[nowLenOfHeader] = msg[indexOfMessage++];
                }


                //헤더가 다 밭아졌다고 상태 전환
                statusOfHeader = STATUS.RECIEVED;
                //nowLenOfHeader를 0으로 설정
                nowLenOfHeader = 0;
            } catch (Exception e) {
                Log.w("TCPDemultiplexer", "헤더가 다 받아지지 않음 " + e);
            }
        }

        //Length가 다 받아졌는지 판별
        if (statusOfHeader == STATUS.RECIEVED && statusOfLength == STATUS.NOT_RECIEVED) {
            try {
                for (; nowLenOfLength < LENGTH_SIZE; nowLenOfLength++) {
                    lengthBuffer[nowLenOfLength] = msg[indexOfMessage++];
                }
                statusOfLength = STATUS.RECIEVED;
                nowLenOfLength = 0;

                //messageLength를 설정해줌
                messageLength = byteArrayToInt(lengthBuffer);
                //messageLength만큼 realMessage를 위한 buffer 설정
                realMessage = new byte[messageLength];
            } catch (Exception e) {
                Log.w("TCPDemultiplexer", "길이가 다 받아지지 않음");
            }
        }

        if (statusOfHeader == STATUS.RECIEVED && statusOfLength == STATUS.RECIEVED && statusOfMessage == STATUS.NOT_RECIEVED) {
            try {
                for (; nowLenOfMessage < messageLength; nowLenOfMessage++) {
                    lengthBuffer[nowLenOfMessage] = msg[indexOfMessage++];
                }

                statusOfMessage = STATUS.RECIEVED;

                nowLenOfMessage = 0;

                statusOfLength = STATUS.NOT_RECIEVED;
                statusOfHeader = STATUS.NOT_RECIEVED;
                statusOfMessage = STATUS.NOT_RECIEVED;

                String message = new String(realMessage,"UTF-8");
                int headerInt = byteArrayToInt(headerBuffer);

                String headerString = hex(headerInt);
                Log.e("ReadAndHandleData",headerString);

                handleMap.get(headerString).handleEvent(message);

                if(indexOfMessage < HEADER_SIZE + LENGTH_SIZE + messageLength){
                    Log.w("read Data 재귀호출","");
                    readData(msg, indexOfMessage);
                }

            } catch (Exception e) {
                Log.w("TCPDemultiplexer", "메세지가 다 받아지지 않음 " + e);
            }
        }

    }

    private static int byteArrayToInt(byte[] bytes) {
        final int size = Integer.SIZE / 8;
        ByteBuffer buff = ByteBuffer.allocate(size);
        final byte[] newBytes = new byte[size];
        for (int i = 0; i < size; i++) {
            if (i + bytes.length < size) {
                newBytes[i] = (byte) 0x00;
            } else {
                newBytes[i] = bytes[i + bytes.length - size];
            }
        }
        buff = ByteBuffer.wrap(newBytes);
        buff.order(ByteOrder.BIG_ENDIAN); // Endian에 맞게 세팅
        return buff.getInt();
    }

    public static String hex(int n)
    {
        return String.format("0x%04X", n);
    }
}
