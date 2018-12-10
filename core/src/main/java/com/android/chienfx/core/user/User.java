package com.android.chienfx.core.user;

import android.location.Location;
import android.text.format.Time;

import com.android.chienfx.core.Definition;
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
    ArrayList<String> mBlackList; // detect sms and cancel (not reply)
    ArrayList<String> mFriendsList;
    ArrayList<History> mHistories;

    String mEmergencyMessage;
    Location mLastKnownLocation; //tracking gps

    boolean mPermissionSMS;
    boolean mPermissionGPS;
    boolean mPermissionCall;

    private User() {
        loadUserData();
    }

    public boolean isEmptyFriendsList() {
        return mFriendsList.isEmpty();
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

    public void addNumberToBlacklist(String number){
        mFriendsList.remove(number);
        mBlackList.add(number);
    }

    public void addNumberToFriendsList(String number){
        mBlackList.remove(number);
        mFriendsList.add(number);
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
        for(String s: mBlackList)
            if(s.compareTo(smsSender)==0)
                return true;
        return false;
    }

    public int sendEmergencySMS() {
        String strSMS = getEmergencySMS();
        int count = 0;
        for(String friend: mFriendsList){
            boolean result = SMSHelper.sendDebugSMS(friend, strSMS);
            if(result) count++;
            writeHistory(new HistoryEmergency(friend, strSMS, this.mPermissionGPS, result));
        }
        return count;
    }

    private String getEmergencySMS() {
        String str = this.mEmergencyMessage;
        if(mPermissionGPS)
            str+="\nMy location is: " + createLocationLink();
        return str;
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
        mEmergencyMessage = Definition.DEFAULT_EMERGENCY_MESSAGE;

        mLastKnownLocation = null;
        mBlackList =  FirebaseHelper.downloadUserBlacklist();
        mSMSReplierRecords = FirebaseHelper.downloadUserSMSReplierRecords();
        mFriendsList = FirebaseHelper.downloadUserFriendsList();
        mHistories = new ArrayList<>();

        this.addNumberToFriendsList("0971096050");
    }

    private Location getCurrentLocation() {
        return null;
    }

    public void resetUser(){
        pushDataToServer();
        pushLocationToServer();
        mHistories.clear();
        mFriendsList.clear();
        mBlackList.clear();
        mSMSReplierRecords.clear();
        mEmergencyMessage = Definition.DEFAULT_EMERGENCY_MESSAGE;
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
