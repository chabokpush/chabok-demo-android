package com.adp.chabok;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.WakefulBroadcastReceiver;

import com.adp.chabok.activity.WallActivity;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.CaptainMessage;
import com.adp.chabok.data.models.MessageTO;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.PushMessage;

import java.sql.Timestamp;
import java.util.Date;

public class PushMessageReceiver extends WakefulBroadcastReceiver {


    public LocalBroadcastManager broadcaster;
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


        String topic = message.getChannel();

        String temp = null;
        if (message.getData() != null) {
            temp = message.getData().toString();
        }
        broadcaster = LocalBroadcastManager.getInstance(ChabokApplication.getContext());

        if (topic != null && topic.contains(Constants.CAPTAIN_NAME)) {

            CaptainMessage newMessage = new CaptainMessage(
                    message.getBody(),
                    new Timestamp(message.getCreatedAt()),
                    new Timestamp(new Date().getTime()),
                    temp,
                    false
            );

            dao.saveCaptainMessage(newMessage);
            Intent intent = new Intent(Constants.CAPTAIN_MESSAGE_RECEIVED);
            intent.putExtra(Constants.CAPTAIN_NEW_MESSAGE, newMessage);
            broadcaster.sendBroadcast(intent);

        } else {

            String senderId = message.getSenderId() != null ? message.getSenderId().trim() : "";
            String contactInfo = AdpPushClient.get().getUserId();
            boolean isMyMessage = senderId.equals(contactInfo);

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
                    Intent wall = new Intent(Constants.SEND_BROADCAST);
                    wall.putExtra(Constants.NEW_MESSAGE, newMessage);
                    broadcaster.sendBroadcast(wall);
                }

            } else {
                dao.updateSendStatus(message.getSentId());
                if (AdpPushClient.get().isForeground()) {
                    Intent intent = new Intent(Constants.SEND_BROADCAST);
                    intent.putExtra(Constants.MY_MESSAGE_SERVER_ID, message.getSentId());
                    broadcaster.sendBroadcast(intent);
                }
            }
        }
    }

}
