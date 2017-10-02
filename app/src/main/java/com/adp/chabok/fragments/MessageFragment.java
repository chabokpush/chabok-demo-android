package com.adp.chabok.fragments;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.activity.adapters.CardViewAdapter;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.DateUtil;
import com.adp.chabok.common.Utils;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.DeliveredMessage;
import com.adp.chabok.data.models.MessageTO;
import com.adp.chabok.ui.EditText;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageFragment extends Fragment {

    private static final String TAG = "MessageFragment";
    private View fragmentView;
    private RecyclerView rv;
    private CardViewAdapter messageAdapter;
    private List<MessageTO> messagesList;
    private Map<String, Integer> serverIdPositionMap;


    public static MessageFragment getInstance() {
        return new MessageFragment();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_message, container, false);
        initView();
        return fragmentView;
    }

    private void initView(){

        TextView title = fragmentView.findViewById(R.id.action_bar_title);
        title.setTypeface(Typeface.createFromAsset(getContext().getAssets(), Constants.APPLICATION_LIGHT_FONT));

        ImageView demoBtn = fragmentView.findViewById(R.id.map_demo);
        demoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.lbl_map_demo)));
                startActivity(i);
            }
        });

        EditText msg = fragmentView.findViewById(R.id.editText_out_message);
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                Log.d(TAG, "onTextChanged: start: " + start + " ,before: " + before + " ,count: " + count);
                if(count == 1 && (count > before)) {
                    Utils.setUserStatus(Constants.STATUS_TYPING, null);
                } else if(count == 0 && before == 1) {
                    Utils.setUserStatus(Constants.STATUS_IDLE, null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Utils.setUserStatus(Constants.STATUS_IDLE, null);
            }
        });
        rv = fragmentView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        rv.setLayoutManager(llm);

        initializeData();
        initializeAdapter();
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public void initializeData() {

        ChabokDAO dao = ChabokDAOImpl.getInstance(ChabokApplication.getContext());
        messagesList = dao.getMessages("receivedDate DESC");
        messagesList = prepareData(messagesList);
        serverIdPositionMap = setupMap();

    }

    private Map<String,Integer> setupMap() {
        Map<String,Integer> map = new HashMap<>();
        for (int i = 0; i < messagesList.size(); i++) {
            map.put(messagesList.get(i).getServerId(), i);
        }
        return map;
    }

    public void initializeAdapter() {

        messageAdapter = new CardViewAdapter(ChabokApplication.getContext(), messagesList);
        if (rv != null) {
            rv.setAdapter(messageAdapter);
        }
    }


    private List<MessageTO> prepareData(List<MessageTO> messageTOList) {
        if (messageTOList != null && messageTOList.size() > 0) {
            Map<Long, Long> map = new HashMap();

            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(messageTOList.get(0).getReceivedDate().getTime());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            Long milestoneDate = cal.getTimeInMillis();

            for (MessageTO messageTO : messageTOList) {
                Long receivedDate = DateUtil.getDateNoTime(messageTO.getReceivedDate()).getTime();

                if (milestoneDate.equals(receivedDate)) {
                    if (!map.containsKey(milestoneDate)) {
                        map.put(milestoneDate, receivedDate);
                        messageTO.setHeader(true);
                    }
                } else {
                    milestoneDate = receivedDate;
                    map.put(milestoneDate, receivedDate);
                    messageTO.setHeader(true);
                }
            }
        }

        return messageTOList;
    }

    public void updateMessageList(MessageTO message) {
        messageAdapter.updateMessageList(message, serverIdPositionMap);

    }

    public void updateMessageItem(String myMessageServerId) {
        messageAdapter.updateMessageItem(myMessageServerId, serverIdPositionMap);

    }

    public void updateDeliveredCount(DeliveredMessage deliveredMessage) {
        messageAdapter.updateDeliveredCount(deliveredMessage.getMap(), serverIdPositionMap);
    }
}