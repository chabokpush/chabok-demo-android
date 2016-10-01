package com.adp.chabok.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.adp.chabok.common.Constants;


public class TextView extends android.widget.TextView {

    public TextView(Context context) {
        super(context);

    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setText(CharSequence text, BufferType type) {
        text = uniform(text.toString());
        super.setText(text, type);

    }


    private String uniform(String input) {

        String uniformConverted = input;

        uniformConverted = uniformConverted
                .replace("0", "۰")
                .replace("1", "۱")
                .replace("2", "۲")
                .replace("3", "۳")
                .replace("4", "۴")
                .replace("5", "۵")
                .replace("6", "۶")
                .replace("7", "۷")
                .replace("8", "۸")
                .replace("9", "۹");


        return uniformConverted;

    }


    public void setTypeface(Typeface tf, int style) {
        Typeface normalTypeface = Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_NORMAL_FONT);
        Typeface boldTypeface = Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_FONT);

        Log.i("typeface", "typeface=" + style);
        if (style == Typeface.NORMAL) {
            super.setTypeface(normalTypeface/*, -1*/);
        }
        if (style == Typeface.BOLD) {
            super.setTypeface(boldTypeface/*, -1*/);
        }
    }

}
