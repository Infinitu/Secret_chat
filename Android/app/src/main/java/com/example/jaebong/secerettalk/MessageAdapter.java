package com.example.jaebong.secerettalk;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by JaeBong on 15. 4. 27..
 */
public class MessageAdapter extends CursorAdapter {

    private Context context;
    private int layoutId;
    private Cursor cursor;
    private LayoutInflater mLayoutInflater;

    public MessageAdapter(Context context, Cursor cursor, int layoutId) {
        super(context, cursor, layoutId);
        this.context = context;
        this.layoutId = layoutId;
        this.cursor = cursor;

        mLayoutInflater = LayoutInflater.from(context);


    }

    static class ViewHolderItem {
        TextView message;
        TextView time;
        TextView nickName;
        ImageView profileImage;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {



        View row = mLayoutInflater.inflate(layoutId, parent, false);
        ViewHolderItem viewHolder;
        viewHolder = new ViewHolderItem();
        viewHolder.nickName = (TextView)row.findViewById(R.id.messageBox_textVIew_name);
        viewHolder.message = (TextView)row.findViewById(R.id.messageBox_textVIew_message);
        viewHolder.time = (TextView)row.findViewById(R.id.messageBox_textView_time);
        viewHolder.profileImage = (ImageView)row.findViewById(R.id.messageBox_imageView_profileImage);
        row.setTag(viewHolder);

        return row;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        String sender = cursor.getString(cursor.getColumnIndex(SecretTalkContract.Messages.SENDER));
        String message = cursor.getString(cursor.getColumnIndex(SecretTalkContract.Messages.MESSAGE));
        long time = cursor.getLong(cursor.getColumnIndex(SecretTalkContract.Messages.SEND_TIME));
        String imageUrl = cursor.getString(cursor.getColumnIndex(SecretTalkContract.Messages.IMAGE_URL));
        String nickName = cursor.getString(cursor.getColumnIndex(SecretTalkContract.Messages.NICK_NAME));

        ViewHolderItem viewHolder = (ViewHolderItem)view.getTag();

        viewHolder.message.setText(message);
        viewHolder.nickName.setText(nickName);


        Date date = new Date(time);

        SimpleDateFormat sdfNow = new SimpleDateFormat("HH:mm");
        String strNow = sdfNow.format(date);

        viewHolder.time.setText(strNow);


        if(sender.equals("Me")){
            viewHolder.message.setBackgroundColor(Color.rgb(254,233,75));
            viewHolder.profileImage.setVisibility(View.GONE);
            viewHolder.nickName.setVisibility(View.GONE);
        }
        else{
            viewHolder.message.setBackgroundColor(Color.rgb(255,255,255));
            viewHolder.profileImage.setVisibility(View.VISIBLE);
            // Picasso.with(context).load(imageUrl).into(viewHolder.profileImage);
        }


    }



}