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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;
import java.util.Locale;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Game> gameList;
    private OnFavoriteClickListener listener;

    public FavoriteAdapter(List<Game> gameList, OnFavoriteClickListener listener) {
        this.gameList = gameList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist_game, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Game game = gameList.get(position);
        Context context = holder.itemView.getContext();

        holder.favGameTitle.setText(game.getTitle());
        holder.favGamePrice.setText(String.format(Locale.US, "LKR %,.2f", game.getPrice()));

<<<<<<< HEAD
        // 1. Clear recycled image
        Glide.with(holder.itemView).clear(holder.favGameImage);
        holder.favGameImage.setImageResource(R.drawable.placeholder_game);

=======
>>>>>>> d0e449b8f2fe214ea1effb6812f4624bd8ff5d73
        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

<<<<<<< HEAD
        // 2. CACHING TRICK: Load from memory if URL is already known
        if (posterName.startsWith("http")) {
            Glide.with(holder.itemView)
                    .load(posterName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .into(holder.favGameImage);
        } else {
            // 3. Fetch from Storage if URL is unknown
            StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                    .child("images")
                    .child("game-images")
                    .child(game.getGameId())
                    .child(posterName);

            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String realUrl = uri.toString();
                game.setPosterUrl(realUrl); // Save URL back to object

                Glide.with(holder.itemView) // Use itemView for lifecycle safety
                        .load(realUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE
                        .placeholder(R.drawable.placeholder_game)
                        .error(R.drawable.placeholder_game)
                        .into(holder.favGameImage);
            }).addOnFailureListener(e -> {
                android.util.Log.e("FavoriteAdapter", "Error loading image for " + game.getGameId() + ": " + e.getMessage());
            });
        }
=======
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("images")
                .child("game-images")
                .child(game.getGameId())
                .child(posterName);

        holder.favGameImage.setImageResource(R.drawable.placeholder_game);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .into(holder.favGameImage);
        }).addOnFailureListener(e -> {
            android.util.Log.e("FavoriteAdapter", "Error loading image for " + game.getGameId() + ": " + e.getMessage());
        });
>>>>>>> d0e449b8f2fe214ea1effb6812f4624bd8ff5d73

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onGameClick(game);
        });

        holder.btnRemoveFav.setOnClickListener(v -> {
            if (listener != null) listener.onRemoveClick(game, position);
        });
    }

    @Override
    public int getItemCount() {
        return gameList.size();
    }

    public void removeGame(int position) {
        gameList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, gameList.size());
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView favGameImage;
        TextView favGameTitle, favGamePrice;
        View btnRemoveFav;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            favGameImage = itemView.findViewById(R.id.favGameImage);
            favGameTitle = itemView.findViewById(R.id.favGameTitle);
            favGamePrice = itemView.findViewById(R.id.favGamePrice);
            btnRemoveFav = itemView.findViewById(R.id.btnRemoveFav);
        }
    }

    public interface OnFavoriteClickListener {
        void onGameClick(Game game);
        void onRemoveClick(Game game, int position);
    }
}