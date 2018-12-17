package com.android.chienfx.cxfactor.fragments.history;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.chienfx.core.history.History;
import com.android.chienfx.core.history.HistoryReplySMS;
import com.android.chienfx.cxfactor.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {
    List<History> mHistories;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView mAction, mFrom, mIncome, mReply, mTime;



        public MyViewHolder(View view) {
            super(view);
            mAction = view.findViewById(R.id.tvHistoryAction);
            mFrom = view.findViewById(R.id.tvHistoryFrom);
            mIncome = view.findViewById(R.id.tvHistoryIncome);
            mReply = view.findViewById(R.id.tvHistoryReply);
            mTime = view.findViewById(R.id.tvHistoryTime);
        }
    }


    public HistoryAdapter(List<History> historyList) {
        mHistories = historyList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        History history = mHistories.get(position);

        holder.mTime.setText(history.getStringTimeStamp());

        if(history instanceof HistoryReplySMS){
            holder.mIncome.setText("Income message: "+((HistoryReplySMS) history).getIncomeMessage());
            holder.mFrom.setText("From: "+((HistoryReplySMS) history).getSender());
            holder.mReply.setText("Reply message: "+((HistoryReplySMS) history).getReplyMessage());
        }
        else{
            holder.mIncome.setText("");
            holder.mFrom.setText("");
            holder.mReply.setText("");
        }

        if(history.getResult() == false) {
            holder.mAction.setBackgroundColor(R.color.red);
            holder.mAction.setText(history.getStringAction()+" [Failed]");
        }
        else {
            holder.mAction.setBackgroundColor(R.color.colorPrimary);
            holder.mAction.setText(history.getStringAction()+" [Done]");
        }
    }

    @Override
    public int getItemCount() {
        return mHistories.size();
    }
}
