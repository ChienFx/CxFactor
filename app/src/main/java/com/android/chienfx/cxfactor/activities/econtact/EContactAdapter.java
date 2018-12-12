package com.android.chienfx.cxfactor.activities.econtact;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.chienfx.core.contact.EContact;
import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.cxfactor.R;

import java.util.List;

public class EContactAdapter extends RecyclerView.Adapter<EContactAdapter.EContactHolder> {

    private List<EContact> mEContacList;

    public class EContactHolder extends RecyclerView.ViewHolder {
        public TextView mName, mNumber, mMessage;

        public EContactHolder(View view) {
            super(view);
            mName = view.findViewById(R.id.tvEContactName);
            mNumber = view.findViewById(R.id.tvEContactNumber);
            mMessage = view.findViewById(R.id.tvEContactMessage);
        }
    }


    public EContactAdapter(List<EContact> eContactList) {
        this.mEContacList = eContactList;
    }

    @Override
    public EContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.econtact_row, parent, false);

        return new EContactHolder(itemView);
    }

    @Override
    public void onBindViewHolder(EContactHolder holder, int position) {
        EContact contact = mEContacList.get(position);

        holder.mName.setText(contact.mName);
        holder.mNumber.setText(contact.mNumber);
        holder.mMessage.setText(contact.mMessage);

        String strName = MyHelper.trimString(contact.mName, 10);
        String strNumber = MyHelper.trimString(contact.mNumber, 12);
        String strMessage = MyHelper.trimString(contact.mMessage, 14);


        holder.mName.setText(strName);
        holder.mNumber.setText(strNumber);
        holder.mMessage.setText(strMessage);
        if(position%2==1)
            holder.itemView.setBackgroundResource(R.color.white);
    }

    @Override
    public int getItemCount() {
        return mEContacList.size();
    }
}
