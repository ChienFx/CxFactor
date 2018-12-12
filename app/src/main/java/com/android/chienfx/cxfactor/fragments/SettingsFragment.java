package com.android.chienfx.cxfactor.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;

import com.android.chienfx.core.Definition;
import com.android.chienfx.core.user.User;
import com.android.chienfx.cxfactor.R;
import com.android.chienfx.cxfactor.activities.MainActivity;
import com.android.chienfx.cxfactor.activities.econtact.EContactListActivity;

import static com.android.chienfx.cxfactor.activities.MainActivity.CURRENT_TAG;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View mViewInstance;
    private OnFragmentInteractionListener mListener;
    private Handler mHandler;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mViewInstance = inflater.inflate(R.layout.fragment_settings, container, false);
        return mViewInstance;
    }

    SwitchCompat swFindMyPhone, swAutoReplySMS, swDeclineCall;
    ImageButton imbtnEmergencyContact, imbtnSmsReplier, imbtnBlaclist;
    User user = User.getInstance();
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        registerViews();

        loadCurrentState();

        handleViewEvents();

        super.onViewCreated(view, savedInstanceState);
    }

    private void handleViewEvents() {
        swFindMyPhone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setFindPhone(isChecked);
            }
        });
        swAutoReplySMS.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setAutoReplySms(isChecked);
            }
        });
        swDeclineCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                user.setDeclineCall(isChecked);
            }
        });

        imbtnEmergencyContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), EContactListActivity.class));
            }
        });
    }

    private void loadCurrentState() {
        swFindMyPhone.setChecked(user.getFindPhone());
        swAutoReplySMS.setChecked(user.getAutoReplySms());
        swDeclineCall.setChecked(user.getDeclineCall());
    }

    private void registerViews() {
        mHandler = new Handler();
        swFindMyPhone = mViewInstance.findViewById(R.id.swFindMyPhone);
        swAutoReplySMS = mViewInstance.findViewById(R.id.swAutoReplySMS);
        swDeclineCall = mViewInstance.findViewById(R.id.swDeclineCall);
        imbtnEmergencyContact = mViewInstance.findViewById(R.id.imbtnEmergencyContact);
        imbtnSmsReplier = mViewInstance.findViewById(R.id.imbtnSMSReplier);
        imbtnBlaclist = mViewInstance.findViewById(R.id.imbtnBlacklist);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
