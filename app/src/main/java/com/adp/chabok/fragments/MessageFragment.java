package com.adp.chabok.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.adp.chabok.R;
import com.adp.chabok.activity.adapters.CardViewAdapter;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.DateUtil;
import com.adp.chabok.data.ChabokDAO;
import com.adp.chabok.data.ChabokDAOImpl;
import com.adp.chabok.data.models.MessageTO;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by m.tajik
 * on 2/6/2016.
 */
public class MessageFragment extends Fragment {

    private static ChabokDAO dao;
    private static RecyclerView rv;
    public static CardViewAdapter messageAdapter;
    private static List<MessageTO> messagesList;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.activity_messages, container, false);

        rv = (RecyclerView) fragmentView.findViewById(R.id.rv);
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        llm.setReverseLayout(true);
        rv.setLayoutManager(llm);

        initializeData();
        initilizeAdapter();
        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();


    }

    public static void initializeData() {

        dao = ChabokDAOImpl.getInstance(ChabokApplication.currentActivity);
        messagesList = dao.getMessages("receivedDate DESC");
        messagesList = prepareData(messagesList);

    }

    static private List<MessageTO> prepareData(List<MessageTO> messageTOList) {
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

    public static void initilizeAdapter() {

        messageAdapter = new CardViewAdapter(messagesList);
        if (rv != null) {
            rv.setAdapter(messageAdapter);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int position = -1;
        String serverId = "";

        try {
            position = (messageAdapter).getPosition();
            serverId = (messageAdapter).getServerId();

        } catch (Exception e) {
            Log.d("Log", e.getLocalizedMessage(), e);
            return super.onContextItemSelected(item);
        }


        switch (item.getItemId()) {
            case R.id.delete:
                dao.deleteMessages(serverId); // delete the mesage from DB
                messagesList.remove(position); // delete the message from arrayList
                messageAdapter.notifyItemRemoved(position); //remove message from Adsapter
                messageAdapter.notifyItemRangeChanged(position, messagesList.size());
//                Toast.makeText(ChabokApplication.currentActivity, getResources().getString(R.string.deleted_sucessfully), Toast.LENGTH_SHORT).show();

                break;
            case R.id.share:

                break;
        }
        return super.onContextItemSelected(item);
    }

}