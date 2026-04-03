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
        // Change "item_favorite_card" if you named your XML file differently
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist_game, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Game game = gameList.get(position);
        Context context = holder.itemView.getContext();

        holder.favGameTitle.setText(game.getTitle());
        holder.favGamePrice.setText(String.format(Locale.US, "LKR %,.2f", game.getPrice()));

        // 1. Safe check for the poster name
        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        // 2. Build the Storage Reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("images")
                .child("game-images")
                .child(game.getGameId())
                .child(posterName);

        // 3. Set placeholder immediately
        holder.favGameImage.setImageResource(R.drawable.placeholder_game);

        // 4. Ask Firebase for the real URL, then load with Glide
        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Success! Load the real image
            Glide.with(context)
                    .load(uri)
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .into(holder.favGameImage);
        }).addOnFailureListener(e -> {
            // Failed to find the image in Storage, log the error so we can see why
            android.util.Log.e("FavoriteAdapter", "Error loading image for " + game.getGameId() + ": " + e.getMessage());
        });

        // Click listeners
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