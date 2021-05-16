package com.carlos.whatsappclone.fragments;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.activities.CompleteInfoActivity;
import com.carlos.whatsappclone.activities.ProfileActivity;
import com.carlos.whatsappclone.databinding.BottomSheetSelectImageBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.ImageProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomappbar.BottomAppBarTopEdgeTreatment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.api.Distribution;

import java.io.File;
import java.nio.file.attribute.UserPrincipal;

public class BottomSheetSelectImage  extends BottomSheetDialogFragment {

    private LinearLayout linearLayoutDeleteImage;
    private LinearLayout linearLayoutSelectImage;

    private BottomSheetSelectImageBinding binding;


    private UsersProvider usersProvider;
    private ImageProvider imageProvider;
    private AuthProvider  authProvider;

    private  User user;

    public BottomSheetSelectImage (User user){
        this.user = user;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomSheetSelectImageBinding.inflate(inflater,container,false);

        usersProvider = new UsersProvider();
        imageProvider = new ImageProvider();

        linearLayoutDeleteImage = binding.linearLayoutDeleteImage;
        linearLayoutSelectImage = binding.linearLayoutSelectImage;
        linearLayoutDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteImage();
            }
        });

        linearLayoutSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateImage();
            }
        });

        if(user.getImage() == null || user.getImage().equals("")){
            linearLayoutDeleteImage.setVisibility(View.GONE);
        }
        return binding.getRoot();
    }

    private void updateImage() {
        ((ProfileActivity) getActivity()).startPix();
        dismiss();

    }

    private void deleteImage() {
        if (user.getImage() != null && !user.getImage().equals("")){
            String deleteImage = user.getImage();
            user.setImage(null);
            usersProvider.updateUser(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if (task2.isSuccessful()){
                        imageProvider.delete(deleteImage).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                              //      setImageDefault();
                                    Toast.makeText(getContext(), "La foto fue eliminada correctamente", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                } else {
                                    Toast.makeText(getContext(), "No se pudo eliminar la imagen", Toast.LENGTH_SHORT).show();
                                    dismiss();
                                }
                            }
                        });
                    }else{
                        dismiss();

                        Toast.makeText(getContext(), "No se pudo eliminar la imagen", Toast.LENGTH_SHORT).show();
                    }
                }
            });



    }
    }

  /*  private void setImageDefault() {
        ((ProfileActivity)getActivity() ).setImageDefault();
        dismiss();
    }*/



}
