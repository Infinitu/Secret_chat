package com.example.jaebong.secerettalk;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


public class AddFriend extends ActionBarActivity implements View.OnClickListener {

    private EditText tagId;
    private ImageView search;
    private ImageView friendImage;
    private TextView friendName;
    private TextView friendGender;
    private ImageView addFriend;

    private Proxy proxy;
    private UserDataDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        tagId = (EditText) findViewById(R.id.addFriend_editText_TagId);
        search = (ImageView) findViewById(R.id.addFriend_imageButton_search);
        friendImage = (ImageView)findViewById(R.id.addFriend_imgView_editable_profileImage);
        friendName = (TextView)findViewById(R.id.addFriend_tv_nickName);
        friendGender = (TextView)findViewById(R.id.addFriend_tv_gender);
        addFriend = (ImageView)findViewById(R.id.addFriend_imgButton_add);

        proxy = new Proxy(getApplicationContext());
        dao = new UserDataDao(getApplicationContext());

        search.setOnClickListener(this);
        friendImage.setOnClickListener(this);
        addFriend.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addFriend_imageButton_search:
                Log.i("AddFriend","Search Click!");
                if (tagId.getText().toString().equals("")){
                    break;
                }else {
                    proxy.findUser(tagId.getText().toString());

                }

                break;
            case R.id.addFriend_imgView_editable_profileImage:
                UserProfile friendProfile = dao.getLastProfile();
                Picasso.with(this).load(friendProfile.getImageUrl()).into(friendImage);
                friendName.setText(friendProfile.getNickName());
                if(friendProfile.getGender().equals("m")){
                    friendGender.setText("남성");
                }else{
                    friendGender.setText("여성");
                }
                break;

            case R.id.addFriend_imgButton_add :
                this.finish();
                break;

            }
        }
    }

