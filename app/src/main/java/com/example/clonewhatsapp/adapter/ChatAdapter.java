package com.example.clonewhatsapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clonewhatsapp.R;
import com.example.clonewhatsapp.helper.UserFirebase;
import com.example.clonewhatsapp.model.Chat;
import com.example.clonewhatsapp.model.Group;
import com.example.clonewhatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MyViewHolder> {

    private List<Chat> contactsChat;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textContactName, textContactQuote;
        CircleImageView contactProfileImage;

        MyViewHolder(View itemView) {
            super(itemView);

            textContactName = itemView.findViewById(R.id.textContactName);
            textContactQuote = itemView.findViewById(R.id.textContactQuote);
            contactProfileImage = itemView.findViewById(R.id.profileContactsImage);
        }
    }

    public List<Chat> getContactsChat(){
        return this.contactsChat;
    }

    public ChatAdapter(List<Chat> contactsList, Context c) {
        this.contactsChat = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public ChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contacts, parent, false);

        return new ChatAdapter.MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(ChatAdapter.MyViewHolder holder, int position) {

        Chat chat = contactsChat.get(position);

        String image, chatName, lastMsg, senderName;
        User currentUser = UserFirebase.getLoggedUserData();

        if(chat.getSenderName()!=null){
            senderName = chat.getSenderName();

            if(currentUser.getName().equals(senderName)){
                lastMsg = chat.getLastMessage();
            }else {
                lastMsg = senderName + ": " + chat.getLastMessage();
            }

        }else {
            lastMsg = chat.getLastMessage();
        }

        if(chat.getIsGroup()){

            Group group = chat.getGroup();

            chatName = group.getGroupName();
            image = group.getGroupPhoto();


        }else {

            User selectedUser = chat.getSelectedUser();

            chatName = selectedUser.getName();
            image = selectedUser.getPhoto();

        }

        holder.textContactName.setText(chatName);
        holder.textContactQuote.setText(lastMsg);

        if( image!=null && !image.equals("")){

            Uri imageUrl = Uri.parse(image);
            Glide.with(context).load(imageUrl).into(holder.contactProfileImage);

        }else{

            if(chat.getIsGroup()){
                holder.contactProfileImage.setImageResource(R.drawable.icone_grupo);
            }else {
                holder.contactProfileImage.setImageResource(R.drawable.padrao);
            }
        }






    }

    @Override
    public int getItemCount() {


        return contactsChat.size();
    }


}
