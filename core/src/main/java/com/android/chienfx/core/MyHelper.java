package com.android.chienfx.core;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

public class MyHelper {
    public static void toast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }



}
