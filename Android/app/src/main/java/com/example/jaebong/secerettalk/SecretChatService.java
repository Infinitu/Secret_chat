package com.example.jaebong.secerettalk;

import org.json.JSONArray;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;

/**
 * Created by JaeBong on 15. 4. 15..
 */
public interface SecretChatService {

    @Multipart
    @POST("/join")
    void sendUserProfile(
            @Part("nickName") String nickName,
            @Part("age") int age,
            @Part("gender") String gender,
            @Part("bloodType") String bloodType,
            @Part("image") TypedInput imageFile,
            Callback<Response> cb
    );

    @FormUrlEncoded
    @POST("/addfriend")
    void sendTag(
            @Field("accessToken") String accessToken,
            @Field("nickNameTag") String nickNameTag,
            Callback<Response> cb
    );


}
