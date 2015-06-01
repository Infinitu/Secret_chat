package com.example.jaebong.secerettalk.start_activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import com.example.jaebong.secerettalk.R;
import com.example.jaebong.secerettalk.join.JoinView;
import com.example.jaebong.secerettalk.tcp.SendSessionLoginRequest;


public class StartView extends ActionBarActivity {
    private SharedPreferences pref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Intent joinIntent = new Intent(this,JoinView.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                startActivity(joinIntent);

            }
        },1000);


    }



}
