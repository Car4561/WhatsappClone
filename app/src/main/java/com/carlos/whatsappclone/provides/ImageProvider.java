package com.carlos.whatsappclone.provides;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.carlos.whatsappclone.utils.CompressorBitmapImage;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.Date;

public class ImageProvider {

    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;

    public ImageProvider(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

    }

    public UploadTask save(Context context, File file){
        byte[] imageByte = CompressorBitmapImage.getImage(context,file.getPath(),500,500);
        storageReference = storageReference.child(new Date() + ".jpg");
        UploadTask task = storageReference.putBytes(imageByte);
        Log.d("TAG1", Thread.currentThread().getName());
        return  task;
    }

    public Task<Uri> getDownloadUri(){
        return storageReference.getDownloadUrl();
    }

    public Task<Void> delete(String url){
        return firebaseStorage.getReferenceFromUrl(url).delete();
    }
}
