package com.adp.chabok.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.adp.chabok.R;
import com.adp.chabok.fragments.AvatarFragment;
import com.adp.chabok.fragments.ChabokStartFragment;
import com.adp.chabok.fragments.MapFragment;
import com.adp.chabok.fragments.UserInfoFragment;

public class IntroActivity extends AppCompatActivity {

    public static final String START_FRAGMENT = "start";
    public static final String USER_INFO_FRAGMENT = "user-info";
    public static final String MAP_FRAGMENT = "map";
    public static final String AVATAR_FRAGMENT = "avatar";
    public static final String AVATAR_ID = "avatar_id";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.frame, new ChabokStartFragment())
                    .commit();
        }

    }

    public void navigateToFragment(String tag, Bundle bundle) {
        Fragment fragment;
        FragmentTransaction tr = getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//        tr.setCustomAnimations(R.anim.left_in, R.anim.left_out);

        if (bundle == null) {
            bundle = new Bundle();
        }

        switch (tag) {
            case START_FRAGMENT:
                fragment = new ChabokStartFragment();
                tr.replace(R.id.frame, fragment).commit();
                break;

            case AVATAR_FRAGMENT:
                fragment = new AvatarFragment();
                tr.replace(R.id.frame, fragment).commit();
                break;

            case MAP_FRAGMENT:
                fragment = new MapFragment();
                tr.replace(R.id.frame, fragment).commit();
                break;

            case USER_INFO_FRAGMENT:
                fragment = new UserInfoFragment();
                fragment.setArguments(bundle);
                tr.replace(R.id.frame, fragment).addToBackStack(AvatarFragment.class.getName()).commit();
                break;

        }

    }

    public void gotToMain() {
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


}
