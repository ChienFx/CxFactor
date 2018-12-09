package com.android.chienfx.core.history;

public class HistoryEmergency extends History {
    String mReceiver;
    String mReplyMessage;
    Boolean mHasLocation;

    public HistoryEmergency(String receiver, String message, Boolean hasLocation,  boolean result) {
        super(History.HISTORY_ACTION_EMERGENCY, result);
        this.mReceiver = receiver;
        this.mReplyMessage = message;
        this.mHasLocation = hasLocation;
    }

    public String getReceiver(){
        return mReceiver;
    }

    public String getReplyMessage(){
        return mReplyMessage;
    }

    public Boolean hasLocation(){
        return mHasLocation;
    }

}
