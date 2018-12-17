package com.android.chienfx.cxfactor.activities.smsreplier;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.core.sms.SMSReplierRecord;
import com.android.chienfx.cxfactor.R;

import java.util.List;

class SmsReplierAdapter extends RecyclerView.Adapter<SmsReplierAdapter.SmsReplierHolder> {

    private List<SMSReplierRecord> mSmsRepliers;

    public class SmsReplierHolder extends RecyclerView.ViewHolder {
        public TextView mStart, mEnd, mMessage;

        public SmsReplierHolder(View view) {
            super(view);
            mStart = view.findViewById(R.id.tvSmsReplierStart);
            mEnd = view.findViewById(R.id.tvSmsReplierEnd);
            mMessage = view.findViewById(R.id.tvSmsReplierMessage);
        }
    }


    public SmsReplierAdapter(List<SMSReplierRecord> smsRepliers) {
        this.mSmsRepliers = smsRepliers;
    }

    @Override
    public SmsReplierAdapter.SmsReplierHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.sms_replier_row, parent, false);

        return new SmsReplierAdapter.SmsReplierHolder(itemView);
    }

    @Override
    public void onBindViewHolder(SmsReplierAdapter.SmsReplierHolder holder, int position) {
        SMSReplierRecord replier = mSmsRepliers.get(position);

        String strMessage = MyHelper.trimString(replier.getMessage(), 25);

        holder.mStart.setText(replier.getTimeStart());
        holder.mEnd.setText(replier.getTimeEnd());
        holder.mMessage.setText(strMessage);

        if(position%2==1)
            holder.itemView.setBackgroundResource(R.color.white);
    }

    @Override
    public int getItemCount() {
        return mSmsRepliers.size();
    }
}

