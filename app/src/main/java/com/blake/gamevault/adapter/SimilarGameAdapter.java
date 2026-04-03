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
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

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
        holder.gamePrice.setText("LKR " + game.getPrice() + "0");

        // 1. Clear old image so recycled views don't show the wrong game momentarily
        Glide.with(holder.itemView).clear(holder.gameImage);
        holder.gameImage.setImageResource(R.drawable.placeholder_game);

        // 2. Get the poster name (default to poster.png if missing)
        String posterName = game.getPosterUrl();
        if (posterName == null || posterName.isEmpty()) {
            posterName = "poster.png";
        }

        // 3. CACHING TRICK: Did we already fetch this URL?
        if (posterName.startsWith("http")) {
            // Yes! Load instantly from memory.
            Glide.with(holder.itemView)
                    .load(posterName)
                    .placeholder(R.drawable.placeholder_game)
                    .centerCrop()
                    .into(holder.gameImage);
        } else {
            // No. Fetch from Firebase Storage.
            String posterPath = "images/game-images/" + game.getGameId() + "/" + posterName;

            FirebaseStorage.getInstance().getReference(posterPath)
                    .getDownloadUrl()
                    .addOnSuccessListener(uri -> {
                        String realUrl = uri.toString();

                        // Save it back to the game object to skip the network call next time
                        game.setPosterUrl(realUrl);

                        // THE SHIELD: holder.itemView ties Glide to the View's lifecycle
                        Glide.with(holder.itemView)
                                .load(realUrl)
                                .placeholder(R.drawable.placeholder_game)
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
