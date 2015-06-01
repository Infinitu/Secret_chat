package com.example.jaebong.secerettalk.home;

import android.content.Context;
import android.util.Log;

import com.example.jaebong.secerettalk.SecretChatService;
import com.example.jaebong.secerettalk.SecretTalkCotract;
import com.example.jaebong.secerettalk.user_profile.UserProfileDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by JaeBong on 15. 5. 10..
 */
public class HomeProxy {

    //HTTP통신을 위해 Retrofit 사용
    private RestAdapter restAdapter;
    private SecretChatService service;
    private UserProfileDao dao;

    public HomeProxy(Context context) {
        //server 주소 지정
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SecretTalkCotract.SERVER_URL)
                .build();

        //어떤 행동을 할 것인지에 대한 정보를 담고있는 interface 지정
        service = restAdapter
                .create(SecretChatService.class);

        dao = new UserProfileDao(context);

    }

    public void InsertUserProfiles(){
        String accessToken = dao.getAccessToken();
        ArrayList<String> friendIdList = dao.getAllFriendId();

        for(int i = 0 ; i < friendIdList.size() ; i++){
            service.sendFriendId(
                    accessToken,
                    friendIdList.get(i),
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


                            String userJsonData = sb.toString();
                            Log.i("Proxy",userJsonData);

                            dao.insertUserProfileListJsonData(userJsonData);

                        }

                    }

            );
        }

    }

}
