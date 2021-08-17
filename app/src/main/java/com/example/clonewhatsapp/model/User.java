package com.example.clonewhatsapp.model;

import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class User implements Serializable {

    private String name, photo, email, pw, userID;
    //private int chatColor;


    public User(){

    }
    public User(String name, String email, String pw){
        this.name = name;
        this.email = email;
        this.pw = pw;
    }

    @Exclude
    public String getUserID() {

        userID = Base64Custom.encodeBase64(this.getEmail());

        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getPw() {
        return pw;
    }

    public void setPw(String pw) {
        this.pw = pw;
    }

    public String getPhoto() {return photo;}

    public void setPhoto(String photo) {this.photo = photo;}



    public void save(){

        DatabaseReference fireDB = ConfigurateFirebase.getFireDBRef();

        fireDB.child("users").child(this.getUserID()).setValue( this);

    }

    public void update(){

        DatabaseReference fireDB = ConfigurateFirebase.getFireDBRef();
        DatabaseReference userRef = fireDB.child("users").child(UserFirebase.getUserID());

        userRef.updateChildren(userMap());
    }

    @Exclude
    public Map <String, Object> userMap(){

        HashMap<String, Object> userMap = new HashMap<>();

        userMap.put("email", getEmail());
        userMap.put("name", getName());
        userMap.put("photo", getPhoto());

        return userMap;
    }

    /*
    public int getChatColor() {
        return chatColor;
    }

    public void setChatColor(int chatColor) {
        this.chatColor = chatColor;
    }
     */
}

