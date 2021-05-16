package com.carlos.whatsappclone.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.carlos.whatsappclone.databinding.ActivityProfileBinding;

import com.carlos.whatsappclone.R;
import com.carlos.whatsappclone.fragments.BottomSheetInfo;
import com.carlos.whatsappclone.fragments.BottomSheetSelectImage;
import com.carlos.whatsappclone.fragments.BottomSheetUsername;
import com.carlos.whatsappclone.models.User;
import com.carlos.whatsappclone.provides.AuthProvider;
import com.carlos.whatsappclone.provides.ImageProvider;
import com.carlos.whatsappclone.provides.UsersProvider;
import com.carlos.whatsappclone.utils.AppBackgroundHelper;
import com.carlos.whatsappclone.utils.MyToolbar;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.PermUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private FloatingActionButton fabBtnSelectImage;

    private BottomSheetSelectImage bottomSheetSelectImage;
    private BottomSheetUsername bottomSheetUsername;
    private BottomSheetInfo bottomSheetInfo;

    private UsersProvider usersProvider;
    private AuthProvider authProvider;
    private ImageProvider imageProvider;

    private TextView tvUserName;
    private TextView tvPhone;
    private TextView tvStatus;
    private CircleImageView circleImgProfile;
    private LinearLayout linearLayoutUsername;
    private LinearLayout linearLayoutInfo;

    private User user;

    private Options options;
    private ArrayList<String> returnValue = new ArrayList<>();
    private File imgFile;

    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        usersProvider = new UsersProvider();
        authProvider = new AuthProvider();

        tvPhone = binding.tvPhone;
        tvUserName = binding.tvUserName;
        tvStatus = binding.tvStatus;
        circleImgProfile = binding.circleImgProfile;
        linearLayoutUsername = binding.linearLayoutUsername;
        linearLayoutInfo = binding.linearLayoutInfo;

        MyToolbar.show(this,"Perfil",true);

        options = Options.init()
                .setRequestCode(100)                                           //Request code for activity results
                .setCount(3)                                                   //Number of images to restict selection count
                .setFrontfacing(false)                                         //Front Facing camera on start
                .setPreSelectedUrls(returnValue)                               //Pre selected Image Urls
                .setSpanCount(4)                                               //Span count for gallery min 1 & max 5
                .setExcludeVideos(false)                                       //Option to exclude videos
                .setVideoDurationLimitinSeconds(0)                            //Duration for video recording
                .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)     //Orientaion
                .setPath("/pix/images");

        fabBtnSelectImage = binding.fabBtnImage;
        fabBtnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetImage();
            }
        });

        linearLayoutUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetUsername();
            }
        });

        linearLayoutInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheetInfo();
            }
        });

        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppBackgroundHelper.online(ProfileActivity.this);

    }

    @Override
    protected void onStop() {
        super.onStop();
        AppBackgroundHelper.online(ProfileActivity.this);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerRegistration != null){
            listenerRegistration.remove();
        }
    }

    private void getUserInfo() {

        listenerRegistration =  usersProvider.getUserInfo(authProvider.getUid()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(User.class);
                        if (user.getInfo() != null){
                            tvStatus.setText(user.getInfo());
                        }
                        tvUserName.setText(user.getUsername());
                        if (user.getInfo() != null){
                            tvStatus.setText(user.getInfo());
                        }
                        tvPhone.setText(user.getPhone());
                        if (user.getImage() != null && !user.getImage().equals("")) {
                            Picasso.get()
                                    .load(user.getImage())
                                    .placeholder(R.drawable.icon_person_white)
                                    .error(R.drawable.icon_person_white)
                                    .into(circleImgProfile);

                        }else{
                            setImageDefault();
                        }
                    } else {
                        Toast.makeText(ProfileActivity.this, "Sucedió un error", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    }
                }
            }
        });
    }

    private void openBottomSheetImage() {
        bottomSheetSelectImage = new BottomSheetSelectImage(user);
        bottomSheetSelectImage.show(getSupportFragmentManager(),bottomSheetSelectImage.getTag());
    }

    private void openBottomSheetUsername() {
        bottomSheetUsername = new BottomSheetUsername(user);
        bottomSheetUsername.show(getSupportFragmentManager(),bottomSheetUsername.getTag());
    }

    private void openBottomSheetInfo() {
        bottomSheetInfo = new BottomSheetInfo(user);
        bottomSheetInfo.show(getSupportFragmentManager(),bottomSheetInfo.getTag());
    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    public void setImageDefault(){
        circleImgProfile.setImageResource(R.drawable.icon_person_white);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 100) {
            returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            Log.d("TAG1", String.valueOf(returnValue));
            imgFile = new File(returnValue.get(0));
            circleImgProfile.setImageBitmap(BitmapFactory.decodeFile(imgFile.getAbsolutePath()));
        }
        saveImage();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(ProfileActivity.this, options);
                } else {
                    Toast.makeText(ProfileActivity.this, "Por favor concede  los permisos para acceder a la cámara", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void startPix() {
        Pix.start(ProfileActivity.this,options);
    }

    private void saveImage() {
        imageProvider = new ImageProvider();
        if (imgFile != null) {
            imageProvider.save(ProfileActivity.this, imgFile).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {
                        imageProvider.getDownloadUri().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                Log.d("TAG1", url);
                                user.setImage(url);
                                usersProvider.updateUser(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(ProfileActivity.this, "La imagén se actualizó correctamente", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(ProfileActivity.this, "Error al guardar imagén", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}