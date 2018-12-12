package com.android.chienfx.core.contact;

public class Contact {
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
