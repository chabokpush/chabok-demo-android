package com.adp.chabok.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.adp.chabok.common.Constants;

/**
 * Created by m.tajik
 * on 2/6/2016.
 */
public class TextView extends android.widget.TextView {

    public TextView(Context context) {
        super(context);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_FONT);
        setTypeface(tf);
//        setTextSize(this.getTextSize());
    }

    public TextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_FONT);
        setTypeface(tf);
//        setTextSize(this.getTextSize());
    }

    public TextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_FONT);
        setTypeface(tf);
//        setTextSize(this.getTextSize());
    }

    @Override
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

}
