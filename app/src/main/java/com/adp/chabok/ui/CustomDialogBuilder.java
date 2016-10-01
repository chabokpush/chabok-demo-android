package com.adp.chabok.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adp.chabok.R;
import com.adp.chabok.common.Validator;

public class CustomDialogBuilder extends AlertDialog.Builder {

    public CustomDialogBuilder(Activity context, String message) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_error, (ViewGroup) context.findViewById(R.id.chabok_diag_root));

        TextView text = (TextView) layout.findViewById(R.id.error_fragment_title);
        text.setText(message);

        Button confirm = (Button) layout.findViewById(R.id.confirmButton_error_fragment);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (Validator.dialog != null) {
                    Validator.dialog.dismiss();
                    Validator.dialog = null;
                }
            }
        });

        super.setView(layout);
    }
}
