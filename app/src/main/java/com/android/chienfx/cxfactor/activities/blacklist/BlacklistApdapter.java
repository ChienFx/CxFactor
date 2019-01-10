package com.android.chienfx.cxfactor.activities.blacklist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.chienfx.cxfactor.core.contact.Contact;
import com.android.chienfx.cxfactor.R;

import java.util.List;

class BlacklistApdapter extends RecyclerView.Adapter<BlacklistApdapter.BlacklistHolder>{
    private List<Contact> mBlacklists;

    public class BlacklistHolder extends RecyclerView.ViewHolder {
        public TextView mNumber, mName;

        public BlacklistHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.tvContactName);
            mNumber = view.findViewById(R.id.tvContactNumber);
        }
    }


    public BlacklistApdapter(List<Contact> blacklist) {
        this.mBlacklists = blacklist;
    }

    @Override
    public BlacklistApdapter.BlacklistHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row, parent, false);

        return new BlacklistApdapter.BlacklistHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistApdapter.BlacklistHolder holder, int position) {
        Contact contact = mBlacklists.get(position);

        holder.mName.setText(contact.mName);
        holder.mNumber.setText(contact.mNumber);

        holder.itemView.setBackgroundResource(R.color.white);
    }



    @Override
    public int getItemCount() {
        return mBlacklists.size();
    }
}
