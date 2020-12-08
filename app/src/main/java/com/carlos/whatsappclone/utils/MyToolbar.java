package com.carlos.whatsappclone.utils;


import android.app.Notification;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.databinding.ActionBarToolbarBinding;
import com.carlos.whatsappclone.databinding.ActivityProfileBinding;

public class MyToolbar{


    public static void show(AppCompatActivity activity, String tittle, boolean upButton){
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(tittle);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

}
