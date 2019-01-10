package com.android.chienfx.cxfactor.core.history;

public class HistoryPushData
        extends History {

    public  static String PUSH_LOCATION = "Location";
    public static String PUSH_SETTING_RECORDS = "Setting Record";
    String mWhichData; //location, setting records

    public HistoryPushData(String mWhichData, boolean result){
        super(History.HISTORY_ACTION_PUSH_DATA, result);
        this.mWhichData = mWhichData;
    }

    public String getWhichData() {
        return mWhichData;
    }
}
