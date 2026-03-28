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

    // Update constructor to accept gameId
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
        // images.get(position) should now be the filename (e.g., "image1.png")
        String fileName = images.get(position);

        // Build the path: images/game-images/{gameId}/{fileName}
        String storagePath = "images/game-images/" + gameId + "/" + fileName;

        FirebaseStorage.getInstance().getReference(storagePath)
                .getDownloadUrl()
                .addOnSuccessListener(uri -> {
                    Glide.with(holder.itemView.getContext())
                            .load(uri)
                            .placeholder(R.drawable.game) // Show a gray box while loading
                            .error(R.drawable.close)            // Show this if path is wrong
                            .transition(com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade())
                            .centerCrop()
                            .into(holder.imageView);
                });
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