package com.android.chienfx.core.history;

import android.text.format.Time;

public class History {
    public static final boolean ACTION_SUCCESS = true;
    public static final boolean ACTION_FAIL = false;

    public static final String HISTORY_ACTION_EMERGENCY = "Emergency";
    public static final String HISTORY_ACTION_PUSH_DATA = "Push Data";
    public static final String HISTORY_ACTION_REPLY_CALL = "Reply Call";
    public static final String HISTORY_ACTION_REPLY_SMS = "Reply SMS";

    //Attibutes
    Time mTime;         // auto set to now
    String mAction;     // Reply SMS, Reply Phone Call, Emergency
    boolean mResult;


    public History(String action, boolean result) {
        mTime = new Time();
        mTime.setToNow();
        mAction = action;
        mResult = result;
    }

    public Time getTime(){return mTime;}
    public String getAction(){return mAction;}
}
