package com.example.jaebong.secerettalk;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedInput;


public class Join extends ActionBarActivity implements View.OnClickListener {

    private String imgDecodableString;
    public UserProfile profile;

    private ImageView addProfileImage;

    private EditText nickName;

    private LinearLayout birthLayout;
    private TextView birth;
    private int year;
    private int month;
    private int day;
    private String date;
    private Intent intent;
    DatePickerDialog dialog;

    RadioGroup sexGroup;
    RadioButton sexButton;

    RadioGroup bloodTypeGroup;
    RadioButton bloodTypeButton;

    private RelativeLayout startLayout;

    Proxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        profile = new UserProfile();

        addProfileImage = (ImageView) findViewById(R.id.join_add_profile);
        addProfileImage.setOnClickListener(this);

        //nickName을 받아오기 위해서
        nickName = (EditText) findViewById(R.id.join_editText_nickName);

        //birth를 위한 작업
        birth = (TextView) findViewById(R.id.join_tv_birth);
        birthLayout = (LinearLayout) findViewById(R.id.join_layout_birth);

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_YEAR);
        dialog = new DatePickerDialog(this, dateSetListener, year, month, day);

        birthLayout.setOnClickListener(this);

        //sex를 받아오기 위한 작업
        sexGroup = (RadioGroup) findViewById(R.id.join_radioGroup_sex);

        //BloodType을 받아오기 위한 작업
        bloodTypeGroup = (RadioGroup) findViewById(R.id.join_radioGroup_bloodType);


        startLayout = (RelativeLayout) findViewById(R.id.join_layout_start);
        startLayout.setOnClickListener(this);

        proxy = new Proxy();


    }


    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            //picker에서 입력받은 생일을 저장
            date = String.format("%d", year);
            //birthday에 date정보를 string으로 저장
            birth.setText(date);
        }
    };

    private static int REQUEST_PHOTO_ALBUM = 1;
    private String filePath;
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
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    addProfileImage.setImageBitmap(bitmap);
                    imageTypedFile = new TypedFile("image/jpeg", new File(fileUri.getPath()));

                } else {
                    ParcelFileDescriptor parcelFileDescriptor;
                    try {
                        parcelFileDescriptor = getContentResolver().openFileDescriptor(fileUri, "r");
                        FileDescriptor fileDescriptor2 = parcelFileDescriptor.getFileDescriptor();
                        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor2);
                        parcelFileDescriptor.close();
                        addProfileImage.setImageBitmap(image);


                        parcelFileDescriptor = getContentResolver().openFileDescriptor(fileUri, "r");
                        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                        final InputStream fileStream = new FileInputStream(fileDescriptor);

                        final long fsize = parcelFileDescriptor.getStatSize();
                        imageTypedFile = new TypedInput() {
                            @Override
                            public String mimeType() {
                                return "image/jpeg";
                            }

                            @Override
                            public long length() {
                                return fsize;
                            }

                            @Override
                            public InputStream in() throws IOException {
                                return fileStream;
                            }
                        };


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
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
                dialog.show();
                break;

            case R.id.join_layout_start:

                Log.i("JOIN", nickName.getText().toString());


                //성별을 profile에 저장
                int selectedSexId = sexGroup.getCheckedRadioButtonId();
                sexButton = (RadioButton) findViewById(selectedSexId);

                profile.setNickName(nickName.getText().toString());
                profile.setBirthYear(date);


                Log.i("Join", "sex" + sexButton.getText());
                if (sexButton.getText().equals("남성"))
                    profile.setGender("m");
                else
                    profile.setGender("w");

                int selectedBloodTypeId = bloodTypeGroup.getCheckedRadioButtonId();
                bloodTypeButton = (RadioButton) findViewById(selectedBloodTypeId);

                //혈액형을 profile에 저장
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

                proxy.sendUserProfile(profile, imageTypedFile);

                intent = new Intent(this, MainActivity.class);
                startActivity(intent);

                break;


        }
    }

}
