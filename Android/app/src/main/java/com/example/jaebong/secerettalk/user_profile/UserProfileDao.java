package com.example.jaebong.secerettalk.user_profile;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jaebong.secerettalk.user_profile.UserProfileDTO;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by JaeBong on 15. 4. 17..
 */
public class UserProfileDao {
    private Context context;
    private SQLiteDatabase db;


    public UserProfileDao(Context context) {
        this.context = context;
        sqLiteInitialize();
        profileTableCreate();

        accessTokenTableCreate();
        chatTableCreate();
    }

    public boolean isMyDataTableExist(){
        String searchTable = "select DISTINCT tbl_name from sqlite_master where tbl_name = 'MyData'";
        Cursor cursor = db.rawQuery(searchTable,null);

        if(cursor.getCount() == 0){
            return false;
        }
        Log.i("UserProfileDao","MyData already Exists");

        cursor.close();
        return true;
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

    public String getAccessToken() {
        String accessToken;
        String sql = "SELECT * FROM AccessToken";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();

        accessToken = cursor.getString(0);

        return accessToken;

    }

    public void myDataTableCreate() {
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

    public void insertMyData(UserProfileDTO profile) {
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

        Log.i("Dao", "profile : " + profile.toString());

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

    public UserProfileDTO getMyData() {

        UserProfileDTO profile;

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

        String sql = "SELECT * FROM MyData";
        Cursor cursor = db.rawQuery(sql, null);

        cursor.moveToNext();

        nickName = cursor.getString(0);
        age = cursor.getInt(1);
        gender = cursor.getString(2);
        bloodType = cursor.getString(3);
        imageUrl = cursor.getString(4);
        chatLevel = cursor.getInt(5);
        gentle = cursor.getInt(6);
        cool = cursor.getInt(7);
        pervert = cursor.getInt(8);
        common = cursor.getInt(9);


        profile = new UserProfileDTO(nickName, age, gender, bloodType, imageUrl, chatLevel, gentle, cool, pervert, common);


        cursor.close();

        return profile;
    }

    private void profileTableCreate() {
        try {
            String userProfileSQL = "CREATE TABLE IF NOT EXISTS UserProfiles(" +
                    " id text primary key not null," +
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

    public ArrayList<String> getAllFriendId() {
        ArrayList<String> friendIdlist = new ArrayList<String>();

        String sql = "SELECT id FROM UserProfiles;";
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            friendIdlist.add(cursor.getString(0));
        }

        cursor.close();

        return friendIdlist;
    }

    public void insertUserProfileListJsonData(String jsonData) {


        String id;
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


        try {
            JSONObject jObj = new JSONObject(jsonData);


            id = jObj.getString("id");
            nickName = jObj.getString("nickName");
            age = jObj.getInt("age");
            gender = jObj.getString("gender");
            bloodType = jObj.getString("bloodType");
            imageUrl = jObj.getString("imageUrl");
            chatLevel = jObj.getInt("chatLevel");
            gentle = jObj.getInt("gentle");
            cool = jObj.getInt("cool");
            pervert = jObj.getInt("pervert");
            common = jObj.getInt("common");

            String sql = "INSERT INTO UserProfiles" +
                    "(id, nickName, age, gender, bloodType, imageUrl, chatLevel, gentle, cool, pervert, common) VALUES("
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

        } catch (Exception e) {
            Log.e("test", "JSON ERROR! - " + e);
            e.printStackTrace();
        }


    }


    public ArrayList<UserProfileDTO> getUserProfiles() {
        ArrayList<UserProfileDTO> userProfileList = new ArrayList<UserProfileDTO>();

        String id;
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

        String sql = "SELECT * FROM UserProfiles;";
        Cursor cursor = db.rawQuery(sql, null);

        while (cursor.moveToNext()) {
            id = cursor.getString(0);
            nickName = cursor.getString(1);
            age = cursor.getInt(2);
            gender = cursor.getString(3);
            bloodType = cursor.getString(4);
            imageUrl = cursor.getString(5);
            chatLevel = cursor.getInt(6);
            gentle = cursor.getInt(7);
            cool = cursor.getInt(8);
            pervert = cursor.getInt(9);
            common = cursor.getInt(10);

            userProfileList.add(new UserProfileDTO(id, nickName, age, gender, bloodType, imageUrl, chatLevel, gentle, cool, pervert, common));
        }

        cursor.close();

        return userProfileList;

    }


    private void chatTableCreate() {
        try {
            String chattingSQL = "CREATE TABLE IF NOT EXISTS Messages(" +
                    " type text not null," +
                    " imageUrl text not null," +
                    " address text not null," +
                    " sender text not null," +
                    " message text," +
                    " sendTime long not null," +
                    " nickName text)";
            db.execSQL(chattingSQL);

            Log.i("Dao", "Chatting Sql Success");
        } catch (Exception e) {
            Log.e("Dao", "Chatting Sql Fail" + e);
            e.printStackTrace();
        }
    }


}

