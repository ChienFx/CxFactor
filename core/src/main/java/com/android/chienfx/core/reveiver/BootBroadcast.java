package com.android.chienfx.core.reveiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.android.chienfx.core.services.SMSReceiveService;

public class BootBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SMSReceiveService.class));
    }
}
