package com.example.clonewhatsapp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.example.clonewhatsapp.adapter.ContactsAdapter;
import com.example.clonewhatsapp.adapter.SelectedContactsAdapater;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.RecyclerItemClickListener;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.widget.AdapterView;

import com.example.clonewhatsapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class GroupActivity extends AppCompatActivity {

    private RecyclerView recyclerContactsGroup, recyclerSelectedContacts;
    private ContactsAdapter contactsGroupAdapter;
    private SelectedContactsAdapater selectedContactsGroupAdapter;

    private DatabaseReference usersRef;
    private ValueEventListener selectedGroupMembersListener;

    private List<User> contactsList = new ArrayList<>();
    private List<User> selectedContactsList = new ArrayList<>();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New Group");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerContactsGroup = findViewById(R.id.recyclerContactsGroup);
        recyclerSelectedContacts = findViewById(R.id.recyclerSelectedContacts);
        usersRef = ConfigurateFirebase.getFireDBRef().child("users");

        selectedContactsGroupAdapter = new SelectedContactsAdapater(selectedContactsList, this);
        RecyclerView.LayoutManager selecetedLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerSelectedContacts.setLayoutManager(selecetedLayoutManager);
        recyclerSelectedContacts.setHasFixedSize(true);
        recyclerSelectedContacts.setAdapter(selectedContactsGroupAdapter);

        contactsGroupAdapter = new ContactsAdapter(contactsList, this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerContactsGroup.setLayoutManager(layoutManager);
        recyclerContactsGroup.setHasFixedSize(true);
        recyclerContactsGroup.setAdapter(contactsGroupAdapter);



        FloatingActionButton fab = findViewById(R.id.fabGoCreateActivity);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupActivity.this, CreateGroupActivity.class);
                if(selectedContactsList != null) {
                    intent.putExtra("selectedContactsList", (Serializable) selectedContactsList);
                }
                startActivity(intent);
            }
        });

        recyclerSelectedContacts.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerSelectedContacts,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedUser = selectedContactsList.get(position);

                        selectedContactsList.remove(selectedUser);
                        selectedContactsGroupAdapter.notifyDataSetChanged();

                        contactsList.add(selectedUser);
                        contactsGroupAdapter.notifyDataSetChanged();
                        updateToolbarGroupMembers();


                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));

        recyclerContactsGroup.addOnItemTouchListener(new RecyclerItemClickListener(getApplicationContext(), recyclerContactsGroup,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        User selectedUser = contactsList.get(position);

                        contactsList.remove(selectedUser);
                        contactsGroupAdapter.notifyDataSetChanged();

                        selectedContactsList.add(selectedUser);
                        selectedContactsGroupAdapter.notifyDataSetChanged();
                        updateToolbarGroupMembers();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }));
    }


    @Override
    public void onStart() {
        super.onStart();
        returnContacts();
    }

    @Override
    public void onStop() {
        super.onStop();
        usersRef.removeEventListener(selectedGroupMembersListener);
    }

    public void returnContacts(){

        selectedGroupMembersListener = usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for ( DataSnapshot data: dataSnapshot.getChildren()){

                    User user = data.getValue(User.class);

                    String userId = Base64Custom.encodeBase64(user.getEmail());

                    if ( !userId.equals( UserFirebase.getUserID() )){
                        contactsList.add(user);
                    }
                }

                contactsGroupAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateToolbarGroupMembers(){

        int totalSelected = selectedContactsList.size();

        toolbar.setSubtitle(totalSelected + " selected members ");

    }

}
