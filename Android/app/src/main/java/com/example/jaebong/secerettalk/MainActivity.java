package com.example.jaebong.secerettalk;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    private Button setting;
    private Button addFriend;
    private Button signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setting = (Button)findViewById(R.id.main_temp_button_setting);
        addFriend = (Button)findViewById(R.id.main_temp_button_addFriend);
        signIn = (Button)findViewById(R.id.main_temp_button_signIn);

        setting.setOnClickListener(this);
        addFriend.setOnClickListener(this);
        signIn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.main_temp_button_setting:
                Intent settingIntent = new Intent(this,Setting.class);
                startActivity(settingIntent);
                break;
            case R.id.main_temp_button_addFriend:
                Intent addFriendIntent = new Intent(this,AddFriend.class);
                startActivity(addFriendIntent);
                break;
            case R.id.main_temp_button_signIn:
                Intent signInIntent = new Intent(this,Join.class);
                startActivity(signInIntent);
                break;
        }
    }

}
