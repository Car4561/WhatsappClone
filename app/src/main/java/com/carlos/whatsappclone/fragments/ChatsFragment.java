package com.carlos.whatsappclone.fragments;

import android.app.Activity;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.adapters.ChatsAdapter;
import com.carlos.whatsappclone.adapters.ContactsAdapter;
import com.carlos.whatsappclone.databinding.ActivityHomeBinding;
import com.carlos.whatsappclone.databinding.FragmentChatsBinding;
import com.carlos.whatsappclone.databinding.FragmentContactsBinding;
import com.carlos.whatsappclone.models.Chat;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.ChatsProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import java.nio.file.attribute.UserPrincipal;


public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private RecyclerView rvChats;

    private UsersProvider usersProvider;
    private ChatsProvider chatsProvider;


    private User user;
    private AuthProvider authProvider;
    private ChatsAdapter chatsAdapter;

    public ChatsFragment(User user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        rvChats = binding.rvChats;

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();
        chatsProvider = new ChatsProvider();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvChats.setLayoutManager(linearLayoutManager);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (user != null) {
            Query query = chatsProvider.getUserChats(user.getId());
            FirestoreRecyclerOptions<Chat> options = new FirestoreRecyclerOptions.Builder<Chat>()
                    .setQuery(query, Chat.class)
                    .build();
            chatsAdapter = new ChatsAdapter(options, getContext(), user);
            rvChats.setAdapter(chatsAdapter);
            chatsAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        chatsAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (chatsAdapter.getListener() != null){
            chatsAdapter.getListener().remove();
        }
        if (chatsAdapter.getListenerLastMessage() != null){
            chatsAdapter.getListenerLastMessage().remove();
        }
    }
}