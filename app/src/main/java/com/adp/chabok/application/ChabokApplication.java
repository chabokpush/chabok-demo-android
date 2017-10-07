package com.adp.chabok.application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.adp.chabok.R;
import com.adp.chabok.activity.IntroActivity;
import com.adp.chabok.activity.MainActivity;
import com.adp.chabok.activity.WallActivity;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.Utils;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.ChabokNotification;
import com.adpdigital.push.DeliveryMessage;
import com.adpdigital.push.NotificationHandler;
import com.adpdigital.push.PushMessage;
import com.adpdigital.push.location.LocationManager;
import com.adpdigital.push.location.OnLocationUpdateListener;

import java.util.ArrayList;

import static com.adp.chabok.common.Constants.EVENT_TREASURE;
import static com.adp.chabok.common.Constants.STATUS_DIGGING;

public class ChabokApplication extends Application implements OnLocationUpdateListener {
    private final static int SUMMARY_NOTIFICATION_LIMIT = 1;
    private static final String NOTIFICATION_GROUP_KEY = "group-key";
    private static ChabokApplication instance;
    private AdpPushClient adpPush = null;
    private int messagesCount = 0;
    private ArrayList<String> lines = new ArrayList<>();
    private SharedPreferences myPref;

    private LocationManager locationManger;
    private Location mCurrentLocation;
    private String eventName = "";

    public static Context getContext() {
        return instance.getApplicationContext();
    }


    public static ChabokApplication getInstance() {
        return instance;
    }

    public void clearMessages() {
        lines.clear();
        this.messagesCount = 0;
    }

