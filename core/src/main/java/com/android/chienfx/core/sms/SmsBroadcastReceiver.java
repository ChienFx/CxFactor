package com.android.chienfx.core.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;


import com.android.chienfx.core.MyHelper;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";
            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                smsBody += smsMessage.getMessageBody();
            }

            if (smsBody.startsWith(SMSHelper.SMS_CONDITION)) {
                Log.d(TAG, "Sms with condition detected");
                MyHelper.toast(context, "BroadcastReceiver caught conditional SMS: " + smsBody);
            }
            SMSHelper.sendDebugSMS(smsSender, "Auto reply message :))");
            MyHelper.toast(context, "SMS detected: From " + smsSender + " With text " + smsBody);
            Log.d(TAG, "SMS detected: From " + smsSender + " With text " + smsBody);
        }
    }
}
