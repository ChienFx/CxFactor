package com.android.chienfx.core.user;

import android.location.Location;
import android.text.format.Time;
import android.util.Log;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.sms.SMSHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;

import java.util.ArrayList;

public class User {

    private static final User ourInstance = new User();
    public static User getInstance() {
        return ourInstance;
    }

    public ArrayList<SMSReplierRecord> mSMSReplierRecords;
    public ArrayList<String> mBlacklistNumbers; // detect sms and cancel (not reply)
    public ArrayList<String> mCloseFriends;
    public ArrayList<String> mLogs;
    public String mEmergencyMessage;
    public boolean mFlagSendLocation;
    public Location mLastKnownLocation;

    private User() {
        mSMSReplierRecords= new ArrayList<>();
        mBlacklistNumbers = new ArrayList<>();
        mCloseFriends = new ArrayList<>();
        mLogs = new ArrayList<>();
        mFlagSendLocation = false;
        mEmergencyMessage = Definition.DEFAULT_EMERGENCY_MESSAGE;
    }

    private String createLocationLink() {
        return "http://maps.google.com/?q=" + mLastKnownLocation.getLatitude() + "," + mLastKnownLocation.getLongitude();
        //http://maps.google.com/maps?t=m&q=49.220634+16.647762
    }

    public void addSMSReplierRecord(Time start, Time end, String content){
        mSMSReplierRecords.add(new SMSReplierRecord(start, end, content));
    }

    public void addNumberToBlacklist(String number){
        mBlacklistNumbers.add(number);
    }

    public void replyInComeSMS(String smsSender, String smsBody){
        String strLog;
        Time tNow = new Time();
        if(!isInBlacklist(smsSender) && checkToPassAnalysiz(smsBody)){
            tNow.setToNow();
            String strSMSReply = getSMSReply(tNow);
            SMSHelper.sendDebugSMS(smsSender, strSMSReply);
            strLog = "Replied SMS from "+smsSender+ " at "+tNow.toString();
        }
        else{
            strLog = "Skipped SMS from "+smsSender+ " at "+tNow.toString();
        }

        mLogs.add(strLog);
        Log.d("SMSReply", "SMS detected: From " + smsSender + " With text " + smsBody);
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
        for(String s: mBlacklistNumbers)
            if(s.compareTo(smsSender)==0)
                return true;
        return false;
    }

    public void sendEmergencySMS() {
        String strSMS = getEmergencySMS();
        String strLog;
        for(String friend: mCloseFriends){
            this.replyInComeSMS(friend, strSMS);
            strLog = "[Emergency SMS sent to " + friend;
            mLogs.add(strLog);
        }
        //upload Log to firebase

    }

    private String getEmergencySMS() {
        String str = this.mEmergencyMessage;
        if(mFlagSendLocation)
            str+="My location is: " + createLocationLink();
        return str;
    }
}
