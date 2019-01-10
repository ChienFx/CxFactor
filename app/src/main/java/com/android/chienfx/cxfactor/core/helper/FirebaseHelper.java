package com.android.chienfx.cxfactor.core.helper;

import android.location.Location;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.util.Log;

import com.android.chienfx.cxfactor.activities.MainActivity;
import com.android.chienfx.cxfactor.core.Definition;
import com.android.chienfx.cxfactor.core.MyLatLng;
import com.android.chienfx.cxfactor.core.contact.Contact;
import com.android.chienfx.cxfactor.core.contact.EContact;
import com.android.chienfx.cxfactor.core.sms.SMSReplierRecord;
import com.android.chienfx.cxfactor.core.user.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

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

    public static void pushLocation(final Map<String, Object> locationMap) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(getUserId()).child("locations");
        reference.updateChildren(locationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d(Definition.TAG_LOG, "update location to fire done - " + locationMap.size());
            }
        });
    }

    public static void startDownloadLocationList() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference().child(getUserId()).child("locations");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d(Definition.TAG_LOG, "xxx down the loction list");
                ArrayList<MyLatLng> myList = new ArrayList<>();
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    MyLatLng myLatLng = child.getValue(MyLatLng.class);
                    if(myLatLng!=null)
                        myList.add(myLatLng);
                }
                User.getInstance().notifyDownloadLocationListDone(myList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private static String getUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

}
