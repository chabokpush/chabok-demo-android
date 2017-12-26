package com.adp.chabok.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adpdigital.push.AdpPushClient;

public class SplashActivity extends Activity {

    int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        final SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);


        new Handler().postDelayed(new Runnable() {

            public void run() {

                String clientNo = AdpPushClient.get().getUserId();

                Intent mainIntent = new Intent(SplashActivity.this, (clientNo != null && !"".equals(clientNo)) ? MainActivity.class : IntroActivity.class );
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


}
