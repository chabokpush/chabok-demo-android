package com.adp.chabok.activity.adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.adp.chabok.R;
import com.adp.chabok.application.ChabokApplication;
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.DateUtil;
import com.adp.chabok.data.models.MessageTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;

/**
 * Created by m.tajik
 * on 12/25/2015.
 */
public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MessageViewHolder> {


    List<MessageTO> items;
    private int position;
    private int message_type; //1-incoming 2-outgoing
    private JSONObject dataJson;

    public CardViewAdapter(List<MessageTO> items) {
        this.items = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    public String getServerId() {
        return items.get(position).getServerId();
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_message_item_in, viewGroup, false);
        MessageViewHolder pvh = new MessageViewHolder(v);
        return pvh;
    }

    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (items.get(position).getSenderId() != "") {
            //TODO can change here
        }
        return position % 2 * 2;
    }


    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int i) {

        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(ChabokApplication.currentActivity);
        if (getSenderName(items.get(i)).equals(myPref.getString(Constants.PREFERENCE_NAME, ""))) {  //my own message

            messageViewHolder.out_rll.setVisibility(View.GONE);
            messageViewHolder.in_rll.setVisibility(View.VISIBLE);
            messageViewHolder.messageDate.setText(DateUtil.getTimeNoDateNoSecond(items.get(i).getSentDate(), false)
                    + " " + DateUtil.getSolarDate(ChabokApplication.currentActivity, new Date(items.get(i).getReceivedDate().getTime()), false, false));
            messageViewHolder.messageText.setText(items.get(i).getMessage());
            messageViewHolder.senderName.setText(getSenderName(items.get(i)));
            messageViewHolder.messageSeen.setText(items.get(i).getSeenCounter() + "");

            switch (items.get(i).getSendStatus()) {
                case 0:
                    messageViewHolder.sendStatus.setImageResource(R.drawable.ic_check_gray);
                    break;
                case 1:
                    messageViewHolder.sendStatus.setImageResource(R.drawable.ic_check_blue);
                    break;
                default:
                    messageViewHolder.sendStatus.setImageResource(R.drawable.ic_check_gray);
                    break;
            }


        } else {

            messageViewHolder.out_rll.setVisibility(View.VISIBLE);
            messageViewHolder.in_rll.setVisibility(View.GONE);
            messageViewHolder.messageDate_out.setText(DateUtil.getTimeNoDateNoSecond(items.get(i).getSentDate(), false)
                    + " " + DateUtil.getSolarDate(ChabokApplication.currentActivity, new Date(items.get(i).getReceivedDate().getTime()), false, false));
            messageViewHolder.messageText_out.setText(items.get(i).getMessage());
            messageViewHolder.senderName_out.setText(getSenderName(items.get(i)));
        }

        final int count = i;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String getSenderName(MessageTO message) {
        String result = "";
        if (message.getData() != null) {
            try {
                JSONObject json = new JSONObject(message.getData().toString());
                result = json.getString(Constants.KEY_NAME);
            } catch (JSONException e) {
                Log.e("log", e.getMessage(), e);
                result = ChabokApplication.currentActivity.getString(R.string.app_name);
            }
        } else {

            result = ChabokApplication.currentActivity.getString(R.string.app_name);
        }
        return result;

    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout in_rll;
        TextView senderName;
        TextView messageDate;
        TextView messageText;
        TextView messageSeen;

        RelativeLayout out_rll;
        TextView senderName_out;
        TextView messageDate_out;
        TextView messageText_out;
        ImageView sendStatus;

        MessageViewHolder(View itemView) {
            super(itemView);
            in_rll = (RelativeLayout) itemView.findViewById(R.id.message_in_layouts);
            senderName = (TextView) itemView.findViewById(R.id.inbox_item_sender_ame);
            messageDate = (TextView) itemView.findViewById(R.id.inbox_item_date);
            messageText = (TextView) itemView.findViewById(R.id.inbox_item_message_text);
            messageSeen = (TextView) itemView.findViewById(R.id.seen_counter);

            out_rll = (RelativeLayout) itemView.findViewById(R.id.message_out_layouts);
            senderName_out = (TextView) itemView.findViewById(R.id.inbox_item_sender_ame_out);
            messageDate_out = (TextView) itemView.findViewById(R.id.inbox_item_date_out);
            messageText_out = (TextView) itemView.findViewById(R.id.inbox_item_message_text_out);
            sendStatus = (ImageView) itemView.findViewById(R.id.imageView_mesage_send);
        }
    }
}
