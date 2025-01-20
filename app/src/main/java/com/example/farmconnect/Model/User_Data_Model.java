package com.example.farmconnect.Model;

import java.util.ArrayList;

public class User_Data_Model {
    String uid,type,phone,name,image,email,personalname,state;
    ArrayList<String> location;

    public User_Data_Model(String uid, String type, String phone, String name, String image, String email, ArrayList<String> location,String personalname,String state) {
        this.uid = uid;
        this.type = type;
        this.phone = phone;
        this.name = name;
        this.image = image;
        this.email = email;
        this.location = location;
        this.personalname = personalname;
        this.state = state;
    }

    public User_Data_Model() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ArrayList<String> getLocation() {
        return location;
    }

    public void setLocation(ArrayList<String> location) {
        this.location = location;
    }

    public String getPersonalname() {
        return personalname;
    }

    public void setPersonalname(String personalname) {
        this.personalname = personalname;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
