package com.example.jaebong.secerettalk.home;

import android.content.Intent;
import android.database.ContentObserver;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.jaebong.secerettalk.R;
import com.example.jaebong.secerettalk.tcp.SecretTalkTCPService;
import com.example.jaebong.secerettalk.tcp.SendSessionLoginRequest;
import com.example.jaebong.secerettalk.user_profile.UserProfileDao;
import com.example.jaebong.secerettalk.user_profile.UserProfileDTO;
import com.example.jaebong.secerettalk.add_friend.AddFriendView;
import com.example.jaebong.secerettalk.chatting.ChattingListAdapter;
import com.example.jaebong.secerettalk.chatting.ChattingView;
import com.example.jaebong.secerettalk.setting.SettingView;
import com.gc.materialdesign.views.CustomView;

import java.util.ArrayList;


public class HomeView extends ActionBarActivity implements View.OnClickListener {

    private ImageButton setting;
    private CustomView addFriend;
    private FrameLayout startRandomChat;
    private GridView chattingListView;
    private Intent intent;
    private UserProfileDao dao;

    private ArrayList<UserProfileDTO> chattingList;
    private ContentObserver contentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = (ImageButton)findViewById(R.id.main_imageButton_setting);
        addFriend = (CustomView)findViewById(R.id.main_buttonFloat_addFriend);
        startRandomChat = (FrameLayout)findViewById(R.id.main_layout_startRandomChat);
        chattingListView = (GridView)findViewById(R.id.main_gridView_chattingList);


        setting.setOnClickListener(this);
        addFriend.setOnClickListener(this);
        startRandomChat.setOnClickListener(this);

        dao = new UserProfileDao(getApplicationContext());
        chattingList = dao.getUserProfiles();


        chattingListView.setAdapter(new ChattingListAdapter(this,R.layout.view_chatting_tile,chattingList));

        contentObserver = new ContentObserver(new Handler()) {
            @Override

            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                chattingListView.setAdapter(new ChattingListAdapter(HomeView.this,R.layout.view_chatting_tile,chattingList));
                Log.i("ChattingView","contentsObserver onchange start");
            }
        };
       // startService(new Intent(this, SecretTalkTCPService.class));




    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_imageButton_setting :
                intent = new Intent(this,SettingView.class);
                Log.i("Main","hi");
                startActivity(intent);
                break;
            case R.id.main_buttonFloat_addFriend :
                intent = new Intent(this,AddFriendView.class);
                Log.i("Main","AddFriend");
                startActivity(intent);
                break;
            case R.id.main_layout_startRandomChat:
                intent = new Intent(this,ChattingView.class);
                startActivity(intent);
                break;
        }
    }

}
