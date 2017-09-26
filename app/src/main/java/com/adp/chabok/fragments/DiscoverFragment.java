package com.adp.chabok.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.adp.chabok.R;
import com.adp.chabok.activity.MainActivity;
import com.adp.chabok.ui.MyProgressBar;
import com.adp.chabok.ui.OnProgressCompleteListener;

import java.util.Random;


public class DiscoverFragment extends Fragment {

    private View view;
    private MyProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_discover, container, false);
        initView();
        return view;

    }

    private void initView() {
        progressBar = view.findViewById(R.id.progress_bar);
        setProgressValue(10);
        progressBar.setProgressCompleteListener(new OnProgressCompleteListener() {
            @Override
            public void onComplete() {
                ((MainActivity) getActivity()).showDiggingResult();
            }
        });
    }

    private void setProgressValue(final int progress) {

        // set the progress
        progressBar.setProgress(progress);
        // thread is used to change the progress value
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Random rn = new Random();
                int rand = rn.nextInt(10 - 1 + 1) + 1;

                setProgressValue(progress + rand);
            }
        });
        thread.start();

    }


}
