package com.adp.chabok.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.DeliveredMessage;
import com.adp.chabok.data.models.MessageTO;
import com.adp.chabok.fragments.AboutUsFragment;
import com.adp.chabok.fragments.MessageFragment;
import com.adp.chabok.ui.EditText;
import com.adpdigital.push.AdpPushClient;
import com.adpdigital.push.Callback;
import com.adpdigital.push.ConnectionStatus;
import com.adpdigital.push.PushMessage;
import com.kyleduo.switchbutton.SwitchButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("StatementWithEmptyBody")
public class HomeActivity extends BaseActivity {

    public static int currentPage = 0;
    private TabLayout tabLayout;
    private ChabokDAO dao;
    private MessageFragment messageFragment;
    private BroadcastReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        checkMarshmallowPermissions();

        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);

        @SuppressLint("InflateParams")
        View v = getLayoutInflater().inflate(R.layout.fragment_actionbar_sub, null);

        if (getSupportActionBar() != null) {

            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setCustomView(v, params);
        }


        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getExtras().get(Constants.DELIVERED_MESSAGE) != null){
                    DeliveredMessage deliveredMessage = (DeliveredMessage) intent.getExtras().get(Constants.DELIVERED_MESSAGE);
                    messageFragment.updateDeliveredCount(deliveredMessage);
                }
            }
        };


        dao = ChabokDAOImpl.getInstance(this);
        ((ChabokApplication) getApplication()).clearMessages();
        createTabs();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(Constants.SEND_BROADCAST)
        );
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra(Constants.RELOAD_MESSAEGS, false)) {
            intent.removeExtra(Constants.RELOAD_MESSAEGS);
            if(intent.getExtras().get(Constants.NEW_MESSAGE) != null){
                MessageTO newMessage = (MessageTO) intent.getExtras().get(Constants.NEW_MESSAGE);
                messageFragment.updateMessageList(newMessage);
            }else if(intent.getExtras().get(Constants.MY_MESSAGE_SERVER_ID) != null){
                String myMessageServerId = intent.getExtras().getString(Constants.MY_MESSAGE_SERVER_ID);
                messageFragment.updateMessageItem(myMessageServerId);
            }

        }

    }

    private void checkMarshmallowPermissions() {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            // only for gingerbread and newer versions
            String permission = Manifest.permission.READ_PHONE_STATE;
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 110;
                ActivityCompat.requestPermissions(HomeActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            } else {
                // Add your function here which open camera
            }
        } else {
            // Add your function here which open camera
        }
    }


    public void createTabs() {

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        messageFragment = MessageFragment.getInstance();
        adapter.addFrag(messageFragment, getResources().getString(R.string.title_payam_resan));
        adapter.addFrag(new AboutUsFragment(), getResources().getString(R.string.title_about_chabok));

        if (viewPager != null) {
            viewPager.setOffscreenPageLimit(2);

            viewPager.setAdapter(adapter);
            tabLayout = (TabLayout) findViewById(R.id.tabs);
            if (tabLayout != null) {
                tabLayout.setupWithViewPager(viewPager);
            }
            changeTabsFont();

            DetailOnPageChangeListener myListener = new DetailOnPageChangeListener();
            viewPager.addOnPageChangeListener(myListener);
        }

    }

    public void sendMessage(View v) {

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);
        final EditText msg = (EditText) findViewById(R.id.editText_out_message);

        if (msg != null && !msg.getText().toString().equals(""))
            try {

                AdpPushClient pushClient = ((ChabokApplication) getApplication()).getPushClient();
                PushMessage myPushMessage = new PushMessage();
                myPushMessage.setBody(msg.getText().toString().trim());

                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Constants.KEY_NAME, myPref.getString(Constants.PREFERENCE_NAME, ""));  //TODO until getSenderId works don't need this part
                myPushMessage.setData(jsonObject);
                myPushMessage.setTopicName(Constants.CHANNEL_NAME);
                myPushMessage.setId(UUID.randomUUID().toString());
                myPushMessage.setUseAsAlert(true);
                myPushMessage.setAlertText(myPref.getString(Constants.PREFERENCE_NAME, "") + ": " + msg.getText().toString().trim());
                MessageTO message = new MessageTO();
                message.setMessage(msg.getText().toString().trim());
                message.setData(jsonObject.toString());
                message.setSentDate(new Timestamp(new Date().getTime()));
                message.setReceivedDate(new Timestamp(new Date().getTime()));
                message.setServerId(myPushMessage.getId());
                dao.saveMessage(message, 0);
                messageFragment.updateMessageList(message);
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

    @SuppressWarnings("deprecation")
    private void changeTabsFont() {

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildCount; i++) {
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


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void showSettingDialog(View v) {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = (LayoutInflater) HomeActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        @SuppressLint("InflateParams")
        View dialogView = inflater.inflate(R.layout.activity_settings, null);

        dialogBuilder.setView(dialogView);
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        final ChabokApplication app = (ChabokApplication) getApplication();


        final SwitchButton s1 = (SwitchButton) dialogView.findViewById(R.id.switch1);
        final SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(this);

        Log.i("MAHDI", "myPref.getBoolean(Constants.PREFERENCE_NOTIFY=" + myPref.getBoolean(Constants.PREFERENCE_OFF_NOTIFY, false));
        if (myPref.getBoolean(Constants.PREFERENCE_OFF_NOTIFY, false)) {
            s1.setChecked(false);
        } else {
            s1.setChecked(true);
        }

        s1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (s1.isChecked()) {
                    myPref.edit().putBoolean(Constants.PREFERENCE_OFF_NOTIFY, false).apply();
                    app.getPushClient().updateNotificationSettings(Constants.CHANNEL_NAME, "default", true);
                } else {
                    myPref.edit().putBoolean(Constants.PREFERENCE_OFF_NOTIFY, true).apply();
                    app.getPushClient().updateNotificationSettings(Constants.CHANNEL_NAME, null, false);
                }
            }
        });

    }

    public void openSite(View v) {

        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.lbl_chabok_url)));
        startActivity(i);

    }

    public void dialNumber(View v) {

        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getResources().getString(R.string.lbl_chabok_phone).replace("-", "")));
        startActivity(intent);

    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
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

    }

    public class DetailOnPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
        private final NotificationManager notificationManager;
        private final InputMethodManager imm;

        public DetailOnPageChangeListener() {
            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        }


        @Override
        public void onPageSelected(int position) {
            currentPage = position;


            if (position == 0) {
                notificationManager.cancelAll();
            } else {

                View view = HomeActivity.this.getCurrentFocus();
                if (view != null) {
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }

        }
    }

}
