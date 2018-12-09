package com.android.chienfx.core.history;

public class HistoryReplyCall extends History {
    String mCaller;
    String mReplyMessage;

    public HistoryReplyCall(String caller, String replyMessage, boolean result) {
        super(History.HISTORY_ACTION_REPLY_CALL, result);
        this.mCaller = caller;
        this.mReplyMessage = replyMessage;
    }

    public String getCaller(){
        return mCaller;
    }

    public String getReplyMessage(){
        return mReplyMessage;
    }
}
