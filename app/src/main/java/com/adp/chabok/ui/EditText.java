package com.adp.chabok.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;

import com.adp.chabok.common.Constants;


public class EditText extends android.support.v7.widget.AppCompatEditText {

    OnCustomListener mListener;

    public EditText(Context context) {
        super(context);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_NORMAL_FONT);
        setTypeface(tf);
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_NORMAL_FONT);
        setTypeface(tf);
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_NORMAL_FONT);
        setTypeface(tf);
    }

    public void setCustomEventListener(OnCustomListener eventListener) {
        mListener = eventListener;
    }

    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mListener != null && keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            mListener.onEvent();
        }
        return super.onKeyPreIme(keyCode, event);
    }


    public void setText(CharSequence text, BufferType type) {

        text = uniform(text.toString());
        super.setText(text, type);

    }


    private String uniform(String input) {

        String uniformConverted = input;

        uniformConverted = uniformConverted
                .replace("۰", "0")
                .replace("۱", "1")
                .replace("۲", "2")
                .replace("۳", "3")
                .replace("۴", "4")
                .replace("۵", "5")
                .replace("۶", "6")
                .replace("۷", "7")
                .replace("۸", "8")
                .replace("۹", "9");


        return uniformConverted;

    }

}