package com.carlos.whatsappclone.activities;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carlos.whatsappclone.databinding.ActivityCodeVerificationBinding;
import com.carlos.whatsappclone.databinding.ActivityMainBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.rpc.Code;

import org.w3c.dom.Text;

import java.util.Objects;

public class CodeVerificationActivity extends AppCompatActivity {

    private EditText txtCodeVerification;
    private Button btnCodeVerification;
    private TextView tvSMS;
    private ProgressBar progressBar;

    private ActivityCodeVerificationBinding binding;

    private String extraPhone;
    private String verificationId;

    private AuthProvider authProvider;
    private UsersProvider usersProvider;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         binding = ActivityCodeVerificationBinding.inflate(getLayoutInflater());
         setContentView(binding.getRoot());

         extraPhone = getIntent().getStringExtra("phone");
         authProvider  = new AuthProvider();
         authProvider.sendCodeVerification(extraPhone,callbacks);

         usersProvider = new UsersProvider();

         progressDialog = new ProgressDialog(CodeVerificationActivity.this);
         progressDialog.setTitle("Espere un momento");
         progressDialog.setMessage("Autenticando..");

         btnCodeVerification = binding.btnCodeVerification;
         txtCodeVerification = binding.txtCodeVerification;
         tvSMS = binding.tvSMS;
         progressBar = binding.progressBar;

         btnCodeVerification.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                String code = txtCodeVerification.getText().toString().trim();
                if(!code.isEmpty()){

                    signIn(code);

                }else {
                    Toast.makeText(CodeVerificationActivity.this, "Ingresa el código", Toast.LENGTH_SHORT).show();
                }
             }
         });
         binding.btnCodeVerification.setEnabled(false);
        //  txtCodeVerification.setText("123456");
    }

    PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            progressBar.setVisibility(View.GONE);
            tvSMS.setVisibility(View.GONE);
            btnCodeVerification.setEnabled(true);
            String code =  phoneAuthCredential.getSmsCode();
            if (code != null){
                txtCodeVerification.setText(code);
                signIn(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            progressBar.setVisibility(View.GONE);
            tvSMS.setVisibility(View.GONE);

            Toast.makeText(CodeVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.d("TAG1",e.getMessage());
            Log.d("TAG12",e.getLocalizedMessage());
            onBackPressed();
        }

        @Override
        public void onCodeSent(@NonNull String id, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            btnCodeVerification.setEnabled(true);
            progressBar.setVisibility(View.GONE);
            tvSMS.setVisibility(View.GONE);
            super.onCodeSent(id, forceResendingToken);
            Toast.makeText(CodeVerificationActivity.this, "El código ha sido enviado", Toast.LENGTH_SHORT).show();
            verificationId = id;
        }

    };

    private void signIn(String code) {
        progressDialog.show();
        authProvider.signInPhone(verificationId,code).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    User user = new User();
                    user.setId(authProvider.getUid());
                    user.setPhone(extraPhone);

                    usersProvider.getUserInfo(authProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(!documentSnapshot.exists()){
                                usersProvider.create(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        goToCompleteInfo(user);
                                    }
                                });
                            }else{
                                String usernameDocument = documentSnapshot.getString("username");
                                String imageUrl = documentSnapshot.getString("image");
                                user.setUsername(usernameDocument);
                                user.setImage(imageUrl);
                                if (usernameDocument != null && !usernameDocument.isEmpty() && imageUrl != null && !imageUrl.isEmpty()  ) {
                                    Log.d("TAG1", "1");
                                    goToHomeActivity(user);
                                }else{
                                    goToCompleteInfo(user);
                                }
                                Log.d("TAG1","3");

                            }
                        }
                    });


                    Toast.makeText(CodeVerificationActivity.this, "La autenticación fue exitosa", Toast.LENGTH_SHORT).show();
               }else{
                    Toast.makeText(CodeVerificationActivity.this, "No se pudo autenticar al usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToHomeActivity(User user) {
        progressDialog.dismiss();
        Intent intent = new Intent(CodeVerificationActivity.this,HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("user",user);
        startActivity(intent);
    }

    private void goToCompleteInfo(User user) {
        progressDialog.dismiss();
        Intent intent = new Intent(CodeVerificationActivity.this,CompleteInfoActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK );
        intent.putExtra("user",user);
        startActivity(intent);
    }


}