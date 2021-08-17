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
import com.example.clonewhatsapp.activity.GroupActivity;
import com.example.clonewhatsapp.adapter.ChatAdapter;
import com.example.clonewhatsapp.adapter.ContactsAdapter;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.RecyclerItemClickListener;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.Chat;
import com.example.clonewhatsapp.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContactsFragment extends Fragment {



    private RecyclerView recyclerViewContacts;
    private ContactsAdapter adapterContacts;


    private ArrayList<User> contactsList = new ArrayList<>();

    private DatabaseReference usersRef;

    private ValueEventListener valueEventListenerContacts;



    public ContactsFragment() {
        // Required empty public constructor
    }

    public void setRecyclerViewContacts(boolean onCreate){

        adapterContacts = new ContactsAdapter(contactsList, getActivity());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewContacts.setLayoutManager(layoutManager);
        recyclerViewContacts.setHasFixedSize(true);
        recyclerViewContacts.setAdapter(adapterContacts);

        if(!onCreate){
            adapterContacts.notifyDataSetChanged();
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
         View view  = inflater.inflate(R.layout.fragment_contacts, container, false);

        usersRef = ConfigurateFirebase.getFireDBRef().child("users");

        recyclerViewContacts = view.findViewById(R.id.contactsRecyclerView);

        setRecyclerViewContacts(true);

        recyclerViewContacts.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewContacts, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                List<User> updatedContactsList =  adapterContacts.getContacts();
                User selectedUser = updatedContactsList.get(position);

                if(selectedUser.getEmail()==null){
                    Intent intentGroup = new Intent(getActivity(), GroupActivity.class);
                    startActivity(intentGroup);

                }else {
                    Intent intentChat = new Intent(getActivity(), ChatActivity.class);
                    intentChat.putExtra("selectedUser", selectedUser);
                    startActivity(intentChat);
                }
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

    public void returnContacts(){

        valueEventListenerContacts = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                contactsList.clear();

                User itemGroupChat = new User();
                itemGroupChat.setName("New Group");
                itemGroupChat.setEmail(null);

                contactsList.add(itemGroupChat);

                for ( DataSnapshot data: dataSnapshot.getChildren()){

                    User user = data.getValue(User.class);

                    String userId = Base64Custom.encodeBase64(user.getEmail());

                    if ( !userId.equals( UserFirebase.getUserID() )){
                        contactsList.add(user);
                    }
                }

                adapterContacts.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        returnContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(valueEventListenerContacts);
    }

    public void queryUser(String queryText){

        List<User> queryUserList = new ArrayList<>();

        for (User user : contactsList){

            String name = user.getName().toLowerCase();

            if (name.contains(queryText)) {
                queryUserList.add(user);
            }
        }

        adapterContacts = new ContactsAdapter(queryUserList, getActivity());
        recyclerViewContacts.setAdapter(adapterContacts);
        adapterContacts.notifyDataSetChanged();

    }
}
