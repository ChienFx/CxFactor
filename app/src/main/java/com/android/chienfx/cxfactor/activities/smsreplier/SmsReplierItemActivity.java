package com.android.chienfx.cxfactor.activities.smsreplier;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatCheckBox;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TimePicker;

import com.android.chienfx.core.IntentCode;
import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;
import com.android.chienfx.core.user.User;
import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.activities.econtact.EContactItemActivity;

import java.util.Calendar;

public class SmsReplierItemActivity extends AppCompatActivity {

    EditText edTimeStart, edTimeEnd, edMessage;
    AppCompatCheckBox cbLocation;
    ImageButton btnSave, btnDel;
    int iTimeStart, iTimeEnd;
    String strMessage;
    SMSReplierRecord mSmsReplier;
    private boolean lockMessage = false;
    private boolean mAddNew = false;
    TimePickerDialog  picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_replier_item);
        Intent intent = getIntent();
        
        int position = intent.getIntExtra("position", -1);
        mSmsReplier = User.getInstance().getSmsReplierRecordtByIndex(position);
        if(mSmsReplier ==null) {
            mSmsReplier = new SMSReplierRecord();
            mAddNew = true;
        }

        initSmsReplier();

        registerViews();

        handleViewEvents();

        updateUI();
    }

    private void handleViewEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(lockMessage)
                    MyHelper.toast(getApplicationContext(), "Missing Message!");
                else {
                    getUiValue();
                    saveRecord();
                    finish();
                }
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(SmsReplierItemActivity.this);
                builder.setTitle("DELETE");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        User.getInstance().deleteSmsReplierSms(mSmsReplier);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        edTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(SmsReplierItemActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                iTimeStart = sHour*60 + sMinute;
                                edTimeStart.setText(MyHelper.getTimeString(iTimeStart));
                            }
                        }, hour, minutes, false);
                picker.show();
            }
        });

        edTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(SmsReplierItemActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                iTimeEnd = sHour*60 + sMinute;
                                edTimeEnd.setText(MyHelper.getTimeString(iTimeEnd));
                            }
                        }, hour, minutes, false);
                picker.show();
            }
        });

        edMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String str = String.valueOf(edMessage.getText()).trim();
                if(str.length()>1) {
                    lockMessage = false;
                    edMessage.setCompoundDrawablesWithIntrinsicBounds(0,0, 0,0);
                }
                else {
                    lockMessage = true;
                    edMessage.setCompoundDrawablesWithIntrinsicBounds(0,0, R.drawable.icon_alert,0);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
        edMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && edMessage.getText().length() < 1) {
                    MyHelper.toast(getApplicationContext(), "Message must be filled");
                }
            }
        });
    }

    private void registerViews() {
        edTimeStart = findViewById(R.id.edSmsReplierTimeStart);
        edTimeEnd = findViewById(R.id.edSmsReplierTimeEnd);
        edMessage = findViewById(R.id.edSmsreplierItemMessage);
        btnSave = findViewById(R.id.btnSmsReplierItemSave);
        btnDel = findViewById(R.id.btnSmsReplierItemDel);
        final Calendar c = Calendar.getInstance();
    }

    private void initSmsReplier() {
        iTimeStart = mSmsReplier.getIStart();
        iTimeEnd = mSmsReplier.getIEnd();
        strMessage = mSmsReplier.getMessage();
    }

    private void saveRecord() {
        mSmsReplier.mStart = this.iTimeStart;
        mSmsReplier.mEnd = this.iTimeEnd;
        mSmsReplier.mMessage = strMessage;
        if(mAddNew)
            User.getInstance().addSMSReplierRecord(mSmsReplier);
    }

    private void updateUI(){
        edTimeStart.setText(MyHelper.getTimeString(iTimeStart));
        edTimeEnd.setText(MyHelper.getTimeString(iTimeEnd));
        edMessage.setText(strMessage);
    }

    private void getUiValue(){
        strMessage = edMessage.getText().toString();
    }

}
