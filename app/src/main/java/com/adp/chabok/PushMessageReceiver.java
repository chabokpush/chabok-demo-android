package com.adp.chabok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.adp.chabok.activity.HomeActivity;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.MessageTO;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.PushMessage;

import java.sql.Timestamp;
import java.util.Date;

public class PushMessageReceiver extends WakefulBroadcastReceiver {


    private ChabokDAO dao;

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle notificationData = intent.getExtras();
        String newTopic = notificationData.getString(AdpPushClient.PUSH_MSG_RECEIVED_TOPIC);
        String newData = notificationData.getString(AdpPushClient.PUSH_MSG_RECEIVED_MSG);
        PushMessage push = PushMessage.fromJson(newData, newTopic);
        dao = ChabokDAOImpl.getInstance(context);

        handleNewMessage(context, push);
        completeWakefulIntent(intent);
    }


    private void handleNewMessage(Context context, PushMessage message) {

        String temp = null;
        if (message.getData() != null) {
            temp = message.getData().toString();
        }

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(ChabokApplication.getContext());

        String senderId = message.getSenderId() != null ?  message.getSenderId().trim() : "";
        String registrationEmail = myPref.getString(Constants.PREFERENCE_EMAIL_ADD, "");
        boolean isMyMessage = senderId.equals(registrationEmail);




        if (!isMyMessage) {

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

            dao.saveMessage(newMessage, 0);

            if (AdpPushClient.get().isForeground()) {
                Intent reloadIntent = new Intent(context, HomeActivity.class);
                reloadIntent.putExtra(Constants.RELOAD_MESSAEGS, true);
                reloadIntent.putExtra(Constants.NEW_MESSAGE, newMessage);
                reloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(reloadIntent);
            }

        } else {
            dao.updateSendStatus(message.getSentId());
            if (AdpPushClient.get().isForeground()) {
                Intent reloadIntent = new Intent(context, HomeActivity.class);
                reloadIntent.putExtra(Constants.RELOAD_MESSAEGS, true);
                reloadIntent.putExtra(Constants.MY_MESSAGE_SERVER_ID, message.getSentId());
                reloadIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(reloadIntent);
            }
        }


    }

}