    public synchronized AdpPushClient getPushClient(Class activityClass) {
        try {
            if (adpPush == null) {
                adpPush = AdpPushClient.init(
                        getApplicationContext(),
                        activityClass,
                        Constants.APP_ID,
                        Constants.API_KEY,
                        Constants.USER_NAME,
                        Constants.PASSWORD
                );

                adpPush.setDevelopment(Constants.DEV_MODE);
                adpPush.enableDeliveryTopic();
                adpPush.addListener(this);


                myPref = PreferenceManager.getDefaultSharedPreferences(this);
                String clientNo = myPref.getString(Constants.PREFERENCE_CONTACT_INFO, "");
                if (!"".equals(clientNo)) {
                    adpPush.register(clientNo, new String[]{Constants.CHANNEL_NAME, Constants.CAPTAIN_CHANNEL_NAME});
                }
            }

            NotificationHandler nh = new NotificationHandler() {

                @Override
                public Class getActivityClass(ChabokNotification chabokNotification) {
                    if (chabokNotification.getMessage() != null && chabokNotification.getMessage().getTopicName() != null) {
                        String topic = chabokNotification.getMessage().getTopicName();
                        return topic != null && topic.contains(Constants.CAPTAIN_CHANNEL_NAME) ? MainActivity.class : WallActivity.class;
                    } else return WallActivity.class;

                }


                @Override
                public boolean buildNotification(ChabokNotification chabokNotification, NotificationCompat.Builder builder) {

                    PushMessage pushMessage = chabokNotification.getMessage();

                    if (pushMessage != null) {
                        lines.add(pushMessage.getBody());

                        if (pushMessage.getData() != null && pushMessage.getSenderId() != null) {
                            if (pushMessage.getSenderId().trim().equals(myPref.getString(Constants.PREFERENCE_CONTACT_INFO, ""))) {   // it's users own message
                                return false;
                            } else {
                                if (!pushMessage.getSenderId().trim().equals("")) {  // it's  from users and have proper sender name

                                    builder.setTicker(pushMessage.getData().optString(Constants.KEY_NAME) + ": " + pushMessage.getBody());
                                    builder.setContentText(pushMessage.getData().optString(Constants.KEY_NAME) + ": " + pushMessage.getBody());
                                }
                            }

                        } else {   //it's from server

                            builder.setTicker(getResources().getString(R.string.app_name) + ": " + pushMessage.getBody());
                            builder.setContentText(getResources().getString(R.string.app_name) + ": " + pushMessage.getBody());

                        }
                    } else {
                        lines.add(chabokNotification.getText());
                    }

                    NotificationManagerCompat mNotificationManager = NotificationManagerCompat.from(getApplicationContext());
                    messagesCount++;
                    builder.setSmallIcon(getNotificationIcon());

                    if (messagesCount > SUMMARY_NOTIFICATION_LIMIT) {
                        if (messagesCount == (SUMMARY_NOTIFICATION_LIMIT + 1)) {
                            mNotificationManager.cancelAll();
                        }
                        builder.setGroup(NOTIFICATION_GROUP_KEY);
                        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                                .setSummaryText(messagesCount + " new messages")
                                .setBigContentTitle(getString(R.string.app_name));
                        int count = 0;
                        for (int i = lines.size() - 1; i >= 0 && count < 5; i--) {
                            inboxStyle.addLine(lines.get(i));
                            count++;
                        }
                        builder.setContentTitle(getString(R.string.app_name))
                                .setContentText(messagesCount + " new messages")
                                .setStyle(inboxStyle)
                                .setNumber(messagesCount)
                                .setGroupSummary(true);
                        Notification notification = builder.build();
                        mNotificationManager.notify(SUMMARY_NOTIFICATION_LIMIT + 1, notification);

                    } else {
                        Notification notification = builder.build();
                        mNotificationManager.notify(messagesCount + 1, notification);
                    }


                    if (getApplicationContext() instanceof WallActivity) {
                        ring();
                        return false;    // user in message tab
                    }

                    return false;

                }
            };
            adpPush.addNotificationHandler(nh);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return adpPush;
    }

    @SuppressWarnings("unused")
    public void onEvent(DeliveryMessage message) {

        ChabokDAO dao = ChabokDAOImpl.getInstance(this);
        dao.updateCounter(message.getDeliveredMessageId());

        Intent intent = new Intent(Constants.SEND_BROADCAST);
        intent.putExtra(Constants.DELIVERED_MESSAGE_SERVER_ID, message.getDeliveredMessageId());
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(ChabokApplication.getContext());
        broadcaster.sendBroadcast(intent);

    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.ic_silhouette : R.drawable.ic_launcher;
    }

    public synchronized AdpPushClient getPushClient() {
        if (adpPush == null) {
            throw new IllegalStateException("Adp Push Client not initialized");
        }
        return adpPush;
    }


    private void ring() {

        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    public void onCreate() {
        super.onCreate();
        getPushClient(IntroActivity.class);
        instance = this;
        adpPush.addListener(this);
        adpPush.enableEventDelivery(EVENT_TREASURE);
        initializeLocationManager();

    }


    @Override
    public void onTerminate() {
        // dismiss push client on app termination
        adpPush.dismiss();
        super.onTerminate();
    }


    @Override
    public void onLocationUpdated(Location location) {
        mCurrentLocation = location;
        updateUserStatus(location);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (mCurrentLocation == null) {
            mCurrentLocation = locationManger.getLastLocation();
            updateUserStatus(mCurrentLocation);
        }
    }

    private void updateUserStatus(Location location) {
        if (location != null) {
            if (STATUS_DIGGING.equalsIgnoreCase(eventName)) {
                Utils.setUserStatus(STATUS_DIGGING, location);
                eventName = "";
            }
        }
    }

    private void initializeLocationManager() {

        locationManger = LocationManager.init(getContext());

//        locationManger.enableLocationOnLaunch();
        locationManger.addListener(this);

    }

    public LocationManager getLocationManger() {
        return locationManger;
    }

    public Location getmCurrentLocation() {
        return mCurrentLocation;
    }

    public void setmCurrentLocation(Location mCurrentLocation) {
        this.mCurrentLocation = mCurrentLocation;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}
