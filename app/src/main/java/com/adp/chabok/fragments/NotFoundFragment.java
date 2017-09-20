package com.adp.chabok.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.common.Constants;


public class NotFoundFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_not_found, container, false);

        TextView msg = view.findViewById(R.id.not_found_msg);
        msg.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_MEDIUM_FONT));

        return view;
    }

}
