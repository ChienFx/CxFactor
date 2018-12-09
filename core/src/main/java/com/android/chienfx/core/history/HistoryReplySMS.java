package com.android.chienfx.core.history;

public class HistoryReplySMS extends History {
    String mSender;
    String mIncomeMessage;
    String mReplyMessage;

    public HistoryReplySMS(String sender, String incomeSMS, String replySMS, boolean result) {
        super(History.HISTORY_ACTION_REPLY_SMS, result);
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
