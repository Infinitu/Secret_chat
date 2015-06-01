package com.example.jaebong.secerettalk.chatting;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jaebong.secerettalk.R;
import com.example.jaebong.secerettalk.user_profile.UserProfileDTO;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by JaeBong on 15. 5. 10..
 */
public class ChattingListAdapter extends ArrayAdapter<UserProfileDTO> {
    private Context context;
    private int layoutResourceId;
    private List<UserProfileDTO> friendList;

    public ChattingListAdapter(Context context, int layoutResourceId, List<UserProfileDTO> friendList) {
        super(context, layoutResourceId, friendList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.friendList = friendList;

    }

    public View getView(int position, View convertView, ViewGroup parent){
        View row = convertView;

        if(row == null){
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId,parent,false);
        }
        ImageView profileImage = (ImageView)row.findViewById(R.id.tile_imageView_left);
        TextView friendNickName = (TextView)row.findViewById(R.id.tile_textView_name_left);
        TextView friendCharacter = (TextView)row.findViewById(R.id.tile_textView_character_left);

        Picasso.with(context).load(friendList.get(position).getImageUrl()).resize(180,150).centerCrop().into(profileImage);
        friendNickName.setText(friendList.get(position).getNickName());
        friendCharacter.setText(""+friendList.get(position).getChatLevel());


        return row;

    }
}
