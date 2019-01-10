package com.android.chienfx.cxfactor.core.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;


import com.android.chienfx.cxfactor.core.helper.MyHelper;
import com.android.chienfx.cxfactor.core.user.User;

/**
 * A broadcast receiver who listens for incoming SMS
 */
public class SmsBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            String smsSender = "";
            String smsBody = "";

            for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                if(smsMessage == null)
                    break;
                smsBody += smsMessage.getMessageBody();
                smsSender += smsMessage.getOriginatingAddress();
            }

            Log.d("SMS income: ", smsBody);

            String strResult = User.getInstance().replyInComeSMS(smsSender, smsBody);
            MyHelper.toast(context, strResult +" from "+smsSender);
        }
    }
}
