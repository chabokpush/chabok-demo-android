package com.adp.chabok.activity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.adp.chabok.R;
import com.adp.chabok.fragments.DiscoverFragment;
import com.adp.chabok.fragments.InboxFragment;
import com.adp.chabok.fragments.NotFoundFragment;
import com.adp.chabok.fragments.RewardFragment;

public class MainActivity extends AppCompatActivity {

    public static final String DISCOVER_FRAGMENT = "discover";
    public static final String REWARD_FRAGMENT = "reward";
    public static final String NOT_FOUND_FRAGMENT = "not-found";
    public static final String INBOX_FRAGMENT = "inbox";

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private SensorEventListener mSensorListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame, new InboxFragment())
                    .commit();
        }

        mSensorListener = new SensorEventListener() {

            public void onSensorChanged(SensorEvent se) {
                float x = se.values[0];
                float y = se.values[1];
                float z = se.values[2];
                mAccelLast = mAccelCurrent;
                mAccelCurrent = (float) Math.sqrt((double) (x * x + y * y + z * z));
                float delta = mAccelCurrent - mAccelLast;
                mAccel = mAccel * 0.9f + delta;

                if (mAccel > 10) {

                   if(getFragmentManager().getBackStackEntryCount() > 0 ){
                       // TODO: fill the progressBar
                   } else  {
                       navigateToFragment(MainActivity.DISCOVER_FRAGMENT, null);

                   }

                }
            }

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }
        };

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mAccel = 0.00f;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;


    }

    public void navigateToFragment(String tag, Bundle bundle) {
        Fragment fragment;
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);

        if (bundle == null) {
            bundle = new Bundle();
        }

        switch (tag) {
            case INBOX_FRAGMENT:
                fragment = new InboxFragment();
                tr.replace(R.id.frame, fragment, INBOX_FRAGMENT).commit();
                break;

            case DISCOVER_FRAGMENT:
                fragment = new DiscoverFragment();
                tr.replace(R.id.frame, fragment, DISCOVER_FRAGMENT).addToBackStack(InboxFragment.class.getName()).commit();
                break;

            case REWARD_FRAGMENT:
                fragment = new RewardFragment();
                fragment.setArguments(bundle);
                tr.replace(R.id.frame, fragment, REWARD_FRAGMENT).addToBackStack(InboxFragment.class.getName()).commit();
                break;

            case NOT_FOUND_FRAGMENT:
                fragment = new NotFoundFragment();
                tr.replace(R.id.frame, fragment, NOT_FOUND_FRAGMENT).addToBackStack(InboxFragment.class.getName()).commit();
                break;

        }

    }


    public void gotoChatActivity() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void gotoAboutUsActivity() {
        Intent intent = new Intent(MainActivity.this, AboutActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
    }
}
