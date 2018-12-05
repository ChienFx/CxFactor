package com.android.chienfx.core.sms;


import android.telephony.SmsManager;
import android.util.Patterns;

class SMSHelper {
    public static final String SMS_CONDITION = "Some conditional";

    public static boolean isValidPhoneNumber(String phoneNumber){
        return Patterns.PHONE.matcher(phoneNumber).matches();
    }

    public static void sendDebugSMS(String number, String smsBody){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, smsBody, null, null);
    }
}
