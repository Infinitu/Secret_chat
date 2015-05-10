package com.example.jaebong.secerettalk;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * Created by JaeBong on 15. 4. 24..
 */
public class SecretTalkProvider extends ContentProvider {

    private SQLiteDatabase db;
    private Context context;
    private final String TABLE_NAME_CHAT = "Messages";

    private static final int MESSAGE_LIST = 1;
    private static final int MESSAGE_ID = 2;

    private static final UriMatcher URI_MATCHER;
    static{
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(SecretTalkContract.AUTHORITY,"Messages",MESSAGE_LIST);
        URI_MATCHER.addURI(SecretTalkContract.AUTHORITY,"Messages/#", MESSAGE_ID);
    }

    private void sqLiteInitialize(){
        db = context.openOrCreateDatabase("LocalDATA.db", SQLiteDatabase.CREATE_IF_NECESSARY,null);

    }
    private void chatTableCreate(){
        try{
            String chattingSQL = "CREATE TABLE IF NOT EXISTS Messages(" +
                    "_id integer primary key autoincrement," +
                    "type text not null," +
                    " imageUrl text not null," +
                    " address text not null," +
                    " sender text not null," +
                    " message text," +
                    " sendTime long not null,"+
                    " nickName text not null )";
            db.execSQL(chattingSQL);

            Log.i("Dao", "Chatting Sql Success");
        }catch(Exception e){
            Log.e("Dao","Chatting Sql Fail" + e);
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreate()
    {
        //Application의 context를 기억
        this.context = getContext();
        //DB 생성
        sqLiteInitialize();
        //chatTable생성 (없으면)
        chatTableCreate();
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        switch(URI_MATCHER.match(uri)){
            case MESSAGE_LIST :
                break;
            case MESSAGE_ID:
                break;
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
        Cursor cursor = db.query(TABLE_NAME_CHAT, SecretTalkContract.Messages.PROJECTION_ALL, selection,
                selectionArgs,null,null,sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        if(URI_MATCHER.match(uri) != MESSAGE_LIST){
            throw new IllegalArgumentException("Insertion을 지원하지 않는 URI 입니다:" + uri);

        }

        if(URI_MATCHER.match(uri) == MESSAGE_LIST){

            //Database에 Insert하고 ID를 리턴받음

            long id = db.insert("Messages",null,values);

            Uri itemUri = ContentUris.withAppendedId(uri,id);
            getContext().getContentResolver().notifyChange(itemUri, null);

            return itemUri;

        }
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }

}
