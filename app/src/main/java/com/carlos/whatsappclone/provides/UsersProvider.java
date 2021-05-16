package com.carlos.whatsappclone.provides;

import com.carlos.whatsappclone.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import kotlinx.coroutines.AwaitKt;

public class UsersProvider {

    private CollectionReference usersCollectionReference;

    public UsersProvider(){
        usersCollectionReference = FirebaseFirestore.getInstance().collection("Users");
    }

    public DocumentReference getUserInfo(String id){
        return usersCollectionReference.document(id);
    }

    public Query getAllUserByName(){
        return usersCollectionReference.orderBy("username").whereEqualTo("status",1);
    }


    public CollectionReference getAllUser(){
        return usersCollectionReference;
    }

    public Task<Void> create(User user){
        return usersCollectionReference.document(user.getId()).set(user);
    }

    public Task<Void> update(User user){
        Map<String,Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("image",user.getImage());
        return usersCollectionReference .document(user.getId()).update(map);
    }

  /*public Task<Void> updateImage(String id,String url){
        Map<String,Object> map = new HashMap<>();
        map.put("image",url);
        return usersCollectionReference.document(id).update(map);
    }*/

    public Task<Void> updateUser(User user){
        return usersCollectionReference.document(user.getId()).set(user);
    }

    public Task<Void> updateOnline(String id,boolean online){
        Map<String,Object> map = new HashMap<>();
        map.put("online",online);
        map.put("lastConnect",new Date().getTime());
        return usersCollectionReference.document(id).update(map);
    }
}
