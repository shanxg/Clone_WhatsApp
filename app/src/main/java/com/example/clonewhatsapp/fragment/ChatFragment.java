package com.example.clonewhatsapp.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.activity.ChatActivity;
import com.example.clonewhatsapp.adapter.ChatAdapter;
import com.example.clonewhatsapp.adapter.ContactsAdapter;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.RecyclerItemClickListener;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.Chat;
import com.example.clonewhatsapp.model.Group;
import com.example.clonewhatsapp.model.User;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatFragment extends Fragment {


    private List<Chat> contactsChatList = new ArrayList<>();
    private RecyclerView recyclerContactsChat;
    private ChatAdapter chatAdapter;

    private DatabaseReference dbRef, chatRef;
    private ChildEventListener chatChildEvenListener;

    private Chat selectedChat;
    private Group selectedGroupChat;
    private User selectedUser;


    public ChatFragment() {
        // Required empty public constructor
    }

    public void setRecyclerContactsChat(boolean onCreate){

        if(recyclerAddress.getAdapter()==null) {
            chatAdapter = new ChatAdapter(contactsChatList, getActivity());

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerContactsChat.setLayoutManager(layoutManager);
            recyclerContactsChat.setHasFixedSize(true);
            recyclerContactsChat.setAdapter(chatAdapter);
        }

        if(!onCreate){
            chatAdapter.notifyDataSetChanged();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        recyclerContactsChat = view.findViewById(R.id.recyclerContactsChat);


        String userID = UserFirebase.getUserID();

        dbRef = ConfigurateFirebase.getFireDBRef();
        chatRef = dbRef.child("chat").child("chatExhibit").child(userID);


        setRecyclerContactsChat(true);

        recyclerContactsChat.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerContactsChat,
                        new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                Intent intentChat = new Intent(getActivity(), ChatActivity.class);

                List<Chat> updatedChatListToUse = chatAdapter.getContactsChat();
                selectedChat = updatedChatListToUse.get(position);

                if(selectedChat.getIsGroup()){
                    selectedGroupChat = selectedChat.getGroup();
                    intentChat.putExtra("selectedGroup", selectedGroupChat);

                } else {
                    selectedUser = selectedChat.getSelectedUser();
                    intentChat.putExtra("selectedUser", selectedUser);
                }
                startActivity(intentChat);

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }) {
        });


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        returnContactsChat();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatRef.removeEventListener(chatChildEvenListener);
    }


    public void reloadChat(){

        chatAdapter = new ChatAdapter(contactsChatList, getActivity());
        recyclerContactsChat.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

    }


    public void queryMessageText(String queryText){

        List<Chat> queryChatList = new ArrayList<>();



        for (Chat chat: contactsChatList){
            String name;
            String lastMessage = chat.getLastMessage().toLowerCase();

            if(chat.getIsGroup()){ name = chat.getGroup().getGroupName().toLowerCase(); }
            else {                 name = chat.getSelectedUser().getName().toLowerCase();}

            if (name.contains(queryText) || lastMessage.contains(queryText)) {
                queryChatList.add(chat);
            }
        }

        chatAdapter = new ChatAdapter(queryChatList, getActivity());
        recyclerContactsChat.setAdapter(chatAdapter);
        chatAdapter.notifyDataSetChanged();

    }

    private void returnContactsChat(){

        contactsChatList.clear();

      chatChildEvenListener =

              chatRef.addChildEventListener(new ChildEventListener() {
                  @Override
                  public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                          Chat chat = dataSnapshot.getValue(Chat.class);
                          contactsChatList.add(chat);
                          chatAdapter.notifyDataSetChanged();

                  }
                  @Override
                  public void onChildChanged(DataSnapshot childSnapshot, String s) {

                      Chat chat = childSnapshot.getValue(Chat.class);
                      changeChatData(chat);
                      chatAdapter.notifyDataSetChanged();
                  }
                  @Override
                  public void onChildRemoved(DataSnapshot dataSnapshot) {

                  }
                  @Override
                  public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                  }
                  @Override
                  public void onCancelled(DatabaseError databaseError) {

                  }
              });



    }


    private void changeChatData(Chat chat){

        for( Chat chatToCheck : contactsChatList ){

            if(chat.getIsGroup()){
                String selectedGroupID = chat.getGroup().getGroupId();

                if(chatToCheck.getIsGroup()) {
                    String compareID = chatToCheck.getGroup().getGroupId();

                    if (selectedGroupID.equals(compareID)) {

                        String lastMessage = chat.getLastMessage();
                        String compareLastMessage = chatToCheck.getLastMessage();

                        if (lastMessage != null && compareLastMessage != null) {

                                int position = contactsChatList.indexOf(chatToCheck);
                                contactsChatList.set(position, chat);
                        }
                    }
                }

            }else {

                if(chatToCheck.getSelectedUser()!=null) {
                    String selectedUserID = chat.getSelectedUser().getUserID();

                    User compareUser = chatToCheck.getSelectedUser();
                    String compareID = compareUser.getUserID();

                    if (selectedUserID.equals(compareID)) {

                        String lastMessage = chat.getLastMessage();
                        String compareLastMessage = chatToCheck.getLastMessage();

                        if (lastMessage != null && compareLastMessage != null) {

                            int position = contactsChatList.indexOf(chatToCheck);
                            contactsChatList.set(position, chat);
                        }
                    }
                }
            }
        }
    }
}