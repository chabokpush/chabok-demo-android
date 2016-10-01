package com.adp.chabok.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.Validator;
import com.adp.chabok.ui.EditText;
import com.adpdigital.push.AdpPushClient;

public class RegisterActivity extends AppCompatActivity {

    EditText emailEditText;
    EditText nameEditText;
    EditText companyEditText;
    View.OnClickListener doneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String email = emailEditText.getText().toString();
            final String name = nameEditText.getText().toString();

            if (
                    Validator.validateNotNull(RegisterActivity.this, name, R.string.lbl_name) &&
                            Validator.validateNotNull(RegisterActivity.this, email, R.string.lbl_email) &&
                            Validator.validateEmail(RegisterActivity.this, email)) {

                registerPushClient(email, name);
                gotToMain();

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);

        if (!myPref.contains(Constants.PREFERENCE_EMAIL_ADD)) {
            String phoneNo = myPref.getString(Constants.PREFERENCE_EMAIL_ADD, "");

            if (!"".equals(phoneNo))
                gotToMain();

        } else {
            gotToMain();
        }

        emailEditText = (EditText) findViewById(R.id.emailEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        companyEditText = (EditText) findViewById(R.id.companyEditText);

        emailEditText.setHintTextColor(getResources().getColor(R.color.menu_item_hint_text_color));
        nameEditText.setHintTextColor(getResources().getColor(R.color.menu_item_hint_text_color));
        companyEditText.setHintTextColor(getResources().getColor(R.color.menu_item_hint_text_color));

        findViewById(R.id.doneButton).setOnClickListener(doneClickListener);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void gotToMain() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void registerPushClient(String email, String name) {

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);

        if ("".equals(myPref.getString(Constants.PREFERENCE_EMAIL_ADD, ""))) {


            final AdpPushClient client = ((ChabokApplication) getApplication()).getPushClient();

            SharedPreferences.Editor editor = myPref.edit();
            editor.putString(Constants.PREFERENCE_EMAIL_ADD, email);
            editor.putString(Constants.PREFERENCE_NAME, name);
            editor.apply();
            client.register(email, new String[]{Constants.CHANNEL_NAME});

        }

    }

}
