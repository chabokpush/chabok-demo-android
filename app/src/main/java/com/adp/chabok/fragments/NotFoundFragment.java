package com.adp.chabok.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.activity.MainActivity;
import com.adp.chabok.common.Constants;
import com.adp.chabok.ui.Button;


public class NotFoundFragment extends Fragment {

    private View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_not_found, container, false);

        initView();
        return view;
    }

    private void initView() {
        TextView msg = view.findViewById(R.id.not_found_msg);
        msg.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_MEDIUM_FONT));

        Button okBtn = view.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.currentFragmentTag = MainActivity.INBOX_FRAGMENT;
                getActivity().getSupportFragmentManager().popBackStack();

            }
        });

        Bundle bundle = getArguments();
        if (bundle != null) {

            String message = bundle.getString(MainActivity.DIGGING_RESULT_MESSAGE);
            msg.setText(getString(R.string.msg_not_found, message));

        }

    }

}
