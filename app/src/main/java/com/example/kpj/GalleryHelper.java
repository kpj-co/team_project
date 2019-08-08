package com.example.kpj;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;

public class GalleryHelper {

    private final Context context;
    private final GalleryHelper.Callback callback;
    private boolean hasAccess;
    private final static int PICK_FROM_GALLERY = 3;


    public GalleryHelper(Context context, GalleryHelper.Callback callback) {
        this.context = context;
        this.callback = callback;
        hasAccess = false;
    }

    public void pickFromGallery() {
        if (hasAccess) {
            //Create an Intent with action as ACTION_PICK
            Intent intent = new Intent(Intent.ACTION_PICK);
            // Sets the type as image/*. This ensures only components of type image are selected
            intent.setType("image/*");
            //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
            // Launching the Intent
            callback.startActivityForResult(intent);
        }
    }

    public void requestGalleryPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity) context, new String[]
                                {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PICK_FROM_GALLERY);
                requestGalleryPermission();
            } else {
                hasAccess = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getImagePath(Intent data) {
        String imagePath;
        //data.getData return the content URI for the selected Image
        Uri selectedImage = data.getData();
        String[] filePathColumn = { MediaStore.Images.Media.DATA };
        // Get the cursor
        Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        // Move to first row
        cursor.moveToFirst();
        //Get the column index of MediaStore.Images.Media.DATA
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        //Gets the String value in the column
        imagePath = cursor.getString(columnIndex);
        cursor.close();
        return imagePath;
    }



    public interface Callback {
        void startActivityForResult(Intent intent);
    }
}
