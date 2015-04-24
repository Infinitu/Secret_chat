package com.example.jaebong.secerettalk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by JaeBong on 15. 4. 17..
 */
public class Dao {
    private Context context;
    private SQLiteDatabase db;

    public Dao(Context context)
    {
        this.context = context;
        db = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);

        try {
            String userProfileSQL = "CREATE TABLE IF NOT EXISTS UserProfiles(" +
                    "nickName text not null," +
                    "birthYear text not null," +
                    "gender text not null," +
                    "bloodType text not null," +
                    "userCharacter text not null," +
                    "imageUrl text not null)";
            db.execSQL(userProfileSQL);

            Log.i("Dao", "User sql Success");
        }catch(Exception e){
            Log.e("Dao","User SQL Fail"+e);
            e.printStackTrace();
        }

        try{
            String chattingSQL = "CREATE TABLE IF NOT EXISTS ChattingData(" +
                    "type text not null," +
                    " imageUrl text not null," +
                    " address text not null," +
                    " sender text not null," +
                    " message text," +
                    " sendTime long not null )";
            db.execSQL(chattingSQL);

            Log.i("Dao","Chatting Sql Success");
        }catch(Exception e){
            Log.e("Dao","Chatting Sql Fail" + e);
            e.printStackTrace();
        }
    }

    public void insertUserProfileData(String jsonData) {


        String nickName;
        String birthYear;
        String gender;
        String bloodType;
        String userCharacter;
        String image_url;

        try {
            JSONObject jDataObj = new JSONObject(jsonData);
            JSONArray jArr = jDataObj.getJSONArray("data");

            Log.e("abc","jArr.length: " + jArr.length());

            for (int i = 0; i < jArr.length(); ++i) {
                JSONObject jObj = jArr.getJSONObject(i);

                nickName = jObj.getString("nickName");
                birthYear = jObj.getString("birthYear");
                gender = jObj.getString("gender");
                bloodType = jObj.getString("bloodType");
                userCharacter = jObj.getString("userCharacter");
                image_url = jObj.getString("imageUrl");

                String sql = "INSERT INTO UserProfiles(nickName, birthYear, gender, bloodType, userCharacter, imageUrl ) VALUES("
                      +"'"+ nickName + "', '"
                      + birthYear + "', '"
                      + gender + "','"
                      + bloodType + "', '"
                      + userCharacter + "', '"
                      + image_url + "');";

                db.execSQL(sql);
                Log.i("Dao","jsonData Insert Success");
            }

        } catch (Exception e) {
            Log.e("Dao", "JSON ERROR! - " + e);
            e.printStackTrace();
        }


    }

    public void insertChattingMessage(Message msg){
        String sql = "INSERT INTO ChattingData(type,imageUrl,address,sender,message,sendTime) VALUES("
                +"'"+msg.getType()+"','"
                +msg.getImageUrl()+"','"
                +msg.getAdress()+"','"
                +msg.getSender()+"','"
                +msg.getMessage()+"','"
                +msg.getSendTime()+"');";
        db.execSQL(sql);
        Log.i("Dao","ChattingMessageInsert Success");

    }
}
