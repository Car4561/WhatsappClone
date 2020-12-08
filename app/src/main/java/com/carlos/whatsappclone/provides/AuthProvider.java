package com.carlos.whatsappclone.provides;

import androidx.arch.core.executor.TaskExecutor;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.security.PublicKey;
import java.util.concurrent.TimeUnit;

public class AuthProvider {

    private  FirebaseAuth auth;

    public AuthProvider(){
        auth = FirebaseAuth.getInstance();
        auth.setLanguageCode("Es");
    }

    public void sendCodeVerification(String phone, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks){

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,
                callbacks
        );

    }

    public Task<AuthResult> signInPhone(String verifiactionId,String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verifiactionId,code);
        return auth.signInWithCredential(credential);
    }

    public FirebaseUser getSessionUser(){
        return  auth.getCurrentUser();
    }

    public String getUid(){
        if(auth.getCurrentUser() != null){
            return  auth.getCurrentUser().getUid();
        }
        return  null;
    }

    public void signOut(){
        auth.signOut();
    }

}
