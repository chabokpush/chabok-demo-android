package com.adp.chabok.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adp.chabok.R;

public class CustomDialogBuilder extends AlertDialog.Builder {

    private OnCustomListener mListener;

    public CustomDialogBuilder(Activity context, final String message) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.fragment_error, (ViewGroup) context.findViewById(R.id.chabok_diag_root));

        TextView text = layout.findViewById(R.id.error_fragment_title);
        text.setText(message);

        Button confirm = layout.findViewById(R.id.ok_btn);
        confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mListener.onEvent();

            }
        });

        super.setView(layout);
    }

    public void setCustomEventListener(OnCustomListener eventListener) {
        mListener = eventListener;
    }

}
