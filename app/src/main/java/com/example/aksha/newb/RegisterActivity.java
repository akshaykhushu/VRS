package com.example.aksha.newb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    EditText email;
    EditText password;
    Button signUp;
    private FirebaseAuth firebaseAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email = findViewById(R.id.editTextRegisterEmail);
        password = findViewById(R.id.editTextRegisterPassword);
        signUp = findViewById(R.id.buttonSignUp);
        firebaseAuth = FirebaseAuth.getInstance();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }


    public void registerUser(){
        String emailStr = email.getText().toString().trim();
        String passwordStr = password.getText().toString().trim();

        if(TextUtils.isEmpty(emailStr)){
            Toast toast = Toast.makeText(this, "Please Enter Email ID", Toast.LENGTH_SHORT);
            toast.show();

        }

        if(TextUtils.isEmpty(passwordStr)){
            Toast toast = Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT);
            toast.show();

        }

        firebaseAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Registered Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    userId =  firebaseAuth.getCurrentUser().getUid();
                    intent.putExtra("UserId", userId);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Could Not Register. Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
