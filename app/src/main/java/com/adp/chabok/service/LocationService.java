package com.adp.chabok.service;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.adpdigital.push.AdpPushClient;
import com.google.android.gms.location.LocationResult;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by mohammad
 * on 2/25/17.
 */

public class LocationService extends IntentService {

    public static final String ACTION = "com.adp.chabok.intent.action.PENDING_INTENT_SERVICE";
    private static final String TAG = "LocationService";
    public static final String KEY_COUNTER = "key_counter";
    private int conter;

    public LocationService() {super("NotificationService");}
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LocationService(String name) {
        super(name);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean result = LocationResult.hasResult(intent);
        LocationResult resultData = LocationResult.extractResult(intent);
        if(resultData != null) {
            Location location = resultData.getLastLocation();
            publishLocation(location);
            createNotification(location);
            Log.d(TAG, "onHandleIntent: onHandle called, result: " + result + "\n"
            + "lat: " + location.getLatitude() + " , lon: " + location.getLongitude());
        }
        //Util.setAlarmTask(this);

    }

    private void publishLocation(Location location) {
        try {
            JSONObject data = new JSONObject();
            data.put("lat", location.getLatitude());
            data.put("lng", location.getLongitude());
            data.put("ts", location.getTime());
            AdpPushClient.get().publishEvent("geo", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private static int getNotificationIcon() {
        boolean useSilhouette = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
        return useSilhouette ?
                AdpPushClient.get().getNotificationIconSilhouette()
                :
                AdpPushClient.get().getNotificationIcon();
    }
    private void createNotification(Location location) {
        Notification notification = new NotificationCompat.Builder(getApplicationContext())
                .setContentTitle(getString(com.adpdigital.push.R.string.app_name))
                .setTicker(getString(com.adpdigital.push.R.string.app_name))
                .setContentText("data:" + SystemClock.elapsedRealtime() + " ,lat: " + location.getLatitude() + " ,long: " + location.getLongitude())
                .setSmallIcon(getNotificationIcon())
                //.setContentIntent(pendingIntent)
                //.setOngoing(true)
                .build();
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(10, notification);
    }
}
