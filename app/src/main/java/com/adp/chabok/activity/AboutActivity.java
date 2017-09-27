package com.adp.chabok.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
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

    }

    public void showSettingDialog(View v) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(AboutActivity.this);
        LayoutInflater inflater = (LayoutInflater) AboutActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.activity_settings, null);

        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        final ChabokApplication app = (ChabokApplication) getApplication();


        final SwitchButton s1 = dialogView.findViewById(R.id.switch1);
        final SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (myPref.getBoolean(Constants.PREFERENCE_OFF_NOTIFY, false)) {
            s1.setChecked(false);
        } else {
            s1.setChecked(true);
        }

        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s1.isChecked()) {
                    myPref.edit().putBoolean(Constants.PREFERENCE_OFF_NOTIFY, false).apply();
                    app.getPushClient().updateNotificationSettings(Constants.CHANNEL_NAME, "default", true);
                } else {
                    myPref.edit().putBoolean(Constants.PREFERENCE_OFF_NOTIFY, true).apply();
                    app.getPushClient().updateNotificationSettings(Constants.CHANNEL_NAME, null, false);
                }
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
