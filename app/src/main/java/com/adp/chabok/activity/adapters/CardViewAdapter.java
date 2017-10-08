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

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_message_item, viewGroup, false);
        return new MessageViewHolder(v);
    }


    @Override
    public void onBindViewHolder(MessageViewHolder messageViewHolder, int i) {


        if (getSenderName(items.get(i)).equals(senderName)) {  //my own message

            messageViewHolder.incomingMessage.setVisibility(View.GONE);
            messageViewHolder.myMessage.setVisibility(View.VISIBLE);
            String messageDate = DateUtil.getTimeNoDateNoSecond(items.get(i).getSentDate(), false) + " " +
                    DateUtil.getSolarDate(context, new Date(items.get(i).getReceivedDate().getTime()), false, false);
            messageViewHolder.messageDate.setText(messageDate);
            messageViewHolder.messageText.setText(items.get(i).getMessage());
            messageViewHolder.messageSeen.setText(String.valueOf(items.get(i).getSeenCounter()));

            switch (items.get(i).getSendStatus()) {
                case 0:
                    messageViewHolder.sendStatus.setImageResource(R.drawable.ic_sending);
                    messageViewHolder.sendStatusTxt.setText(context.getString(R.string.sending));
                    break;
                case 1:
                    messageViewHolder.sendStatus.setImageResource(R.drawable.ic_delivered);
                    messageViewHolder.sendStatusTxt.setText(context.getString(R.string.delivered));
                    break;
                default:
                    messageViewHolder.sendStatus.setImageResource(R.drawable.ic_sending);
                    messageViewHolder.sendStatusTxt.setText(context.getString(R.string.sending));
                    break;
            }


        } else {

            messageViewHolder.incomingMessage.setVisibility(View.VISIBLE);
            messageViewHolder.myMessage.setVisibility(View.GONE);
            String messageDateOut =  DateUtil.getTimeNoDateNoSecond(items.get(i).getReceivedDate(), false) + " " +
                    DateUtil.getSolarDate(context, new Date(items.get(i).getReceivedDate().getTime()), false, false);
            messageViewHolder.incomingMessageDate.setText(messageDateOut);
            messageViewHolder.incomingMessageText.setText(items.get(i).getMessage());
            messageViewHolder.messageSenderName.setText(getSenderName(items.get(i)));
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

    public void updateMessageList(MessageTO message) {
        items.add(0, message);
        this.notifyDataSetChanged();
    }

    public void updateMessageItem(String myMessageServerId, Map<String, MessageTO> MessageServerIdMap) {
        if (MessageServerIdMap != null && MessageServerIdMap.get(myMessageServerId) != null) {

            int position = items.indexOf(MessageServerIdMap.get(myMessageServerId));
            items.get(position).setSendStatus(1);
            this.notifyItemChanged(position);
        }
    }

    public void updateDeliveredCount(String myMessageServerId, Map<String, MessageTO> MessageServerIdMap) {

        if (MessageServerIdMap != null && MessageServerIdMap.get(myMessageServerId) != null) {

            int position = items.indexOf(MessageServerIdMap.get(myMessageServerId));
            items.get(position).setSeenCounter(items.get(position).getSeenCounter() + 1);
            this.notifyItemChanged(position);
        }

    }

    class MessageViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout myMessage, incomingMessage;
        TextView messageDate, messageText, messageSeen, sendStatusTxt, messageSenderName, incomingMessageDate, incomingMessageText;
        ImageView sendStatus;

        MessageViewHolder(View itemView) {
            super(itemView);
            myMessage = itemView.findViewById(R.id.my_message_layout);
            messageDate = itemView.findViewById(R.id.my_message_date);
            messageText = itemView.findViewById(R.id.my_message_text);
            messageSeen = itemView.findViewById(R.id.seen_counter);
            sendStatus = itemView.findViewById(R.id.my_message_send);
            sendStatusTxt = itemView.findViewById(R.id.my_message_send_txt);

            incomingMessage = itemView.findViewById(R.id.incoming_message_layout);
            messageSenderName = itemView.findViewById(R.id.incoming_mesage_name);
            incomingMessageDate = itemView.findViewById(R.id.incoming_message_date);
            incomingMessageText = itemView.findViewById(R.id.incoming_message_text);
        }
    }
}
