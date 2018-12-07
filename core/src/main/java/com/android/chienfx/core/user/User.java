package com.android.chienfx.core.user;

import android.text.format.Time;
import android.util.Log;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.MyHelper;
import com.android.chienfx.core.sms.SMSHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;

import java.util.ArrayList;
import java.util.List;
public class User {

    private static final User ourInstance = new User();
    public static User getInstance() {
        return ourInstance;
    }

    public ArrayList<SMSReplierRecord> mSMSReplierRecords;
    public ArrayList<String> mBlacklistNumbers; // detect sms and cancel (not reply)
    public ArrayList<String> mLogs;

    private User() {
        mSMSReplierRecords= new ArrayList<>();
        mBlacklistNumbers = new ArrayList<>();
        mLogs = new ArrayList<>();
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
}
