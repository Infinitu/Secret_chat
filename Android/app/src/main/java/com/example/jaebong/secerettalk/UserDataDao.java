package com.example.jaebong.secerettalk;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

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
        myDataTableCreate();
        accessTokenTableCreate();
        chatTableCreate();
    }


    private void sqLiteInitialize() {
        db = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }

    private void accessTokenTableCreate() {
        try {
            String accessToken = "CREATE TABLE IF NOT EXISTS AccessToken(accessToken text not null);";
            db.execSQL(accessToken);
            Log.i("Dao", "AccessToken Table Success");
        } catch (Exception e) {
            Log.e("Dao", "AccessToken Table fail" + e);
        }
    }

    public void insertAccessToken(String accessToken) {
        String sql = "INSERT INTO AccessToken(accessToken) Values("
                + "'" + accessToken + "');";
        db.execSQL(sql);

    }

    private void myDataTableCreate() {
        try {
            String myDataSQL = "CREATE TABLE IF NOT EXISTS MyData(" +
                    " nickName text not null," +
                    " age int not null," +
                    " gender text not null," +
                    " bloodType text not null," +
                    " imageUrl text," +
                    " chatLevel int not null," +
                    " gentle int not null," +
                    " cool int not null," +
                    " pervert int not null," +
                    " common int not null," +
                    " nickNameTag text)";
            db.execSQL(myDataSQL);

            Log.i("Dao", "MY DATA SQL Success");
        } catch (Exception e) {
            Log.e("Dao", "MY DATA SQL Fail" + e);
            e.printStackTrace();
        }
    }

    public void insertMyData(UserProfile profile) {
        String nickName;
        int age;
        String gender;
        String bloodType;
        String imageUrl;
        int chatLevel;
        int gentle;
        int cool;
        int pervert;
        int common;

        nickName = profile.getNickName();
        age = profile.getAge();
        gender = profile.getGender();
        bloodType = profile.getBloodType();
        imageUrl = profile.getImageUrl();
        chatLevel = profile.getChatLevel();
        gentle = profile.getGentle();
        cool = profile.getCool();
        pervert = profile.getPervert();
        common = profile.getCommon();

        Log.i("Dao","profile : "+ profile.toString());

        try {
            String sql = "INSERT INTO MyData(nickName, age, gender, bloodType, imageUrl, chatLevel, gentle, cool, pervert,common) VALUES("
                    + "'" + nickName + "', '"
                    + age + "', '"
                    + gender + "','"
                    + bloodType + "', '"
                    + imageUrl + "', '"
                    + chatLevel + "', '"
                    + gentle + "', '"
                    + cool + "', '"
                    + pervert + "', '"
                    + common + "');";

            db.execSQL(sql);
            Log.i("Dao", "MyData Insert Success");
        } catch (Exception e) {
            Log.e("Dao", "MyData Insert FAIL" + e);
        }
    }

    private void profileTableCreate() {
        try {
            String userProfileSQL = "CREATE TABLE IF NOT EXISTS UserProfiles(" +
                    "id text primary key not null," +
                    " nickName text not null," +
                    " age int not null," +
                    " gender text not null," +
                    " bloodType text not null," +
                    " imageUrl text not null," +
                    " chatLevel int not null," +
                    " gentle int not null," +
                    " cool int not null," +
                    " pervert int not null," +
                    " common int not null)";

            db.execSQL(userProfileSQL);

            Log.i("Dao", "User sql Success");
        } catch (Exception e) {
            Log.e("Dao", "User SQL Fail" + e);
            e.printStackTrace();
        }
    }

    public void insertJSONUserProfileData(String jsonData) {

        String id;
        String nickName;
        String age;
        String gender;
        String bloodType;
        String imageUrl;
        int chatLevel;
        int gentle;
        int cool;
        int pervert;
        int common;

        try {
            JSONObject jDataObj = new JSONObject(jsonData);
            JSONArray jArr = jDataObj.getJSONArray("data");

            Log.e("abc", "jArr.length: " + jArr.length());

            for (int i = 0; i < jArr.length(); ++i) {
                JSONObject jObj = jArr.getJSONObject(i);

                id = jObj.getString("id");
                nickName = jObj.getString("nickName");
                age = jObj.getString("age");
                gender = jObj.getString("gender");
                bloodType = jObj.getString("bloodType");
                imageUrl = jObj.getString("imageUrl");
                chatLevel = jObj.getInt("chatLevel");
                gentle = jObj.getInt("gentle");
                cool = jObj.getInt("cool");
                pervert = jObj.getInt("pervert");
                common = jObj.getInt("common");

                String sql = "INSERT INTO UserProfiles(id,nickName, age, gender, bloodType, imageUrl, chatLevel, gentle, cool, pervert,common ) VALUES("
                        + "'" + id + "', '"
                        + nickName + "', '"
                        + age + "', '"
                        + gender + "','"
                        + bloodType + "', '"
                        + imageUrl + "', '"
                        + chatLevel + "', '"
                        + gentle + "', '"
                        + cool + "', '"
                        + pervert + "', '"
                        + common + "');";

                db.execSQL(sql);
                Log.i("Dao", "jsonData Insert Success");
            }

        } catch (Exception e) {
            Log.e("Dao", "JSON ERROR! - " + e);
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
                    " sendTime long not null," +
                    "nickName text)";
            db.execSQL(chattingSQL);

            Log.i("Dao", "Chatting Sql Success");
        } catch (Exception e) {
            Log.e("Dao", "Chatting Sql Fail" + e);
            e.printStackTrace();
        }
    }


}

