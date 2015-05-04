package com.example.jaebong.secerettalk;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by JaeBong on 15. 4. 24..
 */
public class SecretTalkContract {
    public static final String AUTHORITY = "com.example.jaebong.secerettalk.SecretTalkProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final class Messages implements BaseColumns{
        public static final String _ID = "_id";
        public static final String TYPE = "type";
        public static final String IMAGE_URL = "imageUrl";
        public static final String ADDRESS = "address";
        public static final String SENDER = "sender";
        public static final String MESSAGE = "message";
        public static final String SEND_TIME = "sendTime";
        public static final String NICK_NAME = "nickName";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(
                SecretTalkContract.CONTENT_URI,Messages.class.getSimpleName()
        );

        public static final String[] PROJECTION_ALL = {_ID, TYPE,IMAGE_URL,ADDRESS,SENDER,MESSAGE,SEND_TIME,NICK_NAME};

        public static final String SORT_ORDER_DEFAULT = _ID ;
    }


}