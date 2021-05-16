package com.carlos.whatsappclone.provides;

import com.carlos.whatsappclone.models.Chat;
import com.carlos.whatsappclone.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatsProvider {

    CollectionReference chatsCollectionReference;

    public ChatsProvider(){
        chatsCollectionReference = FirebaseFirestore.getInstance().collection("Chats");
    }

    public Task<Void> create(Chat chat){
        return chatsCollectionReference.document(chat.getId()).set(chat);
    }


    public Query getUserChats(String idUser){
        return chatsCollectionReference.whereArrayContains("contactsIds",idUser).whereGreaterThan("numberMessages",0);
    }

    public Query getChatByUser1AndUser2(String idUser1, String idUser2){
        ArrayList<String> ids = new ArrayList<>();
        ids.add(idUser1+idUser2);
        ids.add(idUser2+idUser1);
        return chatsCollectionReference.whereIn("id",ids);
    }

    public DocumentReference getChat(String id){
        return  chatsCollectionReference.document(id);
    }

    public void updateNumberMessages(Chat chat){
        Map<String, Object> map = new HashMap<>();
       // chat.setNumberMessages(chat.getNumberMessages() + 1);
        map.put("numberMessages", chat.getNumberMessages() + 1);
        chatsCollectionReference.document(chat.getId()).update(map);
    }
    public Task<Void> updateChat(Chat chat){
        return chatsCollectionReference.document(chat.getId()).set(chat);
    }

    
}
