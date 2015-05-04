package com.example.jaebong.secerettalk;

/**
 * Created by JaeBong on 15. 4. 15..
 */
public class UserProfile {
    public String id;
    public String nickName;
    public int age;
    public String gender;
    public String bloodType;
    public String imageUrl;
    public int chatLevel;
    public int gentle;
    public int cool;
    public int pervert;
    public int common;

    public UserProfile(){
        this.chatLevel = 0;
        this.gentle = 0;
        this.cool = 0;
        this.pervert = 0;
        this.common = 0;
    };

    public UserProfile(String id, String nickName, int age, String gender, String bloodType, String imageUrl) {
        this.id = id;
        this.nickName = nickName;
        this.age = age;
        this.gender = gender;
        this.bloodType = bloodType;
        this.imageUrl = imageUrl;
        this.chatLevel = 0;
        this.gentle = 0;
        this.cool = 0;
        this.pervert = 0;
        this.common = 0;
    }

    public UserProfile(String id, String nickName, int age, String gender, String bloodType, String imageUrl, int chatLevel, int gentle, int cool, int pervert, int common) {
        this.id = id;
        this.nickName = nickName;
        this.age = age;
        this.gender = gender;
        this.bloodType = bloodType;
        this.imageUrl = imageUrl;
        this.chatLevel = chatLevel;
        this.gentle = gentle;
        this.cool = cool;
        this.pervert = pervert;
        this.common = common;
    }

    public String getId() {        return id; }

    public String getNickName() {        return nickName; }

    public int getAge() {     return age;  }

    public String getGender() {    return gender; }

    public String getBloodType() {     return bloodType; }

    public String getImageUrl() {     return imageUrl; }

    public int getChatLevel() {     return chatLevel; }

    public int getGentle() {     return gentle;  }

    public int getCool() {     return cool; }

    public int getPervert() {     return pervert;}

    public int getCommon() {     return common;    }

    public void setId(String id) {
        this.id = id;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setChatLevel(int chatLevel) {
        this.chatLevel = chatLevel;
    }

    public void setGentle(int gentle) {
        this.gentle = gentle;
    }

    public void setCool(int cool) {
        this.cool = cool;
    }

    public void setPervert(int pervert) {
        this.pervert = pervert;
    }

    public void setCommon(int common) {
        this.common = common;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "id='" + id + '\'' +
                ", nickName='" + nickName + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", chatLevel=" + chatLevel +
                ", gentle=" + gentle +
                ", cool=" + cool +
                ", pervert=" + pervert +
                ", common=" + common +
                '}';
    }
}
