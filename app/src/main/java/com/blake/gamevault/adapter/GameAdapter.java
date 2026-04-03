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

import java.util.List;
import java.util.Locale;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private List<Game> games;
    private OnGameClickListener listener;

    public GameAdapter(List<Game> games, OnGameClickListener listener) {
        this.games = games;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GameAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.game_listing, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GameAdapter.ViewHolder holder, int position) {
        Game game = games.get(position);
        holder.gameTitle.setText(game.getTitle());

        // Fixed the currency formatting to be bulletproof!
        holder.gamePrice.setText(String.format(Locale.US, "LKR %,.2f", game.getPrice()));

        Glide.with(holder.itemView).clear(holder.gameImage);
        holder.gameImage.setImageResource(R.drawable.placeholder_game);

        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        if (posterName.startsWith("http")) {
<<<<<<< HEAD
            // Yes! Load instantly from Glide's disk/memory cache
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
            String storagePath = "images/game-images/" + game.getGameId() + "/" + posterName;

            com.google.firebase.storage.FirebaseStorage.getInstance().getReference(storagePath)
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
                        android.util.Log.e("GameAdapter", "Failed to load image for: " + game.getGameId());
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
            gameImage = itemView.findViewById(R.id.gameImage);
            gameTitle = itemView.findViewById(R.id.gameName);
            gamePrice = itemView.findViewById(R.id.gamePrice);
        }
    }

    public interface OnGameClickListener{
        void onGameClick(Game game);
    }
}