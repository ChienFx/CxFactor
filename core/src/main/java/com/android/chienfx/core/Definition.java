package com.android.chienfx.core;

import android.Manifest;

import java.util.Arrays;
import java.util.List;

public final class Definition {
    public static final int MINIMUM_PASSWORD_LENGTH = 8;
    public static final String EMAIL_REGEX = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String DEFAULT_SMS_REPLY = "I am busy now. I'll call you back later. [CxFactor: This is a automatically SMS]";
    public static final List<String> spamSMSKeywords = Arrays.asList("[QC]", "VIETTEL", "viettel", "trúng thưởng", "1900");

    public static final int REQUEST_PERMISSION_CODE = 103;

    public static String[] PERMISSIONS = {
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS
    };
}
