package com.adp.chabok.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.activity.MainActivity;
import com.adp.chabok.activity.adapters.InboxAdapter;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.Utils;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.CaptainMessage;
import com.adpdigital.push.AdpPushClient;

import java.util.List;

import static com.adp.chabok.common.Constants.STATUS_DIGGING;


public class InboxFragment extends Fragment {

    public BroadcastReceiver receiver;
    private View view;
    private RecyclerView messageRV;
    private List<CaptainMessage> messages;
    private ChabokDAO dao;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_inbox, container, false);
        dao = ChabokDAOImpl.getInstance(getContext());
        initView();
        return view;
    }

    private void initView() {
        TextView title = view.findViewById(R.id.action_bar_title);
        title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_LIGHT_FONT));

        FloatingActionButton fab = view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Utils.isNetworkAvailable(getActivity())) {
                    if(((MainActivity) getActivity()).checkLocationAndSetStatus(STATUS_DIGGING)){

                        ((MainActivity) getActivity()).navigateToFragment(MainActivity.DISCOVER_FRAGMENT, null);
                    }
                } else {
                    Snackbar.make(view, R.string.internet_error_desc, Snackbar.LENGTH_LONG).show();
                }
            }
        });


        ImageView chabok = view.findViewById(R.id.chabok);
        chabok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).gotoAboutUsActivity();
            }
        });


        ImageView chat = view.findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) getActivity()).gotoWallActivity();
            }
        });

        messages = dao.getCaptainMessages();

        InboxAdapter adapter = new InboxAdapter(messages, InboxFragment.this.getContext());

        messageRV = view.findViewById(R.id.rv_inbox);
        messageRV.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager mManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL, false);
        messageRV.setLayoutManager(mManager);
        messageRV.setAdapter(adapter);


        receiver = new BroadcastReceiver() {  // create a receiver that receive message receiver intent after data saved
            @Override
            public void onReceive(Context context, Intent intent) {


                if (intent.getExtras().get(Constants.CAPTAIN_NEW_MESSAGE) != null) {
                    CaptainMessage captainMessage = (CaptainMessage) intent.getExtras().get(Constants.CAPTAIN_NEW_MESSAGE);
                    onMessageReceive(captainMessage);
                }
            }
        };

    }


    protected void onMessageReceive(CaptainMessage captainMessage) {

        messages.add(0, captainMessage);
        messageRV.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.CAPTAIN_MESSAGE_RECEIVED);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, intentFilter);
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
        AdpPushClient client = ChabokApplication.getInstance().getPushClient();
        client.resetBadge();
    }

}
