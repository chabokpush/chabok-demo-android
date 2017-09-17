package com.adp.chabok.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adp.chabok.R;
import com.adp.chabok.activity.MainActivity;
import com.adp.chabok.ui.Button;


public class MapFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map, container, false);

        Button okBtn = view.findViewById(R.id.ok_btn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).navigateToFragment(MainActivity.AVATAR_FRAGMENT);
            }
        });

        return view;

    }

}
