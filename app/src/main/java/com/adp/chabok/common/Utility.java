package com.adp.chabok.common;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.adp.chabok.application.ChabokApplication;

public class Utility {

    public static void sendResult() {
        Intent intent = new Intent(Constants.MSG_SAVED_2_DB);
        intent.putExtra(Constants.MSG_SAVED_2_DB_EXTRA, 1);
        LocalBroadcastManager broadcaster = LocalBroadcastManager.getInstance(ChabokApplication.getContext());
        broadcaster.sendBroadcast(intent);

    }
}
