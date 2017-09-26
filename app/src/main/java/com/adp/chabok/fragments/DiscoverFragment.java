package com.adp.chabok.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.adp.chabok.R;


public class DiscoverFragment extends Fragment {

    private View view;
    private ProgressBar progressBar;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_discover, container, false);
        initView();
        return view;

    }

    private void initView() {
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setMax(100);
//        progressBar.setProgressCompleteListener(new OnProgressCompleteListener() {
//            @Override
//            public void onComplete() {
//                ((MainActivity) getActivity()).showDiggingResult();
//            }
//        });

        setProgressValue(10);
    }

    private void setProgressValue(int progress) {

        progressBar.setProgress(progress);

        if (progressBar.getProgress() < progressBar.getMax()) {

//            Random rn = new Random();
//            int rand = rn.nextInt(10 - 1 + 1) + 1;

            setProgressValue(progress + 10);

        }

    }

}
