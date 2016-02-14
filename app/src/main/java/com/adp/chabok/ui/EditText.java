package com.adp.chabok.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.adp.chabok.common.Constants;

/**
 * Created by m.tajik
 * on 2/6/2016.
 */
public class EditText extends android.widget.EditText {

    public EditText(Context context) {
        super(context);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_FONT);
        setTypeface(tf);
//        setTextSize(this.getTextSize());
    }

    public EditText(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.editTextStyle);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_FONT);
        setTypeface(tf);
//        setTextSize(this.getTextSize());
    }

    public EditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_FONT);
        setTypeface(tf);
//        setTextSize(this.getTextSize());
    }
}