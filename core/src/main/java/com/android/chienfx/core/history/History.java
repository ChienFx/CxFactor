package com.android.chienfx.core.history;

import android.text.format.Time;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Date;

public class History implements Serializable {
    public static final boolean ACTION_SUCCESS = true;
    public static final boolean ACTION_FAIL = false;

    public static final String HISTORY_ACTION_EMERGENCY = "Emergency";
    public static final String HISTORY_ACTION_PUSH_DATA = "Push Data";
    public static final String HISTORY_ACTION_REPLY_CALL = "Reply Call";
    public static final String HISTORY_ACTION_REPLY_SMS = "Reply SMS";

    //Attibutes
    long mTimeStamp;         // auto set to now
    String mAction;     // Reply SMS, Reply Phone Call, Emergency
    boolean mResult;


    public History(String action, boolean result) {
        Date date = new Date();

        mTimeStamp = date.getTime();
        mAction = action;
        mResult = result;
    }
    public String getTimeStampString(){
        Timestamp ts = new Timestamp(mTimeStamp);
        return ts.toString();
    }
    public long getmTimeStamp(){return mTimeStamp;}
    public String getAction(){return mAction;}
}
