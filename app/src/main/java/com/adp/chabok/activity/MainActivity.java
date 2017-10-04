package com.adp.chabok.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Utils;
import com.adp.chabok.fragments.DiscoverFragment;
import com.adp.chabok.fragments.InboxFragment;
import com.adp.chabok.fragments.NotFoundFragment;
import com.adp.chabok.fragments.RewardFragment;
import com.adp.chabok.ui.CustomDialogBuilder;
import com.adp.chabok.ui.OnCustomListener;
import com.adpdigital.push.EventMessage;
import com.adpdigital.push.location.LocationAccuracy;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.adp.chabok.common.Constants.STATUS_DIGGING;

public class MainActivity extends AppCompatActivity {

    public static final String DISCOVER_FRAGMENT = "discover";
    public static final String REWARD_FRAGMENT = "reward";
    public static final String NOT_FOUND_FRAGMENT = "not-found";
    public static final String INBOX_FRAGMENT = "inbox";
    public static final String REWARD_MESSAGE = "reward-message";
    private static final String TAG = "MainActivity";

    private static final LocationAccuracy LOCATION_ACCURACY = LocationAccuracy.HIGH;
    private static final int SMALLEST_DISTANCE = 0;
    private static final int INTERVAL = 5000;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private SensorEventListener mSensorListener;

    private String currentFragmentTag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChabokApplication.getInstance().getPushClient().addListener(this);

        checkPermissions();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame, new InboxFragment())
                    .commitAllowingStateLoss();
            currentFragmentTag = INBOX_FRAGMENT;
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

                    if (getFragmentManager().getBackStackEntryCount() == 0 && INBOX_FRAGMENT.equals(currentFragmentTag)) {
                        if(checkLocationAndSetStatus(STATUS_DIGGING)){

                            navigateToFragment(MainActivity.DISCOVER_FRAGMENT, null);
                        }

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
                currentFragmentTag = INBOX_FRAGMENT;
                break;

            case DISCOVER_FRAGMENT:
                fragment = new DiscoverFragment();
                tr.replace(R.id.frame, fragment, DISCOVER_FRAGMENT).addToBackStack(InboxFragment.class.getName()).commitAllowingStateLoss();
                currentFragmentTag = DISCOVER_FRAGMENT;
                break;

            case REWARD_FRAGMENT:
                fragment = new RewardFragment();
                fragment.setArguments(bundle);
                tr.replace(R.id.frame, fragment, REWARD_FRAGMENT).addToBackStack(InboxFragment.class.getName()).commitAllowingStateLoss();
                currentFragmentTag = REWARD_FRAGMENT;
                break;

            case NOT_FOUND_FRAGMENT:
                fragment = new NotFoundFragment();
                tr.replace(R.id.frame, fragment, NOT_FOUND_FRAGMENT).addToBackStack(InboxFragment.class.getName()).commitAllowingStateLoss();
                currentFragmentTag = NOT_FOUND_FRAGMENT;
                break;

        }

    }


    public void gotoWallActivity() {
        Intent intent = new Intent(MainActivity.this, WallActivity.class);
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

    private void checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i(TAG, "Permission check failed. Please handle it in your app before setting up location");
            String[] permissions = new String[2];
            permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
            permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
            ActivityCompat.requestPermissions(this, permissions, 10);

        } else {
            Log.d(TAG, "checkPermissions: permissions already granted");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
            ChabokApplication.getInstance().getLocationManger().resume();
//            ChabokApplication.getInstance().getPushClient().getLocationManager().startTrackingMe(3 * 60 * 60, 10 * 60, 50);
            ChabokApplication.getInstance().getPushClient().getLocationManager().startTrackingMe(3 * 60 * 60, 10, 0);

        } else {
            finish();
        }
    }

    public void onEvent(final EventMessage message) {
        if (message != null && !this.isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: onEvent" + message.getName());
                    showDiggingResult(message);
                }
            });
        }
    }

    public boolean checkLocationAndSetStatus(String status) {
        boolean isLocationAvailable = false;

        if (STATUS_DIGGING.equalsIgnoreCase(status)) {

            if (ChabokApplication.getInstance().getLocationManger().getLastLocation() != null) {
                isLocationAvailable = true;
                ChabokApplication.getInstance().setmCurrentLocation(ChabokApplication.getInstance().getLocationManger().getLastLocation());
                Utils.setUserStatus(status, ChabokApplication.getInstance().getmCurrentLocation());
            } else {
                showLocationUnavailable();
                ChabokApplication.getInstance().setEventName(STATUS_DIGGING);
            }
        } else {
            Utils.setUserStatus(status, null);
        }
        return isLocationAvailable;
    }

    private void showLocationUnavailable() {

        CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(MainActivity.this, getResources().getString(R.string.location_unavailable));
        final AlertDialog dialog = dialogBuilder.create();
        dialogBuilder.setCustomEventListener(new OnCustomListener() {
            @Override
            public void onEvent() {
                dialog.dismiss();
                getSupportFragmentManager().popBackStack();
            }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


    }

    /*private void startLocation() {
        locationManger.startLocationUpdates(
                new LocationParams.Builder()
                        .setAccuracy(LOCATION_ACCURACY)
                        .setDistance(SMALLEST_DISTANCE)
                        .setInterval(INTERVAL).build());
    }*/

    public void showDiggingResult(EventMessage result) {

        getSupportFragmentManager().popBackStack();
        if (result != null) {
            try {
                JSONObject data = result.getData();
                Log.d(TAG, "handleMessage: called");
                if (data.has("found")) {
                    boolean found = data.getBoolean("found");
                    if (found) {
                        Bundle bundle = new Bundle();
                        bundle.putString(REWARD_MESSAGE, data.getString("msg"));
                        navigateToFragment(REWARD_FRAGMENT, bundle);
                    } else {
                        navigateToFragment(NOT_FOUND_FRAGMENT, null);
                    }
                } else {
                    navigateToFragment(NOT_FOUND_FRAGMENT, null);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }
}
