package com.example.jaebong.secerettalk.chatting;

/**
 * Created by JaeBong on 15. 4. 20..
 */
public class MessageDTO {
    private static int _id;
    private String type;
    private String imageUrl;
    private String address;
    private String sender;
    private String message;
    private long sendTime;
    private String nickName;

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {

        return nickName;
    }

    public MessageDTO() {
        this._id = 0;

        this.imageUrl = "";
        this.message = "";
    }

    public int get_id() {
        return _id;
    }

    public MessageDTO(int _id, String type, String imageUrl, String address, String sender, String message, long sendTime, String nickName) {
        this._id = _id;
        this.type = type;
        this.imageUrl = imageUrl;
        this.address = address;
        this.sender = sender;
        this.message = message;
        this.sendTime = sendTime;
        this.nickName = nickName;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public void setSendTime(long sendTime) {
        this.sendTime = sendTime;
    }

    public String getType() {
        return type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getAddress() {
        return address;
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getSendTime() {
        return sendTime;
    }
}