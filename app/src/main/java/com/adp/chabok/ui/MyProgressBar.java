package com.adp.chabok.ui;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class MyProgressBar extends ProgressBar {

    OnProgressCompleteListener listener;

    public MyProgressBar(Context context) {
        super(context);
    }

    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgressCompleteListener(OnProgressCompleteListener completeListener) {
        listener = completeListener;
    }


    @Override
    public void setProgress(int progress) {
        super.setProgress(progress);
//        if (progress == this.getMax()) {
//            listener.onComplete();
//        }
    }
}
