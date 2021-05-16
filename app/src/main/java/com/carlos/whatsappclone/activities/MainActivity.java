package com.carlos.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.rpc.context.AttributeContext;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {


    private AuthProvider authProvider;
    private UsersProvider usersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authProvider = new AuthProvider();
        usersProvider = new UsersProvider();
     //   txtPhone.setText("6505551234");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authProvider.getSessionUser() != null){
            usersProvider.getUserInfo(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Intent intent = new Intent(MainActivity.this,HomeActivity.class);
                    User user = documentSnapshot.toObject(User.class);
                    intent.putExtra("user",user);
                    startActivity(intent);
                }
            });

        }else{
            Intent intent = new Intent(MainActivity.this,CodeInputActivity.class);
            startActivity(intent);

        }
    }



}