package com.example.jaebong.secerettalk;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
    private Dao dao;
    private long time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);
        dao = new Dao(getApplicationContext());
        message = new Message();

        time= System.currentTimeMillis();

        Date date = new Date(time);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strNow = sdfNow.format(date);


        editMessageBox = (EditText)findViewById(R.id.chatting_editText_eidtMessage);
        sendButton = (TextView)findViewById(R.id.chatting_tv_sendButton);

        sendButton.setOnClickListener(this);
        Log.i("Chatting","cal :" + strNow);


    }

//    private void chattingListiew(ArrayList<Message> messages){
//
//        Cursor mCursor = getContentResolver().query(
//
//        );
//
//
//    }


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
                message.setType("Text");
                message.setAdress("1234");
                message.setSender("Me");
                message.setSendTime(time);

                dao.insertChattingMessage(message);
                message.setSender("You");
                dao.insertChattingMessage(message);

                editMessageBox.setText("");

        }
    }
}
