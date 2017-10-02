package com.adp.chabok.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import com.adp.chabok.common.Constants;

public class Button extends android.support.v7.widget.AppCompatButton {

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs, android.R.attr.buttonStyle);
        if (!isInEditMode()) {
            final Typeface tf = Typeface.createFromAsset(context.getAssets(), Constants.APPLICATION_NORMAL_FONT);
            setTypeface(tf);
        }
    }

}
