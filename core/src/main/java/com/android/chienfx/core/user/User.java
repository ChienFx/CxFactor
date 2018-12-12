package com.android.chienfx.core.user;

import android.content.Context;
import android.location.Location;
import android.text.format.Time;
import android.util.Log;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.contact.Contact;
import com.android.chienfx.core.contact.EContact;
import com.android.chienfx.core.helper.FirebaseHelper;
import com.android.chienfx.core.helper.InternalStorage;
import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.core.history.History;
import com.android.chienfx.core.history.HistoryEmergency;
import com.android.chienfx.core.history.HistoryPushData;
import com.android.chienfx.core.history.HistoryReplySMS;
import com.android.chienfx.core.sms.SMSHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class User implements Serializable{
    private static final User ourInstance = new User();
    public static User getInstance() {
        return ourInstance;
    }

    ArrayList<SMSReplierRecord> mSMSReplierRecords;
    ArrayList<Contact> mBlackList; // detect sms and cancel (not reply)
    ArrayList<EContact> mEContacts;
    ArrayList<History> mHistories;

    Location mLastKnownLocation; //tracking gps

    boolean mPermissionSMS;
    boolean mPermissionGPS;
    boolean mPermissionCall;

    private User() {
        initUserData();
    }

    public boolean isEmptyFriendsList() {
        return mEContacts.isEmpty();
    }

    private String createLocationLink() {
        String strLoc = "Not found";
        if(mLastKnownLocation!=null)
            strLoc = "http://maps.google.com/maps?t=m&q=" + mLastKnownLocation.getLatitude() + "+" + mLastKnownLocation.getLongitude();
        //http://maps.google.com/maps?t=m&q=49.220634+16.647762
        return strLoc;
    }

    public void addSMSReplierRecord(int start, int end, String content){
        mSMSReplierRecords.add(new SMSReplierRecord(start, end, content));
    }

    public void addNumberToBlacklist(Contact contact){
        mEContacts.remove(contact);
        mBlackList.add(contact);
    }

    public void addEmergencyContact(EContact contactEmergency){
        mBlackList.remove(contactEmergency);
        mEContacts.add(contactEmergency);
    }

    public void replyInComeSMS(String smsSender, String smsBody){
        String strLog;
        if(mPermissionSMS){
            if(!isInBlacklist(smsSender) && checkToPassAnalysiz(smsBody)){
                String strSMSReply = getSMSReply();
                boolean result = SMSHelper.sendDebugSMS(smsSender, strSMSReply);
                strLog = "Replied SMS from "+smsSender;
                writeHistory(new HistoryReplySMS(smsSender, smsBody, strSMSReply, result));
            }
            else{
                writeHistory(new HistoryReplySMS(smsSender, smsBody, "In Blaclist or did not pass Analyze Ad Filter", History.ACTION_FAIL));
            }
        }
        else
        {
            writeHistory(new HistoryReplySMS(smsSender, smsBody, "Deny Permission", History.ACTION_FAIL));
        }
    }

    private void writeHistory(History history) {
        mHistories.add(history);
    }

    public void pushDataToServer() {
        ////
        boolean result = pushLocationToServer();
        writeHistory(new HistoryPushData(HistoryPushData.PUSH_LOCATION, result));

        //result = FirebaseHelper.updateUserBlacklist();
        //

    }

    public boolean pushLocationToServer() {
        return FirebaseHelper.uploadUserLocationToListOfLocation(this.mLastKnownLocation);
    }

    private boolean checkToPassAnalysiz(String smsBody) {
        for(String word: Definition.spamSMSKeywords){
            if(smsBody.contains(word)) {
                return false; // failed
            }
        }
        return true; //passed
    }

    private String getSMSReply() {
        Date now = new Date();
        int tNow = now.getHours()*60 + now.getMinutes();
        for(int i = 0; i < mSMSReplierRecords.size(); i++)
            if(mSMSReplierRecords.get(i).checkInRangeTime(tNow)){
                return mSMSReplierRecords.get(i).getMessage();
            }
        return Definition.DEFAULT_SMS_REPLY;
    }

    private boolean isInBlacklist(String smsSender) {
        for(Contact s: mBlackList)
            if(s.getContactNumber().compareTo(smsSender)==0)
                return true;
        return false;
    }

    public int sendEmergencySMS() {
        int count = 0;
        for(EContact contact: mEContacts){
            String number = contact.getContactNumber();
            String message = contact.getContactMessage();
            if(contact.getLocationFlag() && mPermissionGPS)
                message+="\nMy location is " + createLocationLink();

            boolean result = SMSHelper.sendDebugSMS(number, message);
            if(result) count++;
            writeHistory(new HistoryEmergency(number, message, this.mPermissionGPS, result));
        }
        return count;
    }


    public void loadUserData(Context context){
        try {
            UserCopier userCopier = (UserCopier) InternalStorage.readUserData(context);
            if(userCopier==null)
            {
                MyHelper.toast(context, "userCopier NULL");
            }
            else if(FirebaseAuth.getInstance().getCurrentUser().getUid().compareTo(userCopier.getmUid())==0){
                mPermissionGPS = userCopier.getmPermissionGPS();
                mPermissionCall = userCopier.getmPermissionCall();
                mPermissionSMS = userCopier.getmPermissionSMS();
                mLastKnownLocation = userCopier.getmLastKnownLocation();
                mSMSReplierRecords = userCopier.getmSMSReplierRecords();
                mBlackList = userCopier.getmBlackList();
                mHistories = userCopier.getmHistories();
                mEContacts = userCopier.getmEContacts();
                MyHelper.toast(context, "Loading from file done!");
            }
            else{
                MyHelper.toast(context, "Wrong UID");
                loadUserDataFromFirebase();
            }
        }
        catch (Exception e){
            MyHelper.toast(context, "Loading from file failed!");
            Log.d("MyREAD", e.getMessage());
        }

    }

    public void saveUserData(Context context) {
        //store class with current uid
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            UserCopier userCopier = new UserCopier(uid, mSMSReplierRecords, mBlackList, mEContacts, mHistories, mLastKnownLocation, mPermissionSMS, mPermissionGPS, mPermissionCall);
            InternalStorage.writeUserData(context, userCopier);
            MyHelper.toast(context, "Store data done!");
        }
        catch (Exception e){
            MyHelper.toast(context, "Store data failed!");
            Log.d("MyWRITE", e.getMessage());
        }

        pushUserDataToFirebase();

    }

    private void initUserData() {
        mLastKnownLocation = getCurrentLocation();
        mPermissionSMS = true;
        mPermissionGPS = true;
        mPermissionCall = true;

        mLastKnownLocation = null;
        mBlackList =  FirebaseHelper.downloadUserBlacklist();
        mSMSReplierRecords = FirebaseHelper.downloadUserSMSReplierRecords();
        mEContacts = FirebaseHelper.downloadUserEmergencyContactList();
        mHistories = new ArrayList<>();

        this.addEmergencyContact(new EContact("Chien","0971096050"));
        this.addEmergencyContact(new EContact("H.Luon", "0883142564"));
        this.addEmergencyContact(new EContact("N.Dinh", "0836524253"));
        this.addEmergencyContact(new EContact("V.Loi",  "0382887809"));
    }

    private Location getCurrentLocation() {
        return null;
    }

    public void resetUser(){
        pushDataToServer();
        pushLocationToServer();
        mHistories.clear();
        mEContacts.clear();
        mBlackList.clear();
        mSMSReplierRecords.clear();

        mPermissionSMS = true;
        mPermissionGPS = true;
        mPermissionCall = true;
    }

    public void setCurrentLocation(Location loc) {
        pushLocationToServer();
        this.mLastKnownLocation = loc;
    }

    private void loadUserDataFromFirebase() {

    }

    private void pushUserDataToFirebase() {

    }

    public boolean getFindPhone() {
        return this.mPermissionGPS;
    }

    public void setFindPhone(boolean value){
        mPermissionGPS = value;
    }

    public boolean getAutoReplySms() {
        return this.mPermissionSMS;
    }

    public void setAutoReplySms(boolean value){
        mPermissionSMS = value;
    }

    public boolean getDeclineCall() {
        return this.mPermissionCall;
    }

    public void setDeclineCall(boolean value){
        mPermissionCall = value;
    }

    public List<EContact> getEmergencyContactList() {
        return this.mEContacts;
    }

    public EContact getEContactByIndex(int index) {
        if(index>=0 && index < mEContacts.size())
            return mEContacts.get(index);
        return null;
    }

    public void deleteEmergencyContact(EContact contact) {
        mEContacts.remove(contact);
    }

    public class UserCopier implements Serializable{
        ArrayList<SMSReplierRecord> mSMSReplierRecords;
        ArrayList<Contact> mBlackList; // detect sms and cancel (not reply)
        ArrayList<EContact> mEContacts;
        ArrayList<History> mHistories;

        Location mLastKnownLocation; //tracking gps

        boolean mPermissionSMS;
        boolean mPermissionGPS;
        boolean mPermissionCall;

        String mUid;

        UserCopier(String uid,
                   ArrayList<SMSReplierRecord> smsReplierRecords,
                ArrayList<Contact> blackList,
                ArrayList<EContact> eContacts,
                ArrayList<History> histories,
                Location lastKnownLocation,
                boolean permissionSMS,
                boolean permissionGPS,
                boolean permissionCall){
            mUid = uid;
            mSMSReplierRecords = smsReplierRecords;
            mBlackList = blackList;
            mEContacts = eContacts;
            mHistories =histories;
            mLastKnownLocation = lastKnownLocation;
            mPermissionCall = permissionCall;
            mPermissionGPS = permissionGPS;
            mPermissionSMS = permissionSMS;
        }

        public String getmUid(){return mUid;}

        public ArrayList<Contact> getmBlackList() {
            return mBlackList;
        }

        public ArrayList<EContact> getmEContacts() {
            return mEContacts;
        }

        public ArrayList<History> getmHistories() {
            return mHistories;
        }

        public ArrayList<SMSReplierRecord> getmSMSReplierRecords() {
            return mSMSReplierRecords;
        }

        public Location getmLastKnownLocation() {
            return mLastKnownLocation;
        }

        public boolean getmPermissionGPS() {
            return mPermissionGPS;
        }

        public boolean getmPermissionSMS() {
            return mPermissionSMS;
        }

        public boolean getmPermissionCall() {
            return mPermissionCall;
        }
    }
}
