package com.example.jaebong.secerettalk.chatting;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jaebong.secerettalk.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChattingView extends ActionBarActivity implements View.OnClickListener {

    private ImageView backButton;

    private EditText editMessageBox;
    private TextView sendButton;
    private MessageDTO message;
    private ChattingProviderDao dao;
    private ListView messageListView;
    private long time;
    private MessageAdapter adapter;
    private ContentObserver contentObserver;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        context = getApplicationContext();

        dao = new ChattingProviderDao(getApplicationContext());
        message = new MessageDTO();


        time = System.currentTimeMillis();

        Date date = new Date(time);

        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        String strNow = sdfNow.format(date);

        backButton = (ImageView) findViewById(R.id.chatting_imageButton_back);
        editMessageBox = (EditText) findViewById(R.id.chatting_editText_eidtMessage);
        sendButton = (TextView) findViewById(R.id.chatting_tv_sendButton);
        messageListView = (ListView) findViewById(R.id.chatting_listView_messageList);

        sendButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
        chattingListView();

        contentObserver = new ContentObserver(new Handler()) {
            @Override

            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                chattingListView();
                Log.i("ChattingView","contentsObserver onchange start");
            }
        };


        Log.i("Chatting", "cal :" + strNow);


    }


    private void chattingListView() {

        Cursor mCursor = getContentResolver().query(
                ChattingContract.Messages.CONTENT_URI,
                ChattingContract.Messages.PROJECTION_ALL, null, null,
                ChattingContract.Messages.SEND_TIME

        );

        adapter = new MessageAdapter(this, mCursor, R.layout.view_message_box);
        messageListView.setAdapter(adapter);


    }

    public void sendMessage() {
        String msg = editMessageBox.getText().toString();

        if (msg.equals("")) {
            return;
        }

        message.setMessage(msg);
        message.set_id(message.get_id() + 1);
        message.setType("Text");
        message.setAddress("1234");
        message.setSender("Me");
        message.setNickName("윤영기");
        message.setSendTime(time);
        dao.insertMyChattingMessage(message);

        message.setSender("Others");
        message.set_id(message.get_id() + 1);
        dao.insertMyChattingMessage(message);

        editMessageBox.setText("");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("ChattingView","onResume start");
        getContentResolver().registerContentObserver(ChattingContract.CONTENT_URI,true,contentObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.chatting_tv_sendButton:
                sendMessage();
                break;

            case R.id.chatting_imageButton_back:
                this.finish();
                break;

        }
    }
}