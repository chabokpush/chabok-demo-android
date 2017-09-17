package com.adp.chabok.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.adp.chabok.R;
import com.adp.chabok.fragments.ChabokStartFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame, new ChabokStartFragment())
                    .commit();
        }

    }


}
