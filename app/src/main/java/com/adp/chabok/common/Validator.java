package com.adp.chabok.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.adp.chabok.R;
import com.adp.chabok.ui.CustomDialogBuilder;
import com.adp.chabok.ui.OnCustomListener;

import java.util.regex.Pattern;


public class Validator {


    public static final Pattern EMAIL_ADDRESS_PATTERN = Pattern.compile(
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    );

    public static final Pattern IRAN_MOBILE_PATTERN = Pattern.compile(
            "^(989|09|9)[0-9]{9}"
    );

    public static AlertDialog dialog;


    public static boolean validate(Activity activity, String src, String regex, String errorMsg) {
        boolean result = src.matches(regex);

        if (!result) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity, errorMsg);
            createAndShowDialog(dialogBuilder);

        }

        return result;
    }


    public static boolean validateNotNull(Activity activity, String src, int label) {
        if ((src == null) || (src.trim().isEmpty())) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity,
                    activity.getString(R.string.msg_null_not_allowed, activity.getString(label)));
            createAndShowDialog(dialogBuilder);

            return false;
        }
        return true;
    }

    public static boolean validateName(Activity activity, String src) {
        if (src.length() < 3) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity,
                    activity.getString(R.string.msg_invalid_name));
            createAndShowDialog(dialogBuilder);
            return false;
        }
        return true;
    }


    public static boolean validateEmail(Activity activity, String string) {

        if (!EMAIL_ADDRESS_PATTERN.matcher(string).matches()) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity, activity.getResources().getString(R.string.msg_invalid_email));
            createAndShowDialog(dialogBuilder);
            return false;
        }
        return true;
    }

    public static boolean validatePhoneNumber(Activity activity, String phoneNumber) {

        if (!IRAN_MOBILE_PATTERN.matcher(phoneNumber).matches()) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity, activity.getResources().getString(R.string.msg_invalid_mobile));
            createAndShowDialog(dialogBuilder);

            return false;
        }
        return true;

    }

    public static String createUserId(String phoneNo) {
        if (phoneNo.startsWith("0")) {
            phoneNo = phoneNo.substring(1);
            return createUserId(phoneNo);
        }
        return phoneNo.startsWith("989")? phoneNo : "98".concat(phoneNo);

    }

    private static void createAndShowDialog(CustomDialogBuilder dialogBuilder) {
        dialog = dialogBuilder.create();
        dialogBuilder.setCustomEventListener(new OnCustomListener() {
            @Override
            public void onEvent() {
                dialog.dismiss();
            }
        });
        dialog.show();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }


}
