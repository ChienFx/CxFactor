package com.android.chienfx.cxfactor.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.chienfx.cxfactor.core.Definition;
import com.android.chienfx.cxfactor.core.IntentCode;
import com.android.chienfx.cxfactor.core.helper.MyHelper;
import com.android.chienfx.cxfactor.core.services.SMSReceiveService;
import com.android.chienfx.cxfactor.core.user.User;
import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.fragments.HomeFragment;
import com.android.chienfx.cxfactor.fragments.history.NotificationsFragment;
import com.android.chienfx.cxfactor.fragments.WhereIWasFragment;
import com.android.chienfx.cxfactor.fragments.SettingsFragment;
import com.android.chienfx.cxfactor.login.Login;
import com.android.chienfx.cxfactor.others.CircleTransform;
import com.android.chienfx.cxfactor.others.GpsService;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private static final MainActivity ourInstance = new MainActivity();
    public static MainActivity getInstance() {
        return ourInstance;
    }

    public FirebaseAuth mAuth;
    public FirebaseUser firebaseUser;

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgNavHeaderBg, imgProfile;
    private TextView txtName, txtWebsite;
    private Toolbar toolbar;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments

    public static String CURRENT_TAG = Definition.TAG_HOME;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setContentView(com.android.chienfx.cxfactor.R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

        registerViews();

        loadUserData();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = Definition.TAG_HOME;
            loadHomeFragment();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isLogined()){
            login();
        }
        else {
            checkAllPermissions();
            updateUI();
        }
    }

    @Override
    protected void onResume() {
        //load user data
        loadUserData();
        super.onResume();
    }

    @Override
    protected void onPause() {
        saveUserData();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        saveUserData();
        super.onDestroy();
    }

    private void loadUserData() {
        User.getInstance().loadUserData(this);
    }

    private void saveUserData(){
        User.getInstance().saveUserData(this);
    }

    private void registerViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mHandler = new Handler();

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.name);
        txtWebsite = navHeader.findViewById(R.id.website);
        imgNavHeaderBg = navHeader.findViewById(R.id.img_header_bg);
        imgProfile = navHeader.findViewById(R.id.img_profile);

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);
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
                        loadUserData();
                        checkAllPermissions();
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
            String strName = firebaseUser.getDisplayName();
            String strEmail = firebaseUser.getEmail();
            Uri uriPhoto = firebaseUser.getPhotoUrl();
            if(uriPhoto==null)
                uriPhoto = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                        "://"+getResources().getResourcePackageName(R.drawable.ic_launcher_foreground) +
                        '/'+getResources().getResourceTypeName(R.drawable.ic_launcher_foreground));
            loadNavHeader(strName, strEmail, uriPhoto);
            setUpNavigationView();


        }
        else{
            MyHelper.toast(getApplicationContext(), "User invalid!");
        }
    }

    private boolean isLogined() {
        return this.mAuth.getCurrentUser() != null;
    }

    ProgressDialog progressDialog;
    void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage(getString(R.string.action_loading));
            progressDialog.setIndeterminate(true);
        }
        progressDialog.show();
    }

    void hideProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    private void checkAllPermissions() {
        if (!MyHelper.hasSMSPermissions(this, Definition.PERMISSIONS)) {
            showRequestPermissionsInfoAlertDialog();
        }
    }

    private void showRequestPermissionsInfoAlertDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(com.android.chienfx.cxfactor.R.string.permission_alert_dialog_title);
        builder.setMessage(com.android.chienfx.cxfactor.R.string.permission_dialog_message);

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
                ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.RECEIVE_SMS)
                        && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
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
                    startSmsServices();
                    startGpsServices();
                } else
                {
                    MyHelper.toast(this, "Permissions Denied!");
                }
            }
        }

    }

    private void startSmsServices() {
        Intent receiverService = new Intent(MainActivity.this, SMSReceiveService.class);
        MainActivity.this.startService(receiverService);
        MyHelper.toast(getApplicationContext(), "Receiver SMS servive started");
    }

    private void startGpsServices() {
        Intent gpsService = new Intent(MainActivity.this, GpsService.class);
        MainActivity.this.startService(gpsService);
        //MyHelper.toast(getApplicationContext(), "GPS servive started");
    }


    private void logOut() {
        if(isLogined())
        {
            showProgressDialog();
            mAuth.signOut();
            saveUserData();
            User.getInstance().resetUser();
            hideProgressDialog();
            login();
        }
    }

    /***
     * Load navigation menu header information
     * like background image, profile image
     * name, website, notifications action view (dot)
     */
    private void loadNavHeader(String Name, String Email, Uri Photo) {
        // name, website
        txtName.setText(Name);
        txtWebsite.setText(Email);

        // loading header background image
        Glide.with(this).load(R.drawable.nav_menu_header_bg)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgNavHeaderBg);

        // Loading profile image
        Glide.with(this).load(Photo)
                .crossFade()
                .thumbnail(0.5f)
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(imgProfile);

        // showing dot next to notifications label
        navigationView.getMenu().getItem(2).setActionView(R.layout.menu_dot);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar mStart
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case Definition.FRAGMENT_INDEX_HOME:
                // home
                HomeFragment homeFragment = new HomeFragment();
                return homeFragment;
            case Definition.FRAGMENT_INDEX_WHEREIWAS:
                // photos
                WhereIWasFragment whereIWasFragment = new WhereIWasFragment();
                return whereIWasFragment;

            case Definition.FRAGMENT_INDEX_NOTIFICATION:
                // notifications fragment
                NotificationsFragment notificationsFragment = new NotificationsFragment();
                return notificationsFragment;

            case Definition.FRAGMENT_INDEX_SETTING:
                // settings fragment
                SettingsFragment settingsFragment = new SettingsFragment();
                return settingsFragment;

            default:
                return new HomeFragment();
        }
    }

    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.nav_home:
                        createFragment(Definition.FRAGMENT_INDEX_HOME, Definition.TAG_HOME);
                        break;
                    case R.id.nav_where_i_was:
                        createFragment(Definition.FRAGMENT_INDEX_WHEREIWAS, Definition.TAG_WHEREIWAS);
                        break;
                    case R.id.nav_histories:
                        createFragment(Definition.FRAGMENT_INDEX_NOTIFICATION, Definition.TAG_NOTIFICATIONS);
                        break;
                    case R.id.nav_settings:
                        createFragment(Definition.FRAGMENT_INDEX_SETTING, Definition.TAG_SETTINGS);
                        break;
                    case R.id.nav_logout:
                        logOut();
                        break;
                    case R.id.nav_about_us:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, AboutUsActivity.class));
                        drawer.closeDrawers();
                        return true;
                    case R.id.nav_privacy_policy:
                        // launch new intent instead of loading fragment
                        startActivity(new Intent(MainActivity.this, PrivacyPolicyActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });


        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    public void createFragment(int fragmentIndexHome, String tagHome) {
        navItemIndex = fragmentIndexHome;
        CURRENT_TAG = tagHome;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                createFragment(Definition.FRAGMENT_INDEX_HOME, Definition.TAG_HOME);
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        // show menu only when home fragment is selected
//        if (navItemIndex == 0) {
//            getMenuInflater().inflate(R.menu.main, menu);
//        }
//
//        // when fragment is notifications, load the menu created for notifications
//        if (navItemIndex == 3) {
//            getMenuInflater().inflate(R.menu.notifications, menu);
//        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            Toast.makeText(getApplicationContext(), "Logout user!", Toast.LENGTH_LONG).show();
            return true;
        }

        // user is in notifications fragment
        // and selected 'Mark all as Read'
        if (id == R.id.action_mark_all_read) {
            Toast.makeText(getApplicationContext(), "All notifications marked as read!", Toast.LENGTH_LONG).show();
        }

        // user is in notifications fragment
        // and selected 'Clear All'
        if (id == R.id.action_clear_notifications) {
            Toast.makeText(getApplicationContext(), "Clear all notifications!", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }
}
