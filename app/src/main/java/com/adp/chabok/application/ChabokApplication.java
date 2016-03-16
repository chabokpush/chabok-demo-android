package com.adp.chabok.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.adp.chabok.PushMessageReceiver;
import com.adp.chabok.R;
import com.adp.chabok.activity.BaseActivity;
import com.adp.chabok.activity.HomeActivity;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.DeliveryMessage;
import com.adpdigital.push.NotificationHandler;
import com.adpdigital.push.PushMessage;

/**
 * Created by m.tajik
 * on 2/6/2016.
 */
public class ChabokApplication extends Application {

    public static BaseActivity currentActivity;
    public static Context context;
    AdpPushClient adpPush = null;
    private SharedPreferences myPref;

    public synchronized AdpPushClient getPushClient(Class activityClass) {
        try {
            if (adpPush == null) {
                adpPush = AdpPushClient.init(
                        getApplicationContext(),
                        activityClass,
                        Constants.APP_ID,
                        Constants.USER_NAME,
                        Constants.PASSWORD
                );

                adpPush.setDevelopment(Constants.DEV_MODE);
                adpPush.setSecure(true);
                adpPush.addListener(this);
                myPref = PreferenceManager.getDefaultSharedPreferences(this);
                String clientNo = myPref.getString(Constants.PREFERENCE_EMAIL_ADD, "");
                if (!"".equals(clientNo)) {
                    adpPush.register(clientNo, new String[]{Constants.CHANNEL_NAME});
                }
            }

            NotificationHandler nh = new NotificationHandler() {

                @Override
                public Class getActivityClass(PushMessage pushMessage) {
                    return HomeActivity.class;
                }


                @Override
                public boolean buildNotification(PushMessage pushMessage, NotificationCompat.Builder builder) {
                    boolean result = true;

                    boolean off_notify = myPref.getBoolean(Constants.PREFERENCE_OFF_NOTIFY, false);
                    if (pushMessage.getData() != null && pushMessage.getSenderId() != null) {
                        if (pushMessage.getSenderId().trim().equals(myPref.getString(Constants.PREFERENCE_EMAIL_ADD, ""))) {   // it's users own message
                            return false;
                        } else {
                            if (!pushMessage.getSenderId().trim().equals("")) {  // it's  from users and have proper sender name

                                builder.setTicker(pushMessage.getData().optString(Constants.KEY_NAME) + ": " + pushMessage.getBody().toString());
                                builder.setContentText(pushMessage.getData().optString(Constants.KEY_NAME) + ": " + pushMessage.getBody().toString());
                            }
                        }

                    } else {                                              //it's from server

                        builder.setTicker(getResources().getString(R.string.app_name) + ": " + pushMessage.getBody().toString());
                        builder.setContentText(getResources().getString(R.string.app_name) + ": " + pushMessage.getBody().toString());
                    }

                    if ((HomeActivity.currentPage == 0) && (ChabokApplication.currentActivity instanceof HomeActivity)) {
                        ring();
                        return false;    // user in message tab
                    }

                    if (off_notify) {
                        return false;
                    }

                    return result;
                }
            };
            adpPush.addNotificationHandler(nh);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return adpPush;
    }

    public synchronized AdpPushClient getPushClient() {
        if (adpPush == null) {
            throw new IllegalStateException("Adp Push Client not initialized");
        }
        return adpPush;
    }

    public void onEvent(DeliveryMessage message) {

        ChabokDAO dao = ChabokDAOImpl.getInstance(this);
        dao.updateCounter(message.getDeliveredMessageId());
        PushMessageReceiver.sendResult();

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
        getPushClient(HomeActivity.class);
        this.context = getApplicationContext();

    }


    @Override
    public void onTerminate() {
        // dismiss push client on app termination
        adpPush.dismiss();
        super.onTerminate();
    }

}
