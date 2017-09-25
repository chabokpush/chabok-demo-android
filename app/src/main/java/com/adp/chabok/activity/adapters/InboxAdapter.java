package com.adp.chabok.activity.adapters;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.adp.chabok.common.RoundedTransform;
import com.adp.chabok.ui.TextView;

import com.adp.chabok.R;
import com.adp.chabok.common.Constants;
import com.adp.chabok.data.models.CaptainMessage;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.List;

public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.InboxViewHolder> {

    private Context context;
    private List<CaptainMessage> messages;

    public InboxAdapter(List<CaptainMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }


    @Override
    public InboxViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {


        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_captain_message_item, viewGroup, false);
        return new InboxAdapter.InboxViewHolder(v);
    }

    @Override
    public void onBindViewHolder(InboxViewHolder holder, int position) {

        String messageText = messages.get(position).getMessage();
        messageText = messageText.replaceAll("\\n", "<br/>");
        holder.messageBody.setText(Html.fromHtml(messageText));
        holder.messageBody.setMovementMethod(LinkMovementMethod.getInstance());



        String data = messages.get(position).getmData();

        if(data != null){

            try {
                JSONObject dataJson = new JSONObject(data);


               if (dataJson.has(Constants.INBOX_IMAGE_URL)){
                    String imageUrl = dataJson.getString(Constants.INBOX_IMAGE_URL);
                    if (!imageUrl.isEmpty()) {
                        holder.imageViewLayout.setVisibility(View.VISIBLE);

                        Picasso.with(context)
                                .load(imageUrl)
                                .noFade()
                                .transform(new RoundedTransform())
                                .into(holder.imageView);

                    }

                }

               if (dataJson.has(Constants.INBOX_IMAGE_LINK)) {

                    final String imageLink = dataJson.getString(Constants.INBOX_IMAGE_LINK);


                    if (!imageLink.isEmpty())
                        holder.imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(imageLink));
                                context.startActivity(browserIntent);
                            }
                        });

                }

            } catch (Exception e) {
                Log.e("LOG", "e=" + e.getMessage(), e);

            }
        }else{
            holder.imageViewLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class InboxViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout imageViewLayout;

        TextView messageBody;
        ImageView imageView;

        InboxViewHolder(View itemView) {
            super(itemView);
            imageViewLayout = itemView.findViewById(R.id.imageView_layout);
            messageBody = itemView.findViewById(R.id.message_body);
            imageView = itemView.findViewById(R.id.image_view);

        }
    }

}
