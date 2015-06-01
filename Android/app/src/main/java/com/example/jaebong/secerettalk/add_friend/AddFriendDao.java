package com.example.jaebong.secerettalk.add_friend;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.jaebong.secerettalk.user_profile.UserProfileDTO;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by JaeBong on 15. 5. 8..
 */
public class AddFriendDao {
    private Context context;
    private SQLiteDatabase db;
    private final String TABLE_NAME_USER_PROFILE = "UserProfiles";

    public AddFriendDao(Context context){
        this.context = context;
        sqLiteInitialize();
        if(!isTableExist()) {
            profileTableCreate();
        }
    }
    private void sqLiteInitialize() {
        db = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY, null);
    }

    public boolean isUserExist(){
        String searchUser = "select * FROM UserProfiles";
        Cursor cursor = db.rawQuery(searchUser,null);

        if(cursor.getCount() == 0){
            return false;
        }
        cursor.close();
        return true;
    }

    private boolean isTableExist(){
        String searchTable = "select DISTINCT tbl_name from sqlite_master where tbl_name = '"+TABLE_NAME_USER_PROFILE+ "';";
        Cursor cursor = db.rawQuery(searchTable,null);

        if(cursor.getCount() == 0){
            return false;
        }
        Log.i("AddFriendDao",TABLE_NAME_USER_PROFILE+"already Exists");

        cursor.close();
        return true;
    }


    private void profileTableCreate() {
        try {
            String userProfileSQL = "CREATE TABLE IF NOT EXISTS "+ TABLE_NAME_USER_PROFILE +"(" +
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

    public UserProfileDTO getLastProfile(){
        UserProfileDTO profile;

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
        String selectQuery = "SELECT  * FROM UserProfiles";

        Cursor cursor = db.rawQuery(selectQuery, null);
        cursor.moveToLast();

        id = cursor.getString(0);
        nickName = cursor.getString(1);
        age = cursor.getInt(2);
        gender = cursor.getString(3);
        bloodType = cursor.getString(4);
        imageUrl = cursor.getString(5);
        chatLevel = cursor.getInt(6);
        gentle = cursor.getInt(7);
        cool = cursor.getInt(8);;
        pervert = cursor.getInt(9);
        common = cursor.getInt(10);

        profile = new UserProfileDTO(id,nickName,age,gender,bloodType,imageUrl,chatLevel,gentle,cool,pervert,common);

        cursor.close();

        return profile;
    }

    public String getAccessToken(){
        String accessToken;
        String sql = "SELECT * FROM AccessToken";

        Cursor cursor = db.rawQuery(sql, null);
        cursor.moveToNext();

        accessToken = cursor.getString(0);

        return accessToken;

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
            JSONObject jObj = new JSONObject(jsonData);

            id = jObj.getString("_id");
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
        }catch (JSONException e){
            Log.e("Dao","JSON ERROR1 - "+e);

        } catch (Exception e) {
            Log.e("Dao", "JSON ERROR! - " + e);
            e.printStackTrace();
        }

    }

}
