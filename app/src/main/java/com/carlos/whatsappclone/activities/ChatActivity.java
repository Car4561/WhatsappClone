package com.carlos.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.adapters.ChatsAdapter;
import com.carlos.whatsappclone.adapters.MessagesAdapter;
import com.carlos.whatsappclone.databinding.ActivityChatBinding;
import com.carlos.whatsappclone.databinding.ChatToolbarBinding;
import com.carlos.whatsappclone.models.Chat;
import com.carlos.whatsappclone.models.Message;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.ChatsProvider;
import com.carlos.whatsappclone.provides.MessagesProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.carlos.whatsappclone.utils.AppBackgroundHelper;
import com.carlos.whatsappclone.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.internal.StreamListener;

public class ChatActivity extends AppCompatActivity {

    private ActivityChatBinding binding;
    private User userContact;
    private User user;

    private String idChat;
    private Chat chat;

    private UsersProvider usersProvider;
    private ChatsProvider chatsProvider;
    private MessagesProvider messagesProvider;

    private EditText txtChat;
    private ImageView btnSend;

    private TextView textViewUserName;
    private TextView textViewOnline;
    private ImageView circleImageUser;
    private RecyclerView rvMessage;

    private MessagesAdapter messagesAdapter;

    private  LinearLayoutManager linearLayoutManager;

    private ListenerRegistration listenerChat;

    private Timer timer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        chatsProvider = new ChatsProvider();
        messagesProvider = new MessagesProvider();
        usersProvider = new UsersProvider();

        userContact = (User) getIntent().getSerializableExtra("userContact");
        user = (User) getIntent().getSerializableExtra("user");
        chat = (Chat) getIntent().getSerializableExtra("chat");

