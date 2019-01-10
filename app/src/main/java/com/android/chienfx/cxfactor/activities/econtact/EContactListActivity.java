package com.android.chienfx.cxfactor.activities.econtact;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.android.chienfx.cxfactor.core.IntentCode;
import com.android.chienfx.cxfactor.core.contact.EContact;
import com.android.chienfx.cxfactor.core.user.User;
import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.activities.RecyclerTouchListener;

import java.util.List;

public class EContactListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EContactAdapter mAdapter;
    private List<EContact> mEContacts;
    private FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_econtact_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fabAddRecord);

        recyclerView = findViewById(R.id.recyclerViewEContact);

        mEContacts = User.getInstance().getEmergencyContactList();

        mAdapter = new EContactAdapter(mEContacts);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerView.setLayoutManager(mLayoutManager);

        recyclerView.setAdapter(mAdapter);

        // row click listener
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                startEContactItemActivity(position);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEContactItemActivity(-1);
            }
        });
    }

    public void startEContactItemActivity(int position) {
        Intent intent = new Intent(getApplicationContext(), EContactItemActivity.class);
        intent.putExtra("position", position);
        startActivityForResult(intent, IntentCode.REQUEST_EMERGENCY_RECORD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IntentCode.REQUEST_EMERGENCY_RECORD){
            mAdapter.notifyDataSetChanged();
        }
    }
}
