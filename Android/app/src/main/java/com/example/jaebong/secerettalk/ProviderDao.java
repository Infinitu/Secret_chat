package com.example.jaebong.secerettalk;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;

/**
 * Created by JaeBong on 15. 4. 24..
 */
public class ProviderDao {
    private Context context;

    public ProviderDao(Context context){
        this.context = context;
    }

    public ArrayList<Message> getMessageList(){
        ArrayList<Message> messageList = new ArrayList<Message>();

        int _id;
        String type;
        String imageUrl;
        String address;
        String sender;
        String message;
        long sendTime;
        String nickName;

        Cursor cursor = context.getContentResolver().query(
                SecretTalkContract.Messages.CONTENT_URI,
                SecretTalkContract.Messages.PROJECTION_ALL, null, null,
                SecretTalkContract.Messages._ID);
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


                messageList.add(new Message(_id,type,imageUrl,address,sender,message,sendTime,nickName));

                cursor.moveToNext();
            }

        }
        cursor.close();

        return messageList;

    }

    public void insertMyChattingMessage(Message msg){
        ContentValues values = new ContentValues();
        values.put("_id",msg.get_id());
        values.put("type",msg.getType());
        values.put("imageUrl",msg.getImageUrl());
        values.put("address",msg.getAddress());
        values.put("sender",msg.getSender());
        values.put("message",msg.getMessage());
        values.put("sendTime",msg.getSendTime());
        values.put("nickName",msg.getNickName());

        context.getContentResolver().insert(SecretTalkContract.Messages.CONTENT_URI,values);

    }

}