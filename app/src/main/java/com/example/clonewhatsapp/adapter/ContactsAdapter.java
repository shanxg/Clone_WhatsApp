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
import com.example.clonewhatsapp.model.User;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;




public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.MyViewHolder> {

    private List<User> contacts;
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

    public List<User> getContacts(){
        return this.contacts;
    }

    public ContactsAdapter(List<User> contactsList, Context c) {
        this.contacts = contactsList;
        this.context = c;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.adapter_contacts, parent, false);

        return new MyViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = contacts.get(position);

        holder.textContactName.setText(user.getName());

        boolean newGroupBoolean;
        if (user.getEmail() == null){ newGroupBoolean = true; }else{ newGroupBoolean = false; }

        if(newGroupBoolean){
            holder.textContactQuote.setVisibility(View.GONE);
        }else {
            holder.textContactQuote.setText(user.getEmail());
        }

        if( user.getPhoto()!=null && !user.getPhoto().isEmpty()){
            Uri imageUrl = Uri.parse(user.getPhoto());

            Glide.with(context).load(imageUrl).into(holder.contactProfileImage);
        }else {
            if(newGroupBoolean){
                holder.contactProfileImage.setImageResource(R.drawable.icone_grupo);

            }else {
                holder.contactProfileImage.setImageResource(R.drawable.padrao);
            }
        }
    }

    @Override
    public int getItemCount() {
        return contacts.size();
    }


}
