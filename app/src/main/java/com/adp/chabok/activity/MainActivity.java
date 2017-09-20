package com.adp.chabok.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.service.LocationService;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.location.LocationAccuracy;
import com.adpdigital.push.location.LocationManager;
import com.adpdigital.push.location.LocationParams;
import com.adpdigital.push.location.OnLocationUpdateListener;
import java.util.ArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class MainActivity extends AppCompatActivity implements OnLocationUpdateListener {

    private static final String TAG = "MainActivity";

    private static final LocationAccuracy LOCATION_ACCURACY  = LocationAccuracy.MEDIUM;
    private static final int SMALLEST_DISTANCE = 0;
    private static final int INTERVAL = 500;
    private static final boolean singleUpdate = false;
    private static final boolean backgroundEnabled = true;

    private LocationManager locationManger;
    private Location mCurrentLocation;
    private double latDifference;
    private ListView list;
    private ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter(this,  android.R.layout.simple_list_item_1, new ArrayList<String>());
        list.setAdapter(adapter);

        checkPermissions();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initializeLocationManager();
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
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the
            // missing permissions, and then overriding onRequestPermissionsResult
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
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
        locationManger = client.getLocationManager();

        LocationParams locationParams = new LocationParams.Builder()
                .setAccuracy(LOCATION_ACCURACY)
                .setDistance(SMALLEST_DISTANCE)
                .setInterval(INTERVAL).build();

        locationManger.start(this,
                locationParams,
                backgroundEnabled, singleUpdate, LocationService.ACTION);

    }

    private void updateUI(Location location) {
        adapter.add("lat: " + location.getLatitude() + " ,lon: " + location.getLongitude());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLocationUpdated(Location location) {
        if(mCurrentLocation != null) {
            latDifference = Math.abs(mCurrentLocation.getLatitude() - location.getLatitude());
        }
        mCurrentLocation = location;
        Log.d(TAG, "onLocationChanged: Longitude: " + mCurrentLocation.getLongitude());
        updateUI(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected: called");
        if (mCurrentLocation == null) {
            mCurrentLocation = locationManger.getLastLocation();
            latDifference = 0;
            updateUI(mCurrentLocation);
        }
    }
}
