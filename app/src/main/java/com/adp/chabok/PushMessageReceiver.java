package com.adp.chabok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.MessageTO;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.PushMessage;

import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by m.tajik
 * on 2/7/2016.
 */
public class PushMessageReceiver extends WakefulBroadcastReceiver {

    public static final String TAG = PushMessageReceiver.class.getName();

    public LocalBroadcastManager broadcaster;
    SharedPreferences myPreff = null;

    ChabokDAO dao;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle notificationData = intent.getExtras();
        String newTopic = notificationData.getString(AdpPushClient.PUSH_MSG_RECEIVED_TOPIC);
        String newData = notificationData.getString(AdpPushClient.PUSH_MSG_RECEIVED_MSG);
        PushMessage push = PushMessage.fromJson(newData, newTopic);
        dao = ChabokDAOImpl.getInstance(context);

        handleNewMessage(push);
        completeWakefulIntent(intent);
    }


    private void handleNewMessage(PushMessage message) {

        String temp = null;
        if (message.getData() != null) {
            temp = message.getData().toString();
        }

        myPreff = PreferenceManager.getDefaultSharedPreferences(ChabokApplication.context);


        Log.i("MAHDI", message.getSenderId() + "==" + myPreff.getString(Constants.PREFERENCE_EMAIL_ADD, ""));

        String senderId = "";
        boolean its_my_own_message = false;
        if (message.getSenderId() != null) {
            if (!message.getSenderId().trim().equals(myPreff.getString(Constants.PREFERENCE_EMAIL_ADD, ""))) { // my own message that received
                senderId = message.getSenderId();
                its_my_own_message = false;
            } else {
                its_my_own_message = true;
            }
        }

        MessageTO newMessage = new MessageTO(
                message.getId(),
                message.getBody(),
                new Timestamp(message.getCreatedAt()),
                new Timestamp(new Date().getTime()),
                false,
                temp,
                senderId
        );

        if (!its_my_own_message) {
            dao.saveMessage(newMessage, 0);
            sendResult(message);
        }

    }


    public void sendResult(PushMessage message) {
        Intent intent = new Intent(Constants.MSG_SAVED_2_DB);
        Log.i("MAHDI", "******* sende broad cast");

        intent.putExtra(Constants.MSG_SAVED_2_DB_EXTRA, 1);

//        if (ChabokApplication.currentActivity != null) {
        broadcaster = LocalBroadcastManager.getInstance(ChabokApplication.context);
            broadcaster.sendBroadcast(intent);
//        }

    }
}
