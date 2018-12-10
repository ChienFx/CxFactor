package com.android.chienfx.core.user;

import android.location.Location;
import android.text.format.Time;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.contact.Contact;
import com.android.chienfx.core.contact.ContactEmergency;
import com.android.chienfx.core.helper.FirebaseHelper;
import com.android.chienfx.core.history.History;
import com.android.chienfx.core.history.HistoryEmergency;
import com.android.chienfx.core.history.HistoryPushData;
import com.android.chienfx.core.history.HistoryReplySMS;
import com.android.chienfx.core.sms.SMSHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;

import java.util.ArrayList;

public class User {

    private static final User ourInstance = new User();
    public static User getInstance() {
        return ourInstance;
    }

    ArrayList<SMSReplierRecord> mSMSReplierRecords;
    ArrayList<Contact> mBlackList; // detect sms and cancel (not reply)
    ArrayList<ContactEmergency> mEmergencyContacts;
    ArrayList<History> mHistories;

    Location mLastKnownLocation; //tracking gps

    boolean mPermissionSMS;
    boolean mPermissionGPS;
    boolean mPermissionCall;

    private User() {
        loadUserData();
    }

    public boolean isEmptyFriendsList() {
        return mEmergencyContacts.isEmpty();
    }

    private String createLocationLink() {
        String strLoc = "Not found";
        if(mLastKnownLocation!=null)
            strLoc = "http://maps.google.com/maps?t=m&q=" + mLastKnownLocation.getLatitude() + "+" + mLastKnownLocation.getLongitude();
        //http://maps.google.com/maps?t=m&q=49.220634+16.647762
        return strLoc;
    }

    public void addSMSReplierRecord(Time start, Time end, String content){
        mSMSReplierRecords.add(new SMSReplierRecord(start, end, content));
    }

    public void addNumberToBlacklist(Contact contact){
        mEmergencyContacts.remove(contact);
        mBlackList.add(contact);
    }

    public void addNumberToEmergencyContactList(ContactEmergency contactEmergency){
        mBlackList.remove(contactEmergency);
        mEmergencyContacts.add(contactEmergency);
    }

    public void replyInComeSMS(String smsSender, String smsBody){
        String strLog;
        Time tNow = new Time();
        if(mPermissionSMS){
            if(!isInBlacklist(smsSender) && checkToPassAnalysiz(smsBody)){
                tNow.setToNow();
                String strSMSReply = getSMSReply(tNow);
                boolean result = SMSHelper.sendDebugSMS(smsSender, strSMSReply);
                strLog = "Replied SMS from "+smsSender+ " at "+tNow.toString();
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

    private String getSMSReply(Time tNow) {
        for(int i = 0; i < mSMSReplierRecords.size(); i++)
            if(mSMSReplierRecords.get(i).checkInRangeTime(tNow)){
                return mSMSReplierRecords.get(i).mContent;
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
        for(ContactEmergency contact: mEmergencyContacts){
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


    public void loadUserData(){
//        if(currentUID match with last saved user)
//            loadLastSavedUser();
//        else{
//            loadUserDataFromFirebase();
//        }

        mLastKnownLocation = getCurrentLocation();
        mPermissionSMS = true;
        mPermissionGPS = true;
        mPermissionCall = true;

        mLastKnownLocation = null;
        mBlackList =  FirebaseHelper.downloadUserBlacklist();
        mSMSReplierRecords = FirebaseHelper.downloadUserSMSReplierRecords();
        mEmergencyContacts = FirebaseHelper.downloadUserEmergencyContactList();
        mHistories = new ArrayList<>();

        this.addNumberToEmergencyContactList(new ContactEmergency("chienfx","0971096050"));
    }

    private Location getCurrentLocation() {
        return null;
    }

    public void resetUser(){
        pushDataToServer();
        pushLocationToServer();
        mHistories.clear();
        mEmergencyContacts.clear();
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


    public void saveUserData() {
        //store class with current uid
    }
}
