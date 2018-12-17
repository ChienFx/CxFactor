package com.android.chienfx.core.user;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.contact.Contact;
import com.android.chienfx.core.contact.EContact;
import com.android.chienfx.core.helper.FirebaseHelper;
import com.android.chienfx.core.helper.InternalStorage;
import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.core.history.History;
import com.android.chienfx.core.history.HistoryEmergency;
import com.android.chienfx.core.history.HistoryPushData;
import com.android.chienfx.core.history.HistoryReplySMS;
import com.android.chienfx.core.sms.SMSHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class User implements Serializable{
    private static final User ourInstance = new User();
    public static User getInstance() {
        return ourInstance;
    }

    ArrayList<SMSReplierRecord> mSMSReplierRecords;
    ArrayList<Contact> mBlackList; // detect sms and cancel (not reply)
    ArrayList<EContact> mEContacts;
    ArrayList<History> mHistories;
    ArrayList<Location> mLocationList;

    Location mLastKnownLocation; //tracking gps

    boolean mPermissionSMS;
    boolean mPermissionGPS;
    boolean mPermissionCall;

    private User() {
        initUserData();
    }

    public boolean isEmptyFriendsList() {
        return mEContacts.isEmpty();
    }

    private String createLocationLink() {
        String strLoc = "Not found";
        if(mLastKnownLocation!=null)
            strLoc = "http://maps.google.com/maps?t=m&q=" + mLastKnownLocation.getLatitude() + "+" + mLastKnownLocation.getLongitude();
        //http://maps.google.com/maps?t=m&q=49.220634+16.647762
        return strLoc;
    }

    public void addSMSReplierRecord(SMSReplierRecord smsReplierRecord){
        mSMSReplierRecords.add(smsReplierRecord);
    }

    public void addNumberToBlacklist(Contact contact){
        mBlackList.add(contact);
    }

    public void addEmergencyContact(EContact contactEmergency){
        if(!mEContacts.contains(contactEmergency)){
            mEContacts.add(contactEmergency);
        }

    }

    public String replyInComeSMS(String smsSender, String smsBody){
        String strLog;String strSMSReply = "";
        Boolean result = false;
        if(mPermissionSMS){
            if(!isInBlacklist(smsSender) && checkToPassAnalysiz(smsBody)){
                strSMSReply = getSMSReply();
                result = SMSHelper.sendDebugSMS(smsSender, strSMSReply);
                strLog = "Replied SMS";
            }
            else{
                strLog = "Skipped SMS";
            }
        }
        else
        {
            strLog = "Denied SMS permission to reply SMS";
        }
        writeHistory(new HistoryReplySMS(strLog, smsSender, smsBody, strSMSReply, result));
        return strLog;
    }

    private void writeHistory(History history) {
        mHistories.add(history);
    }

    public void pushDataToServer() {
        ////
        boolean result = pushLocationToServer();
        writeHistory(new HistoryPushData(HistoryPushData.PUSH_LOCATION, result));

        //result = FirebaseHelper.updateUserBlacklist();
        //

    }

    public boolean pushLocationToServer() {
        return FirebaseHelper.uploadUserLocationToListOfLocation(this.mLastKnownLocation);
    }

    private boolean checkToPassAnalysiz(String smsBody) {
        for(String word: Definition.spamSMSKeywords){
            if(smsBody.contains(word)) {
                return false; // failed
            }
        }
        return true; //passed
    }

    private String getSMSReply() {
        Calendar calendar = Calendar.getInstance();

        int tNow = calendar.get(Calendar.HOUR_OF_DAY) + calendar.get(Calendar.MINUTE);

        for(int i = 0; i < mSMSReplierRecords.size(); i++)
            if(mSMSReplierRecords.get(i).checkInRangeTime(tNow)){
                return mSMSReplierRecords.get(i).getMessage();
            }
        return Definition.DEFAULT_SMS_REPLY;
    }

    private boolean isInBlacklist(String smsSender) {
        for(Contact s: mBlackList)
            if(s.getContactNumber().compareTo(smsSender)==0)
                return true;
        return false;
    }

    public int sendEmergencySMS() {
        int count = 0;
        for(EContact contact: mEContacts){
            String number = contact.getContactNumber();
            String message = contact.getContactMessage();
            if(contact.getLocationFlag() && mPermissionGPS)
                message+="\nMy location is " + createLocationLink();

            boolean result = SMSHelper.sendDebugSMS(number, message);
            if(result) count++;
            writeHistory(new HistoryEmergency(number, message, this.mPermissionGPS, result));
        }
        return count;
    }


    public void loadUserData(Context context){
        try {
            UserCopier userCopier = (UserCopier) InternalStorage.readUserData(context);
            if(userCopier==null)
            {
                MyHelper.toast(context, "userCopier NULL");
            }
            else if(FirebaseAuth.getInstance().getCurrentUser().getUid().compareTo(userCopier.getmUid())==0){
                mPermissionGPS = userCopier.getPermissionGPS();
                mPermissionCall = userCopier.getPermissionCall();
                mPermissionSMS = userCopier.getPermissionSMS();
                mLastKnownLocation = userCopier.getLastKnownLocation();
                mSMSReplierRecords = (ArrayList<SMSReplierRecord>) userCopier.getSMSReplierRecords();
                mBlackList = userCopier.getBlackList();
                mHistories = userCopier.getHistories();
                mEContacts = userCopier.getEContacts();
                mLocationList = userCopier.getLocaionList();
                MyHelper.toast(context, "Loading from file done!");
            }
            else{
                MyHelper.toast(context, "Wrong UID");
                loadUserDataFromFirebase();
            }
        }
        catch (Exception e){
            MyHelper.toast(context, "Loading from file failed!");
            Log.d("MyREAD", e.getMessage());
        }

    }

    public void saveUserData(Context context) {
        //store class with current uid
        try {
            String uid = FirebaseAuth.getInstance().getUid();
            UserCopier userCopier = new UserCopier(uid, mSMSReplierRecords, mBlackList, mEContacts, mHistories, mLocationList, mLastKnownLocation, mPermissionSMS, mPermissionGPS, mPermissionCall);
            InternalStorage.writeUserData(context, userCopier);
            MyHelper.toast(context, "Store data done!");
        }
        catch (Exception e){
            MyHelper.toast(context, "Store data failed!");
            Log.d("MyWRITE", e.getMessage());
        }

        pushUserDataToFirebase();

    }

    private void initUserData() {
        mLastKnownLocation = getCurrentLocation();
        mPermissionSMS = true;
        mPermissionGPS = true;
        mPermissionCall = true;

        mLastKnownLocation = null;
        mBlackList =  FirebaseHelper.downloadUserBlacklist();
        mSMSReplierRecords = FirebaseHelper.downloadUserSMSReplierRecords();
        mEContacts = FirebaseHelper.downloadUserEmergencyContactList();

        mHistories = new ArrayList<>();
        mLocationList = new ArrayList<>();

        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", false));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", true));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", true));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", false));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", true));
        writeHistory(new History("Tracked location:" , false));
        writeHistory(new History("Tracked location: 1.020122, 12.21323" , true));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", false));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", true));
        writeHistory(new History("Tracked location:" , true));
        writeHistory(new History("Tracked location: 1.021552, 12.21312" , true));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", false));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", true));
        mHistories.add(new HistoryReplySMS("Replied sms", "0971096050", "Dang lam gi v m?", "Tao dang ban, ti nua toi sex goij laij", false));
    }

    private Location getCurrentLocation() {
        return null;
    }

    public void resetUser(){
        pushDataToServer();
        pushLocationToServer();
        mHistories.clear();
        mEContacts.clear();
        mBlackList.clear();
        mSMSReplierRecords.clear();

        mPermissionSMS = true;
        mPermissionGPS = true;
        mPermissionCall = true;
    }

    public void setCurrentLocation(Location loc) {
        pushLocationToServer();
        boolean res = loc!=null;
        writeHistory(new History("Tracked location:" +loc.toString(), res));
        mLocationList.add(loc);
        this.mLastKnownLocation = loc;
    }

    private void loadUserDataFromFirebase() {

    }

    private void pushUserDataToFirebase() {

    }

    public boolean getFindPhone() {
        return this.mPermissionGPS;
    }

    public void setFindPhone(boolean value){
        mPermissionGPS = value;
    }

    public boolean getAutoReplySms() {
        return this.mPermissionSMS;
    }

    public void setAutoReplySms(boolean value){
        mPermissionSMS = value;
    }

    public boolean getDeclineCall() {
        return this.mPermissionCall;
    }

    public void setDeclineCall(boolean value){
        mPermissionCall = value;
    }

    public List<EContact> getEmergencyContactList() {
        return this.mEContacts;
    }

    public List<SMSReplierRecord> getSmsReplierList(){return this.mSMSReplierRecords;}

    public List<Contact> getBlacklist() {
        return  this.mBlackList;
    }

    public EContact getEContactByIndex(int index) {
        if(index>=0 && index < mEContacts.size())
            return mEContacts.get(index);
        return null;
    }

    public SMSReplierRecord getSmsReplierRecordtByIndex(int index) {
        if(index>=0 && index < mSMSReplierRecords.size())
            return mSMSReplierRecords.get(index);
        return null;
    }

    public Contact getBlacklistContactByIndex(int index) {
        if(index>=0 && index < mBlackList.size())
            return mBlackList.get(index);
        return null;
    }

    public void deleteEmergencyContact(EContact contact) {
        mEContacts.remove(contact);
    }

    public void deleteSmsReplierSms(SMSReplierRecord mSmsReplier) {
        mSMSReplierRecords.remove(mSmsReplier);
    }

    public void deleteBlacklistContact(Contact contact){ mBlackList.remove(contact);}

    public List<History> getHistoryList() {
        return mHistories;
    }

    public class UserCopier implements Serializable{
        ArrayList<SMSReplierRecord> mSMSReplierRecords;
        ArrayList<Contact> mBlackList; // detect sms and cancel (not reply)
        ArrayList<EContact> mEContacts;
        ArrayList<History> mHistories;
        ArrayList<Location> mLocationList;
        Location mLastKnownLocation; //tracking gps

        boolean mPermissionSMS;
        boolean mPermissionGPS;
        boolean mPermissionCall;

        String mUid;


        UserCopier(String uid,
                   ArrayList<SMSReplierRecord> smsReplierRecords,
                   ArrayList<Contact> blackList,
                   ArrayList<EContact> eContacts,
                   ArrayList<History> histories,
                   ArrayList<Location> locationList,
                   Location lastKnownLocation,
                   boolean permissionSMS,
                   boolean permissionGPS,
                   boolean permissionCall){
            mUid = uid;
            mSMSReplierRecords = smsReplierRecords;
            mBlackList = blackList;
            mEContacts = eContacts;
            mHistories =histories;
            mLocationList = locationList;
            mLastKnownLocation = lastKnownLocation;
            mPermissionCall = permissionCall;
            mPermissionGPS = permissionGPS;
            mPermissionSMS = permissionSMS;
        }

        public String getmUid(){return mUid;}

        public ArrayList<Contact> getBlackList() {
            return mBlackList;
        }

        public ArrayList<EContact> getEContacts() {
            return mEContacts;
        }

        public ArrayList<History> getHistories() {
            return mHistories;
        }

        public List<SMSReplierRecord> getSMSReplierRecords() {
            return mSMSReplierRecords;
        }

        public Location getLastKnownLocation() {
            return mLastKnownLocation;
        }

        public boolean getPermissionGPS() {
            return mPermissionGPS;
        }

        public boolean getPermissionSMS() {
            return this.mPermissionSMS;
        }

        public boolean getPermissionCall() {
            return mPermissionCall;
        }

        public ArrayList<Location> getLocaionList() {
            return this.mLocationList;
        }
    }
}
