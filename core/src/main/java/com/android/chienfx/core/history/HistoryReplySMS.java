package com.android.chienfx.core.history;

public class HistoryReplySMS extends History {
    String mSender;
    String mIncomeMessage;
    String mReplyMessage;

    public HistoryReplySMS(String action, String sender, String incomeSMS, String replySMS, boolean result) {
        super(action, result);
        this.mSender = sender;
        this.mIncomeMessage = incomeSMS;
        this.mReplyMessage = replySMS;
    }

    public String getSender(){
        return  mSender;
    }

    public  String getIncomeMessage(){
        return mIncomeMessage;
    }
    public String getReplyMessage(){
        return mReplyMessage;
    }
}
