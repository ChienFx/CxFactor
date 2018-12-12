package com.android.chienfx.core.sms;

import java.io.Serializable;

public class SMSReplierRecord implements Serializable {
    int mStart;      //04:22
    int mEnd;        //23:59
    String mMessage;

    public SMSReplierRecord(int start, int end, String message) {
        this.mStart = start;
        this.mEnd = end;
        this.mMessage = message;
    }

    public boolean checkInRangeTime(int tNow) {
        if(mEnd > mStart) //S < E: 8:00 - 23:00
            if(tNow > mStart && mEnd > tNow) // S < N && N <= E
                return  true;

        else if(mStart > mEnd) //23:00 - 5:00
                return tNow > mStart || mEnd > tNow;
        return false;
    }

    public String getTimeStart(){
        return getTimeString(mStart);
    }
    public String getTimeEnd(){
        return getTimeString(mEnd);
    }
    public String getMessage(){
        return mMessage;
    }

    private String getTimeString(int t){
        return String.valueOf(t/60) + String.valueOf(t%60);
    }
}
