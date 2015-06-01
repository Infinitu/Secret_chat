package com.example.jaebong.secerettalk.add_friend;

import android.content.Context;

/**
 * Created by JaeBong on 15. 5. 11..
 */
public class AddFriendController {
    Context context;

    private AddFriendProxy addFriendProxy;
    private AddFriendDao addFriendDao;

    public AddFriendController(Context context){
        this.context = context;

        addFriendProxy = new AddFriendProxy(context);
        addFriendDao = new AddFriendDao(context);
    }


}
