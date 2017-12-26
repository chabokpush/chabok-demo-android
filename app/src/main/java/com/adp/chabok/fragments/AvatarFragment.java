package com.adp.chabok.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.adp.chabok.R;
import com.adp.chabok.activity.IntroActivity;
import com.adp.chabok.ui.Button;

public class AvatarFragment extends Fragment {

    private View view;
    private int avatarId;
    private Button selectBtn;
    private Button nextBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_avatar, container, false);
        initView();
        return view;
    }

    private void initView() {
        selectBtn = view.findViewById(R.id.select_btn);
        nextBtn = view.findViewById(R.id.next_btn);

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();

                bundle.putInt(IntroActivity.AVATAR_ID, avatarId);
                ((IntroActivity) getActivity()).navigateToFragment(IntroActivity.USER_INFO_FRAGMENT, bundle);

            }
        });

        final ImageView brown = view.findViewById(R.id.brown_av);

        brown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performOnClickedAction();
                brown.setSelected(true);
                brown.setPressed(true);
                avatarId = R.drawable.brown_selected;
            }
        });

        final ImageView white = view.findViewById(R.id.white_av);

        white.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performOnClickedAction();
                white.setSelected(true);
                white.setPressed(true);
                avatarId = R.drawable.white_selected;
            }
        });

        final ImageView red = view.findViewById(R.id.red_av);

        red.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performOnClickedAction();
                red.setSelected(true);
                red.setPressed(true);
                avatarId = R.drawable.red_selected;
            }
        });

        final ImageView gold = view.findViewById(R.id.gold_av);

        gold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                performOnClickedAction();
                gold.setSelected(true);
                gold.setPressed(true);
                avatarId = R.drawable.gold_selected;
            }
        });
    }


    private ImageView getImageViewById(int id) {
        switch (id) {
            case R.drawable.brown_selected:
                return view.findViewById(R.id.brown_av);

            case R.drawable.red_selected:
                return view.findViewById(R.id.red_av);

            case R.drawable.white_selected:
                return view.findViewById(R.id.white_av);

            case R.drawable.gold_selected:
                return view.findViewById(R.id.gold_av);

            default:
                return view.findViewById(R.id.brown_av);
        }
    }

    private void performOnClickedAction() {
        if (avatarId > 0) {
            ImageView image = getImageViewById(avatarId);
            image.setSelected(false);
            image.setPressed(false);
        }

        selectBtn.setVisibility(View.GONE);
        nextBtn.setVisibility(View.VISIBLE);

    }

}
