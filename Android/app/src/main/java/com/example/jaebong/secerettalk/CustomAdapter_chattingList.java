package com.example.jaebong.secerettalk;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by JaeBong on 15. 4. 17..
 */
public class CustomAdapter_chattingList extends ArrayAdapter<UserProfile> {
    LayoutInflater inflater;
    private Context context;
    private int layoutResourceId;
    private ArrayList<UserProfile> ProfileList;


    public CustomAdapter_chattingList(Context context, int layoutResourceId, ArrayList<UserProfile> ProfileList) {
        super(context, layoutResourceId, ProfileList);
        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.ProfileList = ProfileList;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }

//			ImageView userImg = (ImageView)row.findViewById(R.id.tile_imageView_left);
//
//			try{
//				InputStream is = context.getAssets().open(ProfileList.get(position).getImage_url());
//				Drawable d= Drawable.createFromStream(is,null);
//				userImg.setImageDrawable(d);
//			}catch(IOException e){
//				Log.e("ERROR", "ERROR:" + e);
//			}

        TextView userName = (TextView) row.findViewById(R.id.tile_textView_name_left);
        TextView userCharacter = (TextView) row.findViewById(R.id.tile_textView_character_left);

        userName.setText(ProfileList.get(position).getNickName());
        userCharacter.setText(ProfileList.get(position).getCharacter());

        return row;
    }


}