package com.adp.chabok.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.activity.MainActivity;
import com.adp.chabok.common.Constants;

public class InboxFragment extends Fragment {

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inbox, container, false);
        initView();
        return view;

    }

    private void initView() {
        TextView title = view.findViewById(R.id.action_bar_title);
        title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_LIGHT_FONT));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).navigateToFragment(MainActivity.DISCOVER_FRAGMENT, null);
//                ((MainActivity) getActivity()).navigateToFragment(MainActivity.NOT_FOUND_FRAGMENT, null);
//                ((MainActivity) getActivity()).navigateToFragment(MainActivity.REWARD_FRAGMENT, null);
            }
        });


        ImageView chabok = view.findViewById(R.id.chabok);
        chabok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).gotoAboutUsActivity();
            }
        });


        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).gotoChatActivity();
            }
        });


    }


}
