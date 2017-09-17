package com.adp.chabok.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.common.Constants;

public class SplashActivity extends Activity {

    int SPLASH_DISPLAY_LENGTH = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Typeface mediumTypeface = Typeface.createFromAsset(getAssets(), Constants.APPLICATION_MEDIUM_FONT);
        TextView designedBy = findViewById(R.id.designed_by);
        designedBy.setTypeface(mediumTypeface);

        new Handler().postDelayed(new Runnable() {

            public void run() {

                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(mainIntent);
                SplashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }


}
