package com.carlos.whatsappclone.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.adapters.ContactsAdapter;
import com.carlos.whatsappclone.databinding.FragmentContactsBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment {

    private FragmentContactsBinding binding;
    private RecyclerView rvContacts;

    private ContactsAdapter contactsAdapter;

    private UsersProvider usersProvider;
    private AuthProvider authProvider;

    private  User user;
    public ContactsFragment(User user) {
        this.user = user;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding =  FragmentContactsBinding.inflate(inflater, container, false);
        rvContacts = binding.rvContacts;

        usersProvider = new UsersProvider();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        rvContacts.setLayoutManager(linearLayoutManager);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = usersProvider.getAllUserByName();
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query,User.class)
                .build();
        authProvider = new AuthProvider();

        contactsAdapter = new ContactsAdapter(options,getContext(),user);
        rvContacts.setAdapter(contactsAdapter);
        contactsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        contactsAdapter.stopListening();
    }


}