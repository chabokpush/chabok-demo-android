package com.adp.chabok.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.adp.chabok.R;
import com.adp.chabok.common.Constants;
import com.adp.chabok.ui.Button;
import com.adp.chabok.ui.TextView;
import com.adpdigital.push.AdpPushClient;
import com.kyleduo.switchbutton.SwitchButton;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView setting = findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingDialog(view);
            }
        });

        Button joinUs = findViewById(R.id.join_us);
        joinUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSite(view);
            }
        });

        TextView phoneNo = findViewById(R.id.phone_no);
        phoneNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialNumber(view);
            }
        });

    }

    public void showSettingDialog(View v) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AboutActivity.this);
        LayoutInflater inflater = (LayoutInflater) AboutActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.settings_dialog, null);

        dialogBuilder.setView(dialogView);
        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


        final SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);

        final SwitchButton switchBtn = dialogView.findViewById(R.id.switch_btn);
        Button okBtn = dialogView.findViewById(R.id.ok_btn);

        if (myPref.getBoolean(Constants.PREFERENCE_OFF_NOTIFY, false)) {
            switchBtn.setChecked(false);
        } else {
            switchBtn.setChecked(true);
        }

        switchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchBtn.isChecked()) {
                    myPref.edit().putBoolean(Constants.PREFERENCE_OFF_NOTIFY, false).apply();
                    AdpPushClient.get().updateNotificationSettings(Constants.CHANNEL_NAME, "default", true);
                } else {
                    myPref.edit().putBoolean(Constants.PREFERENCE_OFF_NOTIFY, true).apply();
                    AdpPushClient.get().updateNotificationSettings(Constants.CHANNEL_NAME, null, false);
                }
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    public void openSite(View v) {

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.lbl_chabok_url)));
        startActivity(i);

    }

    public void dialNumber(View v) {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.lbl_chabok_phone).replace("-", "")));
        startActivity(intent);

    }

}
