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

public class SelectedContactsAdapater extends RecyclerView.Adapter<SelectedContactsAdapater.SelectedContactsViewHolder> {

    private List<User> selectedContactsList;
    private Context context;

    public SelectedContactsAdapater(List<User> selectedContactsList, Context context) {
        this.selectedContactsList = selectedContactsList;
        this.context = context;
    }

    @NonNull
    @Override
    public SelectedContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemList = LayoutInflater.from(context)
                .inflate(R.layout.adapter_selected_contacts, parent, false);

        return new SelectedContactsViewHolder(itemList);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectedContactsViewHolder holder, int position) {
        User user = selectedContactsList.get(position);

        holder.selectedContactName.setText(user.getName());

        if( user.getPhoto()!=null && !user.getPhoto().isEmpty()){

            Uri imageUrl = Uri.parse(user.getPhoto());
            Glide.with(context).load(imageUrl).into(holder.selectedContactImage);

        }else {
            holder.selectedContactImage.setImageResource(R.drawable.padrao);
        }

    }

    @Override
    public int getItemCount() {
        return selectedContactsList.size();
    }


    class SelectedContactsViewHolder extends RecyclerView.ViewHolder{

        private CircleImageView selectedContactImage;
        private TextView selectedContactName;

        public SelectedContactsViewHolder(@NonNull View itemView) {
            super(itemView);

            selectedContactImage = itemView.findViewById(R.id.selectedContactsImage);
            selectedContactName = itemView.findViewById(R.id.selectedContactName);

        }
    }



}
