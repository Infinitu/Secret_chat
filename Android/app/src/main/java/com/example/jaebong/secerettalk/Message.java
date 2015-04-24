package com.example.jaebong.secerettalk;

/**
 * Created by JaeBong on 15. 4. 20..
 */
public class Message {
    private String type;
    private String imageUrl;
    private String adress;
    private String sender;
    private String message;
    private long sendTime;

    public Message() {
        this.imageUrl = "";
        this.message = "";
    }

    public Message(String type, String imageUrl, String adress, String sender, String message, long sendTime) {
        this.type = type;
        this.imageUrl = imageUrl;
        this.adress = adress;
        this.sender = sender;
        this.message = message;
        this.sendTime = sendTime;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public String getAdress() {
        return adress;
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
