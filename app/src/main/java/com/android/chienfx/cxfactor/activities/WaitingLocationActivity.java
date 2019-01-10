package com.android.chienfx.cxfactor.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.activities.findmyphone.MapsActivity;
import com.android.chienfx.cxfactor.core.Definition;
import com.android.chienfx.cxfactor.core.MyLatLng;
import com.android.chienfx.cxfactor.core.user.User;
import com.google.android.gms.maps.model.LatLng;

public class WaitingLocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_location);

        setContentView(R.layout.splash_screen_waitting_location);  //your layout with the picture

        // Start timer and launch main activity
        IntentLauncher launcher = new IntentLauncher();
        launcher.start();
    }

    public void goAhead(View view) {
        User.getInstance().setCurrentLocation(Definition.DEFAULT_LOCATION);
    }

    private class IntentLauncher extends Thread {
        @Override
        public void run() {
            while (true) {
                MyLatLng currentLocation = User.getInstance().getCurrentLocation();
                if (currentLocation.latitude != 0 && currentLocation.longitude != 0)
                    break;
            }

            // Start main activity
            Intent intent = new Intent(WaitingLocationActivity.this, MapsActivity.class);
            startActivity(intent);
            finish();
        }
    }


}
