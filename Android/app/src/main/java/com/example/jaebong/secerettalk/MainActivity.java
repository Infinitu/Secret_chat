package com.example.jaebong.secerettalk;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.gc.materialdesign.views.CustomView;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private ImageButton setting;
    private CustomView addFriend;
    private FrameLayout startRandomChat;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = (ImageButton)findViewById(R.id.main_imageButton_setting);
        addFriend = (CustomView)findViewById(R.id.main_buttonFloat_addFriend);
        startRandomChat = (FrameLayout)findViewById(R.id.main_layout_startRandomChat);

        setting.setOnClickListener(this);
        addFriend.setOnClickListener(this);
        startRandomChat.setOnClickListener(this);

//        Intent implictIntent = new Intent();
//        implictIntent.setAction("com.example.jaebong.secerettalk.SyncDataService");
//        startService(implictIntent);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_imageButton_setting :
                intent = new Intent(this,Setting.class);
                Log.i("HI","hi");
                startActivity(intent);
                break;
            case R.id.main_buttonFloat_addFriend :
                intent = new Intent(this,AddFriend.class);
                startActivity(intent);
                break;
            case R.id.main_layout_startRandomChat:
                intent = new Intent(this,Chatting.class);
                startActivity(intent);
                break;
        }
    }

}
