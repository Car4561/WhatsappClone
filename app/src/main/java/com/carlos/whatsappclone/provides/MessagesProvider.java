package com.carlos.whatsappclone.provides;

import android.app.Activity;
import android.content.ContentProviderOperation;
import android.util.Log;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carlos.whatsappclone.activities.HomeActivity;
import com.carlos.whatsappclone.activities.ProfileActivity;
import com.carlos.whatsappclone.databinding.ActivityChatBinding;
import com.carlos.whatsappclone.models.Message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class MessagesProvider {

    private CollectionReference messageCollectionReference;

    public MessagesProvider() {
        messageCollectionReference = FirebaseFirestore.getInstance().collection("Messages");

    }

    public Task<Void> create(Message message){
        DocumentReference documentReference = messageCollectionReference.document();
        message.setId(documentReference.getId());
      
        return  documentReference.set(message);
    }

    public Query getMessagesByChat(String idChat){
        return messageCollectionReference.whereEqualTo("idChat",idChat).orderBy("timestamp", Query.Direction.ASCENDING);
    }

    public Task<Void> updateStatus(String idMessage,String status){
        Map<String, Object> map = new HashMap<>();
        map.put("status",status);
        return messageCollectionReference.document(idMessage).update(map);
    }

    public Query getMessagesNotRead(String idChat){
        return messageCollectionReference.whereEqualTo("idChat",idChat).whereEqualTo("status","ENVIADO");
    }

    public Query getLastMessage(String idChat){
        return messageCollectionReference.whereEqualTo("idChat",idChat).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
    }


    public Query getRecivedMessagesNotRead(String idChat,String idReceiver){
        return messageCollectionReference.whereEqualTo("idChat",idChat).whereEqualTo("status","ENVIADO").whereEqualTo("idReceiver",idReceiver);
    }


}
