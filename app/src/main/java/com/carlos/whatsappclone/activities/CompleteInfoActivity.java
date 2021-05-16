package com.carlos.whatsappclone.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.databinding.ActivityCompleteInfoBinding;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.ImageProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CompleteInfoActivity extends AppCompatActivity {

    private  ActivityCompleteInfoBinding binding;
    private  TextInputEditText txtInputUserName;
    private  Button btnConfirm;
    private  CircleImageView circleImgPhoto;
    private FloatingActionButton fabBtnImage;

    private  UsersProvider usersProvider;
    private  ImageProvider imageProvider;

    private  User user;

    private  Options options;

    private  ArrayList<String> returnValue = new ArrayList<>();
    private  File imgFile;

    private String username;

    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCompleteInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        user = (User) getIntent().getSerializableExtra("user");

        usersProvider = new UsersProvider();
        imageProvider = new ImageProvider();

        progressDialog = new ProgressDialog(CompleteInfoActivity.this);
        progressDialog.setTitle("Espere un momento");
        progressDialog.setMessage("Guardando información..");
        options = Options.init()
             .setRequestCode(100)                                           //Request code for activity results
             .setCount(3)                                                   //Number of images to restict selection count
             .setFrontfacing(false)                                         //Front Facing camera on start
             .setPreSelectedUrls(returnValue)                               //Pre selected Image Urls
             .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
             .setExcludeVideos(false)                                       //Option to exclude videos
             .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
             .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
             .setPath("/pix/images");                                       //Custom Path For media Storage


        txtInputUserName = binding.txtInputUserName;
        btnConfirm = binding.btnConfirm;
        circleImgPhoto = binding.circleImgPhoto;
        fabBtnImage = binding.fabBtnImage;

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username =   txtInputUserName.getText().toString().trim();
                if (!username.isEmpty()){
                    saveImage();
                }else {
                    Toast.makeText(CompleteInfoActivity.this, "Ingrese un nombre", Toast.LENGTH_SHORT).show();
                }
            }
        });

        fabBtnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPix();
            }
        });
        txtInputUserName.setText(user.getUsername());
        if(user.getImage() != null && !user.getImage().isEmpty()) {

            Picasso.get()
                    .load(user.getImage())
                    .placeholder(R.drawable.icon_person)
                    .error(R.drawable.icon_person)
                    .into(circleImgPhoto);

        }
    }

    private void startPix() {
        Pix.start(CompleteInfoActivity.this,options);
    }

    private void updateUserInfo() {

        user.setUsername(username);
        usersProvider.update(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                  goToHomeActivity();
            }
        });

    }

    private void goToHomeActivity() {
        progressDialog.dismiss();
        Toast.makeText(CompleteInfoActivity.this, "La información se actualizo correctamente", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(CompleteInfoActivity.this,HomeActivity.class);
        intent.putExtra("user",user);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void saveImage() {
        progressDialog.show();
        if (imgFile != null) {
            imageProvider.save(CompleteInfoActivity.this, imgFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                Log.d("TAG1", url);
                                user.setImage(url);
                                updateUserInfo();
                            }
                        });
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CompleteInfoActivity.this, "Error al guardar imagén", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            updateUserInfo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.d("TAG1", String.valueOf(returnValue));
            imgFile = new File(returnValue.get(0));
            circleImgPhoto.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(CompleteInfoActivity.this, options);
                } else {
                    Toast.makeText(CompleteInfoActivity.this, "Por favor concede  los permisos para acceder a la cámara", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }
}