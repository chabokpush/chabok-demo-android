package com.adp.chabok.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adp.chabok.R;

/**
 * Created by m.tajik
 * on 2/21/2016.
 */
public class SettingsFragment extends Fragment {


    boolean key_1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_settings, container, false);

        initilizeUi(fragmentView);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    private void initilizeUi(View v) {

//        final AdpPushClient client = ChabokApplication.cu.getPushClient();
//
//        final SwitchButton s1 = (SwitchButton) v.findViewById(R.id.switch1);
//
//
//        String[] channels = client.getSubscriptions();
//
//        for (String vc : channels) {
//
//            if (vc.trim().equals(Constants.CHANNEL_NAME)) {
//                key_1 = true;
//                s1.setChecked(key_1);
//            }
//
//        }
//
//
//        s1.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (s1.isChecked()) {
//
//                    client.subscribe(Constants.CHANNEL_NAME, new Callback() {
//                        @Override
//                        public void onSuccess(Object o) {
//
//                        }
//
//                        @Override
//                        public void onFailure(Throwable throwable) {
//
//                        }
//                    });  // activate
//
//                } else {
//
//                    client.unsubscribe(Constants.CHANNEL_NAME, new Callback() {
//                        @Override
//                        public void onSuccess(Object o) {
//
//                        }
//
//                        @Override
//                        public void onFailure(Throwable throwable) {
//
//                        }
//                    }); //de activate
//
//                }
//
//
//            }
//        });


    }

}
