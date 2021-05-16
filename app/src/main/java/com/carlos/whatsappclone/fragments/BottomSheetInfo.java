package com.carlos.whatsappclone.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carlos.whatsappclone.databinding.BottomSheetInfoBinding;
import com.carlos.whatsappclone.databinding.BottomSheetUsernameBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.ImageProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetInfo extends BottomSheetDialogFragment {

    private BottomSheetInfoBinding binding;

    private Button btnCancel;
    private Button btnSave;
    private EditText txtInfo;

    private UsersProvider usersProvider;
    private ImageProvider imageProvider;
    private AuthProvider  authProvider;

    private User user;
    private String  id;

    public BottomSheetInfo(User user){
        this.user = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetInfoBinding.inflate(inflater,container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);



        usersProvider = new UsersProvider();

        txtInfo = binding.txtInfo;
        btnCancel = binding.btnCancel;
        btnSave = binding.btnSave;
        if(user.getInfo() != null) {
            txtInfo.setText(user.getInfo());
        }
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateInfo();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return binding.getRoot();
    }

    private void updateInfo() {
        String info = txtInfo.getText().toString().trim();
        user.setInfo(info);
        if (!info.equals("")) {
            usersProvider.updateUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("TAG1",info);
                    Toast.makeText(getContext(),"El estado se ha actualizado correctamente",Toast.LENGTH_SHORT).show();
                    dismiss();

                }
            });
        }else{
            Toast.makeText(getContext(), "Tu info no puede quedar vacía", Toast.LENGTH_SHORT).show();
        }
    }




}
