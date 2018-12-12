package com.android.chienfx.core.helper;

import android.location.Location;

import com.android.chienfx.core.contact.Contact;
import com.android.chienfx.core.contact.EContact;
import com.android.chienfx.core.sms.SMSReplierRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FirebaseHelper {

    public static ArrayList<EContact> downloadUserEmergencyContactList() {
        ArrayList<EContact> list = new ArrayList<>();
        if(FirebaseAuth.getInstance().getCurrentUser() != null)
            ;//
        return list;
    }

    public static ArrayList<Contact> downloadUserBlacklist() {
        ArrayList<Contact> list = new ArrayList<>();
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
        //check user firebase
        //upload location len LIST location WITH TIEM FOR TRACKING LATER, khong phai replace location ?////
    }
}
