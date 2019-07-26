package com.example.kpj;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;

import java.io.File;

import static com.example.kpj.activities.ComposePostActivity.APP_TAG;

public class CameraLauncher {

    private final  Context context;
    private final Callback callback;

    private final static String PHOTO_FILE_NAME = "photo.jpg";

    public CameraLauncher(Context context, Callback callback) {
        this.context = context;
        this.callback = callback;
    }

    public File onLaunchCamera() {
        // create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference to access to future access
        File photoFile = getPhotoFileUri(PHOTO_FILE_NAME);
        Uri fileProvider = FileProvider.getUriForFile(context,
                "com.codepath.fileprovider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            callback.startActivityForResult(intent);
        }
        return photoFile;
    }

    // Returns the File for a photo stored on disk given the fileName
    private File getPhotoFileUri(String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }
        // Return the file target for the photo based on filename
        File file = new File(mediaStorageDir.getPath() + File.separator + fileName + Math.random());
        return file;
    }

    public interface Callback {
        void startActivityForResult(Intent intent);
    }
}
