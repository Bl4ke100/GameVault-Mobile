package com.blake.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.bumptech.glide.Glide;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class GameSliderAdapter extends RecyclerView.Adapter<GameSliderAdapter.GameSliderViewHolder> {

    private List<String> images;
    private String gameId; // We need this to find the folder in Storage

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

        holder.imageView.setImageResource(R.drawable.placeholder_game);

        if (fileName != null && fileName.startsWith("http")) {
            Glide.with(holder.itemView)
                    .load(fileName)
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
                        }

                        Glide.with(holder.itemView)
                                .load(realUrl)
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