package com.carlos.whatsappclone.fragments;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.signature.AndroidResourceSignature;
import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.activities.ProfileActivity;
import com.carlos.whatsappclone.databinding.BottomSheetSelectImageBinding;
import com.carlos.whatsappclone.databinding.BottomSheetUsernameBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.ImageProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetUsername extends BottomSheetDialogFragment {

    private BottomSheetUsernameBinding binding;

    private Button btnCancel;
    private Button btnSave;
    private EditText txtUsername;

    private UsersProvider usersProvider;
    private ImageProvider imageProvider;
    private AuthProvider  authProvider;

    private User user;
    private String username;
    private String  id;

    public BottomSheetUsername(User user){
        this.user = user;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetUsernameBinding.inflate(inflater,container,false);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);



        usersProvider = new UsersProvider();

        txtUsername = binding.txtUserName;
        btnCancel = binding.btnCancel;
        btnSave = binding.btnSave;

        txtUsername.setText(user.getUsername());

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUserName();
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

    private void updateUserName() {
        String username = txtUsername.getText().toString().trim();
        user.setUsername(username);
        if (!username.equals("")) {
            usersProvider.updateUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getContext(),"El nombre de usuario se ha actualizado correctamente",Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        }else{
            Toast.makeText(getContext(), "Tu nombre no puede quedar vacío", Toast.LENGTH_SHORT).show();
        }
    }




}
