package com.android.chienfx.cxfactor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.chienfx.core.*;
import com.android.chienfx.cxfactor.login.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity{

    public FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //checkRequestSMS(); do in sms package
        firebaseAuth = FirebaseAuth.getInstance();


    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isLogined()){
            login();
        }
        else
            MyHelper.toast(this, "Logined!");
        firebaseUser = firebaseAuth.getCurrentUser();
        //((TextView)findViewById(R.id.username)).setText(firebaseUser.getDisplayName());
    }

    private void checkRequestSMS() {

    }

    private void login() {
        Intent intentLogin = new Intent(this, Login.class);
        startActivityForResult(intentLogin, IntentCode.REQUEST_LOGIN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case IntentCode.REQUEST_LOGIN:
                if(resultCode == IntentCode.RESULT_LOGIN_SUCCESSFUL) {
                    if(data!=null)
                        MyHelper.toast(this, data.getStringExtra("AccessToken"));
                    else
                        MyHelper.toast(this, "login successful");
                    /*
                        Open next intent activity....
                     */
                }
                else{
                    MyHelper.toast(this, "Login Failed");
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean isLogined() {
        return this.firebaseAuth.getCurrentUser() != null;
    }


}
