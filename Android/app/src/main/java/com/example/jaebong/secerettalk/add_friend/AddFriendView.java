package com.example.jaebong.secerettalk.add_friend;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jaebong.secerettalk.R;
import com.example.jaebong.secerettalk.user_profile.UserProfileDTO;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class AddFriendView extends ActionBarActivity implements View.OnClickListener {

    private EditText tagId;
    private ImageView search;
    private ImageView friendImage;
    private TextView friendName;
    private TextView friendGender;
    private ImageView addFriend;

    private AddFriendProxy proxy;
    private AddFriendDao dao;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        //View 구성하기
        tagId = (EditText) findViewById(R.id.addFriend_editText_TagId);
        search = (ImageView) findViewById(R.id.addFriend_imageButton_search);
        friendImage = (ImageView)findViewById(R.id.addFriend_imgView_editable_profileImage);
        friendName = (TextView)findViewById(R.id.addFriend_tv_nickName);
        friendGender = (TextView)findViewById(R.id.addFriend_tv_gender);
        addFriend = (ImageView)findViewById(R.id.addFriend_imgButton_add);

        //친구정보를 읽어오기 위해 proxy사용
        proxy = new AddFriendProxy(getApplicationContext());

        //친구정보를 저장하기 위해 dao사용
        dao = new AddFriendDao(getApplicationContext());

        //search버튼을 누르면 찾기 시작
        search.setOnClickListener(this);

        //friendImage를 누르면 친구 갱신
        friendImage.setOnClickListener(this);

        //addFirend버튼을 누르면 친구를 db에 저장하고 main화면으로 이동
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
                    final Handler handler = new Handler();
                    progressDialog = ProgressDialog.show(AddFriendView.this,"","친구 찾는중...");


                    proxy.findAndAddUser(tagId.getText().toString(),
                            new Callback<Response>() {
                                @Override
                                public void failure(RetrofitError error) {
                                    Log.e("Proxy", "retrofitError " + error);
                                    error.printStackTrace();
                                    progressDialog.cancel();
                                    Toast toast = Toast.makeText(getApplicationContext(),"없는 유저입니다.",Toast.LENGTH_LONG);

                                    toast.show();
                                }

                                @Override
                                public void success(Response result, Response response) {
                                    BufferedReader reader = null;
                                    StringBuilder sb = new StringBuilder();
                                    try {

                                        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));

                                        String line;

                                        try {
                                            while ((line = reader.readLine()) != null) {
                                                sb.append(line);
                                            }
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }


                                    String jsonData = sb.toString();
                                    if(jsonData.equals("error")){
                                        progressDialog.cancel();
                                        Toast toast = new Toast(getApplicationContext());
                                        toast.setText("없는 아이디 입니다.");
                                        toast.show();
                                    }
                                    Log.i("Proxy",jsonData);

                                    dao.insertJSONUserProfileData(jsonData);
                                    progressDialog.cancel();
                                }

                            });

                }

                break;
            case R.id.addFriend_imgView_editable_profileImage:
                if(dao.isUserExist()) {
                    UserProfileDTO friendProfile = dao.getLastProfile();
                    Picasso.with(this).load(friendProfile.getImageUrl()).resize(100, 100).centerCrop().into(friendImage);
                    friendName.setText(friendProfile.getNickName());
                    if (friendProfile.getGender().equals("m")) {
                        friendGender.setText("남성");
                    } else {
                        friendGender.setText("여성");
                    }
                    break;
                }


            case R.id.addFriend_imgButton_add :
                this.finish();
                break;

            }
        }
    }

