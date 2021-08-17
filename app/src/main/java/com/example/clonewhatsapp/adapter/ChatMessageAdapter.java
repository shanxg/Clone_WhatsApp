package com.example.clonewhatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.helper.Base64Custom;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder> {

    private List<ChatMessage> messages;
    private Context context;

    private static final int TYPE_USER = 1;
    private static final int TYPE_SELECTED_USER = 0;



    public ChatMessageAdapter(List<ChatMessage> messages, Context context) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View item = null;


        if (viewType == TYPE_USER){
           item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_message_user,parent, false);

        }else if(viewType==TYPE_SELECTED_USER) {
           item = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.adapter_message_seleteced_user,parent, false);

        }

        return new ChatViewHolder(item);
    }


    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {

        ChatMessage message = messages.get(position);


        if( holder.getItemViewType()==TYPE_SELECTED_USER && message.getSenderName()!=null && !message.getSenderName().isEmpty() ){

            holder.movLayout.setVisibility(View.VISIBLE);
            holder.textMemberName.setText(message.getSenderName());

            String memberEmail = Base64Custom.decodeBase64(message.getUserID());
            holder.textMemberEmail.setText(memberEmail);
        }

        String messageText = message.getMessage();
        String image  = message.getImage();

        if( image != null && !image.isEmpty()){

            holder.chatImage.setVisibility(View.VISIBLE);

            Uri imageUrl = Uri.parse(image);
            Glide.with(context).load(imageUrl).into(holder.chatImage);

            holder.chatText.setVisibility(View.GONE);

        }else {

            holder.chatText.setText(messageText);

        }




    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        ChatMessage message = messages.get(position);

        String userID = UserFirebase.getUserID();

        if( userID.equals( message.getUserID() )){
            return TYPE_USER;
        }
        return TYPE_SELECTED_USER ;
    }

    class ChatViewHolder extends RecyclerView.ViewHolder{

        TextView chatText, textMemberEmail, textMemberName;
        ImageView chatImage;
        LinearLayout movLayout;


        ChatViewHolder(View itemView) {
            super(itemView);
            chatImage = itemView.findViewById(R.id.imageChatMessage);
            chatText = itemView.findViewById(R.id.textChatMessage);

            movLayout = itemView.findViewById(R.id.movLayout);
            textMemberEmail = itemView.findViewById(R.id.textMemberEmail);
            textMemberName = itemView.findViewById(R.id.textMemberName);
        }
    }
}
