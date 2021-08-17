package com.example.clonewhatsapp.model;

import com.google.firebase.database.Exclude;

public class ChatMessage {

    private String userID, message, image, selectedUserID, senderName;


    public ChatMessage() {
    }

    public ChatMessage(String userID, String message, String selectedUserID) {
        this.userID = userID;
        this.message = message;
        this.selectedUserID = selectedUserID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    @Exclude
    public String getSelectedUserID() {
        return selectedUserID;
    }

    public void setSelectedUserID(String selectedUserID) {
        this.selectedUserID = selectedUserID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


}
