package com.example.clonewhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.SystemPermissions;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private int vis = 1;

    private User loggedUser;

    private ImageView imageResCam, imageResGallery, buttonConfirmName;
    private CircleImageView profileCircleImage;
    private EditText editUserName;


    private StorageReference myStorage;
    private FirebaseAuth myAuth;

    private static final int SELECT_CAMERA = 100;
    private static final int SELECT_GALLERY = 200;

    private String[] needPermissions = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        myStorage = ConfigurateFirebase.getStorage();
        loggedUser = UserFirebase.getLoggedUserData();

        editUserName =findViewById(R.id.editName);
        buttonConfirmName = findViewById(R.id.buttonEditName);



        imageResCam = findViewById(R.id.resourceCam);
        imageResGallery = findViewById(R.id.resourceGalerry);


        imageResCam = findViewById(R.id.resourceCam);
        imageResGallery = findViewById(R.id.resourceGalerry);
        profileCircleImage = findViewById(R.id.profileCircleImage);

        SystemPermissions.validatePermissions(needPermissions, this, 1);

        Toolbar toolbar = findViewById(R.id.toolbarMain);
        toolbar.setTitle("Settings");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseUser myUser = UserFirebase.getMyUser();
        Uri profImage = myUser.getPhotoUrl();

        if (profImage!=null){

            Glide.with(SettingsActivity.this)
                    .load(profImage)
                    .into(profileCircleImage);

        }else {
            profileCircleImage.setImageResource(R.drawable.padrao);
        }



        editUserName.setText(myUser.getDisplayName());
        buttonConfirmName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirmName();


            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK){
            Bitmap image = null;

            try {

                switch (requestCode){
                    case SELECT_CAMERA:
                        image = (Bitmap) data.getExtras().get("data");

                        break;
                    case SELECT_GALLERY:
                        Uri selectedImage = data.getData();
                        image = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);

                        break;

                }


                if(image != null){

                    profileCircleImage.setImageBitmap( image );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] imageData = baos.toByteArray();



                    StorageReference myImageRef =
                            myStorage.child("images")
                                        .child("profile")
                                            .child( UserFirebase.getUserID() + ".jpeg");

                    UploadTask uploadTask = myImageRef.putBytes(imageData);

                    uploadTask
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    throwToast("Upload error:\n"+e.getMessage(),Toast.LENGTH_LONG);

                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    Uri imageUrl = taskSnapshot.getDownloadUrl();
                                    updateProfileImage(imageUrl);
                                }
                            });
                }

            }catch (Exception e){
                e.printStackTrace();

            }
        }
    }

    public void updateProfileImage(Uri imageUrl){

        if ( UserFirebase.updateUserProfImage(imageUrl) )
        {
            throwToast("Upload succesful:\n Profile image changed", Toast.LENGTH_SHORT);
            loggedUser.setPhoto(imageUrl.toString());
            loggedUser.update();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissionResult: grantResults){
            if (permissionResult == PackageManager.PERMISSION_DENIED) {
                permisionValidationAlert();
            }
        }

    }

    private void permisionValidationAlert(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissions denied:");
        builder.setMessage("To keep using the App, you need to accept the Requested permissions.");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    //

    public void openGallery(View view){

        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, SELECT_GALLERY);
        }
    }

    public void openCamera(View view){

        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(i.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(i, SELECT_CAMERA);
        }
    }

    public void openResources(View view){

        if (vis == 1) {
            imageResGallery.setVisibility(View.VISIBLE);
            imageResCam.setVisibility(View.VISIBLE);
            vis = 0;
        } else {
            imageResGallery.setVisibility(View.INVISIBLE);
            imageResCam.setVisibility(View.INVISIBLE);
            vis = 1;
        }
    }

    public void throwToast( String message, int lenght){
        Toast.makeText(
                this,
                message,
                lenght).show();

    }


    public void confirmName(){

        String userName = editUserName.getText().toString();

        boolean result = UserFirebase.updateUserProfName(userName);
        if (result) {
            loggedUser.setName(userName);
            loggedUser.update();
            throwToast("Name updated succesful.", Toast.LENGTH_SHORT);
        }

    }




}
