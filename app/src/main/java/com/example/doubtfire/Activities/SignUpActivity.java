package com.example.doubtfire.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doubtfire.Models.UserModel;
import com.example.doubtfire.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    EditText usernameET,emailET,numberET,passwordET;
    Button signUpBtn;
    TextView loginIntent;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    DatabaseReference userReference;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(2,intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        init();
        fAuth = FirebaseAuth.getInstance();
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String name = usernameET.getText().toString().trim();
                final String phone = numberET.getText().toString().trim();
                final String email = emailET.getText().toString().trim();
                final String password = passwordET.getText().toString().trim();
                signUpBtn.setTag("verify");
                //form check
                {if(name.isEmpty())usernameET.setError("username required");
                else usernameET.setError(null);
                if(phone.isEmpty())numberET.setError("phone number required");
                else numberET.setError(null);
                if(password.isEmpty())passwordET.setError("password required");
                else passwordET.setError(null);
                if(email.isEmpty())emailET.setError("email required");
                else emailET.setError(null);}
                if(usernameET.getError()==null && numberET.getError()==null && emailET.getError()==null && passwordET.getError()==null)
              {
                    if(signUpBtn.getTag().toString().equals("verify"))
                    {
                        MaterialAlertDialogBuilder phoneDialog = new MaterialAlertDialogBuilder(SignUpActivity.this);
                        phoneDialog.setTitle("Verify Phone Number").setBackground(getDrawable(R.drawable.feed_back))
                                .setMessage(phone+"\n I'm gonna verify this for you so make sure you added the correct number")
                                .setCancelable(true)
                                .setNegativeButton("Change it", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        numberET.requestFocus();
                                    }
                                })
                                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(SignUpActivity.this,VerifyPhoneActivity.class);
                                        intent.putExtra("phone","+91 "+phone);
                                        startActivityForResult(intent,26);
                                    }
                                }).show();
                    }
                    else if(signUpBtn.getTag().toString().equals("sign"))
                        signupProcess(name,phone,email,password);
                }
            }
        });

        loginIntent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                finish();
            }
        });

    }

    private void init() {
        usernameET = findViewById(R.id.signup_name);
        emailET = findViewById(R.id.signup_email);
        numberET = findViewById(R.id.signup_number);
        passwordET = findViewById(R.id.signup_password);
        signUpBtn = findViewById(R.id.signup_btn);
        loginIntent = findViewById(R.id.login_page_btn);
        progressBar = findViewById(R.id.pgbar);
    }

    public void signupProcess(final String name, final String number, final String email, final String password)
    {
        progressBar.setVisibility(View.VISIBLE);
        fAuth.createUserWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                userReference = FirebaseDatabase.getInstance().getReference();
                FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                UserModel user = new UserModel(fbUser.getUid(),name,number);
                userReference.child("users").child(user.uid).setValue(user);
//                userReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").setValue(name);
//                userReference.child("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("number").setValue(number);
                Toast.makeText(SignUpActivity.this, "User created", Toast.LENGTH_SHORT).show();
                Intent loginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
                finish();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 26
        if(requestCode==26)
        {
            Toast.makeText(SignUpActivity.this,"Phone Number Verified",Toast.LENGTH_LONG).show();
            signUpBtn.setTag("sign");
        }
    }
}
