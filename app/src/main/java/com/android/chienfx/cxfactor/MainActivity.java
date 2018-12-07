package com.android.chienfx.cxfactor;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.chienfx.core.*;
import com.android.chienfx.core.services.SMSReceiveService;
import com.android.chienfx.core.user.User;
import com.android.chienfx.cxfactor.login.*;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity{

    public FirebaseAuth mAuth;
    public FirebaseUser firebaseUser;
    public User mUser = User.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handleViewClickEvents();

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAllPermissions();
    }


    private void checkAllPermissions() {
        if(!MyHelper.hasPermissions(this, Definition.PERMISSIONS)) {
            showRequestPermissionsInfoAlertDialog();
        }
    }
    private void showRequestPermissionsInfoAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.permission_alert_dialog_title);
        builder.setMessage(R.string.permission_dialog_message);

        builder.setPositiveButton(R.string.action_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                requestMyPermissions();
            }
        });
        builder.show();
    }

    private void requestMyPermissions() {
        if (
                ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS) &&
                        ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)
                )
            return;
        ActivityCompat.requestPermissions(this, Definition.PERMISSIONS, Definition.REQUEST_PERMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case Definition.REQUEST_PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    MyHelper.toast(this, "Permissions Granted!");
                    startServices();
                } else
                {
                    MyHelper.toast(this, "Permissions Denied!");
                }
            }
        }

    }

    private void startServices() {
        Intent receiverService = new Intent(MainActivity.this, SMSReceiveService.class);
        MainActivity.this.startService(receiverService);
        MyHelper.toast(getApplicationContext(), "Receiver SMS servive started");
    }


    private void handleViewClickEvents() {
        findViewById(R.id.btnLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOut();}
        });
    }

    private void logOut() {
        if(isLogined())
        {
            mAuth.signOut();
            login();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isLogined()){
            login();
        }
        else
            MyHelper.toast(this, "Logined!");

        checkAllPermissions();
        firebaseUser = mAuth.getCurrentUser();
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
                    else {
                        MyHelper.toast(this, "login successful");
                        updateUI();
                    }
                }
                else{
                    MyHelper.toast(this, "Login Failed");
                }
                break;

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void updateUI() {
        firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser!=null){
            ((TextView)findViewById(R.id.username)).setText(firebaseUser.getDisplayName());
        }
        else{
            MyHelper.toast(getApplicationContext(), "User invalid!");
        }


    }

    private boolean isLogined() {
        return this.mAuth.getCurrentUser() != null;
    }


}
