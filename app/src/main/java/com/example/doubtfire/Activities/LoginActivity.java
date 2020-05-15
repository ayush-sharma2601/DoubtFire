package com.example.doubtfire.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doubtfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    EditText emailET,passwordET;
    Button loginBtn,signIntent;
    ProgressBar progressBar;
    private FirebaseAuth fAuth;
    FirebaseUser user ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();
        if(user!=null){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String email = emailET.getText().toString().trim();
                final String password = passwordET.getText().toString().trim();

                if(email.isEmpty()) emailET.setError("Empty Field");
                else emailET.setError(null);

                if(password.isEmpty()) passwordET.setError("Password Required");
                else passwordET.setError(null);

                if(emailET.getError() == null && passwordET.getError() == null){
                    loginProcess(email,password);
                }

            }
        });
        signIntent .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signupintent = new Intent(LoginActivity.this,SignUpActivity.class);
                signupintent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(signupintent);
                finish();
            }
        });

    }

    private void init() {
        emailET = findViewById(R.id.login_email);
        passwordET = findViewById(R.id.login_password);
        loginBtn = findViewById(R.id.login_btn);
        signIntent = findViewById(R.id.signin_page_btn);
        progressBar = findViewById(R.id.pgbar);
    }
    public void loginProcess(String email, String password){
        progressBar.setVisibility(View.VISIBLE);
        fAuth.signInWithEmailAndPassword(email,password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this,"No corresponding user found. Click on Sign Up to create new account", Toast.LENGTH_LONG).show();
                    }
                });
    }
}
