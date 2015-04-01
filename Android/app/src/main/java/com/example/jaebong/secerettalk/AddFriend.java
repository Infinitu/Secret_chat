package com.example.jaebong.secerettalk;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public class AddFriend extends ActionBarActivity {

    private EditText tagId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        tagId = (EditText)findViewById(R.id.addFriend_editText_TagId);


    }

}
