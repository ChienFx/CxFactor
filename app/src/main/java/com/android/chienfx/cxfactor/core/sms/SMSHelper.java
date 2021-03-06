package com.android.chienfx.cxfactor.core.sms;


import android.telephony.SmsManager;
import android.util.Log;
import android.util.Patterns;

public class SMSHelper {
    public static final String SMS_CONDITION = "Some conditional";
    private static final String TAG = "TAG_SMSHelper";

    public static boolean isValidPhoneNumber(String phoneNumber){
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static boolean sendDebugSMS(String number, String content){
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(number, null, content, null, null);
            return true;
        }
        catch(Exception e)
        {
            Log.d(TAG, "sendDebugSMS: "+e.getMessage());
            return false;
        }
    }
}
