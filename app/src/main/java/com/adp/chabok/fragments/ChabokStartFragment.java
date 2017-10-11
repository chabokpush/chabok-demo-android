package com.adp.chabok.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.activity.IntroActivity;
import com.adp.chabok.common.Constants;


public class ChabokStartFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);
        TextView captainFa = view.findViewById(R.id.captain_fa);
        TextView captainEn = view.findViewById(R.id.captain_en);


        Typeface lightTypeface = Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_LIGHT_FONT);
        captainFa.setTypeface(lightTypeface);
        captainEn.setTypeface(lightTypeface);

        RelativeLayout startBtnLayout = view.findViewById(R.id.start_btn_layout);
        ImageView startBtn = view.findViewById(R.id.start_btn);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((IntroActivity) getActivity()).navigateToFragment(IntroActivity.MAP_FRAGMENT, null);
            }
        };

        startBtnLayout.setOnClickListener(onClickListener);
        startBtn.setOnClickListener(onClickListener);

        return view;

    }

}
