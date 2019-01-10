package com.android.chienfx.cxfactor.activities.blacklist;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.chienfx.cxfactor.core.IntentCode;
import com.android.chienfx.cxfactor.core.contact.Contact;
import com.android.chienfx.cxfactor.core.user.User;
import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.activities.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

public class BlacklistActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BlacklistApdapter mAdapter;
    private List<Contact> mBlacklist;
    private FloatingActionButton fabAdd, fabRemove;
    private List<Integer> mSelectedList;
    private String mNumber;
    private String mName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blacklist);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fabAdd = findViewById(R.id.fabAddBlacklist);
        fabRemove = findViewById(R.id.fabRemoveBlacklist);



        mSelectedList = new ArrayList<>();

        recyclerView = findViewById(R.id.recyclerViewBlacklist);

        mBlacklist = User.getInstance().getBlacklist();


        mAdapter = new BlacklistApdapter(mBlacklist);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
            }

            @Override
            public void onLongClick(View view, int position) {
                if(mSelectedList.contains(position))
                    unselectItem(view, position);
                else
                    selectItem(view, position);
                updateUI();
            }
        }));

        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, IntentCode.REQUEST_PICK_CONTACT);
                updateUI();
            }
        });

        fabRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(BlacklistActivity.this);
                builder.setTitle("DELETE Numbers from Blacklist?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        removeBlacklistNumber();
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
        updateUI();

    }

    private void removeBlacklistNumber() {
        User user = User.getInstance();
        for(int id: mSelectedList){
            User.getInstance().deleteBlacklistContact(user.getBlacklistContactByIndex(id));
        }
        mSelectedList.clear();
        mAdapter.notifyDataSetChanged();
        updateUI();
    }

    @SuppressLint("RestrictedApi")
    private void updateUI() {
        if(mSelectedList.isEmpty()){
            fabRemove.setVisibility(View.GONE);
        }
        else{
            fabRemove.setVisibility(View.VISIBLE);
        }
    }

    private void selectItem(View view, int position) {
        mSelectedList.add(position);
        view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
    }

    private void unselectItem(View view, int position) {
        mBlacklist.remove(position);
        view.setBackgroundColor(getResources().getColor(R.color.transparent));
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

            if(mNumber.length()>0 && mName.length()>0){
                User.getInstance().addNumberToBlacklist(new Contact(mNumber, mName));
                mAdapter.notifyDataSetChanged();
            }

        }
    }
}
