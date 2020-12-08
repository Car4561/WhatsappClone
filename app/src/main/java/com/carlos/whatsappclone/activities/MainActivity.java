package com.carlos.whatsappclone.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.carlos.whatsappclone.databinding.ActivityMainBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.google.rpc.context.AttributeContext;
import com.hbb20.CountryCodePicker;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private EditText txtPhone;
    private CountryCodePicker countryCodePicker;

    private AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        authProvider = new AuthProvider();
        txtPhone = binding.txtPhone;
        countryCodePicker = binding.countryCodePicker;
        binding.btnSetCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPhone();
            }
        });
     //   txtPhone.setText("6505551234");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(authProvider.getSessionUser() != null){
            Intent intent = new Intent(MainActivity.this,HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void test(String c) {
        c="Carlos";
    }


    private void getPhone(){
        String code = countryCodePicker.getSelectedCountryCodeWithPlus();
        String phone = txtPhone.getText().toString().trim();
        if(phone.isEmpty()){
            Toast.makeText(this, "Ingrese su número de teléfono", Toast.LENGTH_SHORT).show();
            return;
        }
        goToCodeVerificationActivity(code + phone);
        //Toast.makeText(this, "Telefono: " + code + phone, Toast.LENGTH_SHORT).show();

    }

    private void goToCodeVerificationActivity(String phone){

        Intent intent = new Intent(MainActivity.this, CodeVerificationActivity.class);
        intent.putExtra("phone",phone);
        startActivity(intent);

    }



}