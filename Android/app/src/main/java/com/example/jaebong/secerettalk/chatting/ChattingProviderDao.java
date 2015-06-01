package com.example.jaebong.secerettalk.chatting;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by JaeBong on 15. 4. 24..
 */
public class ChattingProviderDao {
    private Context context;

    public ChattingProviderDao(Context context){
        this.context = context;
    }

    public ArrayList<MessageDTO> getMessageList(){
        ArrayList<MessageDTO> messageList = new ArrayList<MessageDTO>();

        int _id;
        String type;
        String imageUrl;
        String address;
        String sender;
        String message;
        long sendTime;
        String nickName;

        Cursor cursor = context.getContentResolver().query(
                ChattingContract.Messages.CONTENT_URI,
                ChattingContract.Messages.PROJECTION_ALL, null, null,
                ChattingContract.Messages._ID + "ASC");

        if(cursor != null){
            cursor.moveToFirst();
            while(!(cursor.isAfterLast())){
                _id = cursor.getInt(0);
                type = cursor.getString(1);
                imageUrl = cursor.getString(2);
                address = cursor.getString(3);
                sender = cursor.getString(4);
                message = cursor.getString(5);
                sendTime = cursor.getLong(6);
                nickName = cursor.getString(7);


                messageList.add(new MessageDTO(_id,type,imageUrl,address,sender,message,sendTime,nickName));

                cursor.moveToNext();
            }
            cursor.close();
        }

        return messageList;

    }

    public void insertMyChattingMessage(MessageDTO msg){
        ContentValues values = new ContentValues();
        //values.put("_id",msg.get_id());
        values.put("type",msg.getType());
        values.put("imageUrl",msg.getImageUrl());
        values.put("address",msg.getAddress());
        values.put("sender",msg.getSender());
        values.put("message",msg.getMessage());
        values.put("sendTime",msg.getSendTime());
        values.put("nickName",msg.getNickName());

        Log.i("ChattingProviderDao","insert Success");
        context.getContentResolver().insert(ChattingContract.Messages.CONTENT_URI,values);

    }

}