        txtChat = binding.txtChat;
        btnSend = binding.btnSend;
        rvMessage = binding.rvMessages;

        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        rvMessage.setLayoutManager(linearLayoutManager);
        rvMessage.setHasFixedSize(true);
        if (userContact != null && user != null)
              showChatToolbar(R.layout.chat_toolbar,userContact);
        else {
            onBackPressed();
        }
        if (chat == null ){
            checkIfExistChat();
        }else {
            getMessages();
        }
        updateStatus();
        chatListener();
        userListener();
        setWriting();
        setWritingState();
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMessage();
            }
        });

    }



    private void setWriting() {
        txtChat.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            //Esta escribiendo
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (chat!=null) {
                    chat.setWriting(user.getId());
                    chatsProvider.updateChat(chat);
                    if (timer != null) {
                        timer.cancel();
                    }
                }
            }

            //Dejo de escribir
            @Override
            public void afterTextChanged(Editable s) {
                timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        if (chat!=null) {
                            chat.setWriting("");
                            chatsProvider.updateChat(chat);
                        }
                    }
                },1000);

            }
        });
    }

    private void chatListener() {
        if (chat != null) {
            listenerChat = chatsProvider.getChat(chat.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot chatEvent, @Nullable FirebaseFirestoreException error) {
                    chat = chatEvent.toObject(Chat.class);
                    setWritingState();

                    Log.d("TAGEvent", String.valueOf(chatEvent.toObject(Chat.class).getNumberMessages()));
                    Log.d("TAGEvent", String.valueOf(chatEvent.toObject(Chat.class).getWriting()));

                }
            });
        }
    }

    private void userListener() {
        if (userContact != null) {
            listenerChat = usersProvider.getUserInfo(userContact.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(@Nullable DocumentSnapshot userEvent, @Nullable FirebaseFirestoreException error) {
                    userContact = userEvent.toObject(User.class);
                    setOnlineStatus();
                    setWritingState();
                    Log.d("TAGEventUser", String.valueOf(userContact.isOnline()));

                }
            });
        }
    }

    private void setOnlineStatus() {
        if (userContact.isOnline()){
            textViewOnline.setText("En linea");
        }else if (userContact.getLastConnect() != null){
            String relativeTime = RelativeTime.getTimeAgo(userContact.getLastConnect(),getApplicationContext());
            textViewOnline.setText(relativeTime);
        }

    }

    private void setWritingState(){
        if ( chat.getWriting() != null && !chat.getWriting().equals("")){
            if (!chat.getWriting().equals(user.getId())){
                textViewOnline.setText("Escribiendo...");
            }
        }else {
            setOnlineStatus();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (messagesAdapter != null) {
            messagesAdapter.startListening();
        }
        AppBackgroundHelper.online(ChatActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (messagesAdapter != null) {
            messagesAdapter.stopListening();
        }
        AppBackgroundHelper.online(ChatActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerChat != null){
            listenerChat.remove();
        }
    }

    private void getMessages() {
        Query queryChats = messagesProvider.getMessagesByChat(chat.getId());
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(queryChats, Message.class)
                .build();
        Log.d("TAG1", String.valueOf(options.getSnapshots().size()));
        messagesAdapter = new MessagesAdapter(options, ChatActivity.this,user);
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateStatus();
                rvMessage.scrollToPosition(positionStart);
           }

        });


        rvMessage.setAdapter(messagesAdapter);
        messagesAdapter.startListening();

    }


    private void createMessage() {
        String textMessage = txtChat.getText().toString().trim();
        if (!textMessage.isEmpty()){
            Message message = new Message();
            message.setIdChat(chat.getId());
            message.setIdSender(user.getId());
            message.setIdReceiver(userContact.getId());
            message.setMessage(textMessage);
            message.setStatus("ENVIADO");
            message.setTimestamp(new Date().getTime());
            Log.d("TAG1", String.valueOf(chat.getNumberMessages()));
            messagesProvider.create(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    txtChat.setText("");
                    chatsProvider.updateNumberMessages(chat);
                    Log.d("TAG1", "enviado");

                    Toast.makeText(ChatActivity.this, "Se envio el mensaje correctamente", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "Debe ingresar el mensaje antes de enviar", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfExistChat() {
        chatsProvider.getChatByUser1AndUser2(userContact.getId(),user.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (queryDocumentSnapshots != null){
                    if (queryDocumentSnapshots.size() == 0 ){
                        createChat();
                    }else {
                        chat = queryDocumentSnapshots.getDocuments().get(0).toObject(Chat.class);
                        getMessages();
                        updateStatus();
                        chatListener();
                        Log.d("TAGCHAT", chat.getId());
                        Toast.makeText(ChatActivity.this, "El chat ya existe", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private void updateStatus() {
        if (chat!= null) {
            messagesProvider.getMessagesNotRead(chat.getId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Message message = documentSnapshot.toObject(Message.class);
                        if (!message.getIdSender().equals(user.getId())) {
                            messagesProvider.updateStatus(message.getId(), "VISTO");
                        }
                    }
                }
            });
        }
    }

    private void createChat() {
        Chat chatCreate = new Chat();
        chatCreate.setId(user.getId() + userContact.getId());
        chatCreate.setTimestamp(new Date().getTime());
        chatCreate.setWriting("");
        ArrayList<String> ids = new ArrayList<>();
        ids.add(user.getId());
        ids.add(userContact.getId());

        chatCreate.setContactsIds(ids);

        chatsProvider.create(chatCreate).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                chat = chatCreate;
                chatListener();
                Toast.makeText(ChatActivity.this, "El chat se creo correctamente", Toast.LENGTH_SHORT).show();
                getMessages();

            }
        });
    }

    private void showChatToolbar(int resource,User user){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ChatToolbarBinding chatToolbarBinding = ChatToolbarBinding.inflate(getLayoutInflater());
        View view = chatToolbarBinding.getRoot();
        actionBar.setCustomView(view);

        ImageView imageViewBack = chatToolbarBinding.imageViewBack;
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        textViewUserName = chatToolbarBinding.textViewUserName;
        textViewOnline = chatToolbarBinding.textViewOnline;
        circleImageUser = chatToolbarBinding.circleImageUser;

        textViewUserName.setText(user.getUsername());
        Picasso.get()
                .load(user.getImage())
                .placeholder(R.drawable.icon_person_white)
                .error(R.drawable.icon_person_white)
                .into(circleImageUser);


    }
}