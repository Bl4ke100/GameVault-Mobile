package com.blake.gamevault.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.blake.gamevault.R;
import com.blake.gamevault.model.Game;
import com.blake.gamevault.util.CardFlipAnimator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;
import java.util.Locale;

public class SimilarGameAdapter extends RecyclerView.Adapter<SimilarGameAdapter.ViewHolder> {

    private List<Game> games;
    private OnGameClickListener listener;

    public SimilarGameAdapter(List<Game> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimilarGameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_game_recycler, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimilarGameAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameTitle.setText(game.getTitle());

        // Proper currency formatting instead of appending "0"
        holder.gamePrice.setText(String.format(Locale.US, "LKR %,.2f", game.getPrice()));

        Glide.with(holder.itemView).clear(holder.gameImage);
        holder.gameImage.setImageResource(R.drawable.placeholder_game);

        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        if (posterName.startsWith("http")) {
<<<<<<< HEAD
            // Yes! Load instantly from disk/memory cache.
=======
>>>>>>> d0e449b8f2fe214ea1effb6812f4624bd8ff5d73
            Glide.with(holder.itemView)
                    .load(posterName)
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE ADDED
                    .placeholder(R.drawable.placeholder_game)
                    .error(R.drawable.placeholder_game)
                    .centerCrop()
                    .into(holder.gameImage);
        } else {
            String posterPath = "images/game-images/" + game.getGameId() + "/" + posterName;

            FirebaseStorage.getInstance().getReference(posterPath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String realUrl = uri.toString();

                        game.setPosterUrl(realUrl);

                        Glide.with(holder.itemView)
                                .load(realUrl)
                                .diskCacheStrategy(DiskCacheStrategy.ALL) // DISK CACHE ADDED
                                .placeholder(R.drawable.placeholder_game)
                                .error(R.drawable.placeholder_game)
                                .centerCrop()
                                .into(holder.gameImage);
                    })
                    .addOnFailureListener(e -> {
                        android.util.Log.e("SimilarGameAdapter", "Failed to load image for: " + game.getGameId());
                    });
        }

        CardFlipAnimator.attach(holder.itemView, () -> {
            if (listener != null) {
                listener.onGameClick(game);
            }
        });
    }

    @Override
    public int getItemCount() {
        return games.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView gameImage;
        TextView gameTitle;
        TextView gamePrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            gameImage = itemView.findViewById(R.id.similarGameImage);
            gameTitle = itemView.findViewById(R.id.similarGameName);
            gamePrice = itemView.findViewById(R.id.similarGamePrice);
        }
    }

    public interface OnGameClickListener{
        void onGameClick(Game game);
    }
}