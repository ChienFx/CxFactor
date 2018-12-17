package com.android.chienfx.core.history;

import android.text.format.Time;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Calendar;
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
        Calendar calendar = Calendar.getInstance();
        mTimeStamp = calendar.getTimeInMillis();
        mAction = action;
        mResult = result;
    }
    public String getStringTimeStamp(){
        Timestamp ts = new Timestamp(mTimeStamp);
        return ts.toString();
    }
    public boolean getResult(){return mResult;}
    public String getStringAction(){return mAction;}
}
