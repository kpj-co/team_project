package com.example.kpj.model;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
                    .apply(new RequestOptions().placeholder(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().fallback(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().error(new ColorDrawable(Color.LTGRAY)))
                    .into(imageView);
        } else if (imagePath != null){
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().placeholder(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().fallback(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().error(new ColorDrawable(Color.LTGRAY)))                    .into(imageView);
        } else if (parseFile != null) {
            Glide.with(context)
                    .load(parseFile.getUrl())
                    .apply(new RequestOptions().placeholder(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().fallback(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().error(new ColorDrawable(Color.LTGRAY)))                    .into(imageView);
        } else {
            Toast.makeText(context, "can not load images", Toast.LENGTH_SHORT).show();
        }
    }

    public void loadImage(Context context, ImageView imageView, RequestOptions requestOptions) {
        if (photoFile != null) {
            Glide.with(context)
                    .load(photoFile)
                    .apply(new RequestOptions().placeholder(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().fallback(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().error(new ColorDrawable(Color.LTGRAY)))
                    .apply(requestOptions)
                    .into(imageView);
        } else if (imagePath != null){
            Glide.with(context)
                    .load(imagePath)
                    .apply(new RequestOptions().placeholder(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().fallback(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().error(new ColorDrawable(Color.LTGRAY)))
                    .apply(requestOptions)
                    .into(imageView);
        } else if (parseFile != null) {
            Glide.with(context)
                    .load(parseFile.getUrl())
                    .apply(new RequestOptions().placeholder(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().fallback(new ColorDrawable(Color.LTGRAY)))
                    .apply(new RequestOptions().error(new ColorDrawable(Color.LTGRAY)))
                    .apply(requestOptions)
                    .into(imageView);
        } else {
            Toast.makeText(context, "error loading images", Toast.LENGTH_SHORT).show();
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
