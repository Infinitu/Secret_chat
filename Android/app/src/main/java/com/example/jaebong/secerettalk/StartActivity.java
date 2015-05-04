package com.example.jaebong.secerettalk;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


public class StartActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        final Intent joinIntent = new Intent(this,Join.class);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable(){
            public void run(){
                startActivity(joinIntent);

            }
        },1000);


    }



}
