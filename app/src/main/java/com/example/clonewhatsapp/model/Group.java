package com.example.clonewhatsapp.model;

import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Group implements Serializable {

    private String groupId, groupName, groupPhoto;
    private List<User> members;
    private List<Integer> mapColor;


    public Group(){

    }



    public Group(DatabaseReference dbRef) {
        DatabaseReference groupRef = dbRef.child("groups");

        String firebaseGroupId = groupRef.push().getKey();
        this.setGroupId(firebaseGroupId);

    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupPhoto() {
        return groupPhoto;
    }

    public void setGroupPhoto(String groupPhoto) {
        this.groupPhoto = groupPhoto;
    }

    public List<User> getMembers() {
        return members;
    }

    public void setMembers(List<User> members) {
        this.members = members;
    }

    public void save(){

        DatabaseReference dbRef = ConfigurateFirebase.getFireDBRef();
        DatabaseReference groupRef = dbRef.child("groups");

        groupRef.child(this.getGroupId()).setValue(this);

        for(User member: getMembers()){

            Chat groupChat = new Chat();
            groupChat.setSelectedUserID(this.getGroupId());
            groupChat.setLastMessage("Group created");
            groupChat.setIsGroup(true);
            groupChat.setGroup(this);

            groupChat.saveGroupChat(member,this);

        }

    }

    public List<Integer> getMapColor() {
        return mapColor;
    }

    public void setMapColor(List<Integer> mapColor) {
        this.mapColor = mapColor;
    }
}
