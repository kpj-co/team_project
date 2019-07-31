package com.example.kpj.utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.kpj.R;
import com.example.kpj.model.ImagePreview;

import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.ViewHolder> {

    Context context;
    List<ImagePreview> mImages;

    public ImagePreviewAdapter(Context context, List<ImagePreview> mImages) {
        this.context = context;
        this.mImages = mImages;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View commentView = inflater.inflate(R.layout.image_preview_item, viewGroup, false);
        return new ViewHolder(commentView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        final ImagePreview image = mImages.get(position);
        image.loadImage(context, viewHolder.ivImagePreview);
    }

    @Override
    public int getItemCount() {
        return mImages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagePreview;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagePreview = itemView.findViewById(R.id.ivImagePreview);
        }
    }

}
