package com.carlos.whatsappclone.utils;

import android.view.View;
import android.widget.ImageView;

import com.carlos.whatsappclone.R;
import com.squareup.picasso.Picasso;

public class Global {


    public static void setImagePerson(String urlImage, ImageView imageView) {
        Picasso.get()
                .load(urlImage)
                .placeholder(R.drawable.icon_person)
                .error(R.drawable.icon_person)
                .into(imageView);
    }

}
