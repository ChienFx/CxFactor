package com.android.chienfx.core.sms;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.helper.MyHelper;

import java.io.Serializable;

public class SMSReplierRecord implements Serializable {
    public int mStart;      //04:22
    public int mEnd;        //23:59
    public String mMessage;

    public SMSReplierRecord(int start, int end, String message) {
        this.mStart = start;
        this.mEnd = end;
        this.mMessage = message;
    }

    public SMSReplierRecord() {
        mStart = 0;     //00:00
        mEnd = 1439;    //23:59
        mMessage = Definition.SMS_REPLIER_MESSAGE_BUSY;
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
        return MyHelper.getTimeString(mStart);
    }
    public String getTimeEnd(){
        return MyHelper.getTimeString(mEnd);
    }
    public String getMessage(){
        return mMessage;
    }
    public int getIStart(){return  mStart;}
    public int getIEnd(){return  mEnd;}


}
