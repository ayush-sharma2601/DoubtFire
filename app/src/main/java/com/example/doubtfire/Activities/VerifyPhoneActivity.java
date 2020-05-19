package com.example.doubtfire.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.doubtfire.R;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    EditText codeET;
    Button verifyBtn;
    private String code;
    private String verficationID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);

        codeET = findViewById(R.id.code);
        verifyBtn = findViewById(R.id.verify_btn);

        String phone = getIntent().getStringExtra("phone");

       sendVerificationCode(phone);

       verifyBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String userCode = codeET.getText().toString();
               if(userCode.isEmpty() || userCode.length()<6)codeET.setError("Invalid Code");
               else codeET.setError(null);

               if(codeET.getError()==null)
                   {
                       if(userCode.equals(code))
                       {
                           Intent intent = new Intent();
                           setResult(26,intent);
                           finish();
                       }
                   else
                       {
                           codeET.setError("Wrong Code");
                           codeET.requestFocus();
                       }
                   }
           }
       });
    }

    private void verifyCode(String code){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verficationID,code);
    }

    private void sendVerificationCode(String phone){
        //        OnVerificationStateChangedCallbacks
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);

    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
    mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verficationID = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            code = phoneAuthCredential.getSmsCode();
            if(code!=null)
                {
                    verifyCode(code);
                    codeET.setText(code);
                }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    };



}
