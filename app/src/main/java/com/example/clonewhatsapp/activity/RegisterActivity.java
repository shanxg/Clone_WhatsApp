package com.example.clonewhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText inputUserName, inputUserEmail, inputUserPW;
    private Button buttonRegister;

    private FirebaseAuth authRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);



        inputUserEmail = findViewById(R.id.inputRegUserEmail);
        inputUserName = findViewById(R.id.inputRegUserName);
        inputUserPW = findViewById(R.id.inputRegUserPW);
        buttonRegister = findViewById(R.id.buttonRegister);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateText();
            }
        });

    }

    public void validateText(){

        String textName, textEmail, textPW;

        textName = inputUserName.getText().toString();
        textEmail = inputUserEmail.getText().toString();
        textPW = inputUserPW.getText().toString();

        if( !textName.isEmpty() ){
            if( !textEmail.isEmpty() ){
                if( !textPW.isEmpty() ){

                   User user = new User(textName, textEmail, textPW);

                    String userId = Base64Custom.encodeBase64(textEmail);

                    user.setUserID(userId);
                    user.save();

                    registerUser(user);




                }else {
                    throwToast( getResources().getText(R.string.reg_pw_input_text).toString(),Toast.LENGTH_LONG );
                }

            }else {

                throwToast( getResources().getText(R.string.reg_email_input_text).toString(),Toast.LENGTH_LONG );
            }

        }else {

            throwToast(getResources().getText(R.string.reg_name_input_text).toString(),Toast.LENGTH_LONG);
        }

    }

    public void registerUser(final User user){

        authRef = ConfigurateFirebase.getFirebaseAuth();

        authRef.createUserWithEmailAndPassword( user.getEmail(), user.getPw() )

                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    throwToast("Registration succesful.",Toast.LENGTH_SHORT);

                    UserFirebase.updateUserProfName(user.getName());
                    finish();
                    //goSettingsActicity();

                }else {

                    String message;

                    try {

                        throw new Exception();

                    }catch (FirebaseAuthWeakPasswordException e){
                        message = "Registration failed:.\n"+e.getMessage();

                    }catch (FirebaseAuthInvalidCredentialsException e){
                        message = "Auth Registration failed:.\n"+e.getMessage();

                    }catch (FirebaseAuthUserCollisionException e){
                        message = "Registration failed:.\n"+e.getMessage();

                    }catch (Exception e){
                        message = "Registration failed:.\n"+e.getMessage()+"\n";
                    }

                    throwToast(message,Toast.LENGTH_LONG);

                }

            }
        });


    }

    /*
    public void goSettingsActicity(){
        Intent intent = new Intent(this, SettingsActivity.class);
        finish();
        startActivity(intent);
    }
     */

    public void throwToast( String message, int lenght){

        Toast.makeText(this,
                        message,
                        lenght).show();

    }


}
