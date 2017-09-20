package com.adp.chabok.fragments;

import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.adp.chabok.R;
import com.adp.chabok.activity.IntroActivity;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.Validator;
import com.adp.chabok.ui.Button;
import com.adpdigital.push.AdpPushClient;

import java.util.HashMap;


public class UserInfoFragment extends Fragment {

    private View view;
    private EditText fullName;
    private EditText contactInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_user_info, container, false);
        initView();
        return view;
    }

    private void initView() {
        Typeface lightTypeface = Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_LIGHT_FONT);

        fullName = view.findViewById(R.id.full_name);
        fullName.setTypeface(lightTypeface);

        contactInfo = view.findViewById(R.id.contact_info);
        contactInfo.setTypeface(lightTypeface);

        Bundle bundle = getArguments();
        if (bundle != null) {

            int avatarId = bundle.getInt(IntroActivity.AVATAR_ID);
            ImageView avatar = view.findViewById(R.id.avatar);
            avatar.setBackgroundResource(avatarId);

        }

        Button letsGo = view.findViewById(R.id.lets_go_btn);
        letsGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Validator.validateNotNull(getActivity(), fullName.getText().toString(), R.string.full_name) &&
                        Validator.validateName(getActivity(), fullName.getText().toString()) &&
                        Validator.validateNotNull(getActivity(), contactInfo.getText().toString(), R.string.contact_info) &&
                        Validator.validatePhoneNumber(getActivity(), contactInfo.getText().toString())) {


                    SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(getActivity());

                    if ("".equals(myPref.getString(Constants.PREFERENCE_CONTACT_INFO, ""))) {

                        AdpPushClient client = ChabokApplication.getInstance().getPushClient();

                        SharedPreferences.Editor editor = myPref.edit();
                        editor.putString(Constants.PREFERENCE_CONTACT_INFO, contactInfo.getText().toString());
                        editor.putString(Constants.PREFERENCE_NAME, fullName.getText().toString());
                        editor.apply();

                        HashMap userInfo = new HashMap<>();
                        userInfo.put("name", fullName.getText().toString());
                        client.setUserInfo(userInfo);


                        client.register(contactInfo.getText().toString(), new String[]{Constants.CHANNEL_NAME});

                        ((IntroActivity) getActivity()).gotToMain();


                    }

                }
            }
        });

    }

}
