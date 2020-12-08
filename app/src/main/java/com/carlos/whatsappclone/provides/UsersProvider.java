package com.carlos.whatsappclone.provides;

import com.carlos.whatsappclone.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference usersCollectionReference;

    public UsersProvider(){
        usersCollectionReference = FirebaseFirestore.getInstance().collection("Users");
    }

    public DocumentReference getUserInfo(String id){
        return usersCollectionReference.document(id);
    }

    public Query getAllUserByName(){
        return usersCollectionReference.orderBy("username");
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

    public Task<Void> updateImage(String id,String url){
        Map<String,Object> map = new HashMap<>();
        map.put("image",url);
        return usersCollectionReference.document(id).update(map);
    }

    public Task<Void> updateUsername(String id,String username){
        Map<String,Object> map = new HashMap<>();
        map.put("username",username);
        return usersCollectionReference.document(id).update(map);
    }

    public Task<Void> updateInfo(String id,String info){
        Map<String,Object> map = new HashMap<>();
        map.put("info",info);
        return usersCollectionReference.document(id).update(map);
    }

}
