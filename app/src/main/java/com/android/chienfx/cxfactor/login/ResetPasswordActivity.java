package com.android.chienfx.cxfactor.login;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.chienfx.core.MyHelper;
import com.android.chienfx.cxfactor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edEmail;
    Button btnResetPassword, btnBack;
    ProgressBar progressBar;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edEmail = findViewById(R.id.edResetEmail);
        btnResetPassword = findViewById(R.id.btnResetPassword);
        btnBack = findViewById(R.id.btnBack);
        progressBar = findViewById(R.id.progressBarResetPassword);
        auth = FirebaseAuth.getInstance();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String strEmail = edEmail.getText().toString().trim();
                if(strEmail.isEmpty())
                {
                    MyHelper.toast(getApplicationContext(), "Enter your Email Address!");
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                auth.sendPasswordResetEmail(strEmail)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    MyHelper.toast(getApplicationContext(), "Failed to send reset email. Check your Email!");
                                }
                                else {
                                    MyHelper.toast(getApplicationContext(), "We have sent you instructions to reset password!");
                                }
                                progressBar.setVisibility(View.GONE);
                            }
                        });
            }
        });
    }
}
