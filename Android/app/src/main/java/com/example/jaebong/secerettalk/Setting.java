package com.example.jaebong.secerettalk;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;


public class Setting extends ActionBarActivity implements View.OnClickListener{

    private ImageView backButton;
    private ImageView profileImage;
    private TextView nickName;
    private TextView age;
    private TextView gender;
    private TextView bloodType;
    private TextView level;

    private UserDataDao dao;

    private UserProfile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        backButton = (ImageView)findViewById(R.id.setting_imgBtn_back);
        profileImage = (ImageView)findViewById(R.id.setting_imageButton_profileImage);
        nickName = (TextView)findViewById(R.id.setting_tv_editable_nick);
        age = (TextView)findViewById(R.id.setting_tv_editable_birth);
        gender = (TextView)findViewById(R.id.setting_tv_editable_sex);
        bloodType = (TextView)findViewById(R.id.setting_tv_editable_blood);
        level = (TextView)findViewById(R.id.setting_tv_editable_level);

        dao = new UserDataDao(getApplicationContext());

        backButton.setOnClickListener(this);
        profileImage.setOnClickListener(this);

        profile = dao.getMyData();

        String SAVE_FILE_URL = getApplicationContext().getFilesDir().getPath().toString();
        nickName.setText(profile.getNickName());
        age.setText(""+profile.getAge());
        gender.setText(profile.getGender());
        bloodType.setText(profile.getBloodType());
        filePath = SAVE_FILE_URL + "/MyImage.jpg";
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeFile(filePath);
            profileImage.setImageBitmap(bitmap);
        }catch (Exception e){
            Log.e("Setting",
                    "No image File");
        }

    }


    public void onClick(View v) {
        switch (v.getId()){
            case R.id.chatting_imageButton_back :
                this.finish();
                break;

            case R.id.setting_imageButton_profileImage :
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PHOTO_ALBUM);
                break;

        }
    }

    public String getPath(Uri uri) {
        if (uri == null) {
            return null;
        }
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        return uri.getPath();
    }

    public void SaveBitmapToFileCache(Bitmap bitmap, String strFilePath,
                                      String filename) {

        File file = new File(strFilePath);
        Log.i("Join", strFilePath);

        // If no folders
        if (!file.exists()) {
            file.mkdirs();
            //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        File fileCacheItem = new File(strFilePath + filename);
        OutputStream out = null;

        if (!fileCacheItem.exists()) {
            file.mkdirs();
            //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        }

        try {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static int REQUEST_PHOTO_ALBUM = 1;
    private String filePath = "";
    private String fileName;
    private Uri fileUri;
    private TypedInput imageTypedFile;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PHOTO_ALBUM) {
                fileUri = data.getData();
                if (Build.VERSION.SDK_INT < 19) {
                    filePath = getPath(fileUri);
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 4;
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                    profileImage.setImageBitmap(bitmap);
                    imageTypedFile = new TypedFile("image/jpeg", new File(fileUri.getPath()));

                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        String SAVE_FILE_URL = getApplicationContext().getFilesDir().getPath().toString();
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(fileUri, "r");
                        FileDescriptor fileDescriptor2 = parcelFileDescriptor.getFileDescriptor();

                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 4;

                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor2);
                        parcelFileDescriptor.close();
                        SaveBitmapToFileCache(image, SAVE_FILE_URL, "/MyImage.jpg");
                        profileImage.setImageBitmap(image);
                        filePath = SAVE_FILE_URL + "/MyImage.jpg";

                        imageTypedFile = new TypedFile("image/jpeg", new File(SAVE_FILE_URL + "/MyImage.jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
