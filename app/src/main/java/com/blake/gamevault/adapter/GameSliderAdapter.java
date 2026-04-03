package com.blake.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class GameSliderAdapter extends RecyclerView.Adapter<GameSliderAdapter.GameSliderViewHolder> {

    private List<String> images;
    private String gameId;

    public GameSliderAdapter(List<String> images, String gameId) {
        this.images = images;
        this.gameId = gameId;
    }

    @NonNull
    @Override
    public GameSliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_image_slider, parent, false);
        return new GameSliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameSliderViewHolder holder, int position) {
        String fileName = images.get(position);

        // 1. Clear the view to prevent ghosting when swiping fast
        Glide.with(holder.itemView).clear(holder.imageView);
        holder.imageView.setImageResource(R.drawable.placeholder_game);

        if (fileName != null && fileName.startsWith("http")) {
            Glide.with(holder.itemView)
                    .load(fileName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE ADDED
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.close)
                    .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(holder.imageView);
        } else {
            String storagePath = "images/game-images/" + gameId + "/" + fileName;

            FirebaseStorage.getInstance().getReference(storagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String realUrl = uri.toString();

                        try {
                            images.set(position, realUrl);
                        } catch (UnsupportedOperationException e) {
                            // Ignore if list is immutable
                        }

                        Glide.with(holder.itemView)
                                .load(realUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE ADDED
                                .placeholder(R.drawable.placeholder_game)
                                .error(R.drawable.close)
                                .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                                .centerCrop()
                                .into(holder.imageView);
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("GameSliderAdapter", "Failed to fetch image: " + storagePath);
                        holder.imageView.setImageResource(R.drawable.close);
                    });
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class GameSliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        public GameSliderViewHolder(@NonNull View itemView) {
            super(itemView);
            this.imageView = itemView.findViewById(R.id.game_image_slider_item);
        }
    }
}