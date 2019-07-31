package com.example.kpj.model;

import android.content.Context;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.parse.ParseFile;

import java.io.File;

public class ImagePreview {

    private File photoFile;
    private String imagePath;
    private ParseFile parseFile;

    public ImagePreview(File photoFile) {
        this.photoFile = photoFile;
    }

    public ImagePreview(String imagePath){
        this.imagePath = imagePath;
    }

    public ImagePreview(ParseFile parseFile) {
        this.parseFile = parseFile;
    }

    public void loadImage(Context context, ImageView imageView) {
        if (photoFile != null) {
            Glide.with(context)
                    .load(photoFile)
                    .into(imageView);
        } else if (imagePath != null){
            Glide.with(context)
                    .load(imagePath)
                    .into(imageView);
        } else if (parseFile != null) {
            Glide.with(context)
                    .load(parseFile.getUrl())
                    .into(imageView);
        } else {
            Toast.makeText(context, "can not load images", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadImageRequest(Context context, ImageView imageView, RequestOptions requestOptions) {
        if (photoFile != null) {
            Glide.with(context)
                    .load(photoFile)
                    .apply(requestOptions)
                    .into(imageView);
        } else if (imagePath != null){
            Glide.with(context)
                    .load(imagePath)
                    .apply(requestOptions)
                    .into(imageView);
        } else if (parseFile != null) {
            Glide.with(context)
                    .load(parseFile.getUrl())
                    .apply(requestOptions)
                    .into(imageView);
        } else {
            Toast.makeText(context, "can not load images", Toast.LENGTH_SHORT).show();
        }
    }

    public ParseFile getParseFile() {
        if (photoFile != null) {
            return new ParseFile(photoFile);
        } else if (imagePath != null || imagePath.length() != 0) {
            return new ParseFile(new File(imagePath));
        } else {
            return parseFile;
        }
    }
}
