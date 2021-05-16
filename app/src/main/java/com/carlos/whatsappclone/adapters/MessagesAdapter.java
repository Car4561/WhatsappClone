package com.carlos.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.activities.ChatActivity;
import com.carlos.whatsappclone.activities.HomeActivity;
import com.carlos.whatsappclone.databinding.CardviewChatsBinding;
import com.carlos.whatsappclone.databinding.CardviewMessageBinding;
import com.carlos.whatsappclone.models.Chat;
import com.carlos.whatsappclone.models.Message;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.carlos.whatsappclone.utils.Global;
import com.carlos.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends FirestoreRecyclerAdapter<Message, MessagesAdapter.ViewHolder> {

    private Context context;
    private AuthProvider authProvider;
    private UsersProvider usersProvider;

    private List<User> userList;
    private User user;

    private ListenerRegistration listener;

    public MessagesAdapter(@NonNull FirestoreRecyclerOptions<Message> options, Context context, User user) {
        super(options);
        this.context = context;
        this.user = user;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_message,parent,false);
        Log.d("TAGCount", String.valueOf(getItemCount()));
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Message message) {
     //   holder.setIsRecyclable(false);
        holder.linearLayoutMessage.setPadding(0,0,0,0);

        holder.tvMessage.setText(message.getMessage().trim());
        holder.tvDate.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        );
        if (message.getIdSender().equals(user.getId())){
            params.addRule(RelativeLayout.ALIGN_PARENT_END);
            params.setMargins(150,0,0,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(30,20,50,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_right));
            holder.tvMessage.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.VISIBLE);
            if (message.getStatus().equals("ENVIADO")){
                holder.imageViewCheck.setImageResource(R.drawable.icon_doble_check_gray);
            }else if (message.getStatus().equals("VISTO")){
                holder.imageViewCheck.setImageResource(R.drawable.icon_dobule_check_blue);
            }
        }else {
            params.addRule(RelativeLayout.ALIGN_PARENT_START);
            params.setMargins(0,0,150,0);
            holder.linearLayoutMessage.setLayoutParams(params);
            holder.linearLayoutMessage.setPadding(50,20,0,20);
            holder.linearLayoutMessage.setBackground(context.getResources().getDrawable(R.drawable.bubble_corner_left));
            holder.tvMessage.setTextColor(Color.BLACK);
            holder.tvDate.setTextColor(Color.DKGRAY);
            holder.imageViewCheck.setVisibility(View.INVISIBLE);
        }



    }

    private void goToChatActivity(User user, Chat chat) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userContact",user);
        intent.putExtra("chat",chat);
        context.startActivity(intent);
    }

    public ListenerRegistration getListener(){
        return listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMessage;
        TextView tvDate;
        ImageView imageViewCheck;
        View view;
        LinearLayout linearLayoutMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardviewMessageBinding cardviewMessageBinding ;
            cardviewMessageBinding = CardviewMessageBinding.bind(itemView);
            tvMessage = cardviewMessageBinding.tvMessage;
            tvDate = cardviewMessageBinding.tvDate;
            imageViewCheck = cardviewMessageBinding.imageViewCheck;
            linearLayoutMessage = cardviewMessageBinding.linearLayoutMessage;
            view = itemView;
        }


    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
    }


}
