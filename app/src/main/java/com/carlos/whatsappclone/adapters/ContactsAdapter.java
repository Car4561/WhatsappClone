package com.carlos.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.activities.ChatActivity;
import com.carlos.whatsappclone.databinding.CardviewContactsBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.protobuf.Timestamp;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactsAdapter extends FirestoreRecyclerAdapter<User, ContactsAdapter.ViewHolder> {

    private Context context;
    private AuthProvider authProvider;
    private User user;

    public ContactsAdapter(@NonNull FirestoreRecyclerOptions<User> options, Context context,User user) {
        super(options);
        this.context = context;
        authProvider = new AuthProvider();
        this.user = user;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_contacts,parent,false);
        Log.d("TAG1","hola");
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull User user) {
        if(user.getId().equals(authProvider.getUid())){
            RecyclerView.LayoutParams  params = (RecyclerView.LayoutParams) holder.itemView.getLayoutParams();
            params.height = 0;
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.topMargin = 0;
            params.bottomMargin = 0;
            holder.itemView.setVisibility(View.GONE);
        }
        holder.tvUserName.setText(user.getUsername());
        holder.tvInfo.setText(user.getInfo());
        if (user.getImage() != null && !user.getImage().equals("")) {
            Picasso.get()
                    .load(user.getImage())
                    .placeholder(R.drawable.icon_person)
                    .error(R.drawable.icon_person)
                    .into(holder.circleImageUser);
        }else {
            holder.circleImageUser.setImageResource(R.drawable.icon_person);
        }
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity(user);
            }
        });


    }

    private void goToChatActivity(User user) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userContact",user);
        intent.putExtra("user",this.user);
        context.startActivity(intent);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvInfo;
        CircleImageView circleImageUser;
        View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardviewContactsBinding cardviewContactsBinding ;
            cardviewContactsBinding = CardviewContactsBinding.bind(itemView);
            tvUserName = cardviewContactsBinding.tvUserNameName;
            tvInfo = cardviewContactsBinding.tvInfo;
            circleImageUser = cardviewContactsBinding.circleImageUser;
            view = itemView;
        }


    }

}
