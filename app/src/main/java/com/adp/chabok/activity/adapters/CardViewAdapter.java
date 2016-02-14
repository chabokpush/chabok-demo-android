package com.adp.chabok.activity.adapters;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public String message_sender = "";
    public String message_time = "";
    public String message_text = "";
    public int data_type = -1;

    private int position;

    private int message_type; //1-incoming 2-outgoing

    List<MessageTO> items;
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
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        //automatically change type in: public MessageViewHolder onCreateViewHolder(ViewGroup viewGroup, int type)
        //and then can use swith / case
        if (items.get(position).getSenderId() != "") {

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

//            Glide.with(this).load(R.drawable.intro_1).asGif().into(imgFrgament1);
//
//            Glide.with(ChabokApplication.currentActivity).load(url).asBitmap().centerCrop().into(new BitmapImageViewTarget(imageView) {
//                @Override
//                protected void setResource(Bitmap resource) {
//                    RoundedBitmapDrawable circularBitmapDrawable =
//                            RoundedBitmapDrawableFactory.create(context.getResources(), resource);
//                    circularBitmapDrawable.setCircular(true);
//                    imageView.setImageDrawable(circularBitmapDrawable);
//                }
//            });

        } else {

            messageViewHolder.out_rll.setVisibility(View.VISIBLE);
            messageViewHolder.in_rll.setVisibility(View.GONE);
            messageViewHolder.messageDate_out.setText(DateUtil.getTimeNoDateNoSecond(items.get(i).getSentDate(), true));
            messageViewHolder.messageText_out.setText(items.get(i).getMessage());
            messageViewHolder.senderName_out.setText(getSenderName(items.get(i)));
        }


        final int count = i;

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout in_rll;
        TextView senderName;
        TextView messageDate;
        TextView messageText;

        RelativeLayout out_rll;
        TextView senderName_out;
        TextView messageDate_out;
        TextView messageText_out;

        MessageViewHolder(View itemView) {
            super(itemView);
            in_rll = (RelativeLayout) itemView.findViewById(R.id.message_in_layouts);
            senderName = (TextView) itemView.findViewById(R.id.inbox_item_sender_ame);
            messageDate = (TextView) itemView.findViewById(R.id.inbox_item_date);
            messageText = (TextView) itemView.findViewById(R.id.inbox_item_message_text);

            out_rll = (RelativeLayout) itemView.findViewById(R.id.message_out_layouts);
            senderName_out = (TextView) itemView.findViewById(R.id.inbox_item_sender_ame_out);
            messageDate_out = (TextView) itemView.findViewById(R.id.inbox_item_date_out);
            messageText_out = (TextView) itemView.findViewById(R.id.inbox_item_message_text_out);
//            personPhoto = (ImageView) itemView.findViewById(R.id.person_photo);

        }


    }

    private String getSenderName(MessageTO message) {
        String result = "";
        if (message.getData() != null) {
            try {
                JSONObject json = new JSONObject(message.getData().toString());
                result = json.getString(Constants.KEY_NAME);
            } catch (JSONException e) {
                Log.e("log", e.getMessage(), e);
            }
        } else {

            result = ChabokApplication.currentActivity.getString(R.string.app_name);
        }

        return result;

    }

}
