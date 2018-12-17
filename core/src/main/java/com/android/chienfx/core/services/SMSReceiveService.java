package com.android.chienfx.core.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.core.reveiver.SmsBroadcastReceiver;
import com.android.chienfx.core.user.User;

public class SMSReceiveService extends Service {
    private final SmsBroadcastReceiver smsBroadcastReceiver = new SmsBroadcastReceiver(){
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
    };

    public SMSReceiveService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
       return  null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION);
        filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        registerReceiver(smsBroadcastReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(smsBroadcastReceiver);
    }
}
