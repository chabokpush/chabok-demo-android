package com.adp.chabok.common;

import android.app.Activity;
import android.app.AlertDialog;

import com.adp.chabok.R;
import com.adp.chabok.ui.CustomDialogBuilder;

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

    public static AlertDialog dialog;


    public static boolean validate(Activity activity, String src, String regex, String errorMsg) {
        boolean result = src.matches(regex);

        if (!result) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity, errorMsg);
            dialog = dialogBuilder.create();
            dialog.show();
        }

        return result;
    }


    public static boolean validateNotNull(Activity activity, String src, int label) {
        if ((src == null) || (src.trim().isEmpty())) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity,
                    activity.getString(R.string.msg_null_not_allowed, activity.getString(label)));
            dialog = dialogBuilder.create();
            dialog.show();
            return false;
        }
        return true;
    }


    public static boolean validateEmail(Activity activity, String string) {

        if (!EMAIL_ADDRESS_PATTERN.matcher(string).matches()) {
            CustomDialogBuilder dialogBuilder = new CustomDialogBuilder(activity, activity.getResources().getString(R.string.msg_invalid_email));
            dialog = dialogBuilder.create();
            dialog.show();
            return false;
        }
        return true;
    }


}
