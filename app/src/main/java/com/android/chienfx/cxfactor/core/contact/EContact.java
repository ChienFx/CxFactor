package com.android.chienfx.cxfactor.core.contact;

import com.android.chienfx.cxfactor.core.Definition;

import java.io.Serializable;

public class EContact extends Contact implements Serializable {
    public Boolean mLocationSend;
    public String mMessage;
    public EContact(String name, String number, String message, Boolean locationFlag) {
        super(name, number);
        mLocationSend = locationFlag;
        mMessage = message;
    }

    public EContact(String name, String number) {
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
