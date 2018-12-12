package com.android.chienfx.core.contact;

import java.io.Serializable;

public class Contact implements Serializable {
    public String mName;
    public String mNumber;

    public Contact(String name, String number){
        mName = name;
        mNumber = number;
    }

    public String getContactName(){return mName;}
    public String getContactNumber(){return mNumber;}
    public void setContactName(String name){mName = name;}
    public void setContactNumber(String number){mNumber = number;}
}
