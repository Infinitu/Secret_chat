package com.example.jaebong.secerettalk.add_friend;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.jaebong.secerettalk.R;
import com.example.jaebong.secerettalk.SecretChatService;
import com.example.jaebong.secerettalk.SecretTalkCotract;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;

/**
 * Created by JaeBong on 15. 5. 8..
 */
public class AddFriendProxy {
    //SEVER URL과 PORT를 static겸 fianl로 선언

    private Context context;

    //HTTP통신을 위해 Retrofit 사용
    private RestAdapter restAdapter;
    private SecretChatService service;
    private AddFriendDao dao;

    public AddFriendProxy(Context context) {
        this.context = context;

        //server 주소 지정
        restAdapter = new RestAdapter.Builder()
                .setEndpoint(SecretTalkCotract.SERVER_URL)
                .build();

        //어떤 행동을 할 것인지에 대한 정보를 담고있는 interface 지정
        service = restAdapter
                .create(SecretChatService.class);

        dao = new AddFriendDao(context);

    }

    //사용자를 서버에서 accessToken으로 검색한 후 loaclDB에 추가
    public void findAndAddUser(String nickNameTag, Callback<Response> callback) {
        service.sendTag(
                dao.getAccessToken(),
                nickNameTag,
                callback
        );
    }
}
