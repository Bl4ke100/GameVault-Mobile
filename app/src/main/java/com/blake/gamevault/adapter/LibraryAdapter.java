package com.blake.gamevault.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.Game;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder> {

    private List<Game> gameList;
    private OnKeyClickListener listener;

    public LibraryAdapter(List<Game> gameList, OnKeyClickListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LibraryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_library_game, parent, false);
        return new LibraryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LibraryViewHolder holder, int position) {
        Game game = gameList.get(position);

        holder.gameTitle.setText(game.getTitle());

        // 1. Clear recycled image to prevent ghosting
        Glide.with(holder.itemView).clear(holder.gameImage);
        holder.gameImage.setImageResource(R.drawable.placeholder_game);

        // 2. Check for existing URL first (Caching Trick)
        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        if (posterName.startsWith("http")) {
            // Load instantly from cache
            Glide.with(holder.itemView)
                    .load(posterName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .centerCrop()
                    .into(holder.gameImage);
        } else {
            // Fetch from Storage
            String storagePath = "images/game-images/" + game.getGameId() + "/" + posterName;

            com.google.firebase.storage.FirebaseStorage.getInstance().getReference(storagePath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String realUrl = uri.toString();

                        // Save to object so we don't have to fetch it from Firebase again
                        game.setPosterUrl(realUrl);

                        // THE SHIELD: Use holder.itemView
                        Glide.with(holder.itemView)
                                .load(realUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE
                                .placeholder(R.drawable.placeholder_game)
                                .error(R.drawable.placeholder_game)
                                .centerCrop()
                                .into(holder.gameImage);
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("LibraryAdapter", "Failed to load poster for: " + game.getTitle());
                    });
        }

        holder.btnRevealKey.setOnClickListener(v -> {
            if (listener != null) {
                listener.onKeyClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        ImageView gameImage;
        TextView gameTitle;
        MaterialButton btnRevealKey;

        public LibraryViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.libraryGameImage);
            gameTitle = itemView.findViewById(R.id.libraryGameTitle);
            btnRevealKey = itemView.findViewById(R.id.btnRevealKey);
        }
    }

    public interface OnKeyClickListener {
        void onKeyClick(Game game);
    }
}