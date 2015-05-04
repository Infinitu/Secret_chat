package com.example.jaebong.secerettalk;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

/**
 * `
 * Created by JaeBong on 15. 4. 15..
 */
public class Proxy {
    //SEVER URL과 PORT를 static겸 fianl로 선언
    public static final String SERVER_URL = "http://192.168.1.108:8080";


    //HTTP통신을 위해 Retrofit 사용
    private RestAdapter restAdapter;
    private SecretChatService service;
    private UserDataDao dao;

    public Proxy(Context context) {
        //server 주소 지정
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SERVER_URL)
                .build();

        //어떤 행동을 할 것인지에 대한 정보를 담고있는 interface 지정
        service = restAdapter
                .create(SecretChatService.class);

        dao = new UserDataDao(context);

    }


    public void sendUserProfile(UserProfile profile, TypedInput imageFile) {

        service.sendUserProfile(
                profile.getNickName(),
                profile.getAge(),
                profile.getGender(),
                profile.getBloodType(),
                imageFile,

                new Callback<Response>() {
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Proxy", "retrofitError " + error);
                        error.printStackTrace();

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


                        String accessToken = sb.toString();

                        dao.insertAccessToken(accessToken);

                    }

                }
        );


    }

    public void findUser(String nickNameTag) {
        service.sendTag(
                dao.getAccessToken(),
                nickNameTag,
                new Callback<Response>() {
                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Proxy", "retrofitError " + error);
                        error.printStackTrace();

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
                        Log.i("Proxy",jsonData);

                        dao.insertJSONUserProfileData(jsonData);

                    }

                }
        );
    }

    public String getUserProfileJson() {
        return null;
    }
}
