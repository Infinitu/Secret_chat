package com.example.jaebong.secerettalk.user_profile;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.util.Log;

import com.example.jaebong.secerettalk.SecretChatService;
import com.example.jaebong.secerettalk.SecretTalkCotract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedInput;

/**
 * `
 * Created by JaeBong on 15. 4. 15..
 */
public class UserProfileProxy {
    //SEVER URL과 PORT를 static겸 fianl로 선언


    //HTTP통신을 위해 Retrofit 사용
    private RestAdapter restAdapter;
    private SecretChatService service;
    private UserProfileDao dao;
    private Context context;

    public UserProfileProxy(Context context) {
        this.context = context;

        //server 주소 지정
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SecretTalkCotract.SERVER_URL)
                .build();

        //어떤 행동을 할 것인지에 대한 정보를 담고있는 interface 지정
        service = restAdapter
                .create(SecretChatService.class);

        dao = new UserProfileDao(context);

    }

    public void sendAccessTokenForNicknameTag(){
        service.sendForTag(
                dao.getAccessToken(),
                new Callback<Response>() {
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

                        String nickNameTag = sb.toString();

                        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                        CharSequence cs = nickNameTag;
                        ClipData clip = ClipData.newPlainText(cs,cs);
                        clipboard.setPrimaryClip(clip);

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("Proxy", "retrofitError " + error);
                        error.printStackTrace();
                    }
                }
        );
    }


    public void sendUserProfile(UserProfileDTO profile, TypedInput imageFile) {

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


    public String getUserProfileJson() {
        return null;
    }
}
