package com.example.clonewhatsapp.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.config.ConfigurateFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText inputLoginID, inputLoginPW;
    private Button buttonLogin;

    private FirebaseAuth userAuth = ConfigurateFirebase.getFirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ( checkUserState()) {

        }
        else {
            setContentView(R.layout.activity_login);
            inputLoginID = findViewById(R.id.inputLoginIDText);
            inputLoginPW = findViewById(R.id.inputLoginPWText);
            buttonLogin = findViewById(R.id.buttonLogin);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        checkUserState();
    }

    private boolean checkUserState(){


        if(userAuth.getCurrentUser() != null){
            openMainActivity();
            return true;

        }else {
            return false;
        }

    }

    public void validateText(View view){

        String textEmail, textPW;
        textEmail = inputLoginID.getText().toString();
        textPW = inputLoginPW.getText().toString();

        if( !textEmail.isEmpty() ){
            if( !textPW.isEmpty() ){

                authenticateUser(textEmail, textPW);

            }else {
                throwToast( getResources().getText(R.string.reg_pw_input_text).toString(),Toast.LENGTH_LONG );
            }
        }else {
            throwToast( getResources().getText(R.string.reg_email_input_text).toString(),Toast.LENGTH_LONG );
        }

    }

    public void authenticateUser(String id, String pw){

       userAuth.signInWithEmailAndPassword(id, pw)

               .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                   @Override
                   public void onComplete(@NonNull Task<AuthResult> task) {
                       if(task.isSuccessful()){

                           throwToast("Login successful.",Toast.LENGTH_SHORT);
                           openMainActivity();

                       }else {

                           String message;
                           try {
                               throw task.getException();

                           }catch (FirebaseAuthInvalidUserException e){
                               message = "Login failure:.\n"+e.getMessage();

                           }catch (FirebaseAuthInvalidCredentialsException e){
                               message = "Login failure:.\n"+e.getMessage();

                           }catch (Exception e){
                               message = "Login failure:.\n"+e.getMessage();
                           }

                           throwToast(message, Toast.LENGTH_LONG);
                       }

                   }
               });

    }

    // ############                  HELPERS                  ############ //

    public void openRegisterActivity(View view){

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void openMainActivity(){

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);


    }

    public void throwToast( String message, int lenght){

        switch (lenght) {
            case Toast.LENGTH_LONG:

                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_LONG).show();
                break;

            case Toast.LENGTH_SHORT:

                Toast.makeText(
                        this,
                        message,
                        Toast.LENGTH_SHORT).show();

                break;
        }
    }
}
