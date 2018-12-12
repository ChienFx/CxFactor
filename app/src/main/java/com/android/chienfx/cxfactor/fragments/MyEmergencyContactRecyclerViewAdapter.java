package com.android.chienfx.cxfactor.fragments;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.chienfx.core.contact.ContactEmergency;
import com.android.chienfx.core.helper.MyHelper;
import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.fragments.EmergencyContactFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ContactEmergency} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyEmergencyContactRecyclerViewAdapter extends RecyclerView.Adapter<MyEmergencyContactRecyclerViewAdapter.ViewHolder> {

    private final List<ContactEmergency> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyEmergencyContactRecyclerViewAdapter(List<ContactEmergency> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_emergencycontact, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);

        String strName = MyHelper.trimString(mValues.get(position).mName, 10);
        String strNumber = MyHelper.trimString(mValues.get(position).mNumber, 12);
        String strMessage = MyHelper.trimString(mValues.get(position).mMessage, 14);


        holder.mTvNameView.setText(strName);
        holder.mTvNumberView.setText(strNumber);
        holder.mTvContentView.setText(strMessage);
        if(position%2==1)
            holder.itemView.setBackgroundResource(R.color.white);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    Log.d("Emergency Contact Item", "onClick: hello "+ holder.mTvNameView.getText());
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mTvNameView;
        public final TextView mTvNumberView;
        public final TextView mTvContentView;
        public ContactEmergency mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mTvNameView = view.findViewById(R.id.tvEmergencyContactName);
            mTvNumberView = view.findViewById(R.id.tvEmergencyContactNumber);
            mTvContentView = view.findViewById(R.id.tvEmergencyContactContent);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mTvContentView.getText() + "'";
        }
    }
}
