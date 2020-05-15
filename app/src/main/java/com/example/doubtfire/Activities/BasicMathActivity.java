package com.example.doubtfire.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.doubtfire.R;
import com.example.doubtfire.RetrofitClient;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BasicMathActivity extends AppCompatActivity {
    EditText mathET;
    Button calcBtn;
    TextView answer;
    String expression,finalExpr,expr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic_math);
        init();
        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expression = mathET.getText().toString().trim();
                if(expression.isEmpty())mathET.setError("No expression found");
                else mathET.setError(null);

                if (mathET.getError()==null)
                {
                    try {
                        finalExpr = URLEncoder.encode(expression,"UTF-8").trim().toUpperCase().toString();
//                        expr = finalExpr.
                        answer.setText(finalExpr);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if(!finalExpr.isEmpty())
                    {
                        Call<String> call = RetrofitClient.getClient().getResult(finalExpr);
                        call.enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(Call<String> call, Response<String> response) {

                                if(!response.isSuccessful())
                                {
                                    Toast.makeText(BasicMathActivity.this,"Code: "+response.code(),Toast.LENGTH_LONG).show();
                                    return;
                                }
                                String answerFromApi = response.body();
                                answer.setText("Solution: "+answerFromApi);

                            }
                            @Override
                            public void onFailure(Call<String> call, Throwable t) {
                                Toast.makeText(BasicMathActivity.this,t.getMessage(),Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                    else {
                        Toast.makeText(BasicMathActivity.this,"Problem in encoding expression",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });

    }

    private void init() {
        mathET = findViewById(R.id.math_expression);
        calcBtn = findViewById(R.id.calculate);
        answer = findViewById(R.id.solution);
    }
}
