package com.adp.chabok.activity.adapters;

import android.content.Context;
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
import com.adp.chabok.common.Constants;
import com.adp.chabok.common.DateUtil;
import com.adp.chabok.data.models.MessageTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class CardViewAdapter extends RecyclerView.Adapter<CardViewAdapter.MessageViewHolder> {


    private final String senderName;
    private Context context;
    private List<MessageTO> items;

    public CardViewAdapter(Context context, List<MessageTO> items) {
        SharedPreferences myPref = PreferenceManager.getDefaultSharedPreferences(context);
        senderName = myPref.getString(Constants.PREFERENCE_NAME, "");
        this.context = context;
        this.items = items;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int type) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_message_item_in, viewGroup, false);
        return new MessageViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int i) {


        if (getSenderName(items.get(i)).equals(senderName)) {  //my own message

            messageViewHolder.out_rll.setVisibility(View.GONE);
            messageViewHolder.in_rll.setVisibility(View.VISIBLE);
            String messageDate = DateUtil.getTimeNoDateNoSecond(items.get(i).getSentDate(), false)
                    + " " + DateUtil.getSolarDate(context, new Date(items.get(i).getReceivedDate().getTime()), false, false);
            messageViewHolder.messageDate.setText(messageDate);
            messageViewHolder.messageText.setText(items.get(i).getMessage());
            messageViewHolder.senderName.setText(getSenderName(items.get(i)));
            messageViewHolder.messageSeen.setText(String.valueOf(items.get(i).getSeenCounter()));

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
            String messageDateOut = DateUtil.getTimeNoDateNoSecond(items.get(i).getSentDate(), false)
                    + " " + DateUtil.getSolarDate(context, new Date(items.get(i).getReceivedDate().getTime()), false, false);
            messageViewHolder.messageDate_out.setText(messageDateOut);
            messageViewHolder.messageText_out.setText(items.get(i).getMessage());
            messageViewHolder.senderName_out.setText(getSenderName(items.get(i)));
        }

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String getSenderName(MessageTO message) {
        String result;
        if (message.getData() != null) {
            try {
                JSONObject json = new JSONObject(message.getData());
                result = json.getString(Constants.KEY_NAME);
            } catch (JSONException e) {
                Log.e("log", e.getMessage(), e);
                result = context.getString(R.string.app_name);
            }
        } else {

            result = context.getString(R.string.app_name);
        }
        return result;

    }

    public void updateMessageList(MessageTO message, Map<String, Integer> serverIdPositionMap) {
        items.add(0, message);
        serverIdPositionMap.put(message.getServerId(), items.size() - 1);
        this.notifyDataSetChanged();
    }

    public void updateMessageItem(String myMessageServerId, Map<String, Integer> serverIdPositionMap) {

        int position = items.size() - serverIdPositionMap.get(myMessageServerId) - 1;
        items.get(position).setSendStatus(1);
        this.notifyItemChanged(position);

    }

    public void updateDeliveredCount(Map<String, Integer> serverIdDeliveredCountMap, Map<String, Integer> serverIdPositionMap) {
        for (String serverId : serverIdDeliveredCountMap.keySet()) {

            int position = items.size() - serverIdPositionMap.get(serverId) - 1;
            items.get(position).setSeenCounter(items.get(position).getSeenCounter() + serverIdDeliveredCountMap.get(serverId));
            this.notifyItemChanged(position);
        }

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

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
