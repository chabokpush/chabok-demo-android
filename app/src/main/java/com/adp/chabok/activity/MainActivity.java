package com.adp.chabok.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.AppState;
import com.adpdigital.push.Callback;
import com.adpdigital.push.EventMessage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.adp.chabok.common.Constants.EVENT_TREASURE;
import static com.adp.chabok.common.Constants.STATUS_DIGGING;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String DISCOVER_FRAGMENT = "discover";
    public static final String REWARD_FRAGMENT = "reward";
    public static final String NOT_FOUND_FRAGMENT = "not-found";
    public static final String INBOX_FRAGMENT = "inbox";
    public static final String DIGGING_RESULT_MESSAGE = "digging_result-message";
    private static final String TAG = "MainActivity";
    private static final String KEY_IS_FIRST_TIME = "key_is_first_time";
    private static final String SUBSCRIBE_EVENT = "subscribe_event";
    public static String currentFragmentTag = INBOX_FRAGMENT;
    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private SensorEventListener mSensorListener;

    private boolean mVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ChabokApplication.getInstance().getPushClient().addListener(this);
        ChabokApplication.getInstance().initializeLocationManager();

        checkIfStartTracking();


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

                if (mAccel > 15) {

                    if (getFragmentManager().getBackStackEntryCount() == 0 && INBOX_FRAGMENT.equals(currentFragmentTag)) {
                        if (checkLocationAndSetStatus(STATUS_DIGGING)) {

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

    private void checkIfStartTracking() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationPermissions();
        } else {
            startLocation();
        }
    }

    private void requestLocationPermissions() {
        String[] permissions = new String[2];
        permissions[0] = Manifest.permission.ACCESS_FINE_LOCATION;
        permissions[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
        ActivityCompat.requestPermissions(this, permissions, 10);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: called");
        if (grantResults[0] == PERMISSION_GRANTED && grantResults[1] == PERMISSION_GRANTED) {
            startLocation();

        } else {
            showShouldAllowDialog();
        }
    }

    private void startLocation() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean firstTime = mSharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true);

        if (firstTime) {
            startTracking();
        } else {
            Log.d(TAG, "checkPermissions: permissions already granted, call enableLocationOnLaunch");
            ChabokApplication.getInstance().getLocationManger().enableLocationOnLaunch();
        }
    }

    private void showShouldAllowDialog() {
        CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(MainActivity.this, getResources().getString(R.string.should_allow));
        final AlertDialog dialog = dialogBuilder.create();
        dialogBuilder.setCustomEventListener(new OnCustomListener() {
            @Override
            public void onEvent() {
                dialog.dismiss();
                finish();
            }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


    private void startTracking() {
        Log.d(TAG, "checkIfStartTracking: call startTrackingMe");
        ChabokApplication.getInstance().getLocationManger().startTrackingMe(3 * 60 * 60, 10 * 60, 30);

        PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(KEY_IS_FIRST_TIME, false).apply();
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
                fragment.setArguments(bundle);
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
        mVisible = true;
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(mSensorListener);
        mVisible = false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        currentFragmentTag = INBOX_FRAGMENT;
    }

    private void popFragment() {
        getSupportFragmentManager().popBackStack();
    }

    @SuppressWarnings("unused")
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

    public void onEvent(AppState state) {
        if (state != null && state.equals(AppState.REGISTERED)) {

            subscribeEvent();

        }
    }

    private void subscribeEvent() {

        final SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean subscribed = mSharedPreferences.getBoolean(SUBSCRIBE_EVENT, false);
        AdpPushClient client = AdpPushClient.get();

        if (!subscribed && client.isRegistered()) {


            client.subscribeEvent(EVENT_TREASURE, client.getInstallationId(), new Callback() {
                @Override
                public void onSuccess(Object o) {
                    PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean(SUBSCRIBE_EVENT, true).apply();
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d(TAG, "onFailure: called");
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
                popFragment();
            }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }


    }

    public void showDiggingResult(EventMessage result) {


        if (mVisible && !INBOX_FRAGMENT.equals(currentFragmentTag)) {
            popFragment();
        }

        if (result != null) {
            try {
                JSONObject data = result.getData();

                Bundle bundle = new Bundle();
                bundle.putString(DIGGING_RESULT_MESSAGE, data.getString("msg"));

                Log.d(TAG, "handleMessage: called");
                if (data.has("found")) {
                    boolean found = data.getBoolean("found");
                    if (found) {
                        navigateToFragment(REWARD_FRAGMENT, bundle);
                    } else {
                        navigateToFragment(NOT_FOUND_FRAGMENT, bundle);
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
