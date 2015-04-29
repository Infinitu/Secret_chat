package com.example.jaebong.secerettalk;

/**
 * Created by JaeBong on 15. 4. 15..
 */
public class UserProfile {
    public String nickName;
    public String birthYear;
    public String gender;
    public String bloodType;
    public String character;
    public String image_url;

    public UserProfile( String nickName, String birthYear, String gender, String bloodType, String charater, String image_url) {
        this.nickName = nickName;
        this.birthYear = birthYear;
        this.gender = gender;
        this.bloodType = bloodType;
        this.character = charater;
        this.image_url = image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getImage_url() {

        return image_url;
    }

    public UserProfile() {

    }


    public void setCharacter(String charater) {
        this.character = charater;
    }

    public String getCharacter() {

        return character;
    }

    public String getNickName() {
        return nickName;
    }

    public String getBirthYear() {
        return birthYear;
    }

    public String getGender() {
        return gender;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setBirthYear(String birthYear) {
        this.birthYear = birthYear;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }
}
