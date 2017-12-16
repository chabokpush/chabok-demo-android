package com.adp.chabok.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.Utils;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.MessageTO;
import com.adp.chabok.fragments.MessageFragment;
import com.adp.chabok.ui.EditText;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;
import com.adpdigital.push.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@SuppressWarnings("StatementWithEmptyBody")
public class WallActivity extends BaseActivity {

    private ChabokDAO dao;
    private MessageFragment messageFragment;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wall);

        messageFragment = MessageFragment.getInstance();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame, messageFragment)
                    .commit();
        }


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getExtras().get(Constants.DELIVERED_MESSAGE_SERVER_ID) != null) {
                    String myMessageServerId = intent.getExtras().getString(Constants.DELIVERED_MESSAGE_SERVER_ID);
                    messageFragment.updateDeliveredCount(myMessageServerId);

                } else if (intent.getExtras().get(Constants.NEW_MESSAGE) != null) {
                    MessageTO newMessage = (MessageTO) intent.getExtras().get(Constants.NEW_MESSAGE);
                    messageFragment.updateMessageList(newMessage);

                } else if (intent.getExtras().get(Constants.MY_MESSAGE_SERVER_ID) != null) {
                    String myMessageServerId = intent.getExtras().getString(Constants.MY_MESSAGE_SERVER_ID);
                    messageFragment.updateMessageItem(myMessageServerId);
                }

            }
        };


        dao = ChabokDAOImpl.getInstance(this);
        ((ChabokApplication) getApplication()).clearMessages();


    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SEND_BROADCAST);
        LocalBroadcastManager.getInstance(WallActivity.this).registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    public void sendMessage(View v) {

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        final EditText msg = findViewById(R.id.editText_out_message);

        if (msg != null && !msg.getText().toString().equals(""))
            try {
                Utils.setUserStatus(Constants.STATUS_SENT, null);

                AdpPushClient pushClient = ((ChabokApplication) getApplication()).getPushClient();
                PushMessage myPushMessage = new PushMessage();
                myPushMessage.setBody(msg.getText().toString().trim());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.KEY_NAME, myPref.getString(Constants.PREFERENCE_NAME, ""));
                myPushMessage.setData(jsonObject);
                myPushMessage.setChannel(Constants.CHANNEL_NAME);
                myPushMessage.setId(UUID.randomUUID().toString());
                myPushMessage.setUseAsAlert(true);
                myPushMessage.setAlertText(myPref.getString(Constants.PREFERENCE_NAME, "") + ": " + msg.getText().toString().trim());
                MessageTO message = new MessageTO();
                message.setMessage(msg.getText().toString().trim());
                message.setData(jsonObject.toString());
                message.setSentDate(new Timestamp(new Date().getTime()));
                message.setReceivedDate(new Timestamp(new Date().getTime()));
                message.setServerId(myPushMessage.getId());
                dao.saveMessage(message, 0);
                messageFragment.updateMessageList(message);
                msg.setText("");

                pushClient.publish(myPushMessage, new Callback() {
                    @Override
                    public void onSuccess(Object o) {
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });

            } catch (JSONException e) {
                Log.e("LOG", "e=" + e.getMessage(), e);
            }

    }

    @Override
    public void onEvent(ConnectionStatus status) {
        super.onEvent(status);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onResume() {
        super.onResume();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

}
