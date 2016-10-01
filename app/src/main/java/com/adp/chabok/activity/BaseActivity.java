package com.adp.chabok.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.ui.TextView;
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
            client.setPushListener(this);
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

        TextView status_text = (TextView) findViewById(R.id.textView_status);
        if (status_text != null && status != null) {
            switch (status) {
                case CONNECTED:
                    status_text.setText(getResources().getString(R.string.action_online));
                    break;
                case CONNECTING:
                    status_text.setText(getResources().getString(R.string.action_trying_to_connect));
                    break;
                case DISCONNECTED:
                    status_text.setText(getResources().getString(R.string.action_offline));
                    break;
            }
        }
    }

}
