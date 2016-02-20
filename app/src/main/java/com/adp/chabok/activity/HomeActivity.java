package com.adp.chabok.activity;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.MessageTO;
import com.adp.chabok.fragments.AboutUsFragment;
import com.adp.chabok.fragments.MessageFragment;
import com.adp.chabok.ui.EditText;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;
import com.adpdigital.push.PushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class HomeActivity extends BaseActivity {

    static public int currentPage = 0;
    public static TabLayout tabLayout;
    public static ChabokDAO dao;
    public BroadcastReceiver receiver;
    ViewPagerAdapter adapter;
    int new_messages = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        ChabokApplication.currentActivity = HomeActivity.this;

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        View v = getLayoutInflater().inflate(R.layout.fragment_actionbar_sub, null);

        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setCustomView(v, params);


        receiver = new BroadcastReceiver() {  // create a receiver that receive message receiver intent after data saved
            @Override
            public void onReceive(Context context, Intent intent) {
                String messageType = intent.getStringExtra(Constants.MSG_SAVED_2_DB_EXTRA);
                onMessageReceive(messageType);
            }
        };

        dao = ChabokDAOImpl.getInstance(this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.MSG_SAVED_2_DB)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();

        currentPage = 1;
    }

    @Override
    protected void onResume() {
        super.onResume();

        createTabs();
        currentPage = 0;
    }

    protected void onMessageReceive(String MessageType) {

        new_messages = dao.getNormalUnreadedMessagesCount();
        updateInbox();

    }

    private void updateInbox() {

        MessageFragment.initializeData();
        MessageFragment.initilizeAdapter();
        MessageFragment.messageAdapter.notifyDataSetChanged();
    }


    public void createTabs() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MessageFragment(), getResources().getString(R.string.title_payam_resan));
        adapter.addFrag(new AboutUsFragment(), getResources().getString(R.string.title_about_chabok));

        viewPager.setAdapter(adapter);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        changeTabsFont();
        changeTabsFontBolding(0);

        DetailOnPageChangeListener mylistener = new DetailOnPageChangeListener();
        viewPager.addOnPageChangeListener(mylistener);

    }

    public void sendMessage(View v) {

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        final EditText msg = (EditText) findViewById(R.id.editText_out_message);

        if (!msg.getText().toString().equals(""))
            try {

                AdpPushClient pushClient = ((ChabokApplication) getApplication()).getPushClient();
                PushMessage myPushMessage = new PushMessage();
                myPushMessage.setBody(msg.getText().toString().trim());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.KEY_NAME, myPref.getString(Constants.PREFERENCE_NAME, ""));  //TODO untill getSenderId works dont need this part
                myPushMessage.setData(jsonObject);
                myPushMessage.setTopicName(Constants.CHANNEL_NAME);
                myPushMessage.setId(UUID.randomUUID().toString());
                myPushMessage.setUseAsAlert(true);
                MessageTO message = new MessageTO();
                message.setMessage(msg.getText().toString().trim());
                message.setData(jsonObject.toString());
                message.setSentDate(new Timestamp(new Date().getTime()));
                message.setReceivedDate(new Timestamp(new Date().getTime()));
                message.setServerId(myPushMessage.getId());
                dao.saveMessage(message, 0);
                updateInbox();
                msg.setText("");

                pushClient.publish(myPushMessage, new Callback() {
                    @Override
                    public void onSuccess(Object o) {
                    }

                    @Override
                    public void onFailure(Throwable throwable) {

                    }
                });

            } catch (JSONException e) {
                Log.e("LOG", "e=" + e.getMessage(), e);
            }


    }

    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    Typeface tf = Typeface.createFromAsset(this.getAssets(), Constants.APPLICATION_FONT);
                    ((TextView) tabViewChild).setTypeface(tf);
                    ((TextView) tabViewChild).setTextColor(getResources().getColor(R.color.colorBlue1));
                }
            }
        }
    }

    @Override
    public void onEvent(ConnectionStatus status) {
        super.onEvent(status);
    }

    private void changeTabsFontBolding(int tabPos) {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);

        ViewGroup vgTab = (ViewGroup) vg.getChildAt(tabPos);
        int tabChildsCount = vgTab.getChildCount();
        for (int i = 0; i < tabChildsCount; i++) {
            View tabViewChild = vgTab.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                Typeface tf = Typeface.createFromAsset(this.getAssets(), Constants.APPLICATION_FONT);
                ((TextView) tabViewChild).setTypeface(tf, Typeface.BOLD);
                ((TextView) tabViewChild).setTextSize(32f);
                ((TextView) tabViewChild).setTextColor(getResources().getColor(R.color.colorBlue1));
            }
        }

        int other_pos = (tabPos == 1) ? 0 : 1;

        ViewGroup vgTab_other = (ViewGroup) vg.getChildAt(other_pos);
        int tabChildsCount_other = vgTab.getChildCount();
        for (int i = 0; i < tabChildsCount_other; i++) {
            View tabViewChild = vgTab.getChildAt(i);
            if (tabViewChild instanceof TextView) {
                Typeface tf = Typeface.createFromAsset(this.getAssets(), Constants.APPLICATION_FONT);
                ((TextView) tabViewChild).setTypeface(tf, Typeface.NORMAL);
                ((TextView) tabViewChild).setTextSize(12f);
                ((TextView) tabViewChild).setTextColor(getResources().getColor(R.color.colorBlue1));
            }
        }

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragmentList = new ArrayList<>();
        private List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        public void setmFragmentTitleList(List<String> mFragmentTitleList) {
            this.mFragmentTitleList = mFragmentTitleList;
        }
    }

    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            currentPage = position;


            changeTabsFontBolding(position);


            if (position == 0) {
                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.cancelAll();
            } else {

                View view = HomeActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

            if (position == 0 && new_messages > 0) {
                updateInbox();
                new_messages = 0;
            }
        }
    }

}
