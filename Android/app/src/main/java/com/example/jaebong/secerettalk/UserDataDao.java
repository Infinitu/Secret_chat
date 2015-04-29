package com.example.jaebong.secerettalk;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JaeBong on 15. 4. 17..
 */
public class UserDataDao {
    private Context context;
    private SQLiteDatabase db;


    public UserDataDao(Context context) {
        this.context = context;
        sqLiteInitialize();
        profileTableCreate();
        chatTableCreate();
    }


    private void sqLiteInitialize() {
        db = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }

    private void myDataTableCreate(){
        try{
            String myDataSQL = "CREATE TABLE IF NOT EXISTS MyData(" +
                    "accessToken text not null," +
                    " nickName text not null," +
                    " age int not null," +
                    " gender text not null," +
                    " bloodType text not null," +
                    " imageUrl text not null," +
                    " chatLevel int not null," +
                    " gentle int not null," +
                    " cool int not null," +
                    " pervert int not null," +
                    " common int not null," +
                    " nickNameTag text not null)";
            db.execSQL(myDataSQL);

            Log.i("Dao", "MY DATA SQL Success");
        }catch (Exception e) {
            Log.e("Dao", "MY DATA SQL Fail" + e);
            e.printStackTrace();
        }
    }

    private void profileTableCreate() {
        try {
            String userProfileSQL = "CREATE TABLE IF NOT EXISTS UserProfiles(" +
                    "id text ," +
                    "nickName text not null," +
                    "birthYear text not null," +
                    "gender text not null," +
                    "bloodType text not null," +
                    "userCharacter text not null," +
                    "imageUrl text not null," +
                    "accessToken text)";
            db.execSQL(userProfileSQL);

            Log.i("Dao", "User sql Success");
        } catch (Exception e) {
            Log.e("Dao", "User SQL Fail" + e);
            e.printStackTrace();
        }
    }

    private void chatTableCreate() {
        try {
            String chattingSQL = "CREATE TABLE IF NOT EXISTS Messages(" +
                    "type text not null," +
                    " imageUrl text not null," +
                    " address text not null," +
                    " sender text not null," +
                    " message text," +
                    " sendTime long not null,"+
                    "nickName text)";
            db.execSQL(chattingSQL);

            Log.i("Dao", "Chatting Sql Success");
        } catch (Exception e) {
            Log.e("Dao", "Chatting Sql Fail" + e);
            e.printStackTrace();
        }
    }

    public void insertJSONUserProfileData(String jsonData) {

        String id;
        String nickName;
        String birthYear;
        String gender;
        String bloodType;
        String userCharacter;
        String image_url;

        try {
            JSONObject jDataObj = new JSONObject(jsonData);
            JSONArray jArr = jDataObj.getJSONArray("data");

            Log.e("abc", "jArr.length: " + jArr.length());

            for (int i = 0; i < jArr.length(); ++i) {
                JSONObject jObj = jArr.getJSONObject(i);

                id = jObj.getString("id");
                nickName = jObj.getString("nickName");
                birthYear = jObj.getString("birthYear");
                gender = jObj.getString("gender");
                bloodType = jObj.getString("bloodType");
                userCharacter = jObj.getString("userCharacter");
                image_url = jObj.getString("imageUrl");

                String sql = "INSERT INTO UserProfiles(id,nickName, birthYear, gender, bloodType, userCharacter, imageUrl ) VALUES("
                        + "'" + id + "', '"
                        + nickName + "', '"
                        + birthYear + "', '"
                        + gender + "','"
                        + bloodType + "', '"
                        + userCharacter + "', '"
                        + image_url + "');";

                db.execSQL(sql);
                Log.i("Dao", "jsonData Insert Success");
            }

        } catch (Exception e) {
            Log.e("Dao", "JSON ERROR! - " + e);
            e.printStackTrace();
        }


    }


}

