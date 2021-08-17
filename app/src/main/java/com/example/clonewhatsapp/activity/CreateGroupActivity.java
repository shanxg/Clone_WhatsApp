package com.example.clonewhatsapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.example.clonewhatsapp.adapter.SelectedContactsAdapater;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.Group;
import com.example.clonewhatsapp.model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CreateGroupActivity extends AppCompatActivity {

    private List<User> selectedMembersList = new ArrayList<>();
    private Group group;

    private CircleImageView profileGroupImage;
    private TextInputEditText inputGroupNameText;
    private TextView membersCount;
    private RecyclerView recyclerSelectedContacts;
    private SelectedContactsAdapater selectedContactsGroupAdapter;

    private StorageReference myStorage;
    private DatabaseReference dbRef, groupChatRef;

    private static final int SELECT_GALLERY = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("New Group");
        toolbar.setSubtitle("Define a name");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myStorage = ConfigurateFirebase.getStorage();
        group = new Group(ConfigurateFirebase.getFireDBRef());

        if (getIntent().getExtras() != null){
            selectedMembersList = (List<User>) getIntent().getExtras().getSerializable("selectedContactsList");
            selectedMembersList.add(UserFirebase.getLoggedUserData());
        }

        profileGroupImage = findViewById(R.id.profileGroupImage);
        inputGroupNameText = findViewById(R.id.inputGroupNameText);

        membersCount = findViewById(R.id.textSelectedContactsCount);
        membersCount.setText("Members: "+selectedMembersList.size());

        recyclerSelectedContacts = findViewById(R.id.recyclerCreateGroup);

        selectedContactsGroupAdapter = new SelectedContactsAdapater(selectedMembersList, this);
        RecyclerView.LayoutManager selecetedLayoutManager = new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false);
        recyclerSelectedContacts.setLayoutManager(selecetedLayoutManager);
        recyclerSelectedContacts.setHasFixedSize(true);
        recyclerSelectedContacts.setAdapter(selectedContactsGroupAdapter);



        profileGroupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fabCreateGroup);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                createGroup();
                Intent chatIntent = new Intent(CreateGroupActivity.this, ChatActivity.class);
                chatIntent.putExtra("selectedGroup", group);
                finish();
                startActivity(chatIntent);

            }
        });
    }

    public void createGroup(){

        String groupName = inputGroupNameText.getText().toString();

        group.setMembers(selectedMembersList);

        List<Integer> colorList = new ArrayList<>();

        for(int i = 0 ; i<= selectedMembersList.size();i++) {
           if(i>=1) {
               int lastUserColor = colorList.get(i-1);
               int color = generateColor();

               while (color == lastUserColor) {
                   color = generateColor();
               }

               colorList.add(color);

           }else {

               int color = generateColor();
               colorList.add(color);
           }
        }

        group.setMapColor(colorList);
        group.setGroupName(groupName);
        group.save();



    }

    private void openGallery(){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, SELECT_GALLERY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;

            try {
                switch (requestCode) {
                    case SELECT_GALLERY:
                        Uri selectedImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                        break;
                }

                if(image != null){

                    profileGroupImage.setImageBitmap( image );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 85, baos);
                    byte[] imageData = baos.toByteArray();



                    StorageReference myGroupImageRef =
                            myStorage.child("images")
                                    .child("groups")
                                    .child("profiles")
                                    .child( group.getGroupId()+".jpeg");

                    UploadTask uploadTask = myGroupImageRef.putBytes(imageData);

                    uploadTask
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    throwToast("Upload error:\n"+e.getMessage(), Toast.LENGTH_LONG);

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    String imageUrl = taskSnapshot.getDownloadUrl().toString();
                                    group.setGroupPhoto(imageUrl);

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



    public int generateColor(){
        Random random = new Random();
        int r,g,b;
        r = random.nextInt(255);
        if(r > 200){
            g = random.nextInt(r-100);
        }else {
            g = random.nextInt(r+100);
        }
        if(g < 100){
            b = random.nextInt(g+100);
        }else {
            b = random.nextInt(g - 100);
        }

        float red = (float) r ;
        float green = (float) g;
        float blue = (float) b;

        int color = Color.rgb(red, green, blue);



        return color;
    }

}
