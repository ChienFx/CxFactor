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
    public static final String DEFAULT_EMERGENCY_MESSAGE = "I am in the emergency situation, please help me !" ;
    public static final String SIGNATURE = "\n[Automatic SMS form CxFator]";

    //Fragments Index
    public static final int FRAGMENT_INDEX_HOME = 0;
    public static final int FRAGMENT_INDEX_PHOTO = 1;
    public static final int FRAGMENT_INDEX_MOVIE = 2;
    public static final int FRAGMENT_INDEX_NOTIFICATION = 3;
    public static final int FRAGMENT_INDEX_SETTING = 4;
    public static final int FRAGMENT_INDEX_EMERGENCY_CONTACT = 5;
    public static final int FRAGMENT_INDEX_EMERGENCY_CONTACT_RECORD = 6;


    //Fragments Tag
    public static final String TAG_HOME = "home";
    public static final String TAG_PHOTOS = "photos";
    public static final String TAG_MOVIES = "movies";
    public static final String TAG_NOTIFICATIONS = "notifications";
    public static final String TAG_SETTINGS = "settings";
    public static final String TAG_EMERGENCY_CONTACT = "emergencycontact";
    public static final String TAG_EMERGENCY_CONTACT_RECORD = "emergencycontactrecord";


    public static String[] PERMISSIONS = {
            Manifest.permission.READ_SMS,
            Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
}
