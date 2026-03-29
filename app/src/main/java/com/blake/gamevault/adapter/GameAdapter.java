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

import java.util.List;

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
        holder.gamePrice.setText("LKR " + game.getPrice() + "0");

        Glide.with(holder.itemView).clear(holder.gameImage);
        holder.gameImage.setImageResource(R.drawable.placeholder_game);

        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        if (posterName.startsWith("http")) {
            Glide.with(holder.itemView)
                    .load(posterName)
                    .placeholder(R.drawable.placeholder_game)
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
                                .placeholder(R.drawable.placeholder_game)
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
