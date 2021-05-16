package com.carlos.whatsappclone.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.activities.ChatActivity;
import com.carlos.whatsappclone.databinding.CardviewChatsBinding;
import com.carlos.whatsappclone.databinding.CardviewContactsBinding;
import com.carlos.whatsappclone.databinding.ChatToolbarBinding;
import com.carlos.whatsappclone.models.Chat;
import com.carlos.whatsappclone.models.Message;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.MessagesProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.carlos.whatsappclone.utils.Global;
import com.carlos.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.otaliastudios.cameraview.frame.Frame;
import com.squareup.picasso.Picasso;

import java.net.IDN;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import de.hdodenhof.circleimageview.CircleImageView;
import io.grpc.internal.StreamListener;

public class ChatsAdapter extends FirestoreRecyclerAdapter<Chat, ChatsAdapter.ViewHolder> {

    private Context context;
    private AuthProvider authProvider;
    private UsersProvider usersProvider;
    private MessagesProvider messagesProvider;
    private User user;
    private List<User> userList;

    private ListenerRegistration listener;
    private ListenerRegistration listenerLastMessage;


    public ChatsAdapter(@NonNull FirestoreRecyclerOptions<Chat> options, Context context, User user) {
        super(options);
        this.context = context;
        this.user = user;
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
        messagesProvider = new MessagesProvider();
    //    getUsers();
    }

    private void getUsers() {
        usersProvider.getAllUser().get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Log.d("TAG1",Thread.currentThread().getName().toString());
                    userList = task.getResult().toObjects(User.class);
                    notifyDataSetChanged();
                }
            }
        });
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chats,parent,false);
        return new ViewHolder(view);
    }


    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {
        String id = "";

        for (int i = 0; i < chat.getContactsIds().size(); i++) {
            if (!chat.getContactsIds().get(i).equals(user.getId())) {
                id = chat.getContactsIds().get(i);
                break;
            }
        }
        if (chat.getWriting() != null && !chat.getWriting().equals("") && !chat.getWriting().equals(user.getId())){
            holder.tvLastMessage.setText("Escribiendo...");
            holder.tvLastMessage.setTextColor(context.getResources().getColor(R.color.colorGreenAccent));
            holder.imageViewCheck.setVisibility(View.GONE);
        }else {
            getLastMessage(holder,chat.getId());

        }
        getUserInfo(holder,chat,id);



        getNumberMessagesNoRead(holder,chat.getId());


       /* if (userList != null) {

            Log.d("TAG1", id);

            for (int i = 0; i < userList.size(); i++) {
                if (userList.get(i).getId().equals(id)) {
                    userContact = userList.get(i);
                    break;
                }
            }

            holder.tvUserName.setText(userContact.getUsername());
            Global.setImagePerson(userContact.getImage(), holder.circleImageUser);
            */

    }

    private void getNumberMessagesNoRead(ViewHolder holder, String id) {
        messagesProvider.getRecivedMessagesNotRead(id,user.getId()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null) {
                   // List<Message> messageList = querySnapshot.toObjects(Message.class);
                   // Log.d("TAGLAST", String.valueOf(messageList.size()));
                    if (querySnapshot.size() > 0) {
                        holder.frameLayoutMessagesNotRead.setVisibility(View.VISIBLE);
                        holder.tvTimeStamp.setTextColor(context.getResources().getColor(R.color.colorGreenAccent));
                        holder.tvNumerMessagesNotRead.setText(Integer.toString(querySnapshot.size()));
                    } else {
                        holder.frameLayoutMessagesNotRead.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void getLastMessage(ViewHolder holder, String id) {
       listenerLastMessage =  messagesProvider.getLastMessage(id).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                if (querySnapshot != null){
                    int size = querySnapshot.size();
                    if (size == 1){
                        Message message = querySnapshot.getDocuments().get(0).toObject(Message.class);
                        holder.tvLastMessage.setText(message.getMessage());
                        holder.tvTimeStamp.setText(RelativeTime.timeFormatAMPM(message.getTimestamp(),context));
                        if (!message.getIdSender().equals(user.getId())){
                            holder.imageViewCheck.setVisibility(View.GONE);
                        }else {
                            if (message.getStatus().equals("ENVIADO")){
                                holder.imageViewCheck.setImageResource(R.drawable.icon_doble_check_gray);
                            }else if(message.getStatus().equals("VISTO")){
                                holder.imageViewCheck.setImageResource(R.drawable.icon_dobule_check_blue);
                            }
                        }
                    }else {
                      //  holder.itemView.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void getUserInfo(ViewHolder holder,Chat chat,String id) {
        listener = usersProvider.getUserInfo(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null && documentSnapshot.exists()){
                    User userContact = documentSnapshot.toObject(User.class);
                    holder.tvUserName.setText(userContact.getUsername());
                    Global.setImagePerson(userContact.getImage(),holder.circleImageUser);
                    holder.view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            goToChatActivity(userContact,chat);
                        }
                    });
                }
            }
        });
    }

    private void goToChatActivity(User user, Chat chat) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("userContact",user);
        intent.putExtra("user",this.user);
        intent.putExtra("chat",chat);
        context.startActivity(intent);
    }

    public ListenerRegistration getListener(){
        return listener;
    }
    public ListenerRegistration getListenerLastMessage(){
        return listenerLastMessage;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvUserName;
        TextView tvTimeStamp;
        TextView tvLastMessage;
        TextView tvNumerMessagesNotRead;
        CircleImageView circleImageUser;
        ImageView imageViewCheck;
        View view;
        FrameLayout frameLayoutMessagesNotRead;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            CardviewChatsBinding cardviewChatsBinding ;
            cardviewChatsBinding = CardviewChatsBinding.bind(itemView);
            tvUserName = cardviewChatsBinding.tvUserNameName;
            tvTimeStamp = cardviewChatsBinding.tvTimeStamp;
            tvLastMessage = cardviewChatsBinding.tvLastMessage;
            circleImageUser = cardviewChatsBinding.circleImageUser;
            imageViewCheck = cardviewChatsBinding.imageViewCheck;
            frameLayoutMessagesNotRead = cardviewChatsBinding.frameLayoutMessagesNotRead;
            tvNumerMessagesNotRead = cardviewChatsBinding.tvNumerMessagesNotRead;
            view = itemView;
        }


    }



}
