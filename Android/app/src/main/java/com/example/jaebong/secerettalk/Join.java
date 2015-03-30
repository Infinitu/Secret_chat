package com.example.jaebong.secerettalk;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;


public class Join extends ActionBarActivity implements View.OnClickListener {

    private LinearLayout nickNameLayout;
    private LinearLayout birthLayout;
    private EditText nickName;
    private TextView birth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        birthLayout = (LinearLayout) findViewById(R.id.join_layout_birth);

        nickName = (EditText) findViewById(R.id.join_tv_nickName);
        birth = (TextView)findViewById(R.id.join_tv_birth);

        birthLayout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}
