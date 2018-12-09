package com.android.chienfx.core.helper;

import android.location.Location;

import com.android.chienfx.core.sms.SMSReplierRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FirebaseHelper {

    public static ArrayList<String> downloadUserFriendsList() {
        ArrayList<String> list = new ArrayList<>();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            ;//
        return list;
    }

    public static ArrayList<String> downloadUserBlacklist() {
        ArrayList<String> list = new ArrayList<>();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            ;//
        return list;
    }

    public static ArrayList<SMSReplierRecord> downloadUserSMSReplierRecords() {
        ArrayList<SMSReplierRecord> list = new ArrayList<>();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            ;//
        return list;
    }

    public static boolean uploadUserLocationToListOfLocation(Location mLastKnownLocation) {
        return false;
        //upload location len LIST location WITH TIEM FOR TRACKING LATER, khong phai replace location ?////
    }
}
