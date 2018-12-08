package com.android.chienfx.cxfactor.login;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.chienfx.core.IntentCode;
import com.android.chienfx.core.MyHelper;
import com.android.chienfx.cxfactor.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity{
    private static final String TAG = "GoogleActivity";
    TabAdapter tabAdapter;
    TabLayout tabLayout;
    ViewPager viewPager;

    LoginFragment fmLogin;
    SignupFragment fmSignUp;

    FirebaseAuth auth;
    GoogleSignInClient mGoogleSignInClient;
    private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        registerViews();
        createLoginAndSignTabs();
        setTabShowLoginFragment();
        //setTabShowSignUpFragment();

        initFirebaseEnviroment();

    }

    private void initFirebaseEnviroment() {

        initFirebaseGoogleSignin();

        initFirebaseFacebookSignin();



        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    private void initFirebaseFacebookSignin() {
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        mCallbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(mCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");
                        //MyHelper.toast(getApplicationContext(), "Facebook Login Success");
                        handleFacebookAccessToken(loginResult.getAccessToken());
                    }
                    @Override
                    public void onCancel() {
                        MyHelper.toast(getApplicationContext(), "Facebook Login Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        MyHelper.toast(getApplicationContext(), "Facebook Login Error: "+ exception.getMessage());
                    }
                });
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        fmLogin.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            finishLoginActivity(IntentCode.RESULT_LOGIN_SUCCESSFUL, "Logined in with facebook");

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            MyHelper.toast(getBaseContext(), "Authentication failed. Your email might existed.");
                        }
                    }
                });
    }

    private void initFirebaseGoogleSignin() {
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void registerViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPaper);
    }

    private void createLoginAndSignTabs() {
        tabAdapter = new TabAdapter(getSupportFragmentManager());

        fmLogin = new LoginFragment(this);
        fmSignUp = new SignupFragment(this);

        tabAdapter.addFragment(fmLogin, "Login");
        tabAdapter.addFragment(fmSignUp, "Sign Up");

        viewPager.setAdapter(tabAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    void setTabShowLoginFragment(){
        this.viewPager.setCurrentItem(0);
    }
    void setTabShowSignUpFragment(){
        this.viewPager.setCurrentItem(1);
    }


    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    void finishLoginActivity(int resultCode, Object extraData) {
        Intent resultLoginIntent = new Intent();
        //put login access tokent to intent
        switch (resultCode){
            case IntentCode.RESULT_LOGIN_SUCCESSFUL:
                if(extraData!=null)
                    resultLoginIntent.putExtra("AccessToken", (String)extraData);
                break;
        }

        setResult(resultCode, resultLoginIntent);
        finish(); //finish this activity with resultcode returned
    }

    public void createUserWithEmailAndPasswor(String strEmail, final String strPassword) {
        auth.createUserWithEmailAndPassword(strEmail, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        fmSignUp.progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {
                            // there was an error
                            Toast.makeText(getApplicationContext(), getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                        } else {
                            MyHelper.toast(getApplicationContext(), "Register done! Login again!");
                            fmSignUp.clearSignFields();
                            setTabShowLoginFragment();
                        }
                    }
                });
    }

    public void loginWithEmailAndPassword(String strUsername, String strPassword) {
        auth.signInWithEmailAndPassword(strUsername, strPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        fmLogin.progressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful()){
                            MyHelper.toast(getApplicationContext(), "Login failed. Check your email and password!");
                        }
                        else
                        {
                            finishLoginActivity(IntentCode.RESULT_LOGIN_SUCCESSFUL, "Login successful!");
                        }
                    }
                });
    }

    public void loginWithGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, IntentCode.REQUEST_LOGIN_WITH_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == IntentCode.REQUEST_LOGIN_WITH_GOOGLE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);

            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                MyHelper.toast(getApplicationContext(), "Google login failed");
            }
        }
    }

    // [START auth_with_google]
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        fmLogin.progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            finishLoginActivity(IntentCode.RESULT_LOGIN_SUCCESSFUL, "Logined with Google!");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            MyHelper.toast(getBaseContext(), "Firebase Google Account Authentication failed. Your email might existed.");
                        }
                    }
                });
    }
    // [END auth_with_google]
}
