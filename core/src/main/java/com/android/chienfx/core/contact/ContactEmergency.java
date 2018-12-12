package com.android.chienfx.core.contact;

import com.android.chienfx.core.Definition;

import java.io.BufferedOutputStream;

public class ContactEmergency extends Contact {
    public Boolean mLocationSend;
    public String mMessage;
    public ContactEmergency(String name, String number, String message, Boolean locationFlag) {
        super(name, number);
        mLocationSend = locationFlag;
        mMessage = message;
    }

    public ContactEmergency(String name, String number) {
        super(name, number);
        mLocationSend = true;
        mMessage = Definition.DEFAULT_EMERGENCY_MESSAGE;
    }

    public Boolean getLocationFlag(){return mLocationSend;}
    public void setLocationFlag(Boolean value){mLocationSend = value;}

    public String getContactMessage() {
        return mMessage;
    }
}
