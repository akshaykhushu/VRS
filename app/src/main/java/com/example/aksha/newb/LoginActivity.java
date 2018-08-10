package com.example.aksha.newb;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class LoginActivity extends AppCompatActivity {

    Button signInButton;
    EditText email;
    EditText password;
    TextView register;
    private FirebaseAuth firebaseAuth;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        signInButton = findViewById(R.id.buttonSignIn);
        email = findViewById(R.id.editTextEmail);
        password = findViewById(R.id.editTextPassword);
        register = findViewById(R.id.textViewRegister);
        firebaseAuth = FirebaseAuth.getInstance();

        if (firebaseAuth.getCurrentUser() != null){
            finish();
            Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
            userId =  firebaseAuth.getCurrentUser().getUid();
            intent.putExtra("UserId", userId);
            startActivity(intent);
        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

    }

    public void signIn(){
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

        firebaseAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Log In Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                    userId =  firebaseAuth.getCurrentUser().getUid();
                    intent.putExtra("UserId", userId);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Could Not Sign In. Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
