package com.adp.chabok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

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

    public static LocalBroadcastManager broadcaster;
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

        String senderId = "";
        boolean isMyMessage = false;
        String registrationEmail = myPreff.getString(Constants.PREFERENCE_EMAIL_ADD, "");
        if (message.getSenderId() != null) {
            if (!message.getSenderId().trim().equals(registrationEmail)) {
                senderId = message.getSenderId();
                isMyMessage = false;
            } else {
                dao.updateSendStatus(message.getSentId());
                isMyMessage = true;
            }
        }

        MessageTO newMessage = new MessageTO(
                message.getId(),
                message.getBody(),
                new Timestamp(message.getCreatedAt()),
                new Timestamp(new Date().getTime()),
                false,
                temp,
                senderId,
                0
        );

        if (!isMyMessage) {
            dao.saveMessage(newMessage, 0);
        }
        sendResult();

    }


    public static void sendResult() {
        Intent intent = new Intent(Constants.MSG_SAVED_2_DB);
        intent.putExtra(Constants.MSG_SAVED_2_DB_EXTRA, 1);
        broadcaster = LocalBroadcastManager.getInstance(ChabokApplication.context);
        broadcaster.sendBroadcast(intent);

    }
}
