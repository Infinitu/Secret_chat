package com.example.jaebong.secerettalk;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.GregorianCalendar;


public class Join extends ActionBarActivity implements View.OnClickListener {

    private LinearLayout nickNameLayout;
    private LinearLayout birthLayout;
    private EditText nickName;
    private TextView birth;
    private int year;
    private String date;
    private int month;
    private int day;
    DatePickerDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_YEAR);

        dialog = new DatePickerDialog(this,dateSetListener,year,month,day);

        birthLayout = (LinearLayout) findViewById(R.id.join_layout_birth);

        nickName = (EditText) findViewById(R.id.join_editText_nickName);
        birth = (TextView)findViewById(R.id.join_tv_birth);

        birthLayout.setOnClickListener(this);

        findAndHideField(dialog, "mDaySpinner");
        findAndHideField(dialog, "mMonthSpinner");
    }

    private void findAndHideField(DatePickerDialog datepicker, String name) {
        try {
            Field field = DatePicker.class.getDeclaredField(name);
            field.setAccessible(true);
            View fieldInstance = (View) field.get(datepicker);
            fieldInstance.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
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


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.join_layout_birth:
                dialog.show();
                break;

        }
    }

}
