package com.adp.chabok.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;

public class BaseActivity extends AppCompatActivity {

    String TAG = "chabok_Tag";

    @Override
    protected void onResume() {
        super.onResume();
        attachPushClient();

        final AdpPushClient client = ((ChabokApplication) getApplication()).getPushClient();
        client.resetBadge();
    }

    @Override
    protected void onPause() {
        super.onPause();
        detachPushClient();
    }

    @Override
    protected void onDestroy() {
        detachPushClient();
        super.onDestroy();
    }


    public void onEvent(final ConnectionStatus status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateConnectionStatus(status);
            }
        });
    }

    private void attachPushClient() {
        final AdpPushClient client = ((ChabokApplication) getApplication()).getPushClient();
        if (client != null) {
            client.addListener(this);
        }
        fetchAndUpdateConnectionStatus();
    }

    private void detachPushClient() {
        final AdpPushClient client = ((ChabokApplication) getApplication()).getPushClient();
        if (client != null) {
            client.removePushListener(this);
        }
    }

    protected void fetchAndUpdateConnectionStatus() {
        final AdpPushClient client = ((ChabokApplication) getApplication()).getPushClient();
        if (client == null) {
            return;
        }
        client.getStatus(new Callback<ConnectionStatus>() {
            @Override
            public void onSuccess(ConnectionStatus connectionStatus) {
                updateConnectionStatus(connectionStatus);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.i(TAG, "errrror ");
            }
        });
    }

    protected void updateConnectionStatus(ConnectionStatus status) {

        ImageView connectionStatus = findViewById(R.id.connection_status);
        if (connectionStatus != null && status != null) {
            switch (status) {
                case CONNECTED:
                    connectionStatus.setBackgroundResource(R.drawable.green_circle);
                    break;
                case CONNECTING:
                    connectionStatus.setBackgroundResource(R.drawable.red_circle);
                    break;
                case DISCONNECTED:
                    connectionStatus.setBackgroundResource(R.drawable.red_circle);
                    break;
            }
        }
    }

}
