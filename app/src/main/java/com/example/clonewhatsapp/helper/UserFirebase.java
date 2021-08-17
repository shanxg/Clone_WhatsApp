package com.example.clonewhatsapp.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.model.Group;
import com.example.clonewhatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserFirebase {


    public static String getUserID(){

        FirebaseAuth myAuth = ConfigurateFirebase.getFirebaseAuth();

        return Base64Custom.encodeBase64(myAuth.getCurrentUser().getEmail());
    }

    public  static FirebaseUser getMyUser(){

        FirebaseAuth myAuth = ConfigurateFirebase.getFirebaseAuth();

        return myAuth.getCurrentUser();
    }

    public static boolean updateUserProfImage(Uri profileImage){

        try {
            FirebaseUser user = getMyUser();

            UserProfileChangeRequest userChangeRequest =
                    new UserProfileChangeRequest.Builder()
                            .setPhotoUri(profileImage)
                            .build();


            user.updateProfile(userChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (!task.isSuccessful()){
                        Log.d("UserProfile","erro ao atualizar a imagem de perfil");
                    }
                }
            });
            return true;

        }catch (Exception e){

            e.printStackTrace();
            return false;

        }
    }

    public static boolean updateUserProfName(String profileName){

        try {
            FirebaseUser user = getMyUser();

            UserProfileChangeRequest userChangeRequest =
                    new UserProfileChangeRequest.Builder()
                            .setDisplayName(profileName)
                            .build();


            user.updateProfile(userChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (!task.isSuccessful()){
                        Log.d("UserProfile","erro ao atualizar o nome de perfil");
                    }
                }
            });
            return true;

        }catch (Exception e){

            e.printStackTrace();
            return false;

        }
    }

    public static User getLoggedUserData(){

        FirebaseUser userFB = getMyUser();

        User userData  = new User();
        userData.setEmail(userFB.getEmail());
        userData.setName(userFB.getDisplayName());

        if(userFB.getPhotoUrl() == null){
            userData.setPhoto("");
        }
        else {
            userData.setPhoto(userFB.getPhotoUrl().toString());
        }
        return userData;
    }

    public static Group getCurrentGroup(){



        return null;
    }


}
