package com.adp.chabok.common;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.adp.chabok.application.ChabokApplication;
import com.adpdigital.push.AdpPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import static com.adp.chabok.common.Constants.STATUS_DIGGING;

/**
 * Created by mohammad on 9/23/17.
 */

public class Utils {
    private static final String TAG = "Utils";

    public static void setUserStatus(String status, Location location) {
        try {
            Log.d(TAG, "setUserStatus: status:" + status);
            JSONObject data = new JSONObject();
            data.put("status", status);
            if(STATUS_DIGGING.equalsIgnoreCase(status)) {
                data.put("lat", location.getLatitude());
                data.put("lng", location.getLongitude());
            }
            AdpPushClient.get().publishEvent(Constants.EVENT_STATUS, data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
