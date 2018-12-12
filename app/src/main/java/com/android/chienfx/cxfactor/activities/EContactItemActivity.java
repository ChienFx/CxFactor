package com.android.chienfx.cxfactor.activities;

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

import com.android.chienfx.core.IntentCode;
import com.android.chienfx.core.contact.EContact;
import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.core.user.User;
import com.android.chienfx.cxfactor.R;

public class EContactItemActivity extends AppCompatActivity {

    EditText edContact, edMessage;
    AppCompatCheckBox cbLocation;
    ImageButton btnSave, btnDel;
    String mName, mNumber, mMessage;
    Boolean mLocation, mAddNew = false, lockMessage = false;
    EContact mContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_econtact_item);
        Intent intent = getIntent();

        int position = intent.getIntExtra("position", -1);
        mContact = User.getInstance().getEContactByIndex(position);
        if(mContact==null) {
            mContact = new EContact("[Click here]", "");
            mAddNew = true;
        }
        initContact();

        registerViews();

        handleViewEvents();

        updateUI();
    }

    private void handleViewEvents() {
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNumber.length()<1 || mName.compareTo("[Click here]")==0){
                    MyHelper.toast(getApplicationContext(), "Missing Contact!");
                }
                else if(lockMessage)
                    MyHelper.toast(getApplicationContext(), "Missing Emergency Message!");
                else {
                    getUiValue();
                    saveContact();
                    finish();
                }
            }
        });

        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(EContactItemActivity.this);
                builder.setTitle("DELETE");
                builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        User.getInstance().deleteEmergencyContact(mContact);
                        finishLoginActivity(IntentCode.RESULT_EMERGENC_CONTACT_RECORD, "Deleted");
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
        edContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, IntentCode.REQUEST_PICK_CONTACT);
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
        edContact = findViewById(R.id.edContact);
        edMessage = findViewById(R.id.edMessage);
        cbLocation = findViewById(R.id.cbLoction);
        btnSave = findViewById(R.id.btnSave);
        btnDel = findViewById(R.id.btnDel);
    }

    private void initContact() {
        mName = mContact.mName;
        mNumber = mContact.mNumber;
        mMessage = mContact.mMessage;
        mLocation = mContact.mLocationSend;
    }

    private void saveContact() {
        mContact.mName = mName;
        mContact.mNumber = mNumber;
        mContact.mMessage = mMessage;
        mContact.mLocationSend = mLocation;
        if(mAddNew)
            User.getInstance().addEmergencyContact(mContact);
    }

    private void updateUI(){
        edContact.setText(mName+"\n"+mNumber);
        cbLocation.setChecked(mLocation);
        edMessage.setText(mMessage);
    }

    private void getUiValue(){
        mLocation = cbLocation.isChecked();
        mMessage = edMessage.getText().toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == IntentCode.REQUEST_PICK_CONTACT && resultCode == RESULT_OK) {
            Uri contactUri = data.getData();
            Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
            cursor.moveToFirst();
            int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            mNumber = cursor.getString(column);
            column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            mName = cursor.getString(column);

            edContact.setText(mName+"\n"+mNumber);
        }
    }

    @Override
    public void finishActivity(int requestCode) {
        super.finishActivity(requestCode);
    }

    void finishLoginActivity(int resultCode, Object extraData) {
        Intent resultLoginIntent = new Intent();
        //put login access tokent to intent
        if(resultCode == IntentCode.RESULT_EMERGENC_CONTACT_RECORD){
            if(extraData!=null)
                resultLoginIntent.putExtra("action", (String)extraData);
        }
        setResult(resultCode, resultLoginIntent);
        finish(); //finish this activity with resultcode returned
    }

}
