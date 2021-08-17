package com.example.clonewhatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.adapter.ChatMessageAdapter;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.Chat;
import com.example.clonewhatsapp.model.ChatMessage;
import com.example.clonewhatsapp.model.Group;
import com.example.clonewhatsapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
@RequiresApi(api = Build.VERSION_CODES.O)
public class ChatActivity extends AppCompatActivity {

    private DatabaseReference dbRef, msgRef;
    private StorageReference chatStorage;
    private ChildEventListener chatdEventListener;

    private TextView textFriendChatName, textFriendChatQuote;
    private CircleImageView friendCircleChatImage;
    private ImageView buttonSendImage;
    private TextInputEditText inputChatMessageText;
    private User selectedUser;
    private Group selectedGroupChat;

    private ChatMessageAdapter chatMessageAdapter;
    private RecyclerView chatRecyclerView;

    private String userId, refId;
    private boolean isGroup = false;

    private List<ChatMessage> chatMessages = new ArrayList<>();


    private static final int SELECT_CAMERA = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        userId = UserFirebase.getUserID();


        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        textFriendChatName = findViewById(R.id.textFriendChatName);
        textFriendChatQuote= findViewById(R.id.textFriendChatQuote);
        friendCircleChatImage= findViewById(R.id.friendCircleChatImage);

        inputChatMessageText = findViewById(R.id.inputChatMessageText);
        buttonSendImage= findViewById(R.id.buttonSendImage);

        Bundle bundle = getIntent().getExtras();

        String displayName, displayStatusQuote, displayImage;


        if( bundle!= null ){

            if(bundle.containsKey("selectedGroup")) {
                isGroup = true;
                selectedGroupChat = (Group) bundle.getSerializable("selectedGroup");
            }

            if(isGroup){

                displayName = selectedGroupChat.getGroupName();
                displayStatusQuote = getMembersNameForQuote();
                displayImage = selectedGroupChat.getGroupPhoto();
                refId = Base64Custom.encodeBase64(displayName);
            }
            else {
                selectedUser = (User) bundle.getSerializable("selectedUser");
                displayName = selectedUser.getName();
                displayStatusQuote = selectedUser.getEmail();
                displayImage = selectedUser.getPhoto();
                refId = Base64Custom.encodeBase64( selectedUser.getEmail() );
            }


            textFriendChatName.setText( displayName);
            textFriendChatQuote.setText( displayStatusQuote );



            if( displayImage!= null && !displayImage.isEmpty()){

                Uri imageUrl = Uri.parse(displayImage);
                Glide.with(ChatActivity.this).load(imageUrl).into(friendCircleChatImage);

            }else {

                if(isGroup){
                    friendCircleChatImage.setImageResource(R.drawable.icone_grupo);
                }else {
                    friendCircleChatImage.setImageResource(R.drawable.padrao);
                }
            }
        }

        dbRef = ConfigurateFirebase.getFireDBRef();

        msgRef = dbRef.child("chat")
                        .child("chatMessages")
                            .child(userId)
                                .child(refId);

        chatStorage = ConfigurateFirebase.getStorage();



      //  ################                   RECYCLER                          ###############    //


        chatMessageAdapter = new ChatMessageAdapter(chatMessages, getApplicationContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL, false);
        chatRecyclerView.setLayoutManager(layoutManager);
        chatRecyclerView.setHasFixedSize(true);
        chatRecyclerView.setAdapter(chatMessageAdapter);



      //  ################                  CLICKABLES                         ###############    //

        buttonSendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabSendMessage);
        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        returnChatMessages();
    }

    @Override
    protected void onStop() {
        super.onStop();
        msgRef.removeEventListener(chatdEventListener);
    }
    //  ################                     ##########                            ###############    //


    public void sendMessage(){
        String messageText  = inputChatMessageText.getText().toString();

        if(  !messageText.isEmpty() ){
            ChatMessage message = new ChatMessage(userId, messageText, refId);

            saveMessage(message);
        }
    }


    private void saveMessage(ChatMessage message){

        Chat chat = new Chat(message);
        User currentUser = UserFirebase.getLoggedUserData();

        DatabaseReference msgRef = dbRef.child("chat")
                                            .child("chatMessages");

        if(isGroup){
            List<User> members = selectedGroupChat.getMembers();

            for (User user : members){

                String userId = Base64Custom.encodeBase64(user.getEmail());

                message.setSenderName(currentUser.getName());
                msgRef.child(userId)
                        .child(refId)
                        .push()
                        .setValue(message);

                chat.saveGroupChat(user,selectedGroupChat);
            }

        }else {

            // saving for user

            msgRef.child(userId)
                    .child(refId)
                    .push()
                    .setValue(message);

            // saving for contact

            msgRef.child(refId)
                    .child(userId)
                    .push()
                    .setValue(message);

            chat.save(currentUser, selectedUser);
        }


        inputChatMessageText.setText("");


    }

    private void  returnChatMessages(){

        chatMessages.clear();

        chatdEventListener = msgRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                ChatMessage message = dataSnapshot.getValue(ChatMessage.class);
                chatMessages.add(message);
                chatMessageAdapter.notifyDataSetChanged();
                chatRecyclerView.scrollToPosition(chatMessages.size()-1);

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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

    public void openCamera(){

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, SELECT_CAMERA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap image = null;
            try {

                if (requestCode == SELECT_CAMERA) {
                    image = (Bitmap) data.getExtras().get("data");
                }

                if(image != null){

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] imageData = baos.toByteArray();

                    String imagename = UUID.randomUUID().toString();
                    StorageReference imageRef = chatStorage.child("images")
                                                                .child("pictures")
                                                                    .child(userId)
                                                                        .child(imagename+".jpeg"); //



                    UploadTask uploadTask = imageRef.putBytes(imageData);
                    uploadTask

                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    throwToast("Upload error:\n"+e.getMessage(), Toast.LENGTH_LONG);

                                }
                            })


                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @RequiresApi(api = Build.VERSION_CODES.O)
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                                     ChatMessage message = new ChatMessage();
                                     message.setUserID(userId);
                                     message.setMessage("");
                                     message.setImage(imageUrl);

                                    String sendID;
                                     if(isGroup){
                                         sendID = selectedGroupChat.getGroupId();
                                     }else {
                                         sendID = selectedUser.getUserID();
                                     }
                                     message.setSelectedUserID(sendID);
                                     saveMessage(message);
                                }
                            });
                }


            }catch (Exception e){
            e.printStackTrace();

        }

        }
    }

    public void throwToast( String message, int lenght){
        Toast.makeText(
                this,
                message,
                lenght).show();
    }

    private String getMembersNameForQuote(){
        String membersName = null;
        List<User> members = selectedGroupChat.getMembers();

        for (User user : members){
            if (membersName == null){
                membersName = user.getName();
            }else {
                membersName = membersName + ", " + user.getName();
            }

        }

        return membersName;
    }

}
