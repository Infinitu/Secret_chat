package com.example.jaebong.secerettalk.join;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.jaebong.secerettalk.user_profile.UserProfileDao;
import com.example.jaebong.secerettalk.user_profile.UserProfileDTO;
import com.example.jaebong.secerettalk.user_profile.UserProfileProxy;
import com.example.jaebong.secerettalk.home.HomeView;
import com.example.jaebong.secerettalk.R;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;


public class JoinView extends ActionBarActivity implements View.OnClickListener{
    private ImageView addProfileImage;

    private EditText nickName;

    private LinearLayout birthLayout;
    private TextView age;
    private Intent intent;
    private TextView resister;

    private RadioGroup sexGroup;
    private RadioButton sexButton;

    private RadioGroup bloodTypeGroup;
    private RadioButton bloodTypeButton;

    private RelativeLayout startLayout;

    private UserProfileProxy proxy;

    private UserProfileDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);


        addProfileImage = (ImageView) findViewById(R.id.join_add_profile);
        addProfileImage.setOnClickListener(this);

        resister = (TextView)findViewById(R.id.join_textView_profileResist);

        //nickName을 받아오기 위해서
        nickName = (EditText) findViewById(R.id.join_editText_nickName);

        //birth를 위한 작업
        age = (TextView) findViewById(R.id.join_tv_birth);
        birthLayout = (LinearLayout) findViewById(R.id.join_layout_birth);
        birthLayout.setOnClickListener(this);

        //sex를 받아오기 위한 작업
        sexGroup = (RadioGroup) findViewById(R.id.join_radioGroup_sex);

        //BloodType을 받아오기 위한 작업
        bloodTypeGroup = (RadioGroup) findViewById(R.id.join_radioGroup_bloodType);


        startLayout = (RelativeLayout) findViewById(R.id.join_layout_start);
        startLayout.setOnClickListener(this);

        proxy = new UserProfileProxy(getApplicationContext());
        dao = new UserProfileDao(getApplicationContext());

        if(dao.isMyDataTableExist()){
            intent = new Intent(this, HomeView.class);

            startActivity(intent);
            this.finish();
        }


    }


    public void showAgePicker() {
        RelativeLayout linearLayout = new RelativeLayout(this);
        final NumberPicker aNumberPicker = new NumberPicker(this);
        aNumberPicker.setMaxValue(100);
        aNumberPicker.setMinValue(1);
        aNumberPicker.setValue(20);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(50, 50);
        RelativeLayout.LayoutParams numPicerParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        numPicerParams.addRule(RelativeLayout.CENTER_HORIZONTAL);

        linearLayout.setLayoutParams(params);
        linearLayout.addView(aNumberPicker, numPicerParams);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("나이를 고르세요");
        alertDialogBuilder.setView(linearLayout);
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("확인",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                age.setText("" + aNumberPicker.getValue());

                            }
                        })
                .setNegativeButton("취소",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();


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

                    addProfileImage.setImageBitmap(bitmap);
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
                        resister.setText("");

                        SaveBitmapToFileCache(image, SAVE_FILE_URL, "/MyImage.jpg");
                        addProfileImage.setImageBitmap(image);
                        filePath = SAVE_FILE_URL + "/MyImage.jpg";

                        imageTypedFile = new TypedFile("image/jpeg", new File(SAVE_FILE_URL + "/MyImage.jpg"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
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

    public UserProfileDTO collectProfileData() {
        UserProfileDTO profile = new UserProfileDTO();

        //nickName
        profile.setNickName(nickName.getText().toString());

        //age
        profile.setAge(Integer.parseInt(age.getText().toString()));

        //gender
        int selectedSexId = sexGroup.getCheckedRadioButtonId();
        sexButton = (RadioButton) findViewById(selectedSexId);

        if (sexButton.getText().equals("남성"))
            profile.setGender("m");
        else
            profile.setGender("w");

        //bloodType
        int selectedBloodTypeId = bloodTypeGroup.getCheckedRadioButtonId();
        bloodTypeButton = (RadioButton) findViewById(selectedBloodTypeId);

        String checkedBloodType = (String) bloodTypeButton.getText();
        Log.i("Join", "bloodType" + checkedBloodType);

        if (checkedBloodType.equals("A형"))
            profile.setBloodType("a");
        else if (checkedBloodType.equals("B형"))
            profile.setBloodType("b");
        else if (checkedBloodType.equals("AB형"))
            profile.setBloodType("ab");
        else
            profile.setBloodType("o");

        //imageUrl
        profile.setImageUrl(filePath);


        return profile;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.join_add_profile:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_PHOTO_ALBUM);


                break;

            case R.id.join_layout_birth:
                showAgePicker();
                break;

            case R.id.join_layout_start:

                UserProfileDTO profile = new UserProfileDTO();
                profile = collectProfileData();

                Log.i("Join","profile : "+profile.toString());

                dao.myDataTableCreate();
                dao.insertMyData(profile);
                proxy.sendUserProfile(profile, imageTypedFile);

                intent = new Intent(this, HomeView.class);

                startActivity(intent);
                this.finish();
                break;


        }
    }


}
