package com.adp.chabok.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.ConnectionStatus;
import com.adpdigital.push.EventMessage;
import com.adpdigital.push.location.LocationAccuracy;
import com.adpdigital.push.location.LocationManager;
import com.adpdigital.push.location.OnLocationUpdateListener;

import org.json.JSONException;
import org.json.JSONObject;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static com.adp.chabok.common.Constants.EVENT_TREASURE;
import static com.adp.chabok.common.Constants.STATUS_DIGGING;

public class MainActivity extends AppCompatActivity implements OnLocationUpdateListener {

    public static final String DISCOVER_FRAGMENT = "discover";
    public static final String REWARD_FRAGMENT = "reward";
    public static final String NOT_FOUND_FRAGMENT = "not-found";
    public static final String INBOX_FRAGMENT = "inbox";
    public static final String REWARD_MESSAGE = "reward-message";
    private static final String TAG = "MainActivity";

    private static final LocationAccuracy LOCATION_ACCURACY = LocationAccuracy.MEDIUM;
    private static final int SMALLEST_DISTANCE = 10;
    private static final int INTERVAL = 5000;
    private static final boolean singleUpdate = false;
    private static final boolean backgroundEnabled = true;

    private SensorManager mSensorManager;
    private float mAccel; // acceleration apart from gravity
    private float mAccelCurrent; // current acceleration including gravity
    private float mAccelLast; // last acceleration including gravity
    private SensorEventListener mSensorListener;

    private LocationManager locationManger;
    private Location mCurrentLocation;
    private String eventName = "";
    private EventMessage result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermissions();
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

                if (mAccel > 20) {

                    if (getFragmentManager().getBackStackEntryCount() > 0) {
                        // TODO: fill the progressBar
                    } else {
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

        initializeLocationManager();

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
            locationManger.resume();
        } else {
            finish();
        }
    }

    private void initializeLocationManager() {
        final AdpPushClient client = ((ChabokApplication) getApplication()).getPushClient();
        client.addListener(this);

        locationManger = client.getLocationManager();

        locationManger.enableLocationOnLaunch(this);
        /*LocationParams locationParams = new LocationParams.Builder()
                .setAccuracy(LOCATION_ACCURACY)
                .setDistance(SMALLEST_DISTANCE)
                .setInterval(INTERVAL).build();

        locationManger.start(this,
                locationParams,
                backgroundEnabled, singleUpdate, LocationService.ACTION);*/

    }

    private void updateUI(Location location) {
        if(location != null) {
            //TODO: update ui after location updated
            if(STATUS_DIGGING.equalsIgnoreCase(eventName)) {
                Utils.setUserStatus(STATUS_DIGGING, location);
                eventName = "";
            }
        }
    }

    @Override
    public void onLocationUpdated(Location location) {
        mCurrentLocation = location;
        Log.d(TAG, "onLocationChanged: Longitude: " + mCurrentLocation.getLongitude());
        updateUI(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: called");
        if (mCurrentLocation == null) {
            mCurrentLocation = locationManger.getLastLocation();
            updateUI(mCurrentLocation);
        }
    }

    public void onEvent(final EventMessage message) {
        if (message != null && !this.isFinishing()) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "run: onEvent" + message.getName());
                    result = message;
                }
            });
        }
    }
    public void onEvent(ConnectionStatus state) {
        if (state == ConnectionStatus.CONNECTED) {
            Log.d(TAG, "onEvent: called, connected");
            AdpPushClient.get().enableEventDelivery(EVENT_TREASURE);
        }
    }



    public void setUserStatus(String status) {
        if (STATUS_DIGGING.equalsIgnoreCase(status)) {
            if(mCurrentLocation != null) {
                Utils.setUserStatus(status, mCurrentLocation);
            } else {
                eventName = STATUS_DIGGING;
            }
        } else {
            Utils.setUserStatus(status, null);
        }

    }

    public void showDiggingResult() {
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
