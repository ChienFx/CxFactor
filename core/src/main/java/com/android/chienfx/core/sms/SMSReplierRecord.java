package com.android.chienfx.core.sms;

import android.text.format.Time;

public class SMSReplierRecord{
    public Time mStart;
    public Time mEnd;
    public String mContent;

    public SMSReplierRecord(Time start, Time end, String content) {
        this.mStart = mStart;
        this.mEnd = mEnd;
        this.mContent = mContent;
    }

    public boolean checkInRangeTime(Time tNow) {
        if(isTimeGreater(mEnd, mStart)) //S < E: 8:00 - 23:00
            if(isTimeGreater(tNow, mStart) && isTimeGreater(mEnd, tNow)) // S < N && N <= E
                return  true;

        else if(isTimeGreater(mStart, mEnd)) //23:00 - 5:00
                return isTimeGreater(tNow, mStart) || isTimeGreater(mEnd, tNow);
        return false;
    }

    boolean isTimeGreater(Time t1, Time t2){
        return (t1.hour * 3600 + t1.minute * 60 + t1.second) > (t2.hour * 3600 + t2.minute * 60 + t2.second);
    }
}
