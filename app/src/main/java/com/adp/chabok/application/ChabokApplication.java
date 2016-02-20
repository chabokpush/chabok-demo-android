package com.adp.chabok.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.adp.chabok.PushMessageReceiver;
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
                public boolean buildNotification(PushMessage pushMessage, NotificationCompat.Builder builder) {
                    boolean result = true;

                    if (pushMessage.getData() != null)
                        if (pushMessage.getSenderId().trim().equals(myPref.getString(Constants.PREFERENCE_EMAIL_ADD, ""))) {   // it's users own message

                            return false;
                        }

                    if ((HomeActivity.currentPage == 0) && (ChabokApplication.currentActivity instanceof HomeActivity)) {
                        ring();
                        return false;    // user in message tab
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

    public void onEvent(DeliveryMessage message) {

        Log.i("MAHDI", "seen =" + message);
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


    public synchronized AdpPushClient getPushClient() {
        if (adpPush == null) {
            throw new IllegalStateException("Adp Push Client not initialized");
        }
        return adpPush;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // initialize push client on app start
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
