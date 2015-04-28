package com.example.jaebong.secerettalk;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;


public class Chatting extends ActionBarActivity implements View.OnClickListener {

    private EditText editMessageBox;
    private TextView sendButton;
    private Message message;
    private ProviderDao dao;
    private ListView messageListView;
    private long time;
    private  ArrayList<Message> MessageList;
    private  MessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        dao = new ProviderDao(getApplicationContext());
        message = new Message();

        time= System.currentTimeMillis();

        Date date = new Date(time);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strNow = sdfNow.format(date);


        editMessageBox = (EditText)findViewById(R.id.chatting_editText_eidtMessage);
        sendButton = (TextView)findViewById(R.id.chatting_tv_sendButton);
        messageListView =(ListView)findViewById(R.id.chatting_listView_messageList);

        sendButton.setOnClickListener(this);
        chattingListView(dao.getMessageList());


        Log.i("Chatting","cal :" + strNow);


    }


    private void chattingListView(ArrayList<Message> messages){

        Cursor mCursor = getContentResolver().query(
                SecretTalkContract.Messages.CONTENT_URI,
                SecretTalkContract.Messages.PROJECTION_ALL,null,null,
                SecretTalkContract.Messages.SEND_TIME

        );

        adapter = new MessageAdapter(this,mCursor,R.layout.activity_message_box);
        messageListView.setAdapter(adapter);


    }




    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.chatting_tv_sendButton :
                String msg = editMessageBox.getText().toString();
                if(!msg.equals("")) {
                    message.setMessage(msg);
                }
                else {
                    break;
                }
                message.set_id(message.get_id()+1);
                message.setType("Text");
                message.setAddress("1234");
                message.setSender("Me");
                message.setNickName("윤영기");
                message.setSendTime(time);
                dao.insertMyChattingMessage(message);

                message.setSender("Others");
                message.set_id(message.get_id()+1);
                dao.insertMyChattingMessage(message);

                editMessageBox.setText("");

        }
    }
}
