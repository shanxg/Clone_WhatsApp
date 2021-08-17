package com.example.clonewhatsapp.model;

import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

public class Chat implements Serializable{

    private String userID, lastMessage, selectedUserID, senderName;
    private boolean isGroup;
    private User selectedUser;
    private Group group;

    public Chat() {
        this.isGroup = false;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void setSelectedUser(User selectedUser){
        this.selectedUser = selectedUser;
    }

    public Chat(ChatMessage message) {
        this.userID = message.getUserID();
        this.lastMessage = message.getMessage();
        this.selectedUserID = message.getSelectedUserID();
        this.isGroup = false;
    }

    public boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean isGroup) {
        this.isGroup = isGroup;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    @Exclude
    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    @Exclude
    public String getSelectedUserID() {
        return selectedUserID;
    }

    public void setSelectedUserID(String selectedUserID) {
        this.selectedUserID = selectedUserID;
    }

    public String getSenderName() {        return senderName;    }

    public void setSenderName(String senderName) {        this.senderName = senderName;    }

    public void save(User currentUser, User selectedUser){

        DatabaseReference dbRef = ConfigurateFirebase.getFireDBRef();
        DatabaseReference chatRef = dbRef.child("chat").child("chatExhibit");

        this.setSenderName(UserFirebase.getLoggedUserData().getName());


        //SAVING FOR USER

        this.setSelectedUser(selectedUser);
        chatRef.child(this.userID)
                    .child(this.selectedUserID)
                        .setValue(this);

        //SAVING FOR SELECTED USER

        this.setSelectedUser(currentUser);
        chatRef.child(this.selectedUserID)
                    .child(this.userID)
                        .setValue(this);

    }

    public void saveGroupChat(User currentUser, Group selectedGroup){

        DatabaseReference dbRef = ConfigurateFirebase.getFireDBRef();
        DatabaseReference chatRef = dbRef.child("chat").child("chatExhibit");

        this.setIsGroup(true);

        Group group = new Group();
        group.setGroupName(selectedGroup.getGroupName());

        if(selectedGroup.getGroupPhoto()!=null){
            group.setGroupPhoto(selectedGroup.getGroupPhoto());
        }

        group.setMembers(selectedGroup.getMembers());
        group.setGroupId(selectedGroup.getGroupId());




        //SAVING FOR MEMBERS
        this.setSenderName(UserFirebase.getLoggedUserData().getName());
        this.setGroup(group);
        chatRef.child(currentUser.getUserID())
                    .child(selectedGroup.getGroupId())
                        .setValue(this);


    }


}